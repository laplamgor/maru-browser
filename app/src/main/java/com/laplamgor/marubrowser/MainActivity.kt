package com.laplamgor.marubrowser

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings.VIEWPORT_MODE_MOBILE
import org.mozilla.geckoview.GeckoView


class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view: GeckoView = findViewById(R.id.geckoview)
        val session = GeckoSession()
        session.settings.viewportMode = VIEWPORT_MODE_MOBILE;


        val builder = GeckoRuntimeSettings.Builder().remoteDebuggingEnabled(true)

        var runtime = GeckoRuntime.create(this, builder.build())

        session.open(runtime)
        view.setSession(session)

//        session.loadUri("http://www.dmm.com/netgame/social/-/gadgets/=/app_id=854854/")
        session.loadUri("http://ooi.moe/poi")

        view.setOnTouchListener { v: View?, event: MotionEvent ->
            event.pointerCount > 1
        }
    }
}