package com.teavaro.ecommDemoApp.ui

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.os.StrictMode
import android.util.Log
import com.google.firebase.FirebaseApp
import com.swrve.sdk.SwrveInitMode
import com.swrve.sdk.SwrveNotificationConfig
import com.swrve.sdk.SwrvePushNotificationListener
import com.swrve.sdk.SwrveSDK
import com.swrve.sdk.config.SwrveConfig
import com.swrve.sdk.geo.SwrveGeoConfig
import com.swrve.sdk.geo.SwrveGeoSDK
import com.teavaro.ecommDemoApp.BuildConfig
import com.teavaro.ecommDemoApp.R
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.funnelConnect.data.models.FCOptions
import com.teavaro.funnelConnect.initializer.FunnelConnectSDK
import com.teavaro.utiqTech.data.models.UTIQOptions
import com.teavaro.utiqTech.initializer.UTIQ

@Suppress("unused")
class FCApplication: Application() {

    companion object {
        lateinit var instance: Context
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        this.initAppPolices()
        println("Teavaro:------------------initializing FunnelConnectSDK-${BuildConfig.VERSION_NAME}-------------")
        FunnelConnectSDK.initialize(this, "ko8G.Rv_vT97LiDuoBHbhBJt", R.raw.fc_configs, FCOptions(true))
        val config = resources.openRawResource(R.raw.utiq_configs)
            .bufferedReader()
            .use { it.readText() }
        val options = UTIQOptions()
        options.enableLogging()
        options.setFallBackConfigJson(config)
        UTIQ.initialize(this, "R&Ai^v>TfqCz4Y^HH2?3uk8j", options)
        FirebaseApp.initializeApp(this)
        initSwrve()
    }

    private fun initAppPolices() {
        if (BuildConfig.DEBUG) {
            val threadPolices = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(threadPolices)
        }
    }

    private fun initSwrve(){
        try {
            val config = SwrveConfig()
                config.initMode = SwrveInitMode.MANAGED
                config.isAutoStartLastUser = true
            var channel: NotificationChannel? = null
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                channel = NotificationChannel("123", "Devapp swrve default channel", NotificationManager.IMPORTANCE_DEFAULT);
                if (getSystemService(Context.NOTIFICATION_SERVICE) != null) {
                    val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel);
                }
            }
            val notificationConfig: SwrveNotificationConfig.Builder = SwrveNotificationConfig.Builder(
                com.teavaro.ecommDemoApp.R.drawable.logo1, com.teavaro.ecommDemoApp.R.drawable.logo1, channel)
                .activityClass(MainActivity::class.java)
                    .largeIconDrawableId(com.teavaro.ecommDemoApp.R.drawable.logo1)
                    .accentColorHex("#3949AB")
            config.notificationConfig = notificationConfig.build()
            config.notificationListener = SwrvePushNotificationListener {
                val section = it.getJSONObject("New Group 1").getString("section")
                Store.section = section
//                Log.e("SwrveDemo", section)
            }
            SwrveSDK.createInstance(this, 32153, "FiIpd4eZ8CtQ6carAAx9", config)
            //geo config
            val geoConfig = SwrveGeoConfig.Builder()
                .geofenceTransitionListener { name: String?, transition: String?, triggerLocation: Location?, customProperties: String? ->
                    TrackUtils.geoPlace(name.toString())
                }
                .build()
            SwrveGeoSDK.init(this, geoConfig)
        } catch (exp: IllegalArgumentException) {
            Log.e("SwrveDemo", "Could not initialize the Swrve SDK", exp)
        }
    }
}