package org.cosmic.ide.compiler;

import android.os.Looper;
import android.util.Log;

import org.cosmic.ide.ApplicationLoader;
import org.cosmic.ide.MainActivity;
import org.cosmic.ide.R;
import org.cosmic.ide.android.exception.CompilationFailedException;
import org.cosmic.ide.android.task.JavaBuilder;
import org.cosmic.ide.android.task.dex.D8Task;
import org.cosmic.ide.android.task.exec.ExecuteDexTask;
import org.cosmic.ide.android.task.java.*;
import org.cosmic.ide.android.task.kotlin.KotlinCompiler;
import org.cosmic.ide.common.util.FileUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class CompileTask extends Thread {

    private long d8Time = 0;
    private long ecjTime = 0;

    private boolean showExecuteDialog = false;

    private final MainActivity activity;

    private final CompilerListeners listener;
    private final JavaBuilder builder;

    private final String STAGE_CLEAN;
    private final String STAGE_KOTLINC;
    private final String STAGE_JAVAC;
    private final String STAGE_ECJ;
    private final String STAGE_D8;
    private final String STAGE_LOADING_DEX;

    public CompileTask(MainActivity context, boolean isExecuteMethod, CompilerListeners listener) {
        this.activity = context;
        this.listener = listener;
        this.showExecuteDialog = isExecuteMethod;
        this.builder = new JavaBuilder(activity);

        STAGE_CLEAN = context.getString(R.string.stage_clean);
        STAGE_KOTLINC = context.getString(R.string.stage_kotlinc);
        STAGE_JAVAC = context.getString(R.string.stage_javac);
        STAGE_ECJ = context.getString(R.string.stage_ecj);
        STAGE_D8 = context.getString(R.string.stage_d8);
        STAGE_LOADING_DEX = context.getString(R.string.stage_loading_dex);
    }

    @Override
    public void run() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        try {
            listener.onCurrentBuildStageChanged(STAGE_CLEAN);
            final String code =
                    activity.binding
                            .editor
                            .getText()
                            .toString()
                            .replace("System.exit(", "System.out.println(\"Exit code \" + ");
            final String currentPath = activity.currentWorkingFilePath;
            if (code != FileUtil.readFile(new File(currentPath))) {
                FileUtil.writeFile(currentPath, code);
            }
        } catch (Exception e) {
            listener.onFailed(e.getMessage());
        }

        var time = System.currentTimeMillis();
        // Compile Kotlin files
        compileKotlin();

        // Compile Java Files
        compileJava();

        ecjTime = System.currentTimeMillis() - time;
        time = System.currentTimeMillis();

        // Run D8
        compileDex();
        d8Time = System.currentTimeMillis() - time;

        listener.onSuccess();

        // Executes the class by loading the dex file
        executeDex();
    }

    private void compileKotlin() {
        listener.onCurrentBuildStageChanged(STAGE_KOTLINC);
        try {
            new KotlinCompiler().doFullTask(activity.getProject());
        } catch (CompilationFailedException e) {
            listener.onFailed(e.getMessage());
            return;
        } catch (Throwable e) {
            listener.onFailed(Log.getStackTraceString(e));
            return;
        }
    }

    private void compileJava() {
        final var prefs = ApplicationLoader.getDefaultSharedPreferences();
        try {
            if (prefs.getString("key_java_compiler", activity.getString(R.string.javac))
                    .equals(activity.getString(R.string.javac))) {
                listener.onCurrentBuildStageChanged(STAGE_JAVAC);
                var javaTask = new JavacCompilationTask(prefs);
                javaTask.doFullTask(activity.getProject());
            } else {
                listener.onCurrentBuildStageChanged(STAGE_ECJ);
                var javaTask = new ECJCompilationTask(prefs);
                javaTask.doFullTask(activity.getProject());
            }
        } catch (CompilationFailedException e) {
            listener.onFailed(e.getMessage());
            return;
        } catch (Throwable e) {
            listener.onFailed(Log.getStackTraceString(e));
            return;
        }
    }

    private void compileDex() {
        listener.onCurrentBuildStageChanged(STAGE_D8);
        try {
            new D8Task().doFullTask(activity.getProject());
        } catch (Exception e) {
            listener.onFailed(e.getMessage());
            return;
        }
    }

    private void executeDex() {
        try {
            listener.onCurrentBuildStageChanged(STAGE_LOADING_DEX);
            final var classes = activity.getClassesFromDex();
            if (classes == null) {
                return;
            }
            if (showExecuteDialog) {
                activity.listDialog(
                        "Select a class to execute",
                        classes,
                        (dialog, item) -> {
                            var task = new ExecuteDexTask(ApplicationLoader.getDefaultSharedPreferences(), classes[item]);
                            try {
                                task.doFullTask(activity.getProject());
                            } catch (InvocationTargetException e) {
                                activity.dialog(
                                        "Failed...",
                                        "Runtime error: "
                                                + e.getMessage()
                                                + "\n\nSystem logs:\n"
                                                + task.getLogs(),
                                        true);
                                return;
                            } catch (Exception e) {
                                activity.dialog(
                                        "Failed...",
                                        "Couldn't execute the dex: "
                                                + e.toString()
                                                + "\n\nSystem logs:\n"
                                                + task.getLogs()
                                                + "\n"
                                                + Log.getStackTraceString(e),
                                        true);
                                return;
                            }
                            var s = new StringBuilder();

                            s.append("Compiling took: ");
                            s.append(String.valueOf(ecjTime));
                            s.append("ms, ");
                            s.append("D8");
                            s.append(" took: ");
                            s.append(String.valueOf(d8Time));
                            s.append("ms");

                            activity.dialog(s.toString(), task.getLogs(), true);
                        });
            }
        } catch (Throwable e) {
            listener.onFailed(e.getMessage());
        }
    }

    public static interface CompilerListeners {
        public void onCurrentBuildStageChanged(String stage);

        public void onSuccess();

        public void onFailed(String errorMessage);
    }
}
