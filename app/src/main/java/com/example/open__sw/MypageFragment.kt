package com.example.open__sw

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
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
        return view
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