package com.teavaro.ecommDemoApp.ui.shop

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentShopBinding

class ShopFragment : Fragment() {

    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val shopViewModel =
            ViewModelProvider(this).get(ShopViewModel::class.java)

        TrackUtils.impression("shop_view")
        Store.section = "shop"

        _binding = FragmentShopBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var list = Store.getItems()
        val shopAdapter = ShopAdapter(requireContext(), list)
        for (pos in 0..list.lastIndex){
            binding.listItems.addView(shopAdapter.getView(pos, view, container!!))
        }
        val btnSeeMore = Button(context).apply {
            text = "See More Products"
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16.dpToPx(context)
                marginEnd = 16.dpToPx(context)
            }
            //setBackgroundColor(ContextCompat.getColor(context, R.color.lightGray))
            //setTextColor(ContextCompat.getColor(context, R.color.darkGray))
            //textStyle = Typeface.BOLD
            isAllCaps = true
            gravity = Gravity.CENTER
            //setPadding(0, 12.dpToPx(context), 0, 12.dpToPx(context))
            setOnClickListener {
                val url = Store.getClickIdentLink(context)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        }
        binding.listItems.addView(btnSeeMore)

        return root
    }

    // Extension function to convert dp to px
    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}