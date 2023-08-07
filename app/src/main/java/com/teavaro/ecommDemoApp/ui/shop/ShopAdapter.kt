package com.teavaro.ecommDemoApp.ui.shop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.teavaro.ecommDemoApp.R
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.room.ItemEntity
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.ItemShopBinding
import com.teavaro.ecommDemoApp.ui.ItemDescriptionDialogFragment

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
        binding.txtPrice.text = "$${item.price}"
        val imgId: Int = parent.resources.getIdentifier(item.picture, "drawable", "com.teavaro.ecommDemoApp")
        binding.imgPicture.setImageResource(imgId)
        if(!item.isInStock) {
            binding.btnAddToCart.visibility = Button.GONE
            binding.outOfStock.visibility = TextView.VISIBLE
        }
        binding.btnAddToCart.setOnClickListener {
            val events = mapOf(TrackUtils.CLICK to "add_item_to_cart", "item_id" to item.itemId.toString())
            TrackUtils.events(events)
            Store.addItemToCart(item.itemId)
            Toast.makeText(context, "Product added!", Toast.LENGTH_SHORT).show()
        }
        binding.btnAddToWish.let { imageView ->
            setWishPicture(imageView, item)
            imageView.setOnClickListener {
                if(!item.isInWish) {
                    val events = mapOf(TrackUtils.IMPRESSION to "add_item_to_wish", "item_id" to item.itemId.toString())
                    TrackUtils.events(events)
                    Store.addItemToWish(item.itemId)
                    item.isInWish = true
                    Toast.makeText(context, "Product added!", Toast.LENGTH_SHORT).show()
                }
                else {
                    val events = mapOf(TrackUtils.IMPRESSION to "remove_item_from_wish", "item_id" to item.itemId.toString())
                    TrackUtils.events(events)
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