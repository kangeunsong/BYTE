package com.example.open__sw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.open__sw.databinding.ActivityMypageBinding
import com.google.firebase.auth.FirebaseAuth

class MypageActivity :  AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Toolbar 설정
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //프래그먼트 연결
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MypageFragment())
                .commit()
        }

        binding.back.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slidein_horizon, R.anim.slideout_horizon)
        }

        binding.logoutIV.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        auth.signOut()

        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("autoLogin")
            remove("email")
            remove("password")
            apply()
        }

        val intent = Intent(this, ActivitySplashLogin::class.java)
        startActivity(intent)
        finish()
    }
}
