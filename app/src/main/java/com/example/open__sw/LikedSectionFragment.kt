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

class LikedSectionFragment : Fragment(), NewsdetailFragment.OnLikeStatusChangedListener {

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
            val dialog = LikedNewsDetailDialog.newInstance(sectionName, newsItem.date, newsItem.newsUID)
            dialog.setTargetFragment(this, 0)
            dialog.show(parentFragmentManager, "NewsDetailDialog")
        }

        loadLikedNews(sectionName)

        return view
    }

    override fun onLikeStatusChanged(isLiked: Boolean, sectionName: String, date: String, newsUID: String) {
        if (isLiked) {
            // Fetch the news details and add to the list
            firestore.collection("AllNews").document(sectionName)
                .collection(date).document(newsUID)
                .get()
                .addOnSuccessListener { document ->
                    val title = document.getString("title") ?: "제목 없음"
                    val newsItem = NewsItem(title, date, newsUID)
                    newsList.add(newsItem)
                    adapter.notifyDataSetChanged()
                }
        } else {
            // Remove the news item from the list
            removeNewsFromList(newsUID)
        }
    }
    fun refreshNewsList() {
        Log.d("LikedSectionFragment", "Refreshing news list")
        val sectionName = arguments?.getString("sectionName") ?: return
        loadLikedNews(sectionName)
    }


    fun removeNewsFromList(newsUID: String) {
        val iterator = newsList.iterator()
        var position = -1
        while (iterator.hasNext()) {
            val newsItem = iterator.next()
            position++
            if (newsItem.newsUID == newsUID) {
                iterator.remove()
                adapter.notifyItemRemoved(position)
                Log.d("LikedSectionFragment", "Removed item at position: $position")
                Log.d("LikedSectionFragment", "Remaining items count: ${newsList.size}")
                break
            }
        }
    }

    private fun loadLikedNews(sectionName: String) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("MyPage").document(uid).collection("Liked")
                .document(sectionName).collection("news")
                .get()
                .addOnSuccessListener { documents ->
                    newsList.clear()
                    for (document in documents) {
                        val newsUID = document.id // 문서의 ID가 newsUID입니다.
                        val date = document.getString("date") ?: continue // date 필드가 문서에 있어야 합니다.

                        Log.d("LikedSectionFragment", "Loaded newsUID: $newsUID for date: $date")

                        firestore.collection("AllNews").document(sectionName)
                            .collection(date).document(newsUID)
                            .get()
                            .addOnSuccessListener { newsDocument ->
                                if (newsDocument.exists()) {
                                    val title = newsDocument.getString("title") ?: "제목 없음"
                                    val newsDate = newsDocument.getString("date") ?: date
                                    newsList.add(NewsItem(title, newsDate, newsUID))
                                    adapter.notifyDataSetChanged()
                                } else {
                                    Log.e("LikedSectionFragment", "News document does not exist for newsUID: $newsUID")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("LikedSectionFragment", "Failed to fetch news: $newsUID", e)
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