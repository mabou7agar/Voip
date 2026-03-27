package com.mabou7agar.androidbridge.telecom

import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import com.mabou7agar.androidbridge.bridge.BridgeController
import com.mabou7agar.androidbridge.service.BridgeForegroundService

class BridgeInCallService : InCallService() {
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        BridgeForegroundService.start(this)

        val number = call.details.handle?.schemeSpecificPart
        Log.i(TAG, "System call added number=$number state=${call.state}")
        BridgeController.handleIncomingGsm(number)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        Log.i(TAG, "System call removed")
        BridgeController.teardown("System call removed")
    }

    private companion object {
        const val TAG = "BridgeInCallService"
    }
}
