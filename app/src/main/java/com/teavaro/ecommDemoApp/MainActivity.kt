package com.teavaro.ecommDemoApp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.teavaro.ecommDemoApp.baseClasses.mvvm.BaseActivity
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.room.AppDb
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.ActivityMainBinding
import com.teavaro.funnelConnect.main.FunnelConnectSDK
import com.utiq.utiqTech.main.Utiq


class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val navController by lazy { this.findNavController(R.id.nav_host_fragment_container) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        TrackUtils.lifeCycle(lifecycle)

        var db = Room.databaseBuilder(applicationContext, AppDb::class.java, "TeavaroEcommDB")
            .fallbackToDestructiveMigration()
            .build()

        val navView = viewBinding.navView
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_start,
                R.id.navigation_home,
                R.id.navigation_cart,
                R.id.navigation_wishlist,
                R.id.navigation_shop,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(this.navController, appBarConfiguration)
        navView.setupWithNavController(this.navController)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setIcon(R.drawable.logo2)
        if (resources.getString(R.string.mode) == "Day") {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        }

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_settings -> {
                    Log.d("test->", item.itemId.toString())
                    Store.lastPage = R.id.navigation_settings
                }
                R.id.navigation_home -> {
                    Log.d("test->", item.itemId.toString())
                    Store.lastPage = R.id.navigation_home
                }
            }
            navController.navigate(item.itemId)
            true
        }

        Store.initializeData(this, db) {
            this@MainActivity.runOnUiThread {
                navView.selectedItemId = it
                navController.navigate(it)
            }
        }

        Log.d("okhttp.OkHttpClient:", "before UTIQ.onInitialize")
        FunnelConnectSDK.onInitialize({
            Store.fcStartService(this){
                if (FunnelConnectSDK.getPermissions().isEmpty()) {
                    Store.showPermissionsDialog(this, supportFragmentManager)
                }
            }
        }) {
            Store.umid = "FunnelConnect failed initialization."
            Toast.makeText(FCApplication.instance, it.message, Toast.LENGTH_LONG).show()
        }
        Utiq.onInitialize({
            Log.d("okhttp.OkHttpClient:", "inside UTIQ.onInitialize")
            if (Utiq.isConsentAccepted()) {
                Log.d("okhttp.OkHttpClient:", "isConsentAccepted()")
                Store.utiqStartService(this)
            }
        }, {
            Toast.makeText(FCApplication.instance, it.message, Toast.LENGTH_LONG).show()
        })
        handleIntent(intent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setOverflowButtonColor(toolbar: Toolbar, color: Int) {
        var drawable: Drawable? = toolbar.overflowIcon
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable)
            DrawableCompat.setTint(drawable.mutate(), color)
            toolbar.overflowIcon = drawable
        }
    }

    private fun handleIntent(intent: Intent?) {
        val appLinkAction: String? = intent?.action
        val appLinkData: Uri? = intent?.data
        showDeepLinkOffer(appLinkAction, appLinkData)
    }

    private fun showDeepLinkOffer(appLinkAction: String?, appLinkData: Uri?) {
        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            Store.handleDeepLink(this, appLinkData, supportFragmentManager)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.login_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("test->", item.itemId.toString())
        Store.navigateAction?.invoke(Store.lastPage)
        return true
    }
}