package com.example.open__sw

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LikedSectionFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var newsList = mutableListOf<NewsItem>()
    private lateinit var sectionTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liked_section, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        sectionTextView = view.findViewById(R.id.Section)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = NewsAdapter(newsList)
        recyclerView.adapter = adapter

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val sectionName = arguments?.getString("sectionName") ?: "Unknown"
        sectionTextView.text = sectionName

        adapter.setOnItemClickListener { position ->
            val newsItem = newsList[position]
            val dialog = NewsDetailDialog.newInstance(sectionName, newsItem.date, newsItem.newsUID)
            dialog.show(parentFragmentManager, "NewsDetailDialog")
        }

        loadLikedNews(sectionName)

        return view
    }

    fun removeNewsFromList(newsUID: String) {
        val iterator = newsList.iterator()
        while (iterator.hasNext()) {
            val newsItem = iterator.next()
            if (newsItem.newsUID == newsUID) {
                iterator.remove()
                adapter.notifyDataSetChanged()
                break
            }
        }
    }

    private fun loadLikedNews(sectionName: String) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            // MyPage > userUID > Liked > sectionName > news 경로에서 뉴스 UID 가져오기
            firestore.collection("MyPage").document(uid).collection("Liked")
                .document(sectionName).collection("news")
                .get()
                .addOnSuccessListener { documents ->
                    newsList.clear()
                    for (document in documents) {
                        val date = document.id  // news 컬렉션의 각 문서 ID가 날짜라고 가정

                        // 모든 필드에 대해 반복문 실행
                        for (field in document.data.entries) {
                            val newsUID = field.value as? String  // 필드 값이 newsUID로 가정

                            // 로그 출력 추가
                            Log.d("LikedSectionFragment", "Field Name: ${field.key}, newsUID: $newsUID")

                            if (newsUID != null) {
                                // Firestore에서 뉴스 타이틀 가져오기 (예: AllNews/sectionName/date/newsUID)
                                firestore.collection("AllNews").document(sectionName)
                                    .collection(date).document(newsUID)
                                    .get()
                                    .addOnSuccessListener { newsDocument ->
                                        val title = newsDocument.getString("title") ?: "제목 없음"
                                        val newsDate = newsDocument.getString("date") ?: date
                                        newsList.add(NewsItem(title, newsDate, newsUID))
                                        adapter.notifyDataSetChanged()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("LikedSectionFragment", "Failed to fetch news: $newsUID", e)
                                    }
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LikedSectionFragment", "Failed to fetch liked news for section: $sectionName", e)
                }
        } else {
            Log.e("LikedSectionFragment", "User UID is null, cannot load liked news.")
        }
    }
}