package com.mabou7agar.androidbridge.sip

import android.util.Log
import com.mabou7agar.androidbridge.bridge.BridgeConfig

class PjsipSipManager(
    private val config: BridgeConfig,
) {
    fun register() {
        Log.i(TAG, "Registering SIP bridge endpoint ${config.sipUsername}@${config.asteriskHost}")
        // Replace this placeholder with a real PJSIP account registration flow.
    }

    fun callExtension(extension: String) {
        Log.i(TAG, "Originating SIP call to extension $extension")
        // Replace this placeholder with a real PJSIP outbound call.
    }

    fun handleIncomingInvite(invite: SipInvite) {
        Log.i(TAG, "Received SIP invite ${invite.callId} target=${invite.bridgeTarget}")
    }

    private companion object {
        const val TAG = "PjsipSipManager"
    }
}
