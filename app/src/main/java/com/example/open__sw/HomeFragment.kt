package com.example.open__sw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.widget.Toast
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException
import android.util.Log
import androidx.fragment.app.setFragmentResultListener

class HomeFragment : Fragment() {

    private val client = OkHttpClient()
    private lateinit var newsTitleTextView: TextView
    private lateinit var newsSummaryTextView: TextView
    private lateinit var newsLinkTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        newsTitleTextView = view.findViewById(R.id.news_title)
        newsSummaryTextView = view.findViewById(R.id.news_summary)
        newsLinkTextView = view.findViewById(R.id.news_link)

        return view
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

        // API 요청을 위한 JSON 데이터
        val json = JSONObject().apply {
            put("section", section)
        }

        // 요청 본문 작성
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaType(), json.toString())

        // 요청 작성
        val request = Request.Builder()
            .url("http://172.30.1.95:5000/summarize")  // 여기서 IP와 포트는 api.py에서 실행 중인 서버에 맞춰 변경하세요.
            .post(requestBody)
            .build()

        // 비동기 요청 실행
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 요청 실패 시 처리
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

                        // UI 업데이트는 메인 스레드에서 실행
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
}