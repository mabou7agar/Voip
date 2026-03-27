package com.mabou7agar.androidbridge.bridge

data class BridgeConfig(
    val asteriskHost: String = "167.235.231.202",
    val sipUsername: String = "200",
    val sipPassword: String = "OldPhoneSIP-ChangeMe123",
    val mainExtension: String = "100",
    val bridgeExtensionPrefix: String = "900",
)
