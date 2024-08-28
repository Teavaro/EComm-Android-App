package com.teavaro.ecommDemoApp.ui.cart

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.teavaro.ecommDemoApp.R
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.room.ItemEntity
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.ItemCartBinding
import com.teavaro.ecommDemoApp.ui.ItemDescriptionDialogFragment

class CartAdapter(context: Context, private val listItems: List<ItemEntity>) :
    ArrayAdapter<ItemEntity>(context, 0, listItems) {

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView != null)
            ItemCartBinding.bind(convertView)
        else
            ItemCartBinding.inflate(LayoutInflater.from(context), parent, false)
        val item = listItems[position]
        val subTotal: Float = item.price * item.countInCart
        binding.txtTitle.text = item.title
        binding.txtPrice.text = item.price.toString()
        binding.txtCount.text = item.countInCart.toString()
        binding.txtSubTotal.text = "$$subTotal / piece"
        binding.btnRemove.setOnClickListener {
            TrackUtils.click("remove_item_from_cart" + "," + item.data)
            Store.removeItemFromCart(item.itemId)
            parent.findNavController().navigate(R.id.navigation_cart)
            Toast.makeText(context, "Product removed!", Toast.LENGTH_SHORT).show()
        }
        binding.txtTitle.setOnClickListener{
            ItemDescriptionDialogFragment.open((context as AppCompatActivity).supportFragmentManager, item)
        }
        return binding.root
    }
}