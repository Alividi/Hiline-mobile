package com.example.hiline.admin

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
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
import com.example.hiline.PrefManager
import com.example.hiline.R
import com.example.hiline.user.GantiPasswordActivity
import com.example.hiline.user.MainUserActivity
import com.example.hiline.user.ProfileUserInfoActivity
import com.example.hiline.user.RiwayatPengaduanActivity
import com.squareup.picasso.Picasso

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

        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvUsername: TextView = view.findViewById(R.id.tvUsername)
        val btnProfile: ImageView = view.findViewById(R.id.btnProfile)
        val btnGPw: ImageView = view.findViewById(R.id.btnGPw)
        val ivPP: ImageView = view.findViewById(R.id.ivPP)
        clLogout = view.findViewById(R.id.clLogout)

        val imgUri = prefManager.getPImg()
        Picasso.get().invalidate(imgUri)
        Picasso.get().load(imgUri).into(ivPP)

        tvNama.text = prefManager.getNama()
        tvUsername.text = "@"+prefManager.getUsername()

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