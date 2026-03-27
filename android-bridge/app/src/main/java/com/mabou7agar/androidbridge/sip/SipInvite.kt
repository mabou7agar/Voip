package com.mabou7agar.androidbridge.sip

data class SipInvite(
    val callId: String,
    val from: String,
    val bridgeTarget: String?,
)
