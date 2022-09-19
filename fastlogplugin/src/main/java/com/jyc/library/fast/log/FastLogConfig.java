package com.jyc.library.fast.log;

import com.jyc.library.fast.log.formatter.FastStackTraceFormatter;
import com.jyc.library.fast.log.formatter.FastThreadFormatter;
import com.jyc.library.fast.log.printer.FastLogPrinter;

/// @author jyc
/// 创建日期：2021/4/25
/// 描述：FastLogConfig
public abstract class FastLogConfig {

    public static int MAX_LEN = 512;
    public static FastStackTraceFormatter FAST_STACK_TRACE_FORMATTER = new FastStackTraceFormatter();
    public static FastThreadFormatter FAST_THREAD_FORMATTER = new FastThreadFormatter();

    public JsonParser injectJsonParser() {
        return null;
    }

    public String getGlobalTag() {
        return "FastLog";
    }

    public boolean enable() {
        return true;
    }

    public boolean includeTread() {
        return false;
    }

    public int stackTraceDepth() {
        return 5;
    }

    public FastLogPrinter[] printers(){
         return null;
    }

    public interface JsonParser {
        String toJson(Object src);
    }
}
