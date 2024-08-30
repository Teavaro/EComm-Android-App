package com.teavaro.ecommDemoApp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.teavaro.ecommDemoApp.core.utils.PushNotification
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentNotificationsBinding
import com.teavaro.funnelConnect.main.FunnelConnectSDK


class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        TrackUtils.impression("notifications_view")

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.cashews.setOnClickListener {
            FunnelConnectSDK.getUMID()?.let {
                PushNotification.sendCashews(it){
                    Toast.makeText(requireContext(), "Notification sent!", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.shop.setOnClickListener {
            FunnelConnectSDK.getUMID()?.let {
                PushNotification.sendShop(it){
                    Toast.makeText(requireContext(), "Notification sent!", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.abandonedCart.setOnClickListener {
            FunnelConnectSDK.getUMID()?.let {
                PushNotification.sendAbandonedCart(it){
                    Toast.makeText(requireContext(), "Notification sent!", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.identClick.setOnClickListener {
            FunnelConnectSDK.getUMID()?.let {
                PushNotification.sendIdentClick(it){
                    Toast.makeText(requireContext(), "Notification sent!", Toast.LENGTH_LONG).show()
                }
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}