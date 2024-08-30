package com.teavaro.ecommDemoApp.ui.emails

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentEmailsBinding


class EmailsFragment : Fragment() {

    private var _binding: FragmentEmailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        TrackUtils.impression("emails_view")

        _binding = FragmentEmailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.identClick.setOnClickListener {
            Store.userId?.let {
                Store.getClickIdentLink(requireContext())?.let {
                    shareLink("Ident click link from AndroidDemoApp", it)
                }
            }
        }

        binding.abandonedCart.setOnClickListener {
            val acId = Store.getAbCartId()
            val abandonedCartLink = "http://www.teavarodemoapp.com?ab_cart_id=$acId"
            shareLink("Abandoned cart link from AndroidDemoApp", abandonedCartLink)
        }
        return root
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