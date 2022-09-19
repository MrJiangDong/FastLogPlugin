package com.jyc.library.fast.log.formatter;

/// @author jyc
/// 创建日期：2021/4/25
/// 描述：FastThreadFormatter
public class FastThreadFormatter implements FastLogFormatter<Thread> {

    @Override
    public String format(Thread data) {
        return "Thread:" + data.getName();
    }


}
