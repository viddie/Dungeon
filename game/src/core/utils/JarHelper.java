package core.utils;

import java.io.File;
import java.util.Optional;

public class JarHelper {

  private static Optional<Boolean> isJar = Optional.empty();
  private static File jarFile = null;

  public static boolean isRunningFromJar(){
    if(isJar.isEmpty()){
      checkJar();
    }
    return isJar.get();
  }

  public static File jarFile(){
    if(isJar.isEmpty()){
      checkJar();
    }
    return jarFile;
  }

  private static void checkJar(){
    Thread thread = Thread.currentThread();
    StackTraceElement[] stack = thread.getStackTrace();

    StackTraceElement currentElement = null;
    Class<?> clazz = null;
    for (int i = 1; i < stack.length; i++) {
      currentElement = stack[i];
      if (!currentElement.getClassName().equals(JarHelper.class.getName())) {
        try {
          clazz = ClassLoader.getSystemClassLoader().loadClass(currentElement.getClassName());
        } catch (ClassNotFoundException e) {
          System.err.println(
            "Could not load class " + currentElement.getClassName() + " from stacktrace.");
        }
        break;
      }
    }

    File file = new File(
        (clazz == null ? JarHelper.class : clazz)
          .getProtectionDomain()
          .getCodeSource()
          .getLocation()
          .getPath());

    System.out.println("clazz: "+file.getAbsolutePath()+" | name: "+clazz.getName());

    if(file.exists()){
      jarFile = file;
      isJar = Optional.of(true);
    } else {
      isJar = Optional.of(false);
    }
  }

}
