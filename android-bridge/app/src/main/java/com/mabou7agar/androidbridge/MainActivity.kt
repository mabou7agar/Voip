package com.mabou7agar.androidbridge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mabou7agar.androidbridge.service.BridgeForegroundService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Keep the bridge runtime visible early during testing.
        BridgeForegroundService.start(this)
    }
}
