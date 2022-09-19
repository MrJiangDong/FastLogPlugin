package com.jyc.library.fast.log.printer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.jyc.library.fast.log.FastLogConfig;
import com.jyc.library.fast.log.bean.FastLogModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/// @author jyc
/// 创建日期：2021/4/26
/// 描述：FastFilePrinter
public class FastFilePrinter implements FastLogPrinter {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private final String logPath;
    private final long retentionTime;
    private LogWriter writer;
    private volatile PrintWorker worker;
    private static FastFilePrinter instance;

    /**
     * 创建FastFilePrinter
     *
     * @param logPath       log保存路径,如果是外部路径需要确保已经有外部存储的读写权限
     * @param retentionTime log文件的有效时长,单位毫秒,<=表示一直有效
     */
    public static synchronized FastFilePrinter getInstance(String logPath, long retentionTime) {
        if (instance == null) {
            instance = new FastFilePrinter(logPath, retentionTime);
        }
        return instance;
    }

    private FastFilePrinter(String logPath, long retentionTime) {
        this.logPath = logPath;
        this.retentionTime = retentionTime;
        this.writer = new LogWriter();
        this.worker = new PrintWorker();
        cleanExpiredLog();
    }

    @Override
    public void print(@NonNull FastLogConfig config, int level, String tag, @NonNull String printString) {
        long timeMillis = System.currentTimeMillis();
        if (!worker.isRunning()) {
            worker.start();
        }

        worker.put(new FastLogModel(timeMillis, level, tag, printString));
    }

    private void doPrint(FastLogModel logModel) {
        String lastFileName = writer.getPreFileName();
        if (lastFileName == null) {
            String newFileName = genFileName() + ".txt";

            if (writer.isReady()) {
                writer.close();
            }

            if (!writer.ready(newFileName)) {
                return;
            }
        }
        Log.d("===============", "准备写入成功");
        writer.append(logModel.flattenedLog());
    }

    private String genFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 清楚过期log
     */
    private void cleanExpiredLog() {
        if (retentionTime <= 0) {
            return;
        }

        long currentTimeMillis = System.currentTimeMillis();
        File logDir = new File(logPath);
        File[] files = logDir.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (currentTimeMillis - file.lastModified() > retentionTime) {
                boolean isFileDelete = file.delete();
            }
        }

    }

    private class PrintWorker implements Runnable {
        private BlockingQueue<FastLogModel> logs = new LinkedBlockingQueue<>();

        private volatile boolean running;

        /**
         * 将log放入打印队列
         *
         * @param log 要被打印的log
         */
        void put(FastLogModel log) {
            try {
                logs.put(log);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * 判断工作线程是否还在运行中
         *
         * @return true 在运行
         */
        boolean isRunning() {
            synchronized (this) {
                return running;
            }
        }

        /**
         * 启动工作线程
         */
        void start() {
            synchronized (this) {
                EXECUTOR.execute(this);
                running = true;
            }
        }

        @Override
        public void run() {
            FastLogModel log;
            try {
                while (true) {
                    log = logs.take();
                    doPrint(log);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                synchronized (this) {
                    running = false;
                }
            }
        }
    }

    /**
     * 基于BufferedWriter将log写入文件
     */
    private class LogWriter {

        private String preFileName;
        private File logFile;
        private BufferedWriter bufferedWriter;

        boolean isReady() {
            return bufferedWriter != null;
        }

        String getPreFileName() {
            return preFileName;
        }

        /**
         * log写入前的准备操作
         *
         * @param newFileName 要保持log的文件名
         * @return true 表示准备就绪
         */
        boolean ready(String newFileName) {
            Log.d("===============", "准备写入文件");

            preFileName = newFileName;
            logFile = new File(logPath, newFileName);

            //当log文件不存在时创建log文件
            if (!logFile.exists()) {
                try {
                    File parent = logFile.getParentFile();
                    if (parent != null && !parent.exists()) {
                        boolean isFileMkdirs = parent.mkdirs();
                    }

                    boolean newFile = logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("========", e.toString());
                    preFileName = null;
                    logFile = null;
                    return false;
                }
            }

            try {
                bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            } catch (Exception e) {
                e.printStackTrace();
                preFileName = null;
                logFile = null;
                return false;
            }

            return true;
        }

        /**
         * 关闭bufferedWriter
         */
        boolean close() {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    bufferedWriter = null;
                    preFileName = null;
                    logFile = null;
                }
            }
            return true;
        }

        /**
         * 将log写入文件
         *
         * @param flattenedLog 格式化后的log
         */
        void append(String flattenedLog) {
            try {
                bufferedWriter.write(flattenedLog);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static String getDiskCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

    public static String getDiskFilePath(Context context) {
        return ContextCompat.getExternalFilesDirs(
                context, null
        )[0].getAbsolutePath();
    }

}
