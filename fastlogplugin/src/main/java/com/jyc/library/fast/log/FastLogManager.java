package com.jyc.library.fast.log;

import androidx.annotation.NonNull;


import com.jyc.library.fast.log.printer.FastLogPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/// @author jyc
/// 创建日期：2021/4/25
/// 描述：FastLogManager
public class FastLogManager {
    private FastLogConfig config;
    private static FastLogManager instance;

    private List<FastLogPrinter> printers = new ArrayList<>();

    private FastLogManager(FastLogConfig config, FastLogPrinter[] printers) {
        this.config = config;
        this.printers.addAll(Arrays.asList(printers));
    }

    public static FastLogManager getInstance() {
        return instance;
    }

    public static void init(@NonNull FastLogConfig config, FastLogPrinter... printers) {
        instance = new FastLogManager(config, printers);
    }

    public FastLogConfig getConfig() {
        return config;
    }

    public List<FastLogPrinter> getPrinters() {
        return printers;
    }

    public void addPrinter(FastLogPrinter printer) {
        printers.add(printer);
    }

    public void removePrinter(FastLogPrinter printer) {
        if (printers != null) {
            printers.remove(printer);
        }
    }
}
