package com.teavaro.ecommDemoApp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.teavaro.ecommDemoApp.FCApplication
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.utils.SharedPreferenceUtils
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentIdsBinding


class IdsFragment : Fragment() {

    private var _binding: FragmentIdsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        TrackUtils.impression("ids_view")

        _binding = FragmentIdsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        refreshIds()

        binding.btnRefresh.setOnClickListener {
            refreshIds()
        }
        return root
    }

    fun refreshIds(){
        binding.txtUserid.text = Store.userId
        binding.txtUmid.text = Store.umid
        binding.txtAtid.text = Store.atid
        binding.txtMtid.text = SharedPreferenceUtils.getMartechpass(FCApplication.instance)
        binding.txtInfo.text = Store.attributes
    }

    fun shareLink(subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, "Send Email using:"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}