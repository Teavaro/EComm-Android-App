package com.teavaro.ecommDemoApp.ui.cart

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.teavaro.ecommDemoApp.R
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.utils.StringUtils
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentCartBinding

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cartViewModel =
            ViewModelProvider(this).get(CartViewModel::class.java)

        TrackUtils.impression("cart_view")

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val list = Store.getItemsCart()
        val cartAdapter = CartAdapter(requireContext(), list)
        for (pos in 0..list.lastIndex){
            container?.let {
                binding.listItems.addView(cartAdapter.getView(pos, view, it), 0)
            }
        }

        binding.txtTotal.text = "$${Store.getTotalPriceCart()} in total"

        if(cartAdapter.count == 0)
            binding.txtEmpty.visibility = LinearLayout.VISIBLE
        else
            binding.layTotal.visibility = LinearLayout.VISIBLE

        binding.btnCheckout.setOnClickListener {
            TrackUtils.impression("dialog_checkout")
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Checkout confirmation")
                .setMessage("Do you want to proceed with checkout?")
                .setNegativeButton("Cancel")  {_,_ ->
                    TrackUtils.click("cancel_checkout")
                }
                .setPositiveButton("Proceed") { _, _ ->
                    TrackUtils.click("proceed_checkout")
                    Store.removeAllCartItems()
                    root.findNavController().navigate(R.id.navigation_cart)
                    Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
                }
                .create().show()
        }

        binding.btnClearCart.setOnClickListener {
            TrackUtils.click("clear_cart")
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Clear confirmation")
                .setMessage("Do you want to clear the cart?")
                .setNegativeButton("Cancel")  {_,_ ->
                    TrackUtils.click("clear_cart_cancel")
                }
                .setPositiveButton("Proceed") { _, _ ->
                    val acId = Store.addAbandonedCart(Store.getItemsCart())
                    StringUtils.setClipboard(requireContext(), "http://www.teavarodemoapp.com?ab_cart_id=$acId")
                    TrackUtils.click("clear_cart_confirm")
                    Store.removeAllCartItems()
                    root.findNavController().navigate(R.id.navigation_cart)
                    Toast.makeText(context, "Cart cleared!", Toast.LENGTH_SHORT).show()
                }
                .create().show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}