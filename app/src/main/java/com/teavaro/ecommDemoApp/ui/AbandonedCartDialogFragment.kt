package com.teavaro.ecommDemoApp.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.teavaro.ecommDemoApp.R
import com.teavaro.ecommDemoApp.core.room.ItemEntity
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentAbandonedCartDialogBinding
import com.teavaro.ecommDemoApp.ui.shop.ShopAdapter

class AbandonedCartDialogFragment(private var items: List<ItemEntity>) : DialogFragment(R.layout.fragment_abandoned_cart_dialog) {

    private var _binding: FragmentAbandonedCartDialogBinding? = null
    private val binding get() = _binding!!
    private var addItemsAction: ((List<ItemEntity>) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        TrackUtils.impression("abandoned_cart_view")
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAbandonedCartDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnClose.setOnClickListener {
            this.dismiss()
        }

        binding.btnAddItemsToCart.setOnClickListener {
            addItemsAction?.let {
                TrackUtils.click("add_abandoned_items_to_cart")
                it.invoke(items)
            }
            this.dismiss()
        }

        items.let {
            val shopAdapter = ShopAdapter(requireContext(), it)
            var textView = TextView(requireContext())
            for (pos in 0..it.lastIndex){
                textView.text = pos.toString()
                binding.listItems.addView(shopAdapter.getView(pos, view, root as ViewGroup))
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun open(
            fm: FragmentManager,
            items: List<ItemEntity>,
            addItemsAction: ((List<ItemEntity>) -> Unit)
        ) {
            val dialogFragment = AbandonedCartDialogFragment(items)
            dialogFragment.addItemsAction = addItemsAction
            dialogFragment.show(fm, null)
        }
    }
}