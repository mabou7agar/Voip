package com.mabou7agar.androidbridge.telecom

import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.util.Log

class BridgeConnectionService : ConnectionService() {
    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?,
    ): Connection {
        Log.i(TAG, "Creating incoming bridge connection")
        return BridgeConnection().apply {
            setInitializing()
            setInitialized()
            setRinging()
        }
    }

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?,
    ): Connection {
        Log.i(TAG, "Creating outgoing bridge connection")
        return BridgeConnection().apply {
            setInitializing()
            setInitialized()
            setDialing()
        }
    }

    private companion object {
        const val TAG = "BridgeConnectionSvc"
    }
}
