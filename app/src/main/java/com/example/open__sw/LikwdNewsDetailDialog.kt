package com.example.open__sw

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class LikwdNewsDetailDialog : DialogFragment() {

    private lateinit var titleTextView: TextView
    private lateinit var summaryTextView: TextView
    private lateinit var newsImageView: ImageView
    private lateinit var newsUrlTextView: TextView
    private lateinit var likeNumTextView: TextView
    private lateinit var closeButton: ImageView
    private lateinit var unlikeButton: Button

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_news_detail, container, false)

        titleTextView = view.findViewById(R.id.titleTextView)
        summaryTextView = view.findViewById(R.id.summaryTextView)
        newsImageView = view.findViewById(R.id.newsImageView)
        newsUrlTextView = view.findViewById(R.id.newsUrlTextView)
        likeNumTextView = view.findViewById(R.id.likeNumTextView)
        closeButton = view.findViewById(R.id.closeButton)
        unlikeButton = view.findViewById(R.id.unlikeButton)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val sectionName = arguments?.getString("sectionName") ?: return view
        val date = arguments?.getString("date") ?: return view
        val newsUID = arguments?.getString("newsUID") ?: return view

        loadNewsDetails(sectionName, date, newsUID)

        closeButton.setOnClickListener {
            dismiss() // 팝업 닫기
        }

        unlikeButton.setOnClickListener {
            showUnlikeConfirmationDialog(sectionName, date, newsUID)
        }


        return view
    }

    private fun loadNewsDetails(sectionName: String, date: String, newsUID: String) {
        firestore.collection("AllNews").document(sectionName)
            .collection(date).document(newsUID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    titleTextView.text = document.getString("title") ?: "제목 없음"
                    summaryTextView.text = document.getString("summary") ?: "요약 없음"
                    likeNumTextView.text = "Likes: ${document.getLong("likeNUM") ?: 0}"
                    newsUrlTextView.text = document.getString("newsURL") ?: "URL 없음"

                    val imageUrl = document.getString("imgURL")
                    if (!imageUrl.isNullOrEmpty()) {
                        // 이미지 URL이 있을 경우 이미지를 로드
                        newsImageView.visibility = View.VISIBLE
                        Glide.with(this).load(imageUrl).into(newsImageView)
                    } else {
                        // 이미지 URL이 없을 경우 ImageView를 숨김
                        newsImageView.visibility = View.GONE
                    }
                }
            }
            .addOnFailureListener { e ->
                // 에러 처리
            }
    }

    private fun showUnlikeConfirmationDialog(sectionName: String, date: String, newsUID: String) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("좋아요 취소")
            .setMessage("좋아요를 취소하시겠습니까?")
            .setPositiveButton("Yes") { _, _ ->
                removeLikedNews(sectionName, date, newsUID)
            }
            .setNegativeButton("No", null)
            .create()
        dialog.show()
    }

    private fun removeLikedNews(sectionName: String, date: String, newsUID: String) {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("MyPage").document(uid)
            .collection("Liked").document(sectionName)
            .collection("news").document(date)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    for (field in document.data?.entries.orEmpty()) {
                        if (field.value == newsUID) {
                            val fieldName = field.key
                            firestore.runTransaction { transaction ->
                                val likedNewsRef = firestore.collection("MyPage").document(uid)
                                    .collection("Liked").document(sectionName)
                                    .collection("news").document(date)

                                val newsRef = firestore.collection("AllNews").document(sectionName)
                                    .collection(date).document(newsUID)

                                transaction.update(likedNewsRef, fieldName, FieldValue.delete())
                                transaction.update(newsRef, "likeNUM", FieldValue.increment(-1))
                            }.addOnSuccessListener {

                                // targetFragment를 이용해 통신합니다.
                                (targetFragment as? LikedSectionFragment)?.let { fragment ->
                                    fragment.removeNewsFromList(newsUID)
                                    fragment.refreshNewsList()
                                }
                                dismiss() // 다이얼로그 닫기
                            }.addOnFailureListener { e ->
                                Log.e("NewsDetailDialog", "Failed to unlike news and decrease likeNUM", e)
                            }
                            break
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("NewsDetailDialog", "Failed to fetch liked news document for deletion", e)
            }
    }

    companion object {
        fun newInstance(sectionName: String, date: String, newsUID: String): LikwdNewsDetailDialog {
            val args = Bundle()
            args.putString("sectionName", sectionName)
            args.putString("date", date)
            args.putString("newsUID", newsUID)
            val fragment = LikwdNewsDetailDialog()
            fragment.arguments = args
            return fragment
        }
    }
}