package com.example.open__sw

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.MotionEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore


// Fragment에서의 코드
class ProfileEditFragment : Fragment() {

    private lateinit var usernameET: EditText
    private lateinit var passwordET: EditText
    private lateinit var editUsernameTV: TextView
    private lateinit var editUserpwTV: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_edit, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        usernameET = view.findViewById(R.id.usernameET)
        passwordET = view.findViewById(R.id.passwordET)
        editUsernameTV = view.findViewById(R.id.editUsernameTV)
        editUserpwTV = view.findViewById(R.id.editUserpwTV)

        loadUserInfo()

        editUsernameTV.setOnClickListener {
            enableEditText(usernameET)
        }

        editUserpwTV.setOnClickListener {
            enableEditText(passwordET)
        }

//        val button = view.findViewById<Button>(R.id.saveBtn)
//        button.setOnClickListener { // 저장기능 구현하기
//
//            val fragment = ProfileEditFragment() // 이동하려는 프래그먼트
//            val fragmentManager = parentFragmentManager
//            val fragmentTransaction = fragmentManager.beginTransaction()
//
//            fragmentTransaction.replace(R.id.fragment_container, fragment)
//            fragmentTransaction.addToBackStack(null) // 뒤로가기 백스택에 추가
//            fragmentTransaction.commit()
//        }

        val button = view.findViewById<Button>(R.id.saveBtn)
        button.setOnClickListener {
            saveUserInfo()
        }

        view.setOnTouchListener { _, _ ->
            hideKeyboardAndClearFocus()
            false
        }

        return view
    }

    private fun hideKeyboardAndClearFocus() {
        view?.let { v ->
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
        usernameET.clearFocus()
        passwordET.clearFocus()
    }

    private fun loadUserInfo() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("UserInfo").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name")
                        val password = document.getString("password")

                        usernameET.setText(name ?: "")
                        passwordET.setText(password ?: "")
                    } else {
                        Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to load user info: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserInfo() {
        val uid = auth.currentUser?.uid
        val newUsername = usernameET.text.toString().trim()
        val newPassword = passwordET.text.toString().trim()

        if (uid != null && newUsername.isNotEmpty() && newPassword.isNotEmpty()) {
            firestore.collection("UserInfo").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val currentName = document.getString("name")
                        val currentPassword = document.getString("password")

                        if (newUsername == currentName && newPassword == currentPassword) {
                            Toast.makeText(requireContext(), "Nothing has been changed!", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }

                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            val email = currentUser.email!!
                            val credential = EmailAuthProvider.getCredential(email, currentPassword ?: "")

                            currentUser.reauthenticate(credential)
                                .addOnCompleteListener { reauthTask ->
                                    if (reauthTask.isSuccessful) {
                                        val updates = hashMapOf<String, Any>(
                                            "name" to newUsername,
                                            "password" to newPassword
                                        )
                                        firestore.collection("UserInfo").document(uid)
                                            .update(updates)
                                            .addOnSuccessListener {
                                                Toast.makeText(requireContext(), "User info updated successfully!", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(requireContext(), "Failed to update user info: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }

                                        currentUser.updatePassword(newPassword)
                                            .addOnCompleteListener { updateTask ->
                                                if (updateTask.isSuccessful) {
                                                    Toast.makeText(requireContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(requireContext(), "Failed to update password: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(requireContext(), "Reauthentication failed: ${reauthTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to load user info: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
        }
    }


    private fun enableEditText(editText: EditText) {
        editText.isEnabled = true
        editText.requestFocus() // 포커스를 맞추고
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT) // 키보드 자동으로 나타나게 함
    }
}
