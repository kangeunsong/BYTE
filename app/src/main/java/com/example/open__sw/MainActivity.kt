package com.example.open__sw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.open__sw.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding
    private var sectionEG = "Politics"
    private var selectedOption: String = "Politics" // 기본 선택 상태 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        auth = FirebaseAuth.getInstance()

        binding.bottomNavigationView.background = null

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.news -> openFragment(HomeFragment())
                R.id.my -> openFragment(MypageFragment())
            }
            true
        }

        fragmentManager = supportFragmentManager
        openFragment(HomeFragment())

        binding.fab.setOnClickListener {
            showBottomSheetDialog()
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

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        bottomSheetDialog.setContentView(view)

        val options = listOf(
            view.findViewById<TextView>(R.id.option_politics),
            view.findViewById<TextView>(R.id.option_economy),
            view.findViewById<TextView>(R.id.option_society),
            view.findViewById<TextView>(R.id.option_life_culture),
            view.findViewById<TextView>(R.id.option_world),
            view.findViewById<TextView>(R.id.option_it_science)
        ).filterNotNull() // Null 필터링

        fun updateOptionColors(selectedView: TextView) {
            options.forEach { option ->
                if (option == selectedView) {
                    option.setBackgroundColor(resources.getColor(R.color.black, null))
                    option.setTextColor(resources.getColor(R.color.white, null))
                } else {
                    option.setBackgroundColor(resources.getColor(R.color.white, null))
                    option.setTextColor(resources.getColor(R.color.black, null))
                }
            }
        }

        // 기본 선택 항목 강조 표시
        fun updateInitialOptionColors() {
            options.find { it.text.toString() == selectedOption }?.let {
                updateOptionColors(it)
            }
        }

        updateInitialOptionColors()

        options[0].setOnClickListener {
            sectionEG = "Politics"
            selectedOption = options[0].text.toString()
            updateFragmentWithSection(sectionEG)
            updateOptionColors(options[0])
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Selected: politics", Toast.LENGTH_SHORT).show()
        }

        options[1].setOnClickListener {
            sectionEG = "Economy"
            selectedOption = options[1].text.toString()
            updateFragmentWithSection(sectionEG)
            updateOptionColors(options[1])
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Selected: economy", Toast.LENGTH_SHORT).show()
        }

        options[2].setOnClickListener {
            sectionEG = "Society"
            selectedOption = options[2].text.toString()
            updateFragmentWithSection(sectionEG)
            updateOptionColors(options[2])
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Selected: society", Toast.LENGTH_SHORT).show()
        }

        options[3].setOnClickListener {
            sectionEG = "LifeCulture"
            selectedOption = options[3].text.toString()
            updateFragmentWithSection(sectionEG)
            updateOptionColors(options[3])
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Selected: life/culture", Toast.LENGTH_SHORT).show()
        }

        options[4].setOnClickListener {
            sectionEG = "World"
            selectedOption = options[4].text.toString()
            updateFragmentWithSection(sectionEG)
            updateOptionColors(options[4])
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Selected: world", Toast.LENGTH_SHORT).show()
        }

        options[5].setOnClickListener {
            sectionEG = "ITScience"
            selectedOption = options[5].text.toString()
            updateFragmentWithSection(sectionEG)
            updateOptionColors(options[5])
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Selected: IT/science", Toast.LENGTH_SHORT).show()
        }

        bottomSheetDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        if (fragment is HomeFragment) {
            val bundle = Bundle().apply {
                putString("sectionEG", sectionEG)
            }
            fragment.arguments = bundle
        }

        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private fun updateFragmentWithSection(section: String) {
        sectionEG = section
        openFragment(HomeFragment())
    }
}
