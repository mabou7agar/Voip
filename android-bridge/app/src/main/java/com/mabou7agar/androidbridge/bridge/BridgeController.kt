package com.mabou7agar.androidbridge.bridge

import android.util.Log
import com.mabou7agar.androidbridge.sip.PjsipSipManager
import com.mabou7agar.androidbridge.sip.SipInvite
import java.util.concurrent.atomic.AtomicReference

object BridgeController {
    private const val TAG = "BridgeController"

    private val state = AtomicReference(BridgeState.IDLE)
    private val config = BridgeConfig()
    private val sipManager = PjsipSipManager(config)

    fun currentState(): BridgeState = state.get()

    fun handleIncomingGsm(number: String?) {
        transitionTo(BridgeState.GSM_INCOMING, "Incoming GSM call from $number")
        sipManager.callExtension(config.mainExtension)
        transitionTo(BridgeState.SIP_DIALING, "Dialing main extension ${config.mainExtension}")
    }

    fun handleOutboundBridgeInvite(invite: SipInvite) {
        transitionTo(BridgeState.SIP_RINGING, "Inbound SIP bridge invite ${invite.callId}")
        val target = invite.bridgeTarget
        if (target.isNullOrBlank()) {
            transitionTo(BridgeState.FAILED, "Bridge invite missing X-Bridge-Target")
            return
        }

        transitionTo(BridgeState.GSM_OUTGOING, "Requesting GSM call to $target")
        // The actual GSM leg is still to be implemented through Android Telecom.
    }

    fun markSipActive() {
        transitionTo(BridgeState.SIP_ACTIVE, "SIP leg is active")
    }

    fun markGsmActive() {
        transitionTo(BridgeState.GSM_ACTIVE, "GSM leg is active")
    }

    fun markBridgeActive() {
        transitionTo(BridgeState.BRIDGING, "Bridge active")
    }

    fun teardown(reason: String) {
        transitionTo(BridgeState.TEARDOWN, reason)
        transitionTo(BridgeState.IDLE, "Bridge returned to idle")
    }

    private fun transitionTo(next: BridgeState, reason: String) {
        val previous = state.getAndSet(next)
        Log.i(TAG, "State $previous -> $next: $reason")
    }
}
