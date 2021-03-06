
package com.inbuy.ucommunity.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.inbuy.ucommunity.R;

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
            strs = strs.replace("&rdquo;", "");
            strs = strs.replace("&ldquo;", "");
            strs = strs.replace("&dash;", "");
        }

        return strs;
    }

    public static int getStarsResourceId(int stars) {
        int resId = R.drawable.ic_star_5;
        switch (stars) {
            case 0:
                resId = R.drawable.ic_star_0;
                break;
            case 1:
                resId = R.drawable.ic_star_1;
                break;
            case 2:
                resId = R.drawable.ic_star_2;
                break;
            case 3:
                resId = R.drawable.ic_star_3;
                break;
            case 4:
                resId = R.drawable.ic_star_4;
                break;
            case 5:
                resId = R.drawable.ic_star_5;
                break;
            default:
                break;
        }
        return resId;
    }

    public static Bitmap createStarsImageBitmap(Bitmap originalImage, int stars) {

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        int offset = 2;

        // Create a new bitmap.
        Bitmap bitmap = Bitmap.createBitmap((width + offset) * stars, height, Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        // Draw in the original image
        for (int i = 0; i < stars; i++) {
            canvas.drawBitmap(originalImage, 0 + (width + offset) * i, 0, null);
        }
        return bitmap;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable
                .getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * �ж�GPS�Ƿ���
     * 
     * @return
     */
    public static boolean isGPSEnabled(Context context) {
        LocationManager loctionManager;
        String contextService = Context.LOCATION_SERVICE;
        // ͨ��ϵͳ����ȡ��LocationManager����
        loctionManager = (LocationManager) context.getSystemService(contextService);
        if (loctionManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || loctionManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.i(TAG, "isGPSEnabled");
            return true;
        } else {
            return false;
        }
    }

    /**
     * �ж�Network�Ƿ���(�����ƶ������wifi)
     * 
     * @return
     */
    public static boolean isNetworkEnabled(Context context) {
        return (isWIFIEnabled(context) || isTelephonyEnabled(context));
    }

    /**
     * �ж��ƶ������Ƿ���
     * 
     * @return
     */
    public static boolean isTelephonyEnabled(Context context) {
        boolean enable = false;
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            if (telephonyManager.getNetworkType() != TelephonyManager.NETWORK_TYPE_UNKNOWN) {
                enable = true;
                Log.i(TAG, "isTelephonyEnabled");
            }
        }

        return enable;
    }

    /**
     * �ж�wifi�Ƿ���
     */
    public static boolean isWIFIEnabled(Context context) {
        boolean enable = false;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            enable = true;
            Log.i(TAG, "isWIFIEnabled");
        }
        return enable;
    }

    public static void custimizeSearchView(final SearchView searchView) {
        if (searchView == null) {
            return;
        }

        int searchPlateId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        // Getting the 'search_plate' LinearLayout.
        View searchPlate = searchView.findViewById(searchPlateId);
        // Setting background of 'search_plate' to earlier defined drawable.
        searchPlate.setBackgroundResource(R.drawable.action_search_bar_bg);

        int searchMagId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_mag_icon", null, null);
        ImageView searchMag = (ImageView) searchView.findViewById(searchMagId);
        searchMag.setImageResource(R.drawable.transparent_background);

        int closeBtnId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);

        View closeBtn = searchView.findViewById(closeBtnId);
        closeBtn.setBackgroundResource(R.drawable.transparent_background);
    }
}
