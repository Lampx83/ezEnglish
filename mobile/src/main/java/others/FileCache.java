package others;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileCache {

    private File cacheDir;

    public FileCache(Context context) {
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            // cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
            cacheDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        File f = null;
        if (url.startsWith("http")) {
            String filename = String.valueOf(url.hashCode());
            f = new File(cacheDir, filename);
        } else
            f = new File(cacheDir, url);
        return f;

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

}