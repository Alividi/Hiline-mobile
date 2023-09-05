package com.example.hiline.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.hiline.R
import com.example.hiline.databinding.ActivityMainBinding
import me.ibrahimsn.lib.SmoothBottomBar

class MainAdminActivity : AppCompatActivity() {

    private lateinit var bottomBar: SmoothBottomBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)

        bottomBar = findViewById(R.id.bottomNavbar)

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flContainer, HomeFragment())
        fragmentTransaction.commit()

        bottomBar.setOnItemSelectedListener {
            val fragmentManager: FragmentManager = supportFragmentManager
            val newFragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

            if (it == 0){
                newFragmentTransaction.replace(R.id.flContainer, HomeFragment())
                newFragmentTransaction.commit()
            }
            if (it == 1){
                newFragmentTransaction.replace(R.id.flContainer, ChatFragment())
                newFragmentTransaction.commit()
            }
            if (it == 2){
                newFragmentTransaction.replace(R.id.flContainer, ProfileFragment())
                newFragmentTransaction.commit()
            }
        }

    }
}