package com.example.open__sw

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import com.example.open__sw.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth


class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)

        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.joinET.setOnClickListener {
            val email = binding.emailET.text.toString().trim()
            val password = binding.passwordET.text.toString().trim()
            val rePassword = binding.repasswordET.text.toString().trim()

            if (password == rePassword) {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    signUpUser(email, password)
                } else {
                    Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, navigate to login page
                    Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // If sign up fails, display a message to the user.
                    Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}