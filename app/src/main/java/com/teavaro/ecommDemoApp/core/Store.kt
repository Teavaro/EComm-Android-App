package com.teavaro.ecommDemoApp.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.swrve.sdk.SwrveSDK
import com.swrve.sdk.geo.SwrveGeoSDK
import com.teavaro.ecommDemoApp.FCApplication
import com.teavaro.ecommDemoApp.R
import com.teavaro.ecommDemoApp.core.dataClases.InfoResponse
import com.teavaro.ecommDemoApp.core.room.ACEntity
import com.teavaro.ecommDemoApp.core.room.AppDb
import com.teavaro.ecommDemoApp.core.room.ItemEntity
import com.teavaro.ecommDemoApp.core.utils.SharedPreferenceUtils
import com.teavaro.ecommDemoApp.ui.AbandonedCartDialogFragment
import com.teavaro.ecommDemoApp.ui.ItemDescriptionDialogFragment
import com.teavaro.ecommDemoApp.ui.PermissionConsentDialogFragment
import com.teavaro.ecommDemoApp.ui.UtiqConsent
import com.teavaro.funnelConnect.data.models.PassQuery
import com.teavaro.funnelConnect.main.FunnelConnectSDK
import com.teavaro.funnelConnect.utils.platformTypes.permissionsMap.Permissions
import com.utiq.utiqTech.main.Utiq
import org.json.JSONObject
import java.lang.reflect.Type
import java.net.URLEncoder


@SuppressLint("StaticFieldLeak")
object Store {
    var lastPage = R.id.navigation_home
    val stubToken = "523393b9b7aa92a534db512af83084506d89e965b95c36f982200e76afcb82cb"
    private var db: AppDb? = null
    var listItems: ArrayList<ItemEntity> = ArrayList()
    var listOffers: ArrayList<ItemEntity> = ArrayList()
    var listCart: ArrayList<ItemEntity> = ArrayList()
    var listWish: ArrayList<ItemEntity> = ArrayList()
    var listAc: ArrayList<ACEntity> = ArrayList()
    var section = "none"
    var webView: WebView? = null
    var navigateAction: ((Int) -> Unit)? = null
    var infoResponse: String? = null
    var attributes: String? = null
    val keyOm = "CS-OM"
    val keyOpt = "CS-OPT"
    val keyNba = "CS-NBA"
    val keyUtiq = "CS-UTIQ"
    val fcNotificationsName = "MAIN_CS"
    val utiqNotificationsName = "UTIQ_CS"
    val notificationsVersion = 1
    val userType = "enemail"
    var atid: String = "UTIQ not initialized."
    var mtid: String = "UTIQ not initialized."
    var umid: String? = "FunnelConnect not initialized."
    var userId: String? = null
    var itemId = ""
    var isFunnelConnectStarted = false
    var description =
        "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don’t look even slightly believable. If you are going to use a passage of Lorem Ipsum."
    var refreshCeltraAd: (() -> Unit)? = null

    fun addItemToCart(id: Int) {
        listItems[id].countInCart += 1
        updateItemDB(listItems[id])
        listCart = getItemsCart()
    }

    fun removeItemFromCart(id: Int) {
        listItems[id].countInCart = 0
        updateItemDB(listItems[id])
        listCart = getItemsCart()
    }

    fun updateItemDB(item: ItemEntity) {
        db?.let {
            Thread {
                it.itemDao().update(item)
            }.start()
        }
    }

    fun addItemToWish(id: Int) {
        listItems[id].isInWish = true
        updateItemDB(listItems[id])
        listWish = getItemsWish()
    }

    fun removeItemFromWish(id: Int) {
        listItems[id].isInWish = false
        updateItemDB(listItems[id])
        listWish = getItemsWish()
    }

    fun getItems(): ArrayList<ItemEntity> {
        return listItems
    }

    fun getItemsCart(): ArrayList<ItemEntity> {
        val listCart = ArrayList<ItemEntity>()
        for (item in listItems) {
            if (item.countInCart > 0) {
                listCart.add(item)
            }
        }
        return listCart
    }

    fun getItemsWish(): ArrayList<ItemEntity> {
        val listWish = ArrayList<ItemEntity>()
        for (item in listItems) {
            if (item.isInWish)
                listWish.add(item)
        }
        return listWish
    }

    fun getTotalPriceCart(): Float {
        var total = 0f
        for (item in listItems) {
            total += item.price * item.countInCart
        }
        return total
    }

    fun getItemsOffer(): ArrayList<ItemEntity> {
        val listOffer = ArrayList<ItemEntity>()
        for (item in listItems) {
            if (item.isOffer)
                listOffer.add(item)
        }
        return listOffer
    }

    fun removeAllCartItems() {
        for (item in listItems) {
            if (item.countInCart > 0) {
                item.countInCart = 0
                updateItemDB(item)
            }
        }
    }

    fun showPermissionsDialog(context: Activity, supportFragmentManager: FragmentManager) {
        PermissionConsentDialogFragment.open(
            supportFragmentManager,
            { omPermissionAccepted, optPermissionAccepted, nbaPermissionAccepted ->
                updatePermissions(
                    omPermissionAccepted,
                    optPermissionAccepted,
                    nbaPermissionAccepted,
                    context
                )
                if (omPermissionAccepted || optPermissionAccepted || nbaPermissionAccepted) {
                    val stubToken = SharedPreferenceUtils.getStubToken(context)
                    Utiq.checkMNOEligibility(stubToken, {
                        showUtiqConsent(context, supportFragmentManager)
                    }, {

                    })
                } else {
                    clearData(context)
                }
            },
            {
                updatePermissions(
                    om = false,
                    opt = false,
                    nba = false,
                    context
                )
                clearData(context)
            })
    }

    fun showUtiqConsent(context: Activity, supportFragmentManager: FragmentManager) {
        UtiqConsent.open(supportFragmentManager) { consent ->
            if (Utiq.isInitialized()) {
                if (consent) {
                    Utiq.acceptConsent()
                    utiqStartService(context)
                } else {
                    Utiq.rejectConsent()
                }
                updateUtiqConsent(consent, context)
            }
        }
    }

    private fun updateUtiqConsent(
        consent: Boolean,
        context: Activity
    ) {
        val action = {
            val permissions = Permissions()
            permissions.addPermission(keyUtiq, consent)
            FunnelConnectSDK.updatePermissions(
                permissions,
                utiqNotificationsName,
                notificationsVersion, {
                    updateFCData(it)
                }
            )
        }
        if (isFunnelConnectStarted) {
            action.invoke()
        } else {
            fcStartService(context) {
                action.invoke()
            }
        }
    }

    fun updatePermissions(
        om: Boolean,
        opt: Boolean,
        nba: Boolean,
        context: Activity
    ) {
        val action = {
            val permissions = Permissions()
            permissions.addPermission(keyOm, om)
            permissions.addPermission(keyOpt, opt)
            permissions.addPermission(keyNba, nba)
            FunnelConnectSDK.updatePermissions(
                permissions,
                fcNotificationsName,
                notificationsVersion, {
                    updateFCData(it)
                }
            )
        }
        if (isFunnelConnectStarted) {
            action.invoke()
        } else {
            fcStartService(context) {
                action.invoke()
            }
        }
    }

    fun showAbandonedCartDialog(supportFragmentManager: FragmentManager, items: List<ItemEntity>) {
        AbandonedCartDialogFragment.open(
            supportFragmentManager,
            items
        ) {
            it.forEach { item ->
                addItemToCart(item.itemId)
            }
            navigateAction?.invoke(R.id.navigation_cart)
        }
    }

    fun getAttributesFromInfo(): String? {
        infoResponse?.let { info ->
            var gson = Gson()
            val classOb = InfoResponse::class.java
            classOb?.let { classOnj ->
                var obj: InfoResponse? = gson.fromJson(info, classOnj)
                obj?.let { ob ->
                    ob.attributes?.let { attr ->
                        val gsonType: Type = object : TypeToken<HashMap<*, *>?>() {}.type
                        return gson.toJson(attr, gsonType)
                    }
                }
            }
        }
        return null
    }

    fun getBanner(): String {
        var text = "&amp;attributes=${URLEncoder.encode("{}", "utf-8")}"
        if (isNbaPermissionAccepted()) {
            attributes?.let {
                text = "&amp;attributes=${URLEncoder.encode(it, "utf-8")}&amp;fc_umid=$umid"
            }
            text += "&amp;allowTracking=true"
        } else {
            text += "&amp;allowTracking=false"
        }

        return """
           <!DOCTYPE html>
           <html>
           <body>
                <div class="celtra-ad-v3">
                    <img src="data:image/png,celtra" style="display: none" onerror="
                        (function(img) {
                            var params = {'clickUrl':'','widthBreakpoint':'','expandDirection':'undefined','preferredClickThroughWindow':'new','clickEvent':'advertiser','externalAdServer':'Custom','tagVersion':'html-standard-7'};
                            var req = document.createElement('script');
                            req.id = params.scriptId = 'celtra-script-' + (window.celtraScriptIndex = (window.celtraScriptIndex||0)+1);
                            params.clientTimestamp = new Date/1000;
                            params.clientTimeZoneOffsetInMinutes = new Date().getTimezoneOffset();
                            params.hostPageLoadId=window.celtraHostPageLoadId=window.celtraHostPageLoadId||(Math.random()+'').slice(2);
                            var qs = '';
                            for (var k in params) {
                                qs += '&amp;' + encodeURIComponent(k) + '=' + encodeURIComponent(params[k]);
                            }
                            var src = 'https://funnelconnect.brand-demo.com/op/brand-demo-web-celtra/ad?' + qs + '$text';
                            req.src = src;
                            img.parentNode.insertBefore(req, img.nextSibling);
                        })(this);
                    "/>
                </div>
           </body>
        </html>
        """.trimIndent()
    }

    fun initializeData(context: Context, db: AppDb, action: ((Int) -> Unit)) {
        navigateAction = action
        this.db = db
        Thread {
//            db.itemDao().removeAllItems()
//            db.acDao().removeAllAc()
            if (db.itemDao().getAllItems().isEmpty()) {
                db.itemDao().saveItems(
                    ItemEntity(
                        0,
                        "RUF Porridge Apfel Zimt mit Vollkorn Haferflocken",
                        description,
                        1.69f,
                        "porridge",
                        "/product/ruf-porridge-apfel-zimt-mit-vollkorn-haferflocken/",
                        true
                    )
                )
                db.itemDao().saveItems(
                    ItemEntity(
                        1,
                        "TSARINE Caviar 50g",
                        description,
                        45.50f,
                        "tsarine",
                        "/product/marke-tsarine-caviar-50g/",
                        true,
                        acId = 123
                    )
                )
                db.itemDao().saveItems(
                    ItemEntity(
                        2,
                        "Hillshire Farm Lit'l Smokies Salchicha ahumada, 14 oz",
                        description,
                        5.31f,
                        "hillshire",
                        "/product/hillshire-farm-litl-smokies-salchicha-ahumada-14-oz/"
                    )
                )
                db.itemDao().saveItems(
                    ItemEntity(
                        3,
                        "Good Soy Cookies",
                        description,
                        3.99f,
                        "cookies",
                        "/product/good-soy-cookies/"
                    )
                )
                db.itemDao().saveItems(
                    ItemEntity(
                        4,
                        "Jack Link’s Teriyaki, Beef Jerky",
                        description,
                        6.60f,
                        "teriyaki",
                        "/product/save-on-jack-links-beef-jerky-teriyaki/"
                    )
                )
                db.itemDao().saveItems(
                    ItemEntity(
                        5,
                        "Absolute Organic Cashews",
                        description,
                        20.00f,
                        "cashews",
                        "/product/healthy-snack-box-variety-pack-60-count/"
                    )
                )
                db.itemDao().saveItems(
                    ItemEntity(
                        6,
                        "Pork Cocktail Sausages, Pack",
                        description,
                        3.29f,
                        "pork",
                        "/product/pork-cocktail-sausages-pack/"
                    )
                )
            }
            listItems = db.itemDao().getAllItems() as ArrayList<ItemEntity>
            this.listAc = db.acDao().getAllAcs() as ArrayList<ACEntity>
            listOffers = getItemsOffer()
            action.invoke(R.id.navigation_home)
            userId = SharedPreferenceUtils.getUserId(context)
        }.start()
    }

    fun processCelraAction(
        context: Context,
        data: String,
        supportFragmentManager: FragmentManager
    ) {
        var obj = JSONObject(data)
        obj?.let { ob ->
            ob.getJSONObject("attributes")?.let { attr ->
                if (!attr.isNull("impression")) {
                    attr.getString("impression")?.let { impression ->
                        if (impression == "ShopView") {
                            section = "ShopView"
                            navigateAction?.let {
                                it.invoke(R.id.navigation_shop)
                            }
                        }
                        if (impression == "AbCartView" && listAc.isEmpty()) {
                            Toast.makeText(
                                context,
                                "There isn't abandoned items.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                if (!attr.isNull("item_id")) {
                    attr.getString("item_id")?.let {
                        ItemDescriptionDialogFragment.open(
                            supportFragmentManager,
                            listItems[it.toInt()],
                            {
                                addItemToCart(it.toInt())
                            },
                            {
                                addItemToWish(it.toInt())
                            })
                    }
                }
                if (!attr.isNull("item_data")) {
                    attr.getString("item_data")?.let { data ->
                        Log.d("OkHttp", data)
                        findItemWithData(data)?.let {
                            ItemDescriptionDialogFragment.open(
                                supportFragmentManager,
                                it,
                                {
                                    addItemToCart(it.itemId)
                                },
                                {
                                    addItemToWish(it.itemId)
                                })

                        }
                    }
                }
                if (!attr.isNull("ab_cart_id")) {
                    attr.getString("ab_cart_id")?.let {
                        if (it != "-1") {
                            refreshAcItems(it.toInt()) { list ->
                                showAbandonedCartDialog(supportFragmentManager, list)
                            }
                        }
                    }
                }
                if (!attr.isNull("ident_url")) {
                    attr.getString("ident_url")?.let { url ->
                        if (userId != null) {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(browserIntent)
                        } else
                            Toast.makeText(context, "Log in first please.", Toast.LENGTH_LONG)
                                .show()
                    }
                }
            }
        }
    }

    private fun findItemWithData(data: String): ItemEntity? {
        var index: Int
        for (item in listItems) {
            index = item.data.indexOf(data)
            if (index > -1) {
                return item
            }
        }
        return null
    }

    fun addAbandonedCart(items: List<ItemEntity>): Int {
        val acId = getUniqueId()
        db?.let { db ->
            Thread {
                for (item in items) {
                    item.acId = acId
                    db.itemDao().update(item)
                }
                ACEntity(acId).let {
                    db.acDao().saveAC(it)
                    this.listAc.add(it)
                }
            }.start()
        }
        return acId
    }

    fun getUniqueId(): Int {
        return (0..1000).random()
    }

    fun refreshAcItems(acId: Int, action: ((List<ItemEntity>) -> Unit)) {
        db?.let {
            Thread {
                val list = it.itemDao().getAllAcItems(acId)
                action.invoke(list)
            }.start()
        }
    }

    fun handleDeepLink(
        context: Context,
        appLinkData: Uri,
        supportFragmentManager: FragmentManager
    ) {
        val acId = appLinkData.getQueryParameter("ab_cart_id")
        val itemId = appLinkData.getQueryParameter("item_id")
        val impression = appLinkData.getQueryParameter("impression")
        acId?.let {
            refreshAcItems(it.toInt()) { items ->
                showAbandonedCartDialog(supportFragmentManager, items)
            }
            navigateAction?.invoke(R.id.navigation_home)
        }
        itemId?.let {
            ItemDescriptionDialogFragment.open(
                (context as AppCompatActivity).supportFragmentManager,
                listItems[it.toInt()],
                {
                    addItemToCart(it.toInt())
                },
                {
                    addItemToWish(it.toInt())
                })
            navigateAction?.invoke(R.id.navigation_home)
        }
        impression?.let {
            if (it == "ShopView") {
                navigateAction?.invoke(R.id.navigation_shop)
            }
        }
    }

    fun getAbCartId(): Int? {
        if (this.listAc.isNotEmpty()) {
//            Log.d("iraniranCountAc", this.listAc.size.toString())
            return this.listAc.last().acId
        }
        return null
    }


    fun utiqStartService(context: Context) {
        atid = "{\"status\":\"notFound\"}"
        mtid = "{\"status\":\"notFound\"}"
        val stubToken = SharedPreferenceUtils.getStubToken(context)
        Utiq.fetchIdConnectData(stubToken, {
            Log.d("okhttp.OkHttpClient", "startService good")
            atid = it.atid.toString()
            SharedPreferenceUtils.setMartechpass(context, it.mtid.toString())
            fcStartService(context as Activity) {

            }
        }, {
            Log.d("okhttp.OkHttpClient", it.message)
        })
    }

    fun getClickIdentLink(context: Context): String? {
        SharedPreferenceUtils.getUserId(context)?.let {
            return "https://funnelconnect.brand-demo.com/op/brand-demo-app-click-ident/click?$userType=$userId&uri=https%3A%2F%2Fwww.brand-demo.com%2F"
        }
        return null
    }

    fun clearData(context: Context) {
        umid = ""
        infoResponse = ""
        attributes = ""
        userId = null
        listAc.clear()
        SharedPreferenceUtils.setUserId(context, null)
        SharedPreferenceUtils.setLogin(context, false)
        FunnelConnectSDK.clearData()
        FunnelConnectSDK.clearCookies()
        clearUtiqData(context)
        isFunnelConnectStarted = false
    }

    fun clearUtiqData(context: Context) {
        SharedPreferenceUtils.setStubToken(context, null)
        Utiq.clearData()
        Utiq.clearCookies()
        atid = ""
        mtid = ""
        SharedPreferenceUtils.setMartechpass(context, null)
    }

    fun isNbaPermissionAccepted(): Boolean {
        return FunnelConnectSDK.getPermissions().getPermission(keyNba)
    }

    fun isOptPermissionAccepted(): Boolean {
        return FunnelConnectSDK.getPermissions().getPermission(keyOpt)
    }

    fun fcStartService(
        context: Activity,
        action: (() -> Unit)? = null
    ) {
        FunnelConnectSDK
            .startService(null, SharedPreferenceUtils.getMartechpass(FCApplication.instance)?.let { PassQuery("martechpass", it) }, fcNotificationsName, notificationsVersion, {
                updateFCData(it)
                SwrveSDK.start(context, FunnelConnectSDK.getUMID())
                SwrveGeoSDK.start(context)
                isFunnelConnectStarted = true
                action?.invoke()
            },
                {
                    Log.d("error:", "FunnelConnectSDK.startService")
                })
    }

    fun updateFCData(info: String) {
        infoResponse = info
        attributes = getAttributesFromInfo()
        umid = FunnelConnectSDK.getUMID()
        refreshCeltraAd?.invoke()
    }
}

