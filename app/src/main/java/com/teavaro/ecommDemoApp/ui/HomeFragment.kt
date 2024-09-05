package com.teavaro.ecommDemoApp.ui

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.teavaro.ecommDemoApp.R
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        TrackUtils.impression("home_view")
        Store.section = "home"

        refreshOfferItems(container!!)

        binding.btnExplore.setOnClickListener {
            TrackUtils.click("explore")
            Store.navigateAction?.invoke(R.id.navigation_shop)
        }

        Store.refreshCeltraAd = {
            if (Store.section == "home")
                loadAd()
        }
        return root
    }

    private fun refreshOfferItems(container: ViewGroup) {
        var list = Store.listOffers
        val shopAdapter = ShopAdapter(requireContext(), list)
        shopAdapter.notifyDataSetChanged()
        for (pos in 0..list.lastIndex) {
            binding.listItems.addView(shopAdapter.getView(pos, view, container))
        }
        val btnSeeMore = Button(context).apply {
            text = "Check our recipes"
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16.dpToPx(context)
                marginEnd = 16.dpToPx(context)
            }
            isAllCaps = true
            gravity = Gravity.CENTER
            setOnClickListener {
                TrackUtils.click("publisher")
                //binding.root.findNavController().navigate(R.id.navigation_publisher)
                Store.navigateAction?.invoke(R.id.navigation_publisher)
            }
        }
        binding.listItems.addView(btnSeeMore)
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** Instantiate the interface and set the context  */
    class WebAppInterface(
        private var context: Context,
        private var supportFragmentManager: FragmentManager
    ) {
        /** Show a toast from the web page  */
        @JavascriptInterface
        fun postMessage(data: String) {
            Store.processCelraAction(context, data, supportFragmentManager)
        }
    }

    private fun loadAd() {
//        if(Store.webView == null) {
        binding.webView.removeAllViews()
        var webView = WebView(requireContext())
        val html = Store.getBanner()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                Store.webView = webView
            }
        }
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.addJavascriptInterface(
            WebAppInterface(
                requireContext(),
                parentFragmentManager
            ), "Android"
        )
        webView.loadDataWithBaseURL(
            "http://www.example.com/",
            html,
            "text/html",
            "UTF-8",
            null
        )
        binding.webView.addView(webView)
//        }
//        else{
//            Store.webView?.let {
//                binding.webView.addView(it)
//            }
//        }
    }

    override fun onPause() {
        super.onPause()
        binding.webView.removeAllViews()
    }

    override fun onResume() {
        super.onResume()
        if (Store.infoResponse != null)
            loadAd()
    }
}


