package com.mabou7agar.androidbridge.bridge

enum class BridgeState {
    IDLE,
    GSM_INCOMING,
    GSM_OUTGOING,
    SIP_DIALING,
    SIP_RINGING,
    SIP_ACTIVE,
    GSM_ACTIVE,
    BRIDGING,
    FAILED,
    TEARDOWN,
}
