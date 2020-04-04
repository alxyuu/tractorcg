package tractor.client;

import java.lang.reflect.Field;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarRunner {
	public static void main(String ... bobby) {
	  String os = System.getProperty("os.name");

    try {
      if (os.startsWith("Windows")) {
        copyNativeLibraries("windows");
      } else if (os.startsWith("Mac")) {
        copyNativeLibraries("macosx");
      } else if (os.indexOf("nux") >= 0) {
        copyNativeLibraries("linux");
      } else {
        System.out.println("This OS is not supported: " + os);
        System.exit(1);
      }

      new Client();
    } catch (IOException e) {
      e.printStackTrace(System.out);
    }
	}

  public static List<File> extractZipResource(Class myClass, String zipResource, Path destDir)
  {
    if (myClass == null || zipResource == null || !zipResource.toLowerCase().endsWith(".zip") || !Files.isDirectory(destDir))
    {
      throw new IllegalArgumentException("myClass=" + myClass + " zipResource=" + zipResource + " destDir=" + destDir);
    }

    ArrayList<File> res = new ArrayList<>();

    try (InputStream is = myClass.getResourceAsStream(zipResource);
         BufferedInputStream bis = new BufferedInputStream(is);
         ZipInputStream zis = new ZipInputStream(bis))
    {
      ZipEntry entry;
      byte[] buffer = new byte[2048];
      while ((entry = zis.getNextEntry()) != null)
      {
        // Build destination file
        File destFile = destDir.resolve(entry.getName()).toFile();

        if (entry.isDirectory())
        {
          // Directory, recreate if not present
          if (!destFile.exists() && !destFile.mkdirs())
          {
            System.out.println("extractZipResource() can't create destination folder : " + destFile.getAbsolutePath());
          }
          continue;
        }
        // Plain file, copy it
        try (FileOutputStream fos = new FileOutputStream(destFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length))
        {
          int len;
          while ((len = zis.read(buffer)) > 0)
          {
            bos.write(buffer, 0, len);
          }
        }
        res.add(destFile);
      }
    } catch (IOException ex)
    {
      System.out.println("extractZipResource() problem extracting resource for myClass=" + myClass + " zipResource=" + zipResource);
      ex.printStackTrace(System.out);
    }
    return res;
  }

  public static void copyNativeLibraries(String os) throws IOException {
    Path tmp = Files.createTempDirectory("tractorcg");
    extractZipResource(JarRunner.class, os+".zip", tmp);
    addDir(tmp.toString());
  }

  public static void addDir(String s) throws IOException {
    try {
        // This enables the java.library.path to be modified at runtime
        // From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
        //
        Field field = ClassLoader.class.getDeclaredField("usr_paths");
        field.setAccessible(true);
        String[] paths = (String[])field.get(null);
        for (int i = 0; i < paths.length; i++) {
            if (s.equals(paths[i])) {
                return;
            }
        }
        String[] tmp = new String[paths.length+1];
        System.arraycopy(paths,0,tmp,0,paths.length);
        tmp[paths.length] = s;
        field.set(null,tmp);
        System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s);
    } catch (IllegalAccessException e) {
        throw new IOException("Failed to get permissions to set library path");
    } catch (NoSuchFieldException e) {
        throw new IOException("Failed to get field handle to set library path");
    }
  }
}
