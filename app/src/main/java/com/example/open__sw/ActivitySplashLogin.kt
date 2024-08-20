package com.example.open__sw

import android.content.Intent
import android.os.Bundle
import com.example.open__sw.databinding.ActivitySplashLoginBinding

class ActivitySplashLogin : AnimationActivity(TransitionMode.HORIZON) {
    private lateinit var binding: ActivitySplashLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the click listener using view binding
        binding.signupET.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}
