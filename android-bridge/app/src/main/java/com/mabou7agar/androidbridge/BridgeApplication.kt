package com.mabou7agar.androidbridge

import android.app.Application
import android.util.Log

class BridgeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Android bridge application created")
    }

    private companion object {
        const val TAG = "BridgeApplication"
    }
}
