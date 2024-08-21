package com.example.open__sw

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.open__sw.databinding.ActivitySplashLoginBinding
import com.google.firebase.auth.FirebaseAuth
//AnimationActivity(TransitionMode.HORIZON)
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageView


class ActivitySplashLogin :AppCompatActivity(){
    private lateinit var binding: ActivitySplashLoginBinding
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set up the click listener using view binding
        binding.signupET.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            overridePendingTransition(R.anim.slidein_horizon, R.anim.slideout_horizon)
            startActivity(intent)
        }

        // Set up the click listener for login
        binding.loginET.setOnClickListener {
            val email = binding.emailET.text.toString().trim()
            val password = binding.passwordET.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.togglePasswordVisibility.setOnClickListener {
            if (isPasswordVisible) {
                // Hide password
                binding.passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.togglePasswordVisibility.setImageResource(R.drawable.visibility_off_24px) // 감은 눈 아이콘
            } else {
                // Show password
                binding.passwordET.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.togglePasswordVisibility.setImageResource(R.drawable.visibility_24px) // 열린 눈 아이콘
            }
            isPasswordVisible = !isPasswordVisible
            binding.passwordET.setSelection(binding.passwordET.text.length)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login success
                    val intent = Intent(this, MainActivity::class.java)
                    overridePendingTransition(R.anim.slidein_vertical, R.anim.slideout_vertical)
                    startActivity(intent)
                    finish()
                } else {
                    // If login fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    /* 외부 화면 터치 시 키보드 사라짐 */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}
