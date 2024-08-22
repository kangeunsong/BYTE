package com.example.open__sw

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView


// Fragment에서의 코드
class ProfileEditFragment : Fragment() {

    private lateinit var usernameET: EditText
    private lateinit var passwordET: EditText
    private lateinit var editUsernameTV: TextView
    private lateinit var editUserpwTV: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_edit, container, false)

        usernameET = view.findViewById(R.id.usernameET)
        passwordET = view.findViewById(R.id.passwordET)
        editUsernameTV = view.findViewById(R.id.editUsernameTV)
        editUserpwTV = view.findViewById(R.id.editUserpwTV)

        editUsernameTV.setOnClickListener {
            enableEditText(usernameET)
        }

        editUserpwTV.setOnClickListener {
            enableEditText(passwordET)
        }

        val button = view.findViewById<Button>(R.id.saveBtn)
        button.setOnClickListener { // 저장기능 구현하기

            val fragment = ProfileEditFragment() // 이동하려는 프래그먼트
            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.addToBackStack(null) // 뒤로가기 백스택에 추가
            fragmentTransaction.commit()
        }
        return view
    }

    private fun enableEditText(editText: EditText) {
        editText.isEnabled = true
        editText.requestFocus() // 포커스를 맞추고
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT) // 키보드 자동으로 나타나게 함
    }
}
