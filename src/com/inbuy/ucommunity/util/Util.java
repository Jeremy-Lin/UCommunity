
package com.inbuy.ucommunity.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Util {

    private static final String TAG = "Util";

    private static String DATA_PATH;

    private final static String DATA_DOWNLOAD_DIR = "/inbuy/ucommunity/";

    private static final String DATA_PHOTO_PATH = "photo/";

    private static final String DATA_TMP_PATH = "tmp/";

    public static final String WINDATA_DOWNLOAD_SUFFIX = ".dw";

    public static final String WINDATA_UPLOAD_SUFFIX = ".up";

    public static void setWinDataPath(Application app) {

        if (app == null) {
            return;
        }

        String dataPath = null;
        File dataDir = Environment.getExternalStorageDirectory();
        if (dataDir != null && dataDir.exists()) {
            dataPath = dataDir.getAbsolutePath();
        }

        DATA_PATH = dataPath + DATA_DOWNLOAD_DIR;
        File homeDir = new File(DATA_PATH);
        if (!homeDir.exists()) {
            homeDir.mkdirs();
        }

        if (!homeDir.exists()) {

            dataDir = app.getFilesDir();// Environment.getDataDirectory();
            if (dataDir != null && dataDir.exists()) {
                dataPath = dataDir.getAbsolutePath();
            }

            DATA_PATH = dataPath + '/';
            homeDir = new File(DATA_PATH);
            if (!homeDir.exists()) {
                homeDir.mkdirs();
            }
        }

        if (!homeDir.exists()) {
            dataPath = null;
        }

        if (dataPath == null || dataPath.length() <= 0) {
            Log.d(TAG, "invalide local path for save download document");
            return;
        }

        File album = new File(getDataPhotoPath());
        if (!album.exists()) {
            album.mkdirs();
        }
    }

    public static String getDataPhotoPath() {
        return DATA_PATH + DATA_PHOTO_PATH;

    }

    public static String getUserPhotoFilePath(String userId) {
        String filePath = "user_" + userId + ".jpg";
        return Util.getDataPhotoPath() + filePath;
    }

    /**
     * @param userId
     * @return
     */
    public static String getUserPhotoDownloadPath(String userId) {
        return getUserPhotoFilePath(userId) + WINDATA_DOWNLOAD_SUFFIX;
    }

    /**
     * @param context
     * @param userId
     * @return
     */
    public static Drawable getUserPhotoDrawable(Context context, String userId, int defaultResId) {
        File file = new File(getUserPhotoDownloadPath(userId));
        if ((file != null) && file.exists()) {
            Log.d("Util", "getUserPhotoDrawable: download failed for " + userId);

            if (defaultResId > 0) {
                return context.getResources().getDrawable(defaultResId);
            } else {
                return null;
            }
        }

        String filePath = getUserPhotoFilePath(userId);
        return getDownloadDrawable(context, filePath);
    }

    /**
     * @param context
     * @param filePath
     * @return
     */
    public static Drawable getDownloadDrawable(Context context, String filePath) {
        Bitmap bitmap = getBitmapByPath(filePath);
        if (bitmap != null) {
            try {
                return new BitmapDrawable(context.getResources(), bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static int IMAGE_BLOCK_SIZE = 512 * 1024;

    private static int IMAGE_SAMPLE_FACTOR = 4;

    /**
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapByPath(String filePath) {
        File file = new File(filePath);
        if (file.exists() && (file.length() > 0)) {
            final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inSampleSize = (int) file.length() / IMAGE_BLOCK_SIZE
                    * IMAGE_SAMPLE_FACTOR;
            return BitmapFactory.decodeFile(filePath, decodeOptions);
        } else {
            return null;
        }
    }

    /**
     * create a new FileOutputStream for download pictures, audio, and other
     * documents
     * 
     * @param filePath
     * @return: FileOutputStream to download the media file
     */
    public static FileOutputStream createFileOutputStream(String filePath) {
        try {
            return new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param outStream
     */
    public static void closeFileOutputStream(FileOutputStream outStream) {
        if (outStream != null) {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * call this after user image download is completed, if the image file is
     * not a valid image the file will be deleted.
     * 
     * @param filePath
     * @return
     */
    public static boolean checkDownloadImage(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.length() > 0) {
                final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath, decodeOptions);
                if (bitmap == null) {
                    file.delete();
                } else {
                    bitmap.recycle();
                    return true;
                }
            } else {
                file.delete();
            }
        }

        return false;
    }

    /**
     * call this after user photo download is completed, if the photo file is
     * not a valid image the file will be deleted.
     * 
     * @param photo
     * @return
     */
    public static boolean checkUserDownloadPhoto(String winUserId) {
        String filePath = getUserPhotoDownloadPath(winUserId);
        return checkDownloadImage(filePath);
    }

    public static void renameDownloadFile(String downloadFilePath) {
        File file = new File(downloadFilePath);
        if (file != null) {
            int length = downloadFilePath.length() - WINDATA_DOWNLOAD_SUFFIX.length();
            String newPath = downloadFilePath.substring(0, length);
            File newfile = new File(newPath);
            if (newfile != null) {
                file.renameTo(newfile);
            }
        }
    }

    /**
     * delete all files in a folder, if endwith is not empty, only remove the
     * files end with 'endwith'
     * 
     * @param folder
     * @param endwith
     */
    public static void deleteAllFiles(String folder, String endwith) {
        File fileList = new File(folder);

        if ((fileList != null) && (fileList.exists())) {
            File[] files = fileList.listFiles();
            boolean filter = (endwith != null) && (endwith.length() > 0);

            for (File tmpfile : files) {
                if (filter) {
                    if (tmpfile.getName().endsWith(endwith)) {
                        tmpfile.delete();
                    }
                } else {
                    tmpfile.delete();
                }
            }
        }
    }

    public static String clearStrings(String strs) {
        if (strs != null) {
            strs = strs.replace("&nbsp;", "");
            strs = strs.replace("\n\r", "");
        }

        return strs;
    }

}
