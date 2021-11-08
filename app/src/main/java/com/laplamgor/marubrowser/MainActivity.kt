package com.laplamgor.marubrowser

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentActivity
import org.json.JSONException
import org.json.JSONObject
import org.mozilla.geckoview.*
import org.mozilla.geckoview.GeckoSessionSettings.VIEWPORT_MODE_MOBILE
import org.mozilla.geckoview.WebExtension.MessageDelegate
import org.mozilla.geckoview.WebExtension.MessageSender


class MainActivity : FragmentActivity() {
    private var runtime: GeckoRuntime? = null

    private val EXTENSION_LOCATION = "resource://android/assets/messaging/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view: GeckoView = findViewById(R.id.geckoview)
        val session = GeckoSession()
        session.settings.viewportMode = VIEWPORT_MODE_MOBILE;


        if (runtime == null) {
            val settings = GeckoRuntimeSettings.Builder().remoteDebuggingEnabled(true).build()
            runtime = GeckoRuntime.create(this, settings)
        }


        val messageDelegate: MessageDelegate = object : MessageDelegate {
            @Nullable
            override fun onMessage(
                    nativeApp: String,
                    message: Any,
                    sender: MessageSender): GeckoResult<Any>? {
                if (message is JSONObject) {
                    val json = message
                    try {
                        if (json.has("type") && "WPAManifest" == json.getString("type")) {
                            val manifest = json.getJSONObject("manifest")
                            Log.d("MessageDelegate", "Found WPA manifest: $manifest")
                        }
                    } catch (ex: JSONException) {
                        Log.e("MessageDelegate", "Invalid manifest", ex)
                    }
                }
                return null
            }
        }

        // Make sure the extension is installed
        runtime!!
                .webExtensionController
                .ensureBuiltIn(EXTENSION_LOCATION, "messaging@example.com")
                .accept( // Set delegate that will receive messages coming from this extension.
                        { extension ->
                            session
                                    .webExtensionController
                                    .setMessageDelegate(extension!!, messageDelegate, "browser")
                        }
                )  // Something bad happened, let's log an error
                { e -> Log.e("MessageDelegate", "Error registering extension", e) }

        session.open(runtime!!)
        view.setSession(session)

//        session.loadUri("http://www.dmm.com/netgame/social/-/gadgets/=/app_id=854854/")
        session.loadUri("http://ooi.moe/poi")

        // Disable all two finger gesture within the browser
        // which are reserved for the zoom view
        view.setOnTouchListener { v: View?, event: MotionEvent ->
            event.pointerCount > 1
        }
    }
}