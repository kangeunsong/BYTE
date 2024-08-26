package com.example.open__sw

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.open__sw.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding

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
        fragmentManager=supportFragmentManager
        openFragment(HomeFragment())


        binding.fab.setOnClickListener{
            Toast.makeText(this,"categories",Toast.LENGTH_SHORT).show()
        }

        binding.userinfo.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slidein_vertical, R.anim.slideout_vertical)

        }
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

    private fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container,fragment)
        fragmentTransaction.commit()
    }
}

