package com.example.open__sw

import alirezat775.lib.carouselview.Carousel
import alirezat775.lib.carouselview.CarouselListener
import alirezat775.lib.carouselview.CarouselView
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.open__sw.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        // 액티비티를 안전하게 처리
        val activity = requireActivity() as? AppCompatActivity
        if (activity != null) {
            val adapter = SampleAdapter()
            val carousel = Carousel(activity, binding.carouselView, adapter)
            carousel.setOrientation(CarouselView.VERTICAL, false)
            carousel.scaleView(true)

            // 데이터 추가
            val items = listOf(
                SampleModel(title = "가", context = "라"),
                SampleModel(title = "나", context = "마"),
                SampleModel(title = "다", context = "바")
            )
            adapter.addItems(items)

            carousel.addCarouselListener(object : CarouselListener {
                override fun onPositionChange(position: Int) {
                    Log.d("HomeFragment", "currentPosition : $position")
                }

                override fun onScroll(dx: Int, dy: Int) {
                    Log.d("HomeFragment", "onScroll dx : $dx -- dy : $dy")
                }
            })

            adapter.setOnClickListener(object : SampleAdapter.OnClick {
                override fun click(model: SampleModel) {
                    // 클릭 시의 행동 처리
                }
            })
        } else {
            Log.e("HomeFragment", "Activity is not an instance of AppCompatActivity")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
