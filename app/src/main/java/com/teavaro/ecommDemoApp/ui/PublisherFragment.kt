package com.teavaro.ecommDemoApp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentPublisherBinding

class PublisherFragment : Fragment() {

    private var _binding: FragmentPublisherBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        TrackUtils.impression("publisher_view")

        _binding = FragmentPublisherBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.domStorageEnabled = true
        binding.webView.loadUrl("https://publisher-demo.media?utiq_stub=54fc04fcfdee14ad8268d59b3303baf8854bc4d27308a2668dd47ed2a8a54cae")

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}