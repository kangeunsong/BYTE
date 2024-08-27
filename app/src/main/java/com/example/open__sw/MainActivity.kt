package com.example.open__sw

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.open__sw.databinding.ActivityMainBinding

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: ShareViewModel by viewModels()
    var sectionEG = "Politics"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        // 기본 타이틀 숨기기
        supportActionBar?.setDisplayShowTitleEnabled(false)


        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)
        binding.bottomNavigationView.background=null

        binding.bottomNavigationView.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.a -> openFragment(HomeFragment())
                R.id.b -> openFragment(SecondFragment())
                R.id.d -> openFragment(HomeFragment())
                R.id.e -> openFragment(SecondFragment())
            }
            true
        }

        sharedViewModel.select("some_value")
        openFragment(HomeFragment())

        binding.fab.setOnClickListener{
            showBottomSheetDialog()
        }

        binding.userinfo.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slidein_vertical, R.anim.slideout_vertical)

        }
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        bottomSheetDialog.setContentView(view)

        view.findViewById<TextView>(R.id.option_politics)?.setOnClickListener {
            sectionEG = "Politics"
            updateFragmentWithSection(sectionEG)
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Selected: politics", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<TextView>(R.id.option_economy)?.setOnClickListener {
            sectionEG = "Economy"
            updateFragmentWithSection(sectionEG)
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Selected: economy", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<TextView>(R.id.option_society)?.setOnClickListener {
            sectionEG = "Society"
            updateFragmentWithSection(sectionEG)
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Selected: society", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<TextView>(R.id.option_life_culture)?.setOnClickListener {
            sectionEG = "LifeCulture"
            updateFragmentWithSection(sectionEG)
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Selected: life/culture", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<TextView>(R.id.option_world)?.setOnClickListener {
            sectionEG = "World"
            updateFragmentWithSection(sectionEG)
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Selected: world", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<TextView>(R.id.option_it_science)?.setOnClickListener {
            sectionEG = "ITScience"
            updateFragmentWithSection(sectionEG)
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Selected: IT/science", Toast.LENGTH_SHORT).show()
        }

        bottomSheetDialog.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my -> openFragment(HomeFragment())
            R.id.nav_plus -> openFragment(SecondFragment())
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else {
            super.onBackPressed()
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateFragmentWithSection(section: String) {
//        val bundle = Bundle()
//        bundle.putString("sectionEG", section)
//        supportFragmentManager.setFragmentResult("requestKey", bundle)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment is HomeFragment) {
            fragment.updateSection(section)
        } else {
            val bundle = Bundle()
            bundle.putString("sectionEG", section)
            supportFragmentManager.setFragmentResult("requestKey", bundle)
        }
    }
}

