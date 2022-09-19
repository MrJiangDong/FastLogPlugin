package com.jyc.library.fast.log.printer;

import androidx.annotation.NonNull;

import com.jyc.library.fast.log.FastLogConfig;

/// @author jyc
/// 创建日期：2021/4/25
/// 描述：FastLogPrinter
public interface FastLogPrinter {
    void print(@NonNull FastLogConfig config, int level, String tag, @NonNull String printString);
}
