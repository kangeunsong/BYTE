package com.example.open__sw

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.open__sw.databinding.ActivityMypageBinding

class MypageActivity :  AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root) // 바인딩의 root를 setContentView로 설정해야 합니다.

        // Toolbar 설정
        val toolbar: Toolbar = binding.toolbar // binding을 사용해 뷰에 접근합니다.
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //프래그먼트 연결
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MypageFragment())
                .commit()
        }

        binding.back.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slidein_horizon, R.anim.slideout_horizon) // 이 부분은 startActivity 바로 뒤에 두는 것이 좋습니다.
        }
    }

}
