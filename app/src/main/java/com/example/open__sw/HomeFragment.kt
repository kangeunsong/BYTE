package com.example.open__sw

import alirezat775.lib.carouselview.Carousel
import alirezat775.lib.carouselview.CarouselLazyLoadListener
import alirezat775.lib.carouselview.CarouselListener
import alirezat775.lib.carouselview.CarouselView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.open__sw.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    // private var hasNextPage: Boolean = true
    private val TAG: String = this::class.java.simpleName
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            val adapter = SampleAdapter()
            val carousel = Carousel(activity, binding.carouselView, adapter)
            carousel.setOrientation(CarouselView.VERTICAL, false)
            carousel.scaleView(true)
//            carousel.lazyLoad(true, object : CarouselLazyLoadListener {
//                override fun onLoadMore(page: Int, totalItemsCount: Int, view: CarouselView) {
//                    if (hasNextPage) {
//                        Log.d(TAG, "load new item on lazy mode")
//                        carousel.add(SampleModel(11))
//                        carousel.add(SampleModel(12))
//                        carousel.add(SampleModel(13))
//                        carousel.add(SampleModel(14))
//                        carousel.add(SampleModel(15))
//                        hasNextPage = false
//                    }
//                }
//            })
            adapter.setOnClickListener(object : SampleAdapter.OnClick {
                override fun click(model: SampleModel) {
                    // Handle item click
                }
            })

            carousel.addCarouselListener(object : CarouselListener {
                override fun onPositionChange(position: Int) {
                    Log.d(TAG, "currentPosition : $position")
                }

                override fun onScroll(dx: Int, dy: Int) {
                    Log.d(TAG, "onScroll dx : $dx -- dy : $dy")
                }
            })

            for (i in 1..10) {
                carousel.add(SampleModel(i))
            }
        } else {
            Log.e(TAG, "Host activity is not an instance of AppCompatActivity")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}