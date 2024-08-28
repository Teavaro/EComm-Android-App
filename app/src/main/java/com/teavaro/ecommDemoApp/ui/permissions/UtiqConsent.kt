package com.teavaro.ecommDemoApp.ui.permissions

/**
 * A simple [Fragment] subclass.
 * Use the [UtiqConsent.newInstance] factory method to
 * create an instance of this fragment.
 */

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.teavaro.ecommDemoApp.R
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentUtiqConsentBinding
import com.teavaro.ecommDemoApp.viewBinding

class UtiqConsent : DialogFragment(R.layout.fragment_utiq_consent) {

    private val binding by viewBinding(FragmentUtiqConsentBinding::bind)
    private var action: ((consent: Boolean) -> Unit)? =
        null

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

        binding.acceptButton.setOnClickListener {
            this.action?.invoke(true)
            this.dismiss()
        }
        binding.cancelButton.setOnClickListener {
            this.action?.invoke(false)
            this.dismiss()
        }
        binding.btnClose.setOnClickListener {
            this.dismiss()
        }
    }

    companion object {
        fun open(
            fm: FragmentManager,
            action: (omPermissionAccepted: Boolean) -> Unit = { _ -> }
        ) {
            val dialogFragment = UtiqConsent()
            dialogFragment.action = action
            dialogFragment.show(fm, null)
        }
    }
}