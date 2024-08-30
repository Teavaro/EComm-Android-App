package com.teavaro.ecommDemoApp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.teavaro.ecommDemoApp.R
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.utils.SharedPreferenceUtils
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        TrackUtils.impression("settings_view")
        Store.section = "settings"

        if(SharedPreferenceUtils.isLogin(requireContext())){
            binding.logOut.visibility = Button.VISIBLE
            binding.logIn.visibility = Button.GONE
        }
        else{
            binding.logOut.visibility = Button.GONE
            binding.logIn.visibility = Button.VISIBLE
        }

        binding.clearData.setOnClickListener{
            clearData()
            Store.navigateAction?.invoke(R.id.navigation_settings)
            Toast.makeText(requireContext(), "Data cleared!", Toast.LENGTH_LONG).show()
        }

        binding.notifications.setOnClickListener {
            Store.navigateAction?.invoke(R.id.navigation_notifications)
        }

        binding.ids.setOnClickListener {
            Store.navigateAction?.invoke(R.id.navigation_ids)
        }

        binding.email.setOnClickListener {
            Store.navigateAction?.invoke(R.id.navigation_email)
        }

        binding.logIn.setOnClickListener {
            Store.navigateAction?.invoke(R.id.navigation_login)
        }

        binding.logOut.setOnClickListener {
            TrackUtils.click("dialog_logout")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout confirmation")
                .setMessage("Do you want to proceed with the logout?")
                .setNegativeButton("Cancel")  {_,_ ->
                    TrackUtils.click("cancel_logout")
                }
                .setPositiveButton("Proceed") { _, _ ->
                    TrackUtils.click("proceed_logout")
                    SharedPreferenceUtils.setLogin(requireContext(), false)
                    SharedPreferenceUtils.setUserId(requireContext(), "")
                    Store.userId = ""
                    Store.navigateAction?.invoke(R.id.navigation_settings)
                    Toast.makeText(requireContext(), "Logout success!", Toast.LENGTH_SHORT).show()
                }
                .create().show()
        }

        binding.consentManagement.setOnClickListener {
            Store.showPermissionsDialog(requireActivity(), parentFragmentManager)
        }

        binding.stubMode.isChecked = SharedPreferenceUtils.getStubToken(requireContext()) != null
        binding.stubMode.setOnCheckedChangeListener { _, isStub ->
            if(isStub) {
                SharedPreferenceUtils.setStubToken(requireContext(), Store.stubToken)
                Store.showUtiqConsent(requireActivity(), parentFragmentManager)
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clearData(){
        Store.clearData(requireContext())
    }
}