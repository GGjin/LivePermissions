package com.gg.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileUtil {
    static final String FILES_PATH = "CompressTools";
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    public static final String PHOTO_PATH = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator +
            File.separator + "Cherry"
            + File.separator;

    /**
     * 重命名文件
     *
     * @param file    文件
     * @param newName 新名字
     * @return 新文件
     */
    public static File renameFile(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists()) {
                if (newFile.delete()) {
                    Log.d("FileUtil", "Delete old " + newName + " file");
                }
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile;
    }

    /**
     * 获取临时文件
     *
     * @param context 上下文
     * @param uri     url
     * @return 临时文件
     * @throws IOException
     */
    public static File getTempFile(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = renameFile(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }

    /**
     * 截取文件名称
     *
     * @param fileName 文件名称
     */
    static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    /**
     * 获取文件名称
     *
     * @param context 上下文
     * @param uri     uri
     * @return 文件名称
     */
    static String getFileName(Context context, Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * 获取真实的路径
     *
     * @param context 上下文
     * @param uri     uri
     * @return 文件路径
     */
    static String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String realPath = cursor.getString(index);
            cursor.close();
            return realPath;
        }
    }

    static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    static long copyLarge(InputStream input, OutputStream output) throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }

    static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 创建图片存储路径
     */
    public static File getPhotoFileDir() {
        File fileDir = null;
        if (checkSDCardAvaiable()) {
            fileDir = new File(FileUtil.PHOTO_PATH);
            if (fileDir.exists()) {
                return fileDir;
            } else {
                fileDir.mkdirs();
            }
        }
        return fileDir;
    }

    /**
     * 检测sd卡是否可用
     *
     * @return
     */
    private static boolean checkSDCardAvaiable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    private static ExecutorService sExecutorService = Executors.newSingleThreadExecutor();

    public static void runOnUiThread(Runnable r) {

        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            sHandler.post(r);
        }

    }

    public static void runOnSubThread(Runnable r) {
        sExecutorService.submit(r);
    }


    public static String getImageFilePath() {
        String path = getImageFileDir();
        if (isFolderExists(path)) {
            path += "/" + System.currentTimeMillis() + ".jpg";
        }
        return path;
    }

    public static boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    public static String getImageFileDir() {
        String path = filePathForStorageSDCard();
        path += "image/";
        File file = new File(path);
        file.mkdirs();
        try {
            String oldPath = filePathForStorageSDCard() + "img/";
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {
                createNomediaFile(oldPath);
            }
        } catch (Exception e) {
        }
        return path;
    }

    private static void createNomediaFile(String dirPath) {
        try {
            String nomediaPath = dirPath + ".nomedia";
            File file = new File(nomediaPath);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            Log.e("exception", e.getMessage());
        }
    }

    public static String filePathForStorageSDCard() {
        String path = null;

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            path = Environment.getExternalStorageDirectory().getPath()
                    + "/Duckr/";
            try {
                String nomediaPath = path + ".nomedia";
                File file = new File(nomediaPath);
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                Log.e("exception", e.getMessage());
            }
        }
        return path;
    }

    public static boolean copy(File from, File to) {
        try {
            File temFile = new File(to.getPath() + ".tmp");
            temFile.createNewFile();
            FileInputStream fileInputStream = new FileInputStream(from);
            FileOutputStream fileOutputStream = new FileOutputStream(temFile);
            int count;
            byte[] buffer = new byte[1024];
            while ((count = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, count);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            fileInputStream.close();
            temFile.renameTo(to);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getTempFilePathWithSpecifiedName(Context context, String name) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(FileUtil.getTempDirectoryPath(context));
        buffer.append("/");
        buffer.append(name);
        return buffer.toString();
    }

    public static String getTempDirectoryPath(Context context) {
        File cache;
        // SD Card Mounted
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(Environment.getExternalStorageDirectory().getAbsolutePath()).append(File.separator).append("QiYuan");
            cache = new File(buffer.toString());
        } else {
            // Use internal storage
            cache = context.getCacheDir();
        }

        cache.mkdirs();
        return cache.getAbsolutePath();
    }

    public static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1, url.length());
    }

    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    public static void noticeGallery(Context context, String filePath, String name) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath, name, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
    }
}
