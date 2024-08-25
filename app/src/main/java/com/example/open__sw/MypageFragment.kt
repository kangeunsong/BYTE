package com.example.open__sw

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MypageFragment : Fragment() {
    private lateinit var usernameTV: TextView
    private lateinit var useremailTV: TextView
    private lateinit var nameTV: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // TODO: Rename and change types of parameters
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        usernameTV = view.findViewById(R.id.usernameTV)
        useremailTV = view.findViewById(R.id.useremailTV)
        nameTV = view.findViewById(R.id.nameTV)

        loadUserInfo()

        // "정보수정"을 눌렀을 때 비밀번호 확인 다이얼로그 띄우기
        val imageView = view.findViewById<ImageView>(R.id.editIV)
        imageView.setOnClickListener {
            showConfirmPasswordDialog()
        }


        return view
    }

    private fun showConfirmPasswordDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_confirm_pw)

        // 다이얼로그 레이아웃 크기 조절
        val window = dialog.window
        val layoutParams = window?.attributes
        if (layoutParams != null) {
            layoutParams.width = (resources.displayMetrics.widthPixels)
            window.attributes = layoutParams
        }

        val passwordET = dialog.findViewById<EditText>(R.id.passwordET)
        val confirmBtn = dialog.findViewById<Button>(R.id.confirmBtn)

        confirmBtn.setOnClickListener {
            val enteredPassword = passwordET.text.toString().trim()
            val currentUser = auth.currentUser
            if (currentUser != null && currentUser.email != null) {
                val email = currentUser.email!!
                val credential = EmailAuthProvider.getCredential(email, enteredPassword)

                currentUser.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "비밀번호 확인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()

                            // 프래그먼트 이동 로직
                            val fragment = ProfileEditFragment() // 이동하려는 프래그먼트
                            val fragmentManager = parentFragmentManager
                            val fragmentTransaction = fragmentManager.beginTransaction()

                            fragmentTransaction.replace(R.id.fragment_container, fragment) // R.id.fragment_container는 프래그먼트를 배치할 레이아웃 ID
                            fragmentTransaction.addToBackStack(null) // 뒤로 가기 버튼으로 이전 프래그먼트로 돌아갈 수 있도록 백스택에 추가
                            fragmentTransaction.commit()
                        } else {
                            passwordET.error = "비밀번호가 일치하지 않습니다."
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }


    private fun loadUserInfo() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            Log.d("MypageFragment", "Fetching data for UID: $uid")
            firestore.collection("UserInfo").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name")
                        val email = document.getString("email")

                        activity?.runOnUiThread {
                            usernameTV.text = name ?: "N/A"
                            useremailTV.text = email ?: "N/A"
                            nameTV.text = name ?: "N/A"
                        }

                        Log.d("MypageFragment", "Fetched name: $name, email: $email")
                    } else {
                        Log.d("MypageFragment", "No such document")
                        Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to load user info: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.d("MypageFragment", "User not logged in")
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}