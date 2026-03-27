# Android Bridge Skeleton

This document describes the Android-side bridge app that sits on the old Android phone and works with the Asterisk PBX in this repository.

The goal of the app is not to replace Asterisk. It is the SIM-side control plane that:

- observes GSM call state on the old Android phone
- registers to Asterisk as extension `200`
- accepts bridge requests from Asterisk
- places outbound GSM calls when requested
- later coordinates GSM and SIP call legs

This repository now contains a minimal Android scaffold in `android-bridge/` so another engineer can continue from a concrete baseline.

## Current PBX Contract

The PBX exposes these extensions:

- `100` rings the main phone SIP client
- `200` rings the old Android bridge SIP endpoint
- `900<number>` calls the Android bridge endpoint and injects `X-Bridge-Target: <number>` in the SIP INVITE

Example:

- dialing `90015551234567` from `100` asks the Android bridge to place a GSM call to `15551234567`

The Android app should read that SIP header and treat it as an outbound GSM request.

## Android App Layout

```text
android-bridge/
├── settings.gradle.kts
├── build.gradle.kts
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/mabou7agar/androidbridge/
        │   ├── BridgeApplication.kt
        │   ├── MainActivity.kt
        │   ├── boot/BootReceiver.kt
        │   ├── bridge/BridgeConfig.kt
        │   ├── bridge/BridgeController.kt
        │   ├── bridge/BridgeState.kt
        │   ├── service/BridgeForegroundService.kt
        │   ├── sip/PjsipSipManager.kt
        │   ├── sip/SipInvite.kt
        │   └── telecom/
        │       ├── BridgeConnection.kt
        │       ├── BridgeConnectionService.kt
        │       └── BridgeInCallService.kt
        └── res/
            ├── layout/activity_main.xml
            ├── values/strings.xml
            └── values/themes.xml
```

## Minimal Runtime Design

### Incoming GSM

1. Android telecom reports an incoming carrier call.
2. `BridgeInCallService` notifies `BridgeController`.
3. `BridgeController` asks the SIP layer to call extension `100` on Asterisk.
4. Once the SIP leg is answered, the app transitions into a bridge-active state.
5. Media bridging is still to be implemented.

### Outbound GSM

1. The main phone dials `900<number>`.
2. Asterisk sends a SIP INVITE to extension `200` with header `X-Bridge-Target`.
3. `PjsipSipManager` extracts the header and passes it to `BridgeController`.
4. `BridgeController` asks Android Telecom to place a GSM call to that number.
5. Once the GSM leg is answered, the app transitions into a bridge-active state.

## Current Implementation Scope

The scaffold includes:

- manifest entries for a default-dialer style app
- telecom service classes
- a foreground service for long-running bridge state
- a bridge controller with explicit state transitions
- a SIP abstraction with a placeholder PJSIP-backed implementation surface
- a simple activity with the deployment contract and endpoint summary

The scaffold intentionally does not yet implement:

- real PJSIP native bindings
- persistent account setup storage
- GSM audio to RTP bridging
- production permission UX
- call recording or analytics

## Next Engineering Steps

1. Replace `PjsipSipManager` placeholders with a real PJSIP binding.
2. Add secure settings persistence for server IP, username, and password.
3. Parse the `X-Bridge-Target` SIP header on inbound bridge requests.
4. Add GSM call placement through Android Telecom.
5. Synchronize answer and hangup events across GSM and SIP legs.
6. Prototype media handling and validate device-specific behavior on the old Android phone.

## Device Expectations

Use the old Android phone for the bridge role:

- Android 9 or newer is the practical target for this scaffold
- disable battery optimization for the bridge app
- keep the SIM in that device
- keep the app set as the default phone app during bridge testing

## Development Notes

- The Android app uses package name `com.mabou7agar.androidbridge`
- The SIP endpoint remains extension `200`
- The main phone remains extension `100`
- The PBX server IP remains `167.235.231.202`

