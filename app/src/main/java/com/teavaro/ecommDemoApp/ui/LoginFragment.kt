package com.teavaro.ecommDemoApp.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.swrve.sdk.SwrveSDK
import com.teavaro.ecommDemoApp.R
import com.teavaro.ecommDemoApp.core.Store
import com.teavaro.ecommDemoApp.core.utils.SharedPreferenceUtils
import com.teavaro.ecommDemoApp.core.utils.TrackUtils
import com.teavaro.ecommDemoApp.databinding.FragmentLoginBinding
import com.teavaro.funnelConnect.data.models.FCUser
import com.teavaro.funnelConnect.main.FunnelConnectSDK
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        TrackUtils.impression("login_view")

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnLogin.setOnClickListener {
            TrackUtils.click("login")
            if (!binding.edtEmail.text.isNullOrEmpty() && !binding.edtPassword.text.isNullOrEmpty()) {
                if (FunnelConnectSDK.isInitialized() && Store.isNbaPermissionAccepted()) {
                    binding.btnLogin.text = "Processing..."
                    binding.btnLogin.isEnabled = false
                    binding.edtEmail.text.toString().encryptCBC()?.let {userId ->
                        FunnelConnectSDK.setUser(FCUser("enemail", userId), {
                            Store.infoResponse = it
                            Store.updateFCData(it)
                            Store.umid = FunnelConnectSDK.getUMID()
                            Store.userId = userId
                            SharedPreferenceUtils.setUserId(requireContext(), userId)
                            SharedPreferenceUtils.setLogin(requireContext(), true)
                            root.findNavController().navigate(R.id.navigation_settings)
                            SwrveSDK.start(parentFragment?.activity, Store.umid)
                            Toast.makeText(context, "Login success!", Toast.LENGTH_SHORT).show()
                        },{
                            binding.btnLogin.text = "LOG IN"
                            binding.btnLogin.isEnabled = true
                        })
                    }
                }
                else{
                    Store.showPermissionsDialog(requireActivity(), parentFragmentManager)
                }
            } else
                Toast.makeText(context, "Need to insert email and password!", Toast.LENGTH_SHORT)
                    .show()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun String.encryptCBC(): String? {
        val keyBytes = byteArrayOf(
            46,
            -101,
            23,
            102,
            -56,
            -64,
            -112,
            11,
            98,
            39,
            -111,
            33,
            -102,
            19,
            -83,
            -92,
            24,
            89,
            -16,
            90,
            7,
            12,
            -108,
            -109,
            -123,
            -11,
            -19,
            -50,
            -79,
            56,
            5,
            -126
        )
        val key = SecretKeySpec(keyBytes, "AES")
        val ivBytes = ByteArray(16) // 16 bytes filled with zeroes

        val iv = IvParameterSpec(ivBytes)

        val encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        encryptCipher.init(Cipher.ENCRYPT_MODE, key, iv)

        return encrypt(encryptCipher, this)
    }

    @Throws(IllegalBlockSizeException::class, BadPaddingException::class)
    fun encrypt(encryptCipher: Cipher, plaintext: String): String? {
        val cipherTextBytes: ByteArray =
            encryptCipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getUrlEncoder().encodeToString(cipherTextBytes)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }
}