package com.teavaro.ecommDemoApp.ui.permissions

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.teavaro.ecommDemoApp.R
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentFPermissionsConsentBinding
import com.teavaro.ecommDemoApp.viewBinding
import com.teavaro.funnelConnect.main.FunnelConnectSDK

class PermissionConsentDialogFragment : DialogFragment(R.layout.fragment_f_permissions_consent) {

    private val binding by viewBinding(FragmentFPermissionsConsentBinding::bind)
    private var acceptAction: ((omPermissionAccepted: Boolean, optPermissionAccepted: Boolean, nbaPermissionAccepted: Boolean) -> Unit)? =
        null
    private var rejectAction: (() -> Unit)? = null

//    override fun getTheme() = R.style.FullScreenDimmedDialogFragment

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        TrackUtils.impression("permission_view")
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialPresets()
        binding.saveButton.setOnClickListener {
            val om = binding.swCookies
            val opt = binding.swNetwork
            val nba = binding.swPersonal
            this.acceptAction?.invoke(om.isChecked, opt.isChecked, nba.isChecked)
            this.dismiss()
        }
        binding.acceptButton.setOnClickListener {
            this.acceptAction?.invoke(true, true, true)
            this.dismiss()
        }
        binding.cancelButton.setOnClickListener {
            this.rejectAction?.invoke()
            this.dismiss()
        }
        binding.btnClose.setOnClickListener {
            this.dismiss()
        }
    }

    private fun initialPresets() {
        FunnelConnectSDK.getPermissions().let {
            binding.swCookies.isChecked = it.getPermission(Store.keyOm)
            binding.swNetwork.isChecked = it.getPermission(Store.keyOpt)
            binding.swPersonal.isChecked = it.getPermission(Store.keyNba)
        }
    }

    companion object {

        fun open(
            fm: FragmentManager,
            acceptAction: (omPermissionAccepted: Boolean, optPermissionAccepted: Boolean, nbaPermissionAccepted: Boolean) -> Unit = { _, _, _ -> },
            rejectAction: (() -> Unit)
        ) {
            val dialogFragment = PermissionConsentDialogFragment()
            dialogFragment.acceptAction = acceptAction
            dialogFragment.rejectAction = rejectAction
            dialogFragment.show(fm, null)
        }
    }
}