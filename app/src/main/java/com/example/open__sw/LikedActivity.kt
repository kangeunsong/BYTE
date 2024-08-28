package com.example.open__sw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.open__sw.databinding.ActivityLikedBinding

class LikedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLikedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //프래그먼트 연결
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LikedFragment())
                .commit()
        }

        binding.back.setOnClickListener {
            finish()  // 현재 액티비티 종료 (이전 화면으로 돌아가기)
        }
    }
}