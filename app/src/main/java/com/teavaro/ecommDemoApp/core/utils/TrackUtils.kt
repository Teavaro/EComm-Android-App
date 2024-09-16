package com.teavaro.ecommDemoApp.core.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.teavaro.ecommDemoApp.FCApplication
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.funnelConnect.main.FunnelConnectSDK

object TrackUtils  : LifecycleObserver {
    const val EVENT_NAME = "event_name"
    const val EVENT_DATA = "event_data"

    fun impression(value: String) {
        event(value, "navigation")
    }

    fun click(value: String){
        event(value, "click")
    }

    fun event(value: String, name: String){
        val eventsMap = mutableMapOf( EVENT_NAME to name, EVENT_DATA to value)
        SharedPreferenceUtils.getMartechpass(FCApplication.instance)?.let {
            eventsMap["martechpass"] = it
        }
        events(eventsMap)
    }

    fun events(events: Map<String, String>){
        if(FunnelConnectSDK.isInitialized() && Store.isOptPermissionAccepted()) {
            FunnelConnectSDK.logEvents(events)
        }
    }

    fun geoPlace(value: String){
        event(value, "location")
    }

    fun lifeCycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(object: LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        impression("on_resume_scene")
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        impression("on_pause_scene")
                    }
                    Lifecycle.Event.ON_CREATE -> {
                        impression("on_create_scene")
                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        impression("on_destroy_scene")
                    }
                    else -> { }
                }
            }
        })
    }
}