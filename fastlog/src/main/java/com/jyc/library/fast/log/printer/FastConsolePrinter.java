package com.jyc.library.fast.log.printer;


import static com.jyc.library.fast.log.FastLogConfig.MAX_LEN;

import android.util.Log;

import androidx.annotation.NonNull;

import com.jyc.library.fast.log.FastLogConfig;

/// @author jyc
/// 创建日期：2021/4/25
/// 描述：FastConsolePrinter
public class FastConsolePrinter implements FastLogPrinter {

    @Override
    public void print(@NonNull FastLogConfig config, int level, String tag, @NonNull String printString) {
        int len = printString.length();
        int countOfStub = len / MAX_LEN;
        if (countOfStub > 0) {
            int index = 0;
            for (int i = 0; i < countOfStub; i++) {
                Log.println(level, tag, printString.substring(index, index + MAX_LEN));
                index += MAX_LEN;
            }

            if (index != len) {
                Log.println(level, tag, printString.substring(index, len));
            }
        } else {
            Log.println(level, tag, printString);
        }
    }
}
