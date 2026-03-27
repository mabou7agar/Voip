package com.mabou7agar.androidbridge.telecom

import android.telecom.Connection
import android.telecom.DisconnectCause

class BridgeConnection : Connection() {
    override fun onAnswer() {
        setActive()
    }

    override fun onDisconnect() {
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }
}
