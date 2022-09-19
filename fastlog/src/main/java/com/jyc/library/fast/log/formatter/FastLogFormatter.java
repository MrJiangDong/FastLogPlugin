package com.jyc.library.fast.log.formatter;

/// @author jyc
/// 创建日期：2021/4/25
/// 描述：FastLogFormatter
public interface FastLogFormatter<T> {
    String format(T data);
}
