package com.jyc.fast.log

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jyc.library.fast.log.FastLog
import com.jyc.library.fast.log.FastLogConfig
import com.jyc.library.fast.log.FastLogManager
import com.jyc.library.fast.log.FastLogType
import com.jyc.library.fast.log.printer.FastViewPrinter

class MainActivity : AppCompatActivity() {
    private var viewPrinter: FastViewPrinter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPrinter = FastViewPrinter(this)
        viewPrinter!!.viewProvider.showFloatingView()
        FastLogManager.getInstance().addPrinter(viewPrinter)

        // 自定义log配置
        FastLog.log(object : FastLogConfig() {
            override fun includeTread(): Boolean {
                return true
            }

            override fun stackTraceDepth(): Int {
                return 0
            }
        }, FastLogType.E, "-----------", "Hello World")
    }
}