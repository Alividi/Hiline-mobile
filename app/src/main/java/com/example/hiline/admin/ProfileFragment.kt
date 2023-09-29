package com.example.hiline.admin

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.hiline.MainActivity
import com.example.hiline.service.PrefManager
import com.example.hiline.R
import com.example.hiline.admin.profile.ProfileAdminInfoActivity
import com.example.hiline.GantiPasswordActivity
import com.example.hiline.response.CurrentResponse
import com.example.hiline.service.AuthService
import com.example.hiline.service.Retro
import com.example.hiline.service.TokenAuthenticator
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var prefManager: PrefManager
    private lateinit var clLogout: ConstraintLayout
    private lateinit var tvNama: TextView
    private lateinit var ivPP: ImageView
    private lateinit var tvUsername: TextView
    private var name: String = ""
    private var username: String =""
    private var pImg: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager(requireContext())

        tvNama = view.findViewById(R.id.tvNama)
        tvUsername = view.findViewById(R.id.tvUsername)
        val btnProfile: ImageView = view.findViewById(R.id.btnProfile)
        val btnGPw: ImageView = view.findViewById(R.id.btnGPw)
        ivPP = view.findViewById(R.id.ivPP)
        clLogout = view.findViewById(R.id.clLogout)

        currentUser()

        btnProfile.setOnClickListener {
            val intent = Intent(context, ProfileAdminInfoActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        btnGPw.setOnClickListener {
            val intent = Intent(context, GantiPasswordActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        clLogout.setOnClickListener {
            showLogoutDialog()
        }

    }

    fun currentUser(){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getAuthUrl(okHttpClient)
        val service = retrofit.create(AuthService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.currentUser(aToken)

        call.enqueue(object : Callback<CurrentResponse> {
            override fun onResponse(call: Call<CurrentResponse>, response: Response<CurrentResponse>) {
                if (response.isSuccessful) {
                    //val gson = GsonBuilder().setPrettyPrinting().create()
                    //val responseBody = gson.toJson(response.body())
                    //Log.e("Body: ", responseBody)
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)

                    name = response.body()?.data?.user?.name.toString()
                    username = response.body()?.data?.user?.username.toString()
                    pImg = response.body()?.data?.user?.image.toString()

                    tvNama.text = name
                    tvUsername.text = "@${username}"
                    val imgUri = pImg
                    if (imgUri.isNullOrEmpty()){

                    }else{
                        Picasso.get().invalidate(imgUri)
                        Picasso.get().load(imgUri).into(ivPP)
                    }

                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                    Log.e("Status: ", response.body()?.status.toString())
                }
            }
            override fun onFailure(call: Call<CurrentResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }

    fun logout(){
        val tokenAuthenticator = TokenAuthenticator(prefManager)
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .build()
        val retrofit = Retro().getAuthUrl(okHttpClient)
        val service = retrofit.create(AuthService::class.java)
        val aToken = prefManager.getAccessToken()

        val call = service.logout(aToken)

        call.enqueue(object : Callback<CurrentResponse> {
            override fun onResponse(call: Call<CurrentResponse>, response: Response<CurrentResponse>) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val responseBody = gson.toJson(response.body())
                    Log.e("Body: ", responseBody)
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                } else {
                    val rawResponse = response.raw().toString()
                    Log.e("Raw Response: ", rawResponse)
                    Log.e("Error: ", "unsuccessful response")
                }
            }
            override fun onFailure(call: Call<CurrentResponse>, t: Throwable) {
                Log.e("Network API Error: ", t.message.toString())
                Log.e("Error: ","network or API call failure")
            }
        })
    }

    fun showLogoutDialog() {
        val dialogLogout = Dialog(requireContext(), R.style.MaterialDialogSheet)
        dialogLogout.setContentView(R.layout.dialog_logout)
        dialogLogout.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialogLogout.window?.setGravity(Gravity.BOTTOM)
        dialogLogout.window?.attributes?.windowAnimations = R.style.MaterialDialogSheetAnimation
        dialogLogout.show()

        val btnLogoutDialog = dialogLogout.findViewById<AppCompatButton>(R.id.btnLogoutDialog)
        val btnKembaliDialog = dialogLogout.findViewById<TextView>(R.id.btnKembaliDialog)

        btnLogoutDialog.setOnClickListener {
            logout()
            prefManager.removeData()
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            dialogLogout.dismiss()
            activity?.finish()
        }

        btnKembaliDialog.setOnClickListener {
            dialogLogout.dismiss()
        }
    }
}