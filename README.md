# GSM-to-VoIP Bridge PBX Prototype

This repository provides the PBX layer for a simple GSM-to-VoIP bridge built on Asterisk and deployed in Dokploy with Docker Compose.

Flow:

Old Android phone with SIM
-> Linphone on old Android phone
-> Asterisk in Docker on Dokploy
-> Main phone using Zoiper or Linphone

The design is intentionally small:

- one Asterisk service
- PJSIP only
- host networking for straightforward SIP and RTP handling
- SIP clients register directly to the server IP
- only selected config files are overridden; `/var/lib/asterisk` stays inside the image so built-in assets are preserved

## Repo Layout

```text
.
├── docker-compose.yml
├── README.md
├── .env.example
├── docs/
│   ├── android-bridge.md
│   └── android-bridge-rooted.md
├── android-bridge/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── app/
│       ├── build.gradle.kts
│       └── src/main/
│           ├── AndroidManifest.xml
│           ├── java/com/mabou7agar/androidbridge/
│           └── res/
├── etc/
│   └── asterisk/
│       ├── pjsip.conf
│       ├── extensions.conf
│       ├── logger.conf
│       ├── modules.conf
│       └── rtp.conf
└── var/
    ├── lib/asterisk/
    ├── log/asterisk/
    └── spool/asterisk/
```

## Architecture

- Endpoint `100` is the SIP endpoint used by Zoiper or Linphone on the main phone.
- Endpoint `200` is the SIP endpoint used by Linphone on the old Android phone.
- Extension `100` rings endpoint `100`.
- Extension `200` rings endpoint `200`.
- Extension `900<number>` is reserved for the future Android bridge app and passes the target GSM number in a SIP header.

This lets you build basic call flow quickly:

- dial `100` from the old Android phone to reach the main phone
- dial `200` from the main phone to reach the old Android phone
- dial `90015551234567` from the main phone to test the future Android bridge trigger path

## Asterisk Image Choice

The compose file pins `andrius/asterisk:18.26.4_debian-trixie`.

Reason:

- there is no Docker Official Asterisk image on Docker Hub
- this image is actively maintained
- it publishes current Asterisk 18 tags, including `18.26.4_debian-trixie`

References:

- [Docker Hub search for Asterisk images](https://hub.docker.com/search?q=asterisk&type=image)
- [andrius/asterisk image page](https://hub.docker.com/r/andrius/asterisk)

## Deployment Steps in Dokploy

1. Put this repository in a Git repo or upload it where Dokploy can access it.
2. In Dokploy, create a new project if needed.
3. Create a new Compose application in that project.
4. Point Dokploy at this repository.
5. Set the compose path to `docker-compose.yml`.
6. Optionally copy `.env.example` to `.env` if you want to override `ASTERISK_UID` or `ASTERISK_GID`.
7. Ensure the host firewall allows the SIP and RTP ports listed below.
8. Deploy the compose application.
9. After deploy, verify the container is running and inspect logs.

Important Dokploy notes:

- do not add Traefik labels
- do not expose this through a web proxy
- keep the app on host networking
- SIP clients should register to `167.235.231.202`
- do not bind-mount `/var/lib/asterisk` unless you intentionally pre-seed the image's docs and sound assets

## Firewall Ports

Open these on the server:

- `5060/udp` for SIP signaling
- `10000-10100/udp` for RTP media

Because this configuration uses only UDP transport for SIP, TCP and TLS are not required for the initial prototype.

## SIP Client Registration

Register both clients directly to server IP `167.235.231.202`.

### Endpoint 100

- Username: `100`
- Auth ID: `100`
- Password: `MainPhoneSIP-ChangeMe123`
- Domain or server: `167.235.231.202`
- Transport: `UDP`

### Endpoint 200

- Username: `200`
- Auth ID: `200`
- Password: `OldPhoneSIP-ChangeMe123`
- Domain or server: `167.235.231.202`
- Transport: `UDP`

## How to Test Extensions

1. Register both SIP clients.
2. From endpoint `200`, dial `100`.
3. Confirm endpoint `100` rings.
4. From endpoint `100`, dial `200`.
5. Confirm endpoint `200` rings.
6. Answer both directions and confirm two-way audio.
7. Optionally dial `90015551234567` and confirm the old Android bridge SIP endpoint rings as a future outbound-GSM trigger.

Useful Asterisk CLI checks:

```bash
docker exec -it asterisk-gsm-bridge asterisk -rvvv
```

Inside the Asterisk CLI:

```text
pjsip show endpoints
pjsip show contacts
core show channels
```

## Troubleshooting

### SIP registration failures

Check:

- the client is using server IP `167.235.231.202`
- username and password match `pjsip.conf`
- transport is `UDP`
- server firewall allows `5060/udp`
- Asterisk is listening on `0.0.0.0:5060`

Use:

```text
pjsip show endpoints
pjsip show registrations
```

If the client still fails, enable more logging:

```text
pjsip set logger on
```

### One-way audio

This is usually RTP or NAT related.

Check:

- `10000-10100/udp` is open in the firewall
- the client is not forcing an incompatible transport
- the server public IP in `pjsip.conf` matches the real host IP
- the clients are not on a broken symmetric NAT path

### No audio

Check:

- both sides negotiated `ulaw` or `alaw`
- RTP ports are open
- `direct_media=no` is preserved
- no upstream firewall is dropping UDP media

Use:

```text
rtp set debug on
```

### NAT issues

If audio or registration is inconsistent:

- confirm `rewrite_contact=yes`
- confirm `force_rport=yes`
- confirm `rtp_symmetric=yes`
- confirm `external_signaling_address` and `external_media_address` are set to the server public IP

### RTP port issues

If calls connect but no media flows:

- verify `rtpstart=10000` and `rtpend=10100` in `rtp.conf`
- verify the same UDP range is allowed in the host firewall
- verify the provider or cloud firewall is not silently dropping the RTP range

## Limitations of Android as GSM Bridge

Using an old Android phone as the GSM side is workable for a prototype, but it has constraints:

- Android can suspend background apps aggressively
- Linphone registration may drop under battery optimization
- incoming GSM call handling is app- and device-dependent
- Android does not behave like a native GSM gateway device
- call bridging between cellular audio and SIP often needs manual app logic or a purpose-built bridge app

This repository gives you the PBX core, not the Android telephony automation layer.

The repository now also includes an Android bridge scaffold in `android-bridge/` and a design note in `docs/android-bridge.md`. It is a starting point for a custom default-dialer bridge app, not a finished GSM gateway.

If the old phone is rooted, there is now a second design note in `docs/android-bridge-rooted.md` covering a more aggressive architecture with a privileged native media helper. That rooted path is the realistic way to push beyond call control into device-specific PCM capture and injection.

## Next Step: Android Bridge

The PBX in this repo is the SIP rendezvous point.

Planned connection model:

1. Install Linphone on the old Android phone.
2. Register it to Asterisk as extension `200`.
3. Install Zoiper or Linphone on the main phone.
4. Register it to Asterisk as extension `100`.
5. Use the old phone as the reachable SIP endpoint sitting beside the SIM-based device.
6. Add Android-side automation or a dedicated bridge app to connect cellular call events to SIP behavior.

Android bridge scaffold now included:

1. `android-bridge/` contains a minimal Android app skeleton.
2. The app is structured around `InCallService`, `ConnectionService`, a foreground runtime service, and a bridge controller.
3. Asterisk can now send outbound bridge requests to extension pattern `900<number>`.
4. The target GSM number is passed to the Android app through SIP header `X-Bridge-Target`.
5. The detailed Android-side contract is documented in `docs/android-bridge.md`.
6. A rooted-phone alternative is documented in `docs/android-bridge-rooted.md`.

What this gives you immediately:

- a stable SIP core on a VPS
- direct dialing between the two softphones
- a clean place to add dialplan logic later for call forwarding, recording, AGI, or GSM bridge hooks

## Local Validation

Minimum validation before production use:

```bash
docker compose config
docker compose up -d
docker logs -f asterisk-gsm-bridge
```

If you later move to another server, update the public IP in:

- `etc/asterisk/pjsip.conf`
- your SIP client account settings
