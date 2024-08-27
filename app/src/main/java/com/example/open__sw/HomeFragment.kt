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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private var section: String = "Politics"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        firestore = Firebase.firestore

        // 액티비티를 안전하게 처리
        val activity = requireActivity() as? AppCompatActivity
        if (activity != null) {
            val adapter = SampleAdapter()
            val carousel = Carousel(activity, binding.carouselView, adapter)
            carousel.setOrientation(CarouselView.VERTICAL, false)
            carousel.scaleView(true)

            fetchNewsData(section, adapter)

            parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
                val selectedSection = bundle.getString("sectionEG") ?: "Politics"
                if (section != selectedSection) {
                    section = selectedSection
                    fetchNewsData(section, adapter) // 새로 데이터 로드
                }
            }

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

    fun updateSection(newSection: String) {
        if (section != newSection) {
            section = newSection
            fetchNewsData(section, binding.carouselView.adapter as SampleAdapter)
        }
    }

    private fun fetchNewsData(section: String, adapter: SampleAdapter) {
        val today = getCurrentDateString() // Helper function to get current date in 'yyyyMMdd' format

        firestore.collection("TodayNews").document(today).collection(section)
            .get()
            .addOnSuccessListener { documents ->
                val items = documents.map { document ->
                    SampleModel(
                        title = document.getString("title") ?: "No Title",
                        summary = document.getString("summary") ?: "No Summary",
                        newsURL = document.getString("newsURL") ?: "",
                        imgURL = document.getString("imgURL") ?: ""
                    )
                }
                adapter.addItems(items)
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error getting documents: ", exception)
            }
    }

    private fun getCurrentDateString(): String {
        val date = java.util.Calendar.getInstance().time
        val formatter = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
        return formatter.format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
