package com.mabou7agar.androidbridge.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mabou7agar.androidbridge.service.BridgeForegroundService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            Log.i(TAG, "Boot completed, starting bridge runtime")
            BridgeForegroundService.start(context)
        }
    }

    private companion object {
        const val TAG = "BootReceiver"
    }
}
