package com.fuj.hangcity.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.fuj.hangcity.tools.Constant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fuj
 */
public class LogUtils implements UncaughtExceptionHandler {
	private static String log;
    private static boolean LOGFLAG = true;
    private static int LOGLEVEL = Log.DEBUG;
    private static final String TAG = Constant.TAG;
    private static final String FILENAME = "log";
    private static String DIRNAME = Constant.LOG_DIR;
    private static LogUtils INSTANCE;
	
    private Context mContext;
    private Map<String, String> info = new HashMap<>();
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public static LogUtils getInstance() {
        if(INSTANCE == null) {
            synchronized (LogUtils.class) {
                if(INSTANCE == null) {
                    INSTANCE = new LogUtils();
                }
            }
        }
        return INSTANCE;
    }

    public LogUtils() {}

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

	public static void v(String msg) {
		v(msg == null ? "" : msg, null);
	}

	public static void v(String msg, Throwable thr) {
		if(LOGFLAG) {
			if(LOGLEVEL <= Log.VERBOSE) {
				android.util.Log.v(TAG, buildMessage(msg), thr);
				addRecordToLog(log);
			}
		}
	}

	public static void d(String msg) {
		d(msg == null ? "" : msg, null);
	}

	public static void d(String msg, Throwable thr) {
		if(LOGFLAG) {
			if(LOGLEVEL <= Log.DEBUG) {
				android.util.Log.d(TAG, buildMessage(msg), thr);
				addRecordToLog(log);
			}
		}
	}

	public static void i(String msg) {
		i(msg == null ? "" : msg, null);
	}

	public static void i(String msg, Throwable thr) {
		if(LOGFLAG) {
			if(LOGLEVEL <= Log.INFO) {
				android.util.Log.i(TAG, buildMessage(msg), thr);
				addRecordToLog(log);
			}
		}
	}

	public static void e(String msg) {
		e(msg == null ? "" : msg, null);
	}

	public static void e(String msg, Throwable thr) {
		if(LOGFLAG) {
			if(LOGLEVEL <= Log.ERROR) {
				android.util.Log.e(TAG, buildMessage(msg), thr);
				addRecordToLog(log);
			}
		}
	}

	public static void w(String msg) {
		w(msg == null ? "" : msg, null);
	}

	public static void w(Throwable thr) {
		w("", thr);
	}

	public static void w(String msg, Throwable thr) {
		if(LOGFLAG) {
			if(LOGLEVEL <= Log.WARN) {
				android.util.Log.w(TAG, buildMessage(msg), thr);
				addRecordToLog(log);
			}
		}
	}

	protected static String buildMessage(String msg) {
		StackTraceElement caller = new Throwable().fillInStackTrace()
		        .getStackTrace()[3];
		log = "[" + SimpleDateFormat.getTimeInstance(DateFormat.DEFAULT)
				.format(new Date(System.currentTimeMillis())) + "]"
				+ caller.getClassName() + "." + caller.getMethodName() + "(): " + msg;
		return log;
	}

    public static void addRecordToLog(String message) {
        File dir = new File(DIRNAME);
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if(!dir.exists()) {
                Log.d("Dir created ", ", result = " + dir.mkdirs());
            }
            File logFile = new File(DIRNAME + FILENAME + ".txt");
            
            if(logFile.length() > 1000000) {
                Log.d("File delete ", "result = " + logFile.delete());
            }
            
            if (!logFile.exists())  {
                try  {
                    Log.d("File created ", "result = " + logFile.createNewFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try { 
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.write(message + "\r\n");
                buf.newLine();
                buf.flush();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {  
            mDefaultHandler.uncaughtException(thread, ex);  
        } else {  
            try {  
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ActivityUtils.exit();
        }  
    }  
  
    public boolean handleException(Throwable ex) {
        if (ex == null)  
            return false;  
        new Thread() {
            public void run() {  
                Looper.prepare();
                Toast.makeText(mContext, "程序异常,即将退出", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }  
        }.start();  
        saveCrashToFile(ex);  
        return true;  
    }  
  
    private void saveCrashToFile(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\r\n");
        }  
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        while (cause != null) {  
            cause.printStackTrace(pw);  
            cause = cause.getCause();  
        }
        
        String result = writer.toString();
        sb.append(result);
        e(sb.toString());
        addRecordToLog(sb.toString());
        
        try {
             pw.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }  
}
