package com.teavaro.ecommDemoApp.ui.wishlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.teavaro.ecommDemoApp.R
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.room.ItemEntity
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.ItemWishBinding
import com.teavaro.ecommDemoApp.ui.ItemDescriptionDialogFragment

class WishAdapter(context: Context,
                  private val listItems: List<ItemEntity>) :
    ArrayAdapter<ItemEntity>(context, 0, listItems) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView != null)
            ItemWishBinding.bind(convertView)
        else
            ItemWishBinding.inflate(LayoutInflater.from(context), parent, false)
        val item = listItems[position]
        binding.txtTitle.text = item.title
        binding.txtPrice.text = "$${item.price}"
        if(item.isInStock)
            binding.inStock.visibility = ImageView.VISIBLE
        else {
            binding.noStock.visibility = ImageView.VISIBLE
            binding.btnAddToCart.visibility = Button.GONE
        }
        binding.btnRemove.setOnClickListener {
            TrackUtils.click( "remove_from_wishlist" + "," + item.data)
            Store.removeItemFromWish(item.itemId)
            parent.findNavController().navigate(R.id.navigation_wishlist)
            Toast.makeText(context, "Product removed!", Toast.LENGTH_SHORT).show()
        }
        binding.btnAddToCart.setOnClickListener {
            TrackUtils.click( "add_to_cart" + "," + item.data)
            Store.addItemToCart(item.itemId)
            Toast.makeText(context, "Product added!", Toast.LENGTH_SHORT).show()
        }
        binding.txtTitle.setOnClickListener{
            ItemDescriptionDialogFragment.open((context as AppCompatActivity).supportFragmentManager, item, {
                TrackUtils.click( "add_to_cart" + "," + item.data)
                Store.addItemToCart(item.itemId)
            })
        }
        return binding.root
    }
}