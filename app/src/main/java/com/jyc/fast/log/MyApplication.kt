package com.jyc.fast.log

import android.app.Application
import com.google.gson.Gson
import com.jyc.library.fast.log.FastLog
import com.jyc.library.fast.log.FastLogConfig
import com.jyc.library.fast.log.FastLogManager
import com.jyc.library.fast.log.printer.FastConsolePrinter
import com.jyc.library.fast.log.printer.FastFilePrinter
import com.jyc.library.fast.log.printer.FastViewPrinter


/// @author jyc
/// 创建日期：2021/4/25
/// 描述：MyApplication
class MyApplication : Application() {
    private var isDebug = true
    override fun onCreate() {
        super.onCreate()
        initLog()
    }

    private fun initLog() {
        FastLogManager.init(
            object : FastLogConfig() {

                override fun injectJsonParser(): JsonParser {
                    return JsonParser { src -> Gson().toJson(src) }
                }

                override fun getGlobalTag(): String {
                    return "MyApplication"
                }

                override fun enable(): Boolean {
                    return true
                }
            },

            FastConsolePrinter(),
            FastFilePrinter.getInstance(
                FastFilePrinter.getDiskCachePath(applicationContext), 0
            ),
        )

        FastLog.a(
            FastFilePrinter.getInstance(
                FastFilePrinter.getDiskCachePath(applicationContext), 0
            )
        )
    }
}