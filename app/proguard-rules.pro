-allowaccessmodification

-keep class javax.** { *; }
-keep class com.intellij.** { *; }
-keep class org.jetbrains.kotlin.** { *; }
-keep class org.xml.sax.** { *; }
-keep class com.sun.xml.internal.stream.** { *; }
-keep class com.sun.org.apache.** { *; }
-keep class org.w3c.dom.** { *; }
-keep class jdk.xml.internal.** { *; }
-keepnames class com.google.googlejavaformat.** { *; }
-keep,allowshrinking class com.sun.tools.classfile.** { *; }

-obfuscationdictionary dictionary.txt
-packageobfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt
