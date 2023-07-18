package com.teavaro.ecommDemoApp.ui.ids

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentEmailsBinding
import com.teavaro.ecommDemoApp.databinding.FragmentIdsBinding
import com.teavaro.ecommDemoApp.ui.notifications.NotificationsViewModel
import com.teavaro.funnelConnect.initializer.FunnelConnectSDK
import com.teavaro.utiqTech.initializer.UTIQ


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
        val loginViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

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
        binding.txtUserid.text = FunnelConnectSDK.getUserId()
        binding.txtUmid.text = FunnelConnectSDK.getUMID()
        binding.txtAtid.text = Store.atid
        binding.txtMtid.text = Store.mtid
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