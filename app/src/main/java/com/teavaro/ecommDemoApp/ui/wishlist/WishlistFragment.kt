package com.teavaro.ecommDemoApp.ui.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentWishlistBinding

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val wishlistViewModel =
            ViewModelProvider(this).get(WishlistViewModel::class.java)

        TrackUtils.impression("wishlist_view")
        Store.section = "wishlist"

        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var list = Store.getItemsWish()
        val wishAdapter = WishAdapter(requireContext(), list)
        for (pos in 0..list.lastIndex){
            binding.listItems.addView(wishAdapter.getView(pos, view, container!!))
        }

        if(wishAdapter.count == 0)
            binding.txtEmpty.visibility = LinearLayout.VISIBLE

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}