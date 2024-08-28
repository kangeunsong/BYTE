package com.example.open__sw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.open__sw.databinding.ActivityLikedSectionBinding

class LikedSectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLikedSectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikedSectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 선택한 섹션 이름을 Intent로부터 가져옴
        val sectionName = intent.getStringExtra("sectionName") ?: "Unknown"

        if (savedInstanceState == null) {
            val fragment = LikedSectionFragment()
            val bundle = Bundle()
            bundle.putString("sectionName", sectionName)
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

        binding.back.setOnClickListener {
            finish()  // 현재 액티비티 종료 (이전 화면으로 돌아가기)
        }
    }
}