📞 Android Telephony Manager Integration - Inbound & Outbound Calling App

This Android application demonstrates how to integrate and manage telephony features using Android’s native TelephonyManager API. It enables both inbound (incoming) and outbound (outgoing) call handling, making it a useful base for building custom dialer apps, SIP clients, or telecom SDKs.

⸻

🚀 Features
	•	📲 Outbound Calling
	•	Programmatically initiate phone calls
	•	Handle permissions and runtime dialing flow
	•	Integrate with native phone services
	•	📥 Inbound Call Handling
	•	Detect and respond to incoming calls
	•	Access caller number, state (RINGING, OFFHOOK, IDLE), and display info
	•	Broadcast listener to manage call lifecycle
	•	🔄 Telephony State Management
	•	Monitor real-time changes using PhoneStateListener
	•	Handle edge cases like missed calls, dropped calls, and state transitions
	•	🔐 Permission Handling
	•	Fully implements Android runtime permissions for CALL_PHONE, READ_PHONE_STATE, and more
	•	Graceful fallback and user prompts
	•	📦 Modular Code Design
	•	Built with clean architecture principles
	•	Easily integratable into other projects or custom SDKs

⸻

🔧 Technical Stack
	•	Language: Kotlin
	•	Architecture: MVVM (Model-View-ViewModel)
	•	Permissions: Manifest.permission.CALL_PHONE, READ_PHONE_STATE
	•	API: TelephonyManager, PhoneStateListener, Intent.ACTION_CALL
	•	Target SDK: Android API 33+

⸻

🧩 Use Cases
	•	Build your own dialer or call management app
	•	Integrate custom telecom services into enterprise or internal apps
	•	Create a wrapper SDK for managing calls with enhanced features
	•	Extend with VoIP/SIP integrations
