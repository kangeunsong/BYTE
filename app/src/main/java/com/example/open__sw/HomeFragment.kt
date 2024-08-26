package com.example.open__sw

import alirezat775.lib.carouselview.Carousel
import alirezat775.lib.carouselview.CarouselListener
import alirezat775.lib.carouselview.CarouselView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.open__sw.databinding.FragmentHomeBinding
import android.widget.TextView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException
import androidx.fragment.app.setFragmentResultListener

class HomeFragment : Fragment() {

    private val TAG: String = this::class.java.simpleName
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val client = OkHttpClient()
    private lateinit var newsTitleTextView: TextView
    private lateinit var newsSummaryTextView: TextView
    private lateinit var newsLinkTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        newsTitleTextView = view.findViewById(R.id.news_title)
        newsSummaryTextView = view.findViewById(R.id.news_summary)
        newsLinkTextView = view.findViewById(R.id.news_link)

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

    override fun onResume() {
        super.onResume()
        Log.d("HomeFragment", "onResume called")
        setFragmentResultListener("requestKey") { key, bundle ->
            val sectionKR = bundle.getString("sectionKR")
            if (sectionKR != null) {
                fetchSummary(sectionKR, newsTitleTextView, newsSummaryTextView, newsLinkTextView)
            } else {
                Toast.makeText(requireContext(), "선택된 섹션이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchSummary(section: String, titleView: TextView, summaryView: TextView, linkView: TextView) {
        val json = JSONObject().apply {
            put("section", section)
        }

        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaType(), json.toString())
        val request = Request.Builder()
            .url("http://192.168.0.5:5000/summarize")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.e("HomeFragment", "API 호출 실패: ${e.message}")
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Api 사용에 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val jsonResponse = JSONObject(responseBody.string())
                        val title = jsonResponse.getString("title")
                        val summary = jsonResponse.getString("summary")
                        val url = jsonResponse.getString("url")

                        activity?.runOnUiThread {
                            titleView.text = title
                            summaryView.text = summary
                            linkView.text = url
                        }
                    }
                } else {
                    Log.e("HomeFragment", "API 응답 에러: ${response.code}")
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Api 응답에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
