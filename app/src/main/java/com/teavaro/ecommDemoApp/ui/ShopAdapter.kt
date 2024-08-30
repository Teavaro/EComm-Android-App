package com.teavaro.ecommDemoApp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.teavaro.ecommDemoApp.R
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.room.ItemEntity
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.ItemShopBinding

class ShopAdapter(context: Context,
                  private val listItems: List<ItemEntity>) :
    ArrayAdapter<ItemEntity>(context, 0, listItems) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView != null)
            ItemShopBinding.bind(convertView)
        else
            ItemShopBinding.inflate(LayoutInflater.from(context), parent, false)
        val item = listItems[position]
        binding.txtTitle.text = item.title
        binding.txtPrice.text = buildString {
        append("$")
        append(item.price)
    }
        val imgId: Int = parent.resources.getIdentifier(item.picture, "drawable", "com.teavaro.ecommDemoApp")
        binding.imgPicture.setImageResource(imgId)
        if(!item.isInStock) {
            binding.btnAddToCart.visibility = Button.GONE
            binding.outOfStock.visibility = TextView.VISIBLE
        }
        binding.btnAddToCart.setOnClickListener {
            TrackUtils.click("add_to_cart"  + "," + item.data)
            Store.addItemToCart(item.itemId)
            Toast.makeText(context, "Product added!", Toast.LENGTH_SHORT).show()
        }
        binding.btnAddToWish.let { imageView ->
            setWishPicture(imageView, item)
            imageView.setOnClickListener {
                if(!item.isInWish) {
                    TrackUtils.click("add_to_wishlist"  + "," + item.data)
                    Store.addItemToWish(item.itemId)
                    item.isInWish = true
                    Toast.makeText(context, "Product added!", Toast.LENGTH_SHORT).show()
                }
                else {
                    TrackUtils.click("remove_from_wishlist"  + "," + item.data)
                    Store.removeItemFromWish(item.itemId)
                    item.isInWish = false
                    Toast.makeText(context, "Product removed!", Toast.LENGTH_SHORT).show()
                }
                setWishPicture(imageView, item)
            }
        }
        binding.imgPicture.setOnClickListener{
            if(item.isInWish) {
                ItemDescriptionDialogFragment.open(
                    (context as AppCompatActivity).supportFragmentManager,
                    item,
                    {
                        Store.addItemToCart(item.itemId)
                    })
            }
            else{
                ItemDescriptionDialogFragment.open((context as AppCompatActivity).supportFragmentManager, item, {
                    Store.addItemToCart(item.itemId)
                },{
                    Store.addItemToWish(item.itemId)
                })
            }
        }
        return binding.root
    }

    private fun setWishPicture(imageView: ImageView, item: ItemEntity) {
        if(item.isInWish)
            imageView.setImageResource(R.drawable.ic_wishlist_red_24dp)
        else
            imageView.setImageResource(R.drawable.ic_wishlist_black_24dp)
    }
}