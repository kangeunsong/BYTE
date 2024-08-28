package com.example.open__sw

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class NewsdetailFragment : BottomSheetDialogFragment() {

    private lateinit var titleTextView: TextView
    private lateinit var summaryTextView: TextView
    private lateinit var newsImageView: ImageView
    private lateinit var newsLink: TextView
    private lateinit var likeButton: ImageButton
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var isLiked: Boolean = false

    private var onLikeStatusChangedListener: OnLikeStatusChangedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_newsdetail, container, false)

        titleTextView = view.findViewById(R.id.news_title)
        summaryTextView = view.findViewById(R.id.news_summary)
        newsImageView = view.findViewById(R.id.image)
        newsLink = view.findViewById(R.id.news_link)
        likeButton = view.findViewById(R.id.likebtn)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val sectionName = arguments?.getString("sectionName")
        val date = arguments?.getString("date")
        val newsUID = arguments?.getString("newsUID")

        if (sectionName != null && date != null && newsUID != null) {
            loadNewsDetails(sectionName, date, newsUID)
            setupLikeButton(sectionName, date, newsUID)
        }

        return view
    }

    private fun loadNewsDetails(sectionName: String, date: String, newsUID: String) {
        firestore.collection("TodayNews").document(date)
            .collection(sectionName).document(newsUID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    titleTextView.text = document.getString("title") ?: "제목 없음"
                    summaryTextView.text = document.getString("summary") ?: "요약 없음"

                    val newsURL = document.getString("newsURL")
                    newsLink.text = newsURL ?: "URL 없음"

                    newsLink.setOnClickListener {
                        newsURL?.let {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                            startActivity(intent)
                        }
                    }

                    val imageUrl = document.getString("imgURL")
                    if (!imageUrl.isNullOrEmpty()) {
                        newsImageView.visibility = View.VISIBLE
                        Glide.with(this).load(imageUrl).into(newsImageView)
                    } else {
                        newsImageView.visibility = View.GONE
                    }
                }
            }
            .addOnFailureListener { e ->
                // 에러 처리
            }
    }

    private fun setupLikeButton(sectionName: String, date: String, newsUID: String) {
        val userUID = auth.currentUser?.uid ?: return
        val likedRef = firestore.collection("MyPage").document(userUID)
            .collection("Liked").document(sectionName)

        // Check if the news is liked by the user
        likedRef.collection("news").document(newsUID).get().addOnSuccessListener { document ->
            isLiked = document.exists()
            updateLikeButtonIcon()
        }.addOnFailureListener { e ->
            // 에러 처리
        }

        likeButton.setOnClickListener {
            if (isLiked) {
                unlikeNews(sectionName, date, newsUID, userUID)
            } else {
                likeNews(sectionName, date, newsUID, userUID)
            }
        }
    }

    private fun likeNews(sectionName: String, date: String, newsUID: String, userUID: String) {
        firestore.runBatch { batch ->
            val allNewsRef = firestore.collection("AllNews").document(sectionName)
                .collection(date).document(newsUID)
            val todayNewsRef = firestore.collection("TodayNews").document(date)
                .collection(sectionName).document(newsUID)

            batch.update(allNewsRef, "likeNUM", FieldValue.increment(1))
            batch.update(todayNewsRef, "likeNUM", FieldValue.increment(1))

            // 이름이 newsUID인 문서를 생성하여 저장
            val likedRef = firestore.collection("MyPage").document(userUID)
                .collection("Liked").document(sectionName)
                .collection("news").document(newsUID)  // 여기서 newsUID를 문서 이름으로 사용
            batch.set(likedRef, mapOf("date" to date)) // date 필드를 문서에 저장
        }.addOnSuccessListener {
            isLiked = true
            onLikeStatusChangedListener?.onLikeStatusChanged(true, sectionName, date, newsUID)
            updateLikeButtonIcon()
        }.addOnFailureListener { e ->
            // 에러 처리
        }
    }


    private fun unlikeNews(sectionName: String, date: String, newsUID: String, userUID: String) {
        firestore.runBatch { batch ->
            val allNewsRef = firestore.collection("AllNews").document(sectionName)
                .collection(date).document(newsUID)
            val todayNewsRef = firestore.collection("TodayNews").document(date)
                .collection(sectionName).document(newsUID)

            batch.update(allNewsRef, "likeNUM", FieldValue.increment(-1))
            batch.update(todayNewsRef, "likeNUM", FieldValue.increment(-1))

            // newsUID를 문서 이름으로 사용하여 문서를 삭제
            val likedRef = firestore.collection("MyPage").document(userUID)
                .collection("Liked").document(sectionName)
                .collection("news").document(newsUID)  // 여기서 newsUID를 문서 이름으로 사용
            batch.delete(likedRef)
        }.addOnSuccessListener {
            isLiked = false
            onLikeStatusChangedListener?.onLikeStatusChanged(false, sectionName, date, newsUID)
            updateLikeButtonIcon()
        }.addOnFailureListener { e ->
            // 에러 처리
        }
    }


    private fun updateLikeButtonIcon() {
        if (isLiked) {
            likeButton.setImageResource(R.drawable.ic_like_filled)
        } else {
            likeButton.setImageResource(R.drawable.ic_like_empty)
        }
    }

    fun setOnLikeStatusChangedListener(listener: OnLikeStatusChangedListener) {
        onLikeStatusChangedListener = listener
    }

    interface OnLikeStatusChangedListener {
        fun onLikeStatusChanged(isLiked: Boolean, sectionName: String, date: String, newsUID: String)
    }

    companion object {
        fun newInstance(sectionName: String, date: String, newsUID: String): NewsdetailFragment {
            val args = Bundle()
            args.putString("sectionName", sectionName)
            args.putString("date", date)
            args.putString("newsUID", newsUID)
            val fragment = NewsdetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}