package com.jyc.library.fast.log;

import androidx.annotation.NonNull;


import com.jyc.library.fast.log.printer.FastLogPrinter;
import com.jyc.library.fast.log.util.FastStackTraceUtil;

import java.util.Arrays;
import java.util.List;

/// @author jyc
/// 创建日期：2021/4/25
/// 描述：FastLog
public class FastLog {
    private static final String FAST_LOG_PACKAGE;

    static {
        String className = FastLog.class.getName();
        FAST_LOG_PACKAGE = className.substring(0, className.lastIndexOf('.') + 1);
    }

    public static void v(Object... contents) {
        log(FastLogType.V, contents);
    }

    public static void vt(String tag, Object... contents) {
        log(FastLogType.V, tag, contents);
    }

    public static void d(Object... contents) {
        log(FastLogType.D, contents);
    }

    public static void dt(String tag, Object... contents) {
        log(FastLogType.D, tag, contents);
    }

    public static void i(Object... contents) {
        log(FastLogType.I, contents);
    }

    public static void it(String tag, Object... contents) {
        log(FastLogType.I, tag, contents);
    }

    public static void w(Object... contents) {
        log(FastLogType.W, contents);
    }

    public static void wt(String tag, Object... contents) {
        log(FastLogType.W, tag, contents);
    }

    public static void e(Object... contents) {
        log(FastLogType.E, contents);
    }

    public static void et(String tag, Object... contents) {
        log(FastLogType.E, tag, contents);
    }

    public static void a(Object... contents) {
        log(FastLogType.A, contents);
    }

    public static void at(String tag, Object... contents) {
        log(FastLogType.A, tag, contents);
    }


    public static void log(@FastLogType.TYPE int type, Object... contents) {
        log(type, FastLogManager.getInstance().getConfig().getGlobalTag(), contents);
    }

    public static void log(@FastLogType.TYPE int type, @NonNull String tag, Object... contents) {
        log(FastLogManager.getInstance().getConfig(), type, tag, contents);
    }

    public static void log(@NonNull FastLogConfig config, @FastLogType.TYPE int type, @NonNull String tag, Object... contents) {
        if (!config.enable()) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        //如果开启线程打印,则添加线程打印
        if (config.includeTread()) {
            String threadInfo = FastLogConfig.FAST_THREAD_FORMATTER.format(Thread.currentThread());
            sb.append(threadInfo).append("\n");
        }

        //如果堆栈数大于0，则添加堆栈信息
        if (config.stackTraceDepth() > 0) {
            String stackTrace = FastLogConfig.FAST_STACK_TRACE_FORMATTER.format(
                    FastStackTraceUtil.getCroppedRealStackTrack(new Throwable().getStackTrace(), FAST_LOG_PACKAGE, config.stackTraceDepth()));
            sb.append(stackTrace).append("\n");
        }

        String body = parseBody(contents, config);
        sb.append(body);

        List<FastLogPrinter> printers = config.printers() != null ?
                Arrays.asList(config.printers()) :
                FastLogManager.getInstance().getPrinters();

        if (printers == null) {
            return;
        }

        //打印log
        for (FastLogPrinter printer : printers) {
            printer.print(config, type, tag, sb.toString());
        }
    }

    //序列化信息
    private static String parseBody(@NonNull Object[] contents, @NonNull FastLogConfig config) {
        if (config.injectJsonParser() != null) {
            return config.injectJsonParser().toJson(contents);
        }

        StringBuilder sb = new StringBuilder();
        for (Object o : contents) {
            sb.append(o.toString()).append(";");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}
