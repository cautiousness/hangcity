package com.fuj.hangcity.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by fuj
 */
public class CacheManager {
    private static CacheManager mInstance = null;
    private ExecutorService mExecutor = null;
    private MemoryCache mMemoryCache;
    private DiskCache mDiskCache;
    private String cacheDirName = "cache";

    public static CacheManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (CacheManager.class) {
                if (mInstance == null) {
                    mInstance = new CacheManager();
                    mInstance.init(context);
                }
            }
        }
        return mInstance;
    }

    private void init(Context context) {
        mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        mDiskCache = new DiskCache(context, cacheDirName);
        mMemoryCache = new MemoryCache(new VauleChangedListener() {
            @Override
            public void reput(String key, String value) {
                mDiskCache.put(key, value);
            }
        });
    }

    /**
     * 从缓存中读取value
     */
    public String get(final String key) {
        Future<String> ret = mExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String result = mMemoryCache.get(key);
                if (result == null) {
                    result = mDiskCache.get(key);
                }
                return result;
            }
        });
        try {
            return ret.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将value 写入到缓存中
     */
    public void put(final String key, final String value) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mMemoryCache.put(key, value);
                mDiskCache.put(key,value);
            }
        });
    }

    public int getFileSize() {
        return mDiskCache.size();
    }

    public void clearCache() {
        mDiskCache.deleteFile(cacheDirName);
    }

    static class DiskCache implements Cache{
        private DiskLruCache mDiskLruCache = null;
        private Context mContext;

        public DiskCache(Context context, String cacheDirName){
            try {
                mContext = context;
                File cacheDir = getDiskCacheDir(context, cacheDirName);
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 500 * 1024 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String get(String key) {
            String result;
            try {
                DiskLruCache.Snapshot snapShot = mDiskLruCache.get(hashKeyForDisk(key));
                if (snapShot != null) {
                    result = snapShot.getString(0);
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void put(String key, String value) {
            DiskLruCache.Editor editor;
            try {
                editor = mDiskLruCache.edit(hashKeyForDisk(key));
                if (editor != null) {
                    editor.set(0, value);
                    editor.commit();
                }
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean remove(String key) {
            try {
                return mDiskLruCache.remove(hashKeyForDisk(key));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        public Bitmap getImageCache(String key){
            Bitmap bitmap = null;
            try {
                DiskLruCache.Snapshot snapShot = mDiskLruCache.get(hashKeyForDisk(key));
                if (snapShot != null) {
                    InputStream is = snapShot.getInputStream(0);
                    bitmap = BitmapFactory.decodeStream(is);
                    return bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void putImageCache(final String key){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DiskLruCache.Editor editor = mDiskLruCache.edit(hashKeyForDisk(key));
                        if (editor != null) {
                            OutputStream outputStream = editor.newOutputStream(0);
                            if (downloadUrlToStream(key, outputStream)) {
                                editor.commit();
                            } else {
                                editor.abort();
                            }
                        }
                        mDiskLruCache.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
            HttpURLConnection urlConnection = null;
            BufferedOutputStream out = null;
            BufferedInputStream in = null;
            try {
                final URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
                out = new BufferedOutputStream(outputStream, 8 * 1024);
                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
                return true;
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                try {
                    if(in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if(out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        public String hashKeyForDisk(String key) {
            String cacheKey;
            try {
                final MessageDigest mDigest = MessageDigest.getInstance("MD5");
                mDigest.update(key.getBytes());
                cacheKey = bytesToHexString(mDigest.digest());
            } catch (NoSuchAlgorithmException e) {
                cacheKey = String.valueOf(key.hashCode());
            }
            return cacheKey;
        }

        private String bytesToHexString(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (byte n : bytes) {
                String hex = Integer.toHexString(0xFF & n);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        }

        public File getDiskCacheDir(Context context, String uniqueName) {
            String cachePath;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                cachePath = Environment.getExternalStorageDirectory().getPath();
            } else {
                cachePath = context.getCacheDir().getPath();
            }
            return new File(cachePath + File.separator + context.getPackageName() + File.separator + uniqueName);
        }

        public int getAppVersion(Context context) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                return info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return 1;
        }

        public void deleteFile(String dirName){
            try {
                DiskLruCache.deleteContents(getDiskCacheDir(mContext,dirName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public int size(){
            int size = 0;
            if(mDiskLruCache != null){
                size = (int) mDiskLruCache.size();
            }
            return size;
        }
    }

    class MemoryCache implements Cache {
        private LruCache<String, String> mMemoryLruCache;
        private VauleChangedListener mVauleChangedListener;

        public MemoryCache(VauleChangedListener listener) {
            init();
            this.mVauleChangedListener = listener;
        }

        private void init() {
            mMemoryLruCache = new LruCache<String, String>((int)(Runtime.getRuntime().freeMemory() / 1024) / 3) {
                @Override
                protected int sizeOf(String key, String value) {
                    return value.getBytes().length;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key, String oldValue, String newValue) {
                    if (evicted && mVauleChangedListener != null) {
                        mVauleChangedListener.reput(key, newValue);
                    }
                }
            };
        }

        @Override
        public String get(String key) {
            return mMemoryLruCache.get(key);
        }

        @Override
        public void put(String key, String value) {
            mMemoryLruCache.put(key, value);
        }

        @Override
        public boolean remove(String key) {
            return Boolean.parseBoolean(mMemoryLruCache.remove(key));
        }
    }

    interface Cache {
        String get(final String key);

        void put(final String key, final String value);

        boolean remove(final String key);
    }

    interface VauleChangedListener {

        void reput(String key, String value);
    }
}
