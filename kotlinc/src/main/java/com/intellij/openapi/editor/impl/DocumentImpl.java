package com.intellij.openapi.editor.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.intellij.core.CoreBundle;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.application.TransactionGuardImpl;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Attachment;
import com.intellij.openapi.diagnostic.ExceptionWithAttachments;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.ReadOnlyFragmentModificationException;
import com.intellij.openapi.editor.ReadOnlyModificationException;
import com.intellij.openapi.editor.actionSystem.DocCommandGroupId;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.editor.ex.PrioritizedDocumentListener;
import com.intellij.openapi.editor.ex.RangeMarkerEx;
import com.intellij.openapi.editor.impl.event.DocumentEventImpl;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.ProperTextRange;
import com.intellij.openapi.util.ShutDownTracker;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.reference.SoftReference;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.LocalTimeCounter;
import com.intellij.util.ObjectUtils;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.text.CharArrayUtil;
import com.intellij.util.text.ImmutableCharSequence;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class DocumentImpl extends UserDataHolderBase implements DocumentEx {
    private static final Logger LOG = Logger.getInstance(DocumentImpl.class);
    private static final int STRIP_TRAILING_SPACES_BULK_MODE_LINES_LIMIT = 1000;

    private final LockFreeCOWSortedArray<DocumentListener> myDocumentListeners =
            new LockFreeCOWSortedArray<>(
                    PrioritizedDocumentListener.COMPARATOR, DocumentListener.ARRAY_FACTORY);
    private final RangeMarkerTree<RangeMarkerEx> myRangeMarkers = new RangeMarkerTree<>(this);
    private final RangeMarkerTree<RangeMarkerEx> myPersistentRangeMarkers =
            new RangeMarkerTree<>(this);
    private final List<RangeMarker> myGuardedBlocks = new ArrayList<>();
    //  private ReadonlyFragmentModificationHandler myReadonlyFragmentModificationHandler;

    private final Object myLineSetLock = ObjectUtils.sentinel("line set lock");
    private volatile LineSet myLineSet;
    private volatile ImmutableCharSequence myText;
    private volatile SoftReference<String> myTextString;
    private volatile FrozenDocument myFrozen;

    private boolean myIsReadOnly;
    private volatile boolean isStripTrailingSpacesEnabled = true;
    private volatile long myModificationStamp;
    private final PropertyChangeSupport myPropertyChangeSupport = new PropertyChangeSupport(this);

    private final List<Object> myReadOnlyListeners = ContainerUtil.createLockFreeCopyOnWriteList();

    private int myCheckGuardedBlocks;
    private boolean myGuardsSuppressed;
    private boolean myEventsHandling;
    private final boolean myAssertThreading;
    private volatile boolean myDoingBulkUpdate;
    private volatile Throwable myBulkUpdateEnteringTrace;
    private boolean myUpdatingBulkModeStatus;
    private volatile boolean myAcceptSlashR;
    private boolean myChangeInProgress;
    private volatile int myBufferSize;
    private final CharSequence myMutableCharSequence =
            new CharSequence() {
                @Override
                public int length() {
                    return myText.length();
                }

                @Override
                public char charAt(int index) {
                    return myText.charAt(index);
                }

                @Override
                public @NonNull CharSequence subSequence(int start, int end) {
                    return myText.subSequence(start, end);
                }

                @NonNull
                @Override
                public String toString() {
                    return doGetText();
                }
            };
    private final AtomicInteger sequence = new AtomicInteger();

    public DocumentImpl(@NonNull String text) {
        this(text, false);
    }

    public DocumentImpl(@NonNull CharSequence chars) {
        this(chars, false);
    }

    /**
     * NOTE: if client sets forUseInNonAWTThread to true it's supposed that client will completely
     * control document and its listeners. The noticeable peculiarity of DocumentImpl behavior in
     * this mode is that DocumentImpl won't suppress ProcessCancelledException thrown from listeners
     * during changedUpdate event, so the exception will be rethrown and rest of the listeners WON'T
     * be notified.
     */
    public DocumentImpl(@NonNull CharSequence chars, boolean forUseInNonAWTThread) {
        this(chars, false, forUseInNonAWTThread);
    }

    public DocumentImpl(
            @NonNull CharSequence chars, boolean acceptSlashR, boolean forUseInNonAWTThread) {
        setAcceptSlashR(acceptSlashR);
        assertValidSeparators(chars);
        myText = CharArrayUtil.createImmutableCharSequence(chars);
        setCyclicBufferSize(0);
        setModificationStamp(LocalTimeCounter.currentTime());
        myAssertThreading = !forUseInNonAWTThread;
    }

    static final Key<Reference<RangeMarkerTree<RangeMarkerEx>>> RANGE_MARKERS_KEY =
            Key.create("RANGE_MARKERS_KEY");
    static final Key<Reference<RangeMarkerTree<RangeMarkerEx>>> PERSISTENT_RANGE_MARKERS_KEY =
            Key.create("PERSISTENT_RANGE_MARKERS_KEY");
    //  @ApiStatus.Internal
    public void documentCreatedFrom(@NonNull VirtualFile f, int tabSize) {
        processQueue();
        getSaveRMTree(f, RANGE_MARKERS_KEY, myRangeMarkers, tabSize);
        getSaveRMTree(f, PERSISTENT_RANGE_MARKERS_KEY, myPersistentRangeMarkers, tabSize);
    }

    // are some range markers retained by strong references?
    public static boolean areRangeMarkersRetainedFor(@NonNull VirtualFile f) {
        processQueue();
        // if a marker is retained then so is its node and the whole tree
        // (ignore the race when marker is gc-ed right after this call - it's harmless)
        return SoftReference.dereference(f.getUserData(RANGE_MARKERS_KEY)) != null
                || SoftReference.dereference(f.getUserData(PERSISTENT_RANGE_MARKERS_KEY)) != null;
    }

    private void getSaveRMTree(
            @NonNull VirtualFile f,
            @NonNull Key<Reference<RangeMarkerTree<RangeMarkerEx>>> key,
            @NonNull RangeMarkerTree<RangeMarkerEx> tree,
            int tabSize) {
        RMTreeReference freshRef = new RMTreeReference(tree, f);
        Reference<RangeMarkerTree<RangeMarkerEx>> oldRef;
        do {
            oldRef = f.getUserData(key);
        } while (!f.replace(key, oldRef, freshRef));
        RangeMarkerTree<RangeMarkerEx> oldTree = SoftReference.dereference(oldRef);

        if (oldTree == null) {
            // no tree was saved in virtual file before. happens when created new document.
            // or the old tree got gc-ed, because no reachable markers retaining it are left alive.
            // good riddance.
            return;
        }

        // old tree was saved in the virtual file. Have to transfer markers from there.
        //    oldTree.processAll(r -> {
        //      if (r.isValid()) {
        //        ((RangeMarkerImpl)r).reRegister(this, tabSize);
        //      }
        //      else {
        //        ((RangeMarkerImpl)r).invalidate("document was gc-ed and re-created");
        //      }
        //      return true;
        //    });
    }

    // track GC of RangeMarkerTree: means no-one is interested in range markers for this file
    // anymore
    private static final ReferenceQueue<RangeMarkerTree<RangeMarkerEx>> rmTreeQueue =
            new ReferenceQueue<>();

    private static class RMTreeReference extends WeakReference<RangeMarkerTree<RangeMarkerEx>> {
        @NonNull private final VirtualFile virtualFile;

        RMTreeReference(
                @NonNull RangeMarkerTree<RangeMarkerEx> referent,
                @NonNull VirtualFile virtualFile) {
            super(referent, rmTreeQueue);
            this.virtualFile = virtualFile;
        }
    }

    //  @ApiStatus.Internal
    public static void processQueue() {
        RMTreeReference ref;
        while ((ref = (RMTreeReference) rmTreeQueue.poll()) != null) {
            ref.virtualFile.replace(RANGE_MARKERS_KEY, ref, null);
            ref.virtualFile.replace(PERSISTENT_RANGE_MARKERS_KEY, ref, null);
        }
    }

    /** makes range marker without creating document (which could be expensive) */
    @NonNull
    static RangeMarker createRangeMarkerForVirtualFile(
            @NonNull VirtualFile file,
            int offset,
            int startLine,
            int startCol,
            int endLine,
            int endCol,
            boolean persistent) {
        //    int estimatedLength = RangeMarkerImpl.estimateDocumentLength(file);
        //    offset = Math.min(offset, estimatedLength);
        //    RangeMarkerImpl marker = persistent
        //                             ? new PersistentRangeMarker(file, offset, offset, startLine,
        // startCol, endLine, endCol, estimatedLength, false)
        //                             : new RangeMarkerImpl(file, offset, offset, estimatedLength,
        // false);
        //    Key<Reference<RangeMarkerTree<RangeMarkerEx>>> key = persistent ?
        // PERSISTENT_RANGE_MARKERS_KEY : RANGE_MARKERS_KEY;
        //    RangeMarkerTree<RangeMarkerEx> tree;
        //    while (true) {
        //      Reference<RangeMarkerTree<RangeMarkerEx>> oldRef = file.getUserData(key);
        //      tree = SoftReference.dereference(oldRef);
        //        if (tree != null) {
        //            break;
        //        }
        //      tree = new RangeMarkerTree<>();
        //      RMTreeReference reference = new RMTreeReference(tree, file);
        //        if (file.replace(key, oldRef, reference)) {
        //            break;
        //        }
        //    }
        //    tree.addInterval(marker, offset, offset, false, false, false, 0);
        //
        //    return marker;
        throw new UnsupportedOperationException();
    }

    public boolean setAcceptSlashR(boolean accept) {
        try {
            return myAcceptSlashR;
        } finally {
            myAcceptSlashR = accept;
        }
    }

    public boolean acceptsSlashR() {
        return myAcceptSlashR;
    }

    private LineSet getLineSet() {
        LineSet lineSet = myLineSet;
        if (lineSet == null) {
            synchronized (myLineSetLock) {
                lineSet = myLineSet;
                if (lineSet == null) {
                    lineSet = LineSet.createLineSet(myText);
                    myLineSet = lineSet;
                }
            }
        }

        return lineSet;
    }

    //  @Override
    public void setStripTrailingSpacesEnabled(boolean isEnabled) {
        isStripTrailingSpacesEnabled = isEnabled;
    }

    @VisibleForTesting
    public boolean stripTrailingSpaces(Project project) {
        return stripTrailingSpaces(project, false);
    }

    @VisibleForTesting
    public boolean stripTrailingSpaces(Project project, boolean inChangedLinesOnly) {
        return stripTrailingSpaces(project, inChangedLinesOnly, null);
    }

    @Override
    public boolean isLineModified(int line) {
        LineSet lineSet = myLineSet;
        return lineSet != null && lineSet.isModified(line);
    }

    /**
     * @return true if stripping was completed successfully, false if the document prevented
     *     stripping by e.g. caret(s) being in the way
     */
    boolean stripTrailingSpaces(
            @Nullable final Project project, boolean inChangedLinesOnly, int[] caretOffsets) {
        throw new UnsupportedOperationException();
        //    if (!isStripTrailingSpacesEnabled) {
        //      return true;
        //    }
        //    List<StripTrailingSpacesFilter> filters = new ArrayList<>();
        //    StripTrailingSpacesFilter specialFilter = null;
        //    for (StripTrailingSpacesFilterFactory filterFactory :
        // StripTrailingSpacesFilterFactory.EXTENSION_POINT.getExtensions()) {
        //      StripTrailingSpacesFilter filter = filterFactory.createFilter(project, this);
        //      if (specialFilter == null &&
        //          (filter == StripTrailingSpacesFilter.NOT_ALLOWED || filter ==
        // StripTrailingSpacesFilter.POSTPONED)) {
        //        specialFilter = filter;
        //      }
        //      else if (filter == StripTrailingSpacesFilter.ENFORCED_REMOVAL) {
        //        specialFilter = null;
        //        filters.clear();
        //        break;
        //      }
        //      else {
        //        filters.add(filter);
        //      }
        //    }
        //
        //    if (specialFilter != null) {
        //      return specialFilter == StripTrailingSpacesFilter.NOT_ALLOWED;
        //    }
        //
        //    Int2IntMap caretPositions = null;
        //    if (caretOffsets != null) {
        //      caretPositions = new Int2IntOpenHashMap(caretOffsets.length);
        //      for (int caretOffset : caretOffsets) {
        //        int line = getLineNumber(caretOffset);
        //        // need to remember only maximum caret offset on a line
        //        caretPositions.put(line, Math.max(caretOffset, caretPositions.get(line)));
        //      }
        //    }
        //
        //    LineSet lineSet = getLineSet();
        //    int lineCount = getLineCount();
        //    int[] targetOffsets = new int[lineCount * 2];
        //    int targetOffsetPos = 0;
        //    boolean markAsNeedsStrippingLater = false;
        //    CharSequence text = myText;
        //    for (int line = 0; line < lineCount; line++) {
        //      int maxSpacesToLeave = getMaxSpacesToLeave(line, filters);
        //        if (inChangedLinesOnly && !lineSet.isModified(line) || maxSpacesToLeave < 0) {
        //            continue;
        //        }
        //
        //      int whiteSpaceStart = -1;
        //      final int lineEnd = lineSet.getLineEnd(line) - lineSet.getSeparatorLength(line);
        //      int lineStart = lineSet.getLineStart(line);
        //      for (int offset = lineEnd - 1; offset >= lineStart; offset--) {
        //        char c = text.charAt(offset);
        //        if (c != ' ' && c != '\t') {
        //          break;
        //        }
        //        whiteSpaceStart = offset;
        //      }
        //        if (whiteSpaceStart == -1) {
        //            continue;
        //        }
        //
        //      if (caretPositions != null) {
        //        int caretPosition = caretPositions.get(line);
        //        if (whiteSpaceStart < caretPosition) {
        //          markAsNeedsStrippingLater = true;
        //          continue;
        //        }
        //      }
        //
        //      final int finalStart = whiteSpaceStart + maxSpacesToLeave;
        //      if (finalStart < lineEnd) {
        //        targetOffsets[targetOffsetPos++] = finalStart;
        //        targetOffsets[targetOffsetPos++] = lineEnd;
        //      }
        //    }
        //    int finalTargetOffsetPos = targetOffsetPos;
        //    // document must be unblocked by now. If not, some Save handler attempted to modify
        // PSI
        //    // which should have been caught by assertion in
        // com.intellij.pom.core.impl.PomModelImpl.runTransaction
        //    DocumentUtil.writeInRunUndoTransparentAction(new DocumentRunnable(this, project) {
        //      @Override
        //      public void run() {
        //        DocumentUtil.executeInBulk(DocumentImpl.this, finalTargetOffsetPos >
        // STRIP_TRAILING_SPACES_BULK_MODE_LINES_LIMIT * 2, () -> {
        //          int pos = finalTargetOffsetPos;
        //          while (pos > 0) {
        //            int endOffset = targetOffsets[--pos];
        //            int startOffset = targetOffsets[--pos];
        //            deleteString(startOffset, endOffset);
        //          }
        //        });
        //      }
        //    });
        //    return markAsNeedsStrippingLater;
    }

    //  private static int getMaxSpacesToLeave(int line, @NonNull List<? extends
    // StripTrailingSpacesFilter> filters) {
    //    for (StripTrailingSpacesFilter filter :  filters) {
    //      if (filter instanceof SmartStripTrailingSpacesFilter) {
    //        return ((SmartStripTrailingSpacesFilter)filter).getTrailingSpacesToLeave(line);
    //      }
    //      else  if (!filter.isStripSpacesAllowedForLine(line)) {
    //        return -1;
    //      }
    //    }
    //    return 0;
    //  }

    @Override
    public void setReadOnly(boolean isReadOnly) {
        if (myIsReadOnly != isReadOnly) {
            myIsReadOnly = isReadOnly;
            myPropertyChangeSupport.firePropertyChange(
                    Document.PROP_WRITABLE, !isReadOnly, isReadOnly);
        }
    }

    //  ReadonlyFragmentModificationHandler getReadonlyFragmentModificationHandler() {
    //    return myReadonlyFragmentModificationHandler;
    //  }
    //
    //  void setReadonlyFragmentModificationHandler(final ReadonlyFragmentModificationHandler
    // readonlyFragmentModificationHandler) {
    //    myReadonlyFragmentModificationHandler = readonlyFragmentModificationHandler;
    //  }

    @Override
    public boolean isWritable() {
        if (myIsReadOnly) {
            return false;
        }

        for (DocumentWriteAccessGuard guard : DocumentWriteAccessGuard.EP_NAME.getExtensions()) {
            if (!guard.isWritable(this).isSuccess()) {
                return false;
            }
        }

        return true;
    }

    private RangeMarkerTree<RangeMarkerEx> treeFor(@NonNull RangeMarkerEx rangeMarker) {
        return rangeMarker instanceof PersistentRangeMarker
                ? myPersistentRangeMarkers
                : myRangeMarkers;
    }

    @Override
    public boolean removeRangeMarker(@NonNull RangeMarkerEx rangeMarker) {
        return treeFor(rangeMarker).removeInterval(rangeMarker);
    }

    //  @Override
    public void registerRangeMarker(
            @NonNull RangeMarkerEx rangeMarker,
            int start,
            int end,
            boolean greedyToLeft,
            boolean greedyToRight,
            int layer) {
        throw new UnsupportedOperationException();
        //    treeFor(rangeMarker).addInterval(rangeMarker, start, end, greedyToLeft, greedyToRight,
        // false, layer);
    }

    @VisibleForTesting
    int getRangeMarkersSize() {
        return myRangeMarkers.size() + myPersistentRangeMarkers.size();
    }

    @VisibleForTesting
    int getRangeMarkersNodeSize() {
        return myRangeMarkers.nodeSize() + myPersistentRangeMarkers.nodeSize();
    }

    @Override
    @NonNull
    public RangeMarker createGuardedBlock(int startOffset, int endOffset) {
        LOG.assertTrue(startOffset <= endOffset, "Should be startOffset <= endOffset");
        RangeMarker block = createRangeMarker(startOffset, endOffset, true);
        myGuardedBlocks.add(block);
        return block;
    }

    @Override
    public void removeGuardedBlock(@NonNull RangeMarker block) {
        myGuardedBlocks.remove(block);
    }

    //  @Override
    @NonNull
    public List<RangeMarker> getGuardedBlocks() {
        return myGuardedBlocks;
    }

    @Override
    public RangeMarker getOffsetGuard(int offset) {
        // Way too many garbage is produced otherwise in AbstractList.iterator()
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < myGuardedBlocks.size(); i++) {
            RangeMarker block = myGuardedBlocks.get(i);
            if (offsetInRange(offset, block.getStartOffset(), block.getEndOffset())) {
                return block;
            }
        }

        return null;
    }

    @Override
    public RangeMarker getRangeGuard(int start, int end) {
        for (RangeMarker block : myGuardedBlocks) {
            if (rangesIntersect(
                    start,
                    end,
                    true,
                    true,
                    block.getStartOffset(),
                    block.getEndOffset(),
                    block.isGreedyToLeft(),
                    block.isGreedyToRight())) {
                return block;
            }
        }

        return null;
    }

    @Override
    public void startGuardedBlockChecking() {
        myCheckGuardedBlocks++;
    }

    @Override
    public void stopGuardedBlockChecking() {
        LOG.assertTrue(myCheckGuardedBlocks > 0, "Unpaired start/stopGuardedBlockChecking");
        myCheckGuardedBlocks--;
    }

    private static boolean offsetInRange(int offset, int start, int end) {
        return start <= offset && offset < end;
    }

    private static boolean rangesIntersect(
            int start0,
            int end0,
            boolean start0Inclusive,
            boolean end0Inclusive,
            int start1,
            int end1,
            boolean start1Inclusive,
            boolean end1Inclusive) {
        if (start0 > start1 || start0 == start1 && !start0Inclusive) {
            if (end1 == start0) {
                return start0Inclusive && end1Inclusive;
            }
            return end1 > start0;
        }
        if (end0 == start1) {
            return start1Inclusive && end0Inclusive;
        }
        return end0 > start1;
    }

    @Override
    @NonNull
    public RangeMarker createRangeMarker(
            int startOffset, int endOffset, boolean surviveOnExternalChange) {
        if (!(0 <= startOffset && startOffset <= endOffset && endOffset <= getTextLength())) {
            LOG.error(
                    "Incorrect offsets: startOffset="
                            + startOffset
                            + ", endOffset="
                            + endOffset
                            + ", text length="
                            + getTextLength());
        }

        throw new UnsupportedOperationException();
        //    return surviveOnExternalChange
        //           ? new PersistentRangeMarker(this, startOffset, endOffset, true)
        //           : new RangeMarkerImpl(this, startOffset, endOffset, true, false);
    }

    @Override
    public long getModificationStamp() {
        return myModificationStamp;
    }

    @Override
    public void setModificationStamp(long modificationStamp) {
        myModificationStamp = modificationStamp;
        myFrozen = null;
    }

    //  @Override
    public void replaceText(@NonNull CharSequence chars, long newModificationStamp) {
        replaceString(
                0, getTextLength(), 0, chars, newModificationStamp, true); // TODO: optimization!!!
        clearLineModificationFlags();
    }

    @Override
    public void insertString(int offset, @NonNull CharSequence s) {
        if (offset < 0) {
            throw new IndexOutOfBoundsException("Wrong offset: " + offset);
        }
        if (offset > getTextLength()) {
            throw new IndexOutOfBoundsException(
                    "Wrong offset: " + offset + "; documentLength: " + getTextLength());
        }
        assertWriteAccess();
        assertValidSeparators(s);

        if (s.length() == 0) {
            return;
        }

        RangeMarker marker = getRangeGuard(offset, offset);
        if (marker != null) {
            throwGuardedFragment(marker, offset, "", s);
        }

        ImmutableCharSequence newText = myText.insert(offset, s);
        ImmutableCharSequence newString = newText.subtext(offset, offset + s.length());
        updateText(
                newText,
                offset,
                "",
                newString,
                false,
                LocalTimeCounter.currentTime(),
                offset,
                0,
                offset);
        trimToSize();
    }

    private void trimToSize() {
        if (myBufferSize != 0 && getTextLength() > myBufferSize) {
            deleteString(0, getTextLength() - myBufferSize);
        }
    }

    @Override
    public void deleteString(int startOffset, int endOffset) {
        assertBounds(startOffset, endOffset);

        assertWriteAccess();
        if (startOffset == endOffset) {
            return;
        }

        RangeMarker marker = getRangeGuard(startOffset, endOffset);
        if (marker != null) {
            throwGuardedFragment(
                    marker, startOffset, myText.subSequence(startOffset, endOffset), "");
        }

        ImmutableCharSequence newText = myText.delete(startOffset, endOffset);
        ImmutableCharSequence oldString = myText.subtext(startOffset, endOffset);
        updateText(
                newText,
                startOffset,
                oldString,
                "",
                false,
                LocalTimeCounter.currentTime(),
                startOffset,
                endOffset - startOffset,
                startOffset);
    }

    //  @Override
    public void moveText(int srcStart, int srcEnd, int dstOffset) {
        assertBounds(srcStart, srcEnd);
        if (dstOffset == srcStart || dstOffset == srcEnd) {
            return;
        }
        ProperTextRange srcRange = new ProperTextRange(srcStart, srcEnd);
        assert !srcRange.containsOffset(dstOffset)
                : "Can't perform text move from range ["
                        + srcStart
                        + "; "
                        + srcEnd
                        + ") to offset "
                        + dstOffset;

        String replacement = getCharsSequence().subSequence(srcStart, srcEnd).toString();
        int shift = dstOffset < srcStart ? srcEnd - srcStart : 0;

        // a pair of insert remove modifications
        replaceString(
                dstOffset,
                dstOffset,
                srcStart + shift,
                replacement,
                LocalTimeCounter.currentTime(),
                false);
        replaceString(
                srcStart + shift,
                srcEnd + shift,
                dstOffset,
                "",
                LocalTimeCounter.currentTime(),
                false);
    }

    @Override
    public void replaceString(int startOffset, int endOffset, @NonNull CharSequence s) {
        replaceString(
                startOffset, endOffset, startOffset, s, LocalTimeCounter.currentTime(), false);
    }

    public void replaceString(
            int startOffset,
            int endOffset,
            int moveOffset,
            @NonNull CharSequence s,
            final long newModificationStamp,
            boolean wholeTextReplaced) {
        assertBounds(startOffset, endOffset);

        assertWriteAccess();
        assertValidSeparators(s);

        if (moveOffset != startOffset && startOffset != endOffset && s.length() != 0) {
            throw new IllegalArgumentException(
                    "moveOffset != startOffset for a modification which is neither an insert nor"
                            + " deletion. startOffset: "
                            + startOffset
                            + "; endOffset: "
                            + endOffset
                            + ";"
                            + "; moveOffset: "
                            + moveOffset
                            + ";");
        }

        int initialStartOffset = startOffset;
        int initialOldLength = endOffset - startOffset;

        final int newStringLength = s.length();
        final CharSequence chars = myText;
        int newStartInString = 0;
        while (newStartInString < newStringLength
                && startOffset < endOffset
                && s.charAt(newStartInString) == chars.charAt(startOffset)) {
            startOffset++;
            newStartInString++;
        }
        if (newStartInString == newStringLength && startOffset == endOffset && !wholeTextReplaced) {
            return;
        }

        int newEndInString = newStringLength;
        while (endOffset > startOffset
                && newEndInString > newStartInString
                && s.charAt(newEndInString - 1) == chars.charAt(endOffset - 1)) {
            newEndInString--;
            endOffset--;
        }

        if (startOffset == 0 && endOffset == getTextLength()) {
            wholeTextReplaced = true;
        }

        CharSequence changedPart = s.subSequence(newStartInString, newEndInString);
        CharSequence sToDelete = myText.subtext(startOffset, endOffset);
        RangeMarker guard = getRangeGuard(startOffset, endOffset);
        if (guard != null) {
            throwGuardedFragment(guard, startOffset, sToDelete, changedPart);
        }

        ImmutableCharSequence newText;
        if (wholeTextReplaced && s instanceof ImmutableCharSequence) {
            newText = (ImmutableCharSequence) s;
        } else {
            myText.delete(startOffset, endOffset);
            newText = myText.insert(startOffset, changedPart);
            changedPart = newText.subtext(startOffset, startOffset + changedPart.length());
        }
        boolean wasOptimized =
                initialStartOffset != startOffset || endOffset - startOffset != initialOldLength;
        updateText(
                newText,
                startOffset,
                sToDelete,
                changedPart,
                wholeTextReplaced,
                newModificationStamp,
                initialStartOffset,
                initialOldLength,
                wasOptimized ? startOffset : moveOffset);
        trimToSize();
    }

    private void assertBounds(final int startOffset, final int endOffset) {
        if (startOffset < 0 || startOffset > getTextLength()) {
            throw new IndexOutOfBoundsException(
                    "Wrong startOffset: " + startOffset + "; documentLength: " + getTextLength());
        }
        if (endOffset < 0 || endOffset > getTextLength()) {
            throw new IndexOutOfBoundsException(
                    "Wrong endOffset: " + endOffset + "; documentLength: " + getTextLength());
        }
        if (endOffset < startOffset) {
            throw new IllegalArgumentException(
                    "endOffset < startOffset: "
                            + endOffset
                            + " < "
                            + startOffset
                            + "; documentLength: "
                            + getTextLength());
        }
    }

    //  @ApiStatus.Internal
    //  @ApiStatus.Experimental
    public boolean isWriteThreadOnly() {
        return myAssertThreading;
    }

    private void assertWriteAccess() {
        if (myAssertThreading) {
            final Application application = ApplicationManager.getApplication();
            if (application != null) {
                application.assertWriteAccessAllowed();
                VirtualFile file = FileDocumentManager.getInstance().getFile(this);
                if (file != null && file.isInLocalFileSystem()) {
                    ((TransactionGuardImpl) TransactionGuard.getInstance())
                            .assertWriteActionAllowed();
                }
            }
        }

        if (myIsReadOnly) {
            throw new ReadOnlyModificationException(
                    this, CoreBundle.message("attempt.to.modify.read.only.document.error.message"));
        }

        for (DocumentWriteAccessGuard guard : DocumentWriteAccessGuard.EP_NAME.getExtensions()) {
            DocumentWriteAccessGuard.Result result = guard.isWritable(this);
            if (!result.isSuccess()) {
                throw new ReadOnlyModificationException(
                        this,
                        String.format(
                                "%s: guardClass=%s, failureReason=%s",
                                CoreBundle.message(
                                        "attempt.to.modify.read.only.document.error.message"),
                                guard.getClass().getName(),
                                result.getFailureReason()));
            }
        }
    }

    private void assertValidSeparators(@NonNull CharSequence s) {
        if (myAcceptSlashR) {
            return;
        }
        StringUtil.assertValidSeparators(s);
    }

    /**
     * All document change actions follows the algorithm below:
     *
     * <pre>
     * <ol>
     *   <li>
     *     All {@link #addDocumentListener(DocumentListener) registered listeners} are notified
     *     {@link DocumentListener#beforeDocumentChange(DocumentEvent) before the change};
     *   </li>
     *   <li>The change is performed </li>
     *   <li>
     *     All {@link #addDocumentListener(DocumentListener) registered listeners} are notified
     *     {@link DocumentListener#documentChanged(DocumentEvent) after the change};
     *   </li>
     * </ol>
     * </pre>
     *
     * <p>There is a possible case that {@code 'before change'} notification produces new change. We
     * have a problem then - imagine that initial change was {@code 'replace particular range at
     * document end'} and {@code 'nested change'} was to {@code 'remove text at document end'}. That
     * means that when initial change will be actually performed, the document may be not long
     * enough to contain target range.
     *
     * <p>Current method allows to check if document change is a {@code 'nested call'}.
     *
     * @throws IllegalStateException if this method is called during a {@code 'nested document
     *     modification'}
     */
    private void assertNotNestedModification() throws IllegalStateException {
        if (myChangeInProgress) {
            throw new IllegalStateException("Detected document modification from DocumentListener");
        }
    }

    private void throwGuardedFragment(
            @NonNull RangeMarker guard,
            int offset,
            @NonNull CharSequence oldString,
            @NonNull CharSequence newString) {
        if (myCheckGuardedBlocks > 0 && !myGuardsSuppressed) {
            DocumentEvent event =
                    new DocumentEventImpl(
                            this,
                            offset,
                            oldString,
                            newString,
                            myModificationStamp,
                            false,
                            offset,
                            oldString.length(),
                            offset);
            throw new ReadOnlyFragmentModificationException(event, guard);
        }
    }

    @Override
    public void suppressGuardedExceptions() {
        myGuardsSuppressed = true;
    }

    @Override
    public void unSuppressGuardedExceptions() {
        myGuardsSuppressed = false;
    }

    @Override
    public boolean isInEventsHandling() {
        return myEventsHandling;
    }

    //  @Override
    public void clearLineModificationFlags() {
        myLineSet = getLineSet().clearModificationFlags();
        myFrozen = null;
    }

    public void clearLineModificationFlags(int startLine, int endLine) {
        myLineSet = getLineSet().clearModificationFlags(startLine, endLine);
        myFrozen = null;
    }

    void clearLineModificationFlagsExcept(int[] caretLines) {
        IntList modifiedLines = new IntArrayList(caretLines.length);
        LineSet lineSet = getLineSet();
        for (int line : caretLines) {
            if (line >= 0 && line < lineSet.getLineCount() && lineSet.isModified(line)) {
                modifiedLines.add(line);
            }
        }
        lineSet = lineSet.clearModificationFlags();
        lineSet = lineSet.setModified(modifiedLines);
        myLineSet = lineSet;
        myFrozen = null;
    }

    private void updateText(
            @NonNull ImmutableCharSequence newText,
            int offset,
            @NonNull CharSequence oldString,
            @NonNull CharSequence newString,
            boolean wholeTextReplaced,
            long newModificationStamp,
            int initialStartOffset,
            int initialOldLength,
            int moveOffset) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(
                    "updating document "
                            + this
                            + ".\nNext string:'"
                            + newString
                            + "'\nOld string:'"
                            + oldString
                            + "'");
        }

        assert moveOffset >= 0 && moveOffset <= getTextLength()
                : "Invalid moveOffset: " + moveOffset;
        assertNotNestedModification();
        myChangeInProgress = true;
        DelayedExceptions exceptions = new DelayedExceptions();
        try {
            DocumentEvent event =
                    new DocumentEventImpl(
                            this,
                            offset,
                            oldString,
                            newString,
                            myModificationStamp,
                            wholeTextReplaced,
                            initialStartOffset,
                            initialOldLength,
                            moveOffset);
            beforeChangedUpdate(event, exceptions);
            myTextString = null;
            ImmutableCharSequence prevText = myText;
            myText = newText;
            sequence.incrementAndGet(); // increment sequence before firing events so that
            // modification sequence on commit will match this sequence
            // now
            changedUpdate(event, newModificationStamp, prevText, exceptions);
        } finally {
            myChangeInProgress = false;
            exceptions.rethrowPCE();
        }
    }

    private final class DelayedExceptions {
        Throwable myException;

        void register(Throwable e) {
            if (myException == null) {
                myException = e;
            } else {
                myException.addSuppressed(e);
            }

            if (!(e instanceof ProcessCanceledException)) {
                LOG.error(e);
            } else if (myAssertThreading) {
                LOG.error(
                        "ProcessCanceledException must not be thrown from document listeners for"
                                + " real document",
                        new Throwable(e));
            }
        }

        void rethrowPCE() {
            if (myException instanceof ProcessCanceledException) {
                // the case of some wise inspection modifying non-physical document during
                // highlighting to be interrupted
                throw (ProcessCanceledException) myException;
            }
        }
    }

    @Override
    public int getModificationSequence() {
        return sequence.get();
    }

    private void beforeChangedUpdate(DocumentEvent event, DelayedExceptions exceptions) {
        Application app = ApplicationManager.getApplication();
        if (app != null) {
            FileDocumentManager manager = FileDocumentManager.getInstance();
            VirtualFile file = manager.getFile(this);
            if (file != null && !file.isValid()) {
                LOG.error("File of this document has been deleted: " + file);
            }
        }
        assertInsideCommand();

        getLineSet(); // initialize line set to track changed lines

        if (!ShutDownTracker.isShutdownHookRunning()) {
            DocumentListener[] listeners = getListeners();
            for (int i = listeners.length - 1; i >= 0; i--) {
                try {
                    listeners[i].beforeDocumentChange(event);
                } catch (Throwable e) {
                    exceptions.register(e);
                }
            }
        }

        myEventsHandling = true;
    }

    private void assertInsideCommand() {
        if (!myAssertThreading) {
            return;
        }
        CommandProcessor commandProcessor = CommandProcessor.getInstance();
        if (!commandProcessor.isUndoTransparentActionInProgress()
                && commandProcessor.getCurrentCommand() == null) {
            throw new IncorrectOperationException(
                    "Must not change document outside command or undo-transparent action. See"
                            + " com.intellij.openapi.command.WriteCommandAction or"
                            + " com.intellij.openapi.command.CommandProcessor");
        }
    }

    private void changedUpdate(
            @NonNull DocumentEvent event,
            long newModificationStamp,
            @NonNull CharSequence prevText,
            DelayedExceptions exceptions) {
        try {
            if (LOG.isTraceEnabled()) {
                LOG.trace(new Throwable(event.toString()));
            } else if (LOG.isDebugEnabled()) {
                LOG.debug(event.toString());
            }

            assert event.getOldFragment().length() == event.getOldLength()
                    : "event.getOldFragment().length() = "
                            + event.getOldFragment().length()
                            + "; event.getOldLength() = "
                            + event.getOldLength();
            assert event.getNewFragment().length() == event.getNewLength()
                    : "event.getNewFragment().length() = "
                            + event.getNewFragment().length()
                            + "; event.getNewLength() = "
                            + event.getNewLength();
            assert prevText.length() + event.getNewLength() - event.getOldLength()
                            == getTextLength()
                    : "prevText.length() = "
                            + prevText.length()
                            + "; event.getNewLength() = "
                            + event.getNewLength()
                            + "; event.getOldLength() = "
                            + event.getOldLength()
                            + "; getTextLength() = "
                            + getTextLength();

            myLineSet =
                    getLineSet()
                            .update(
                                    prevText,
                                    event.getOffset(),
                                    event.getOffset() + event.getOldLength(),
                                    event.getNewFragment(),
                                    event.isWholeTextReplaced());
            assert getTextLength() == myLineSet.getLength()
                    : "getTextLength() = "
                            + getTextLength()
                            + "; myLineSet.getLength() = "
                            + myLineSet.getLength();

            myFrozen = null;
            setModificationStamp(newModificationStamp);

            if (!ShutDownTracker.isShutdownHookRunning()) {
                DocumentListener[] listeners = getListeners();
                for (DocumentListener listener : listeners) {
                    try {
                        listener.documentChanged(event);
                    } catch (Throwable e) {
                        exceptions.register(e);
                    }
                }
            }
        } finally {
            myEventsHandling = false;
        }
    }

    @NonNull
    @Override
    public String getText() {
        return ReadAction.compute(this::doGetText);
    }

    @NonNull
    private String doGetText() {
        String s = SoftReference.dereference(myTextString);
        if (s == null) {
            myTextString = new SoftReference<>(s = myText.toString());
        }
        return s;
    }

    @NonNull
    @Override
    public String getText(@NonNull final TextRange range) {
        return ReadAction.compute(
                () -> myText.subSequence(range.getStartOffset(), range.getEndOffset()).toString());
    }

    @Override
    public int getTextLength() {
        return myText.length();
    }

    @Override
    @NonNull
    public CharSequence getCharsSequence() {
        return myMutableCharSequence;
    }

    @NonNull
    @Override
    public CharSequence getImmutableCharSequence() {
        return myText;
    }

    @Override
    public void addDocumentListener(@NonNull DocumentListener listener) {
        if (ArrayUtil.contains(listener, getListeners())) {
            LOG.error("Already registered: " + listener);
        }
        myDocumentListeners.add(listener);
    }

    @Override
    public void addDocumentListener(
            @NonNull final DocumentListener listener, @NonNull Disposable parentDisposable) {
        addDocumentListener(listener);
        Disposer.register(
                parentDisposable, new DocumentListenerDisposable(myDocumentListeners, listener));
    }

    // this contortion is for avoiding document leak when the listener is leaked
    private static class DocumentListenerDisposable implements Disposable {
        @NonNull private final LockFreeCOWSortedArray<? super DocumentListener> myList;
        @NonNull private final DocumentListener myListener;

        DocumentListenerDisposable(
                @NonNull LockFreeCOWSortedArray<? super DocumentListener> list,
                @NonNull DocumentListener listener) {
            myList = list;
            myListener = listener;
        }

        @Override
        public void dispose() {
            myList.remove(myListener);
        }
    }

    @Override
    public void removeDocumentListener(@NonNull DocumentListener listener) {
        boolean success = myDocumentListeners.remove(listener);
        if (!success) {
            LOG.error(
                    "Can't remove document listener ("
                            + listener
                            + "). Registered listeners: "
                            + Arrays.toString(getListeners()));
        }
    }

    @Override
    public int getLineNumber(final int offset) {
        return getLineSet().findLineIndex(offset);
    }

    //  @Override
    //  @NonNull
    //  public LineIterator createLineIterator() {
    //    return getLineSet().createIterator();
    //  }

    @Override
    public int getLineStartOffset(final int line) {
        if (line == 0) {
            return 0; // otherwise it crashed for zero-length document
        }
        return getLineSet().getLineStart(line);
    }

    @Override
    public int getLineEndOffset(int line) {
        if (getTextLength() == 0 && line == 0) {
            return 0;
        }
        int result = getLineSet().getLineEnd(line) - getLineSeparatorLength(line);
        assert result >= 0;
        return result;
    }

    @Override
    public int getLineSeparatorLength(int line) {
        int separatorLength = getLineSet().getSeparatorLength(line);
        assert separatorLength >= 0;
        return separatorLength;
    }

    @Override
    public int getLineCount() {
        int lineCount = getLineSet().getLineCount();
        assert lineCount >= 0;
        return lineCount;
    }

    private DocumentListener[] getListeners() {
        return myDocumentListeners.getArray();
    }

    @Override
    public void fireReadOnlyModificationAttempt() {
        //    for (EditReadOnlyListener listener : myReadOnlyListeners) {
        //      listener.readOnlyModificationAttempt(this);
        //    }
    }

    //  @Override
    //  public void addEditReadOnlyListener(@NonNull EditReadOnlyListener listener) {
    //    myReadOnlyListeners.add(listener);
    //  }
    //
    //  @Override
    //  public void removeEditReadOnlyListener(@NonNull EditReadOnlyListener listener) {
    //    myReadOnlyListeners.remove(listener);
    //  }

    @Override
    public void addPropertyChangeListener(@NonNull PropertyChangeListener listener) {
        myPropertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(@NonNull PropertyChangeListener listener) {
        myPropertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void setCyclicBufferSize(int bufferSize) {
        assert bufferSize >= 0 : bufferSize;
        myBufferSize = bufferSize;
    }

    @Override
    public void setText(@NonNull final CharSequence text) {
        Runnable runnable =
                () ->
                        replaceString(
                                0, getTextLength(), 0, text, LocalTimeCounter.currentTime(), true);
        if (CommandProcessor.getInstance().isUndoTransparentActionInProgress()
                || !myAssertThreading) {
            runnable.run();
        } else {
            CommandProcessor.getInstance()
                    .executeCommand(null, runnable, "", DocCommandGroupId.noneGroupId(this));
        }

        clearLineModificationFlags();
    }

    @Override
    public boolean isInBulkUpdate() {
        return myDoingBulkUpdate;
    }

    @Override
    public void setInBulkUpdate(boolean value) {
        if (myAssertThreading) {
            ApplicationManager.getApplication().assertIsWriteThread();
        }
        if (myUpdatingBulkModeStatus) {
            throw new IllegalStateException(
                    "Detected bulk mode status update from DocumentBulkUpdateListener");
        }
        if (myDoingBulkUpdate == value) {
            return;
        }
        myUpdatingBulkModeStatus = true;
        try {
            if (value) {
                //        getPublisher().updateStarted(this);
                notifyListenersOnBulkModeStarting();
                myBulkUpdateEnteringTrace = new Throwable();
                myDoingBulkUpdate = true;
            } else {
                myDoingBulkUpdate = false;
                myBulkUpdateEnteringTrace = null;
                notifyListenersOnBulkModeFinished();
                //        getPublisher().updateFinished(this);
            }
        } finally {
            myUpdatingBulkModeStatus = false;
        }
    }

    private void notifyListenersOnBulkModeStarting() {
        DelayedExceptions exceptions = new DelayedExceptions();
        DocumentListener[] listeners = getListeners();
        for (int i = listeners.length - 1; i >= 0; i--) {
            try {
                //        listeners[i].bulkUpdateStarting(this);
            } catch (Throwable e) {
                exceptions.register(e);
            }
        }
        exceptions.rethrowPCE();
    }

    private void notifyListenersOnBulkModeFinished() {
        DelayedExceptions exceptions = new DelayedExceptions();
        DocumentListener[] listeners = getListeners();
        for (DocumentListener listener : listeners) {
            try {
                //        listener.bulkUpdateFinished(this);
            } catch (Throwable e) {
                exceptions.register(e);
            }
        }
        exceptions.rethrowPCE();
    }

    //  private static class DocumentBulkUpdateListenerHolder {
    //    private static final DocumentBulkUpdateListener ourBulkChangePublisher =
    //
    // ApplicationManager.getApplication().getMessageBus().syncPublisher(DocumentBulkUpdateListener.TOPIC);
    //  }
    //
    //  @NonNull
    //  private static DocumentBulkUpdateListener getPublisher() {
    //    return DocumentBulkUpdateListenerHolder.ourBulkChangePublisher;
    //  }

    //  @Override
    public boolean processRangeMarkers(@NonNull Processor<? super RangeMarker> processor) {
        return processRangeMarkersOverlappingWith(0, getTextLength(), processor);
    }

    //  @Override
    public boolean processRangeMarkersOverlappingWith(
            int start, int end, @NonNull Processor<? super RangeMarker> processor) {
        //    TextRange interval = new ProperTextRange(start, end);
        //    MarkupIterator<RangeMarkerEx> iterator = IntervalTreeImpl
        //      .mergingOverlappingIterator(myRangeMarkers, interval, myPersistentRangeMarkers,
        // interval, RangeMarker.BY_START_OFFSET);
        //    try {
        //      return ContainerUtil.process(iterator, processor);
        //    }
        //    finally {
        //      iterator.dispose();
        //    }
        throw new UnsupportedOperationException();
    }

    @NonNull
    public String dumpState() {
        StringBuilder result = new StringBuilder();
        result.append("intervals:\n");
        int lineCount = getLineCount();
        for (int line = 0; line < lineCount; line++) {
            result.append(line)
                    .append(": ")
                    .append(getLineStartOffset(line))
                    .append("-")
                    .append(getLineEndOffset(line))
                    .append(", ");
        }
        if (lineCount > 0) {
            result.setLength(result.length() - 2);
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return "DocumentImpl["
                + FileDocumentManager.getInstance().getFile(this)
                + (isInEventsHandling() ? ",inEventHandling" : "")
                + "]";
    }

    @NonNull
    public FrozenDocument freeze() {
        FrozenDocument frozen = myFrozen;
        if (frozen == null) {
            synchronized (myLineSetLock) {
                frozen = myFrozen;
                if (frozen == null) {
                    myFrozen =
                            frozen =
                                    new FrozenDocument(
                                            myText,
                                            myLineSet,
                                            myModificationStamp,
                                            SoftReference.dereference(myTextString));
                }
            }
        }
        return frozen;
    }

    public void assertNotInBulkUpdate() {
        if (myDoingBulkUpdate) {
            throw new UnexpectedBulkUpdateStateException(myBulkUpdateEnteringTrace);
        }
    }

    private static final class UnexpectedBulkUpdateStateException extends RuntimeException
            implements ExceptionWithAttachments {
        private final Attachment[] myAttachments;

        private UnexpectedBulkUpdateStateException(Throwable enteringTrace) {
            super(
                    "Current operation is not permitted in bulk mode, see Document.setInBulkUpdate"
                            + " javadoc");
            myAttachments =
                    enteringTrace == null
                            ? Attachment.EMPTY_ARRAY
                            : new Attachment[] {new Attachment("enteringTrace.txt", enteringTrace)};
        }

        @Override
        public Attachment[] getAttachments() {
            return myAttachments;
        }
    }
}
