package com.example.open__sw

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore


class NewsdetailFragment : BottomSheetDialogFragment() {

    private lateinit var titleTextView: TextView
    private lateinit var summaryTextView: TextView
    private lateinit var newsImageView: ImageView
    private lateinit var newsLink: TextView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_newsdetail, container, false)

        titleTextView = view.findViewById(R.id.news_title)
        summaryTextView = view.findViewById(R.id.news_summary)
        newsImageView = view.findViewById(R.id.image)
        newsLink = view.findViewById(R.id.news_link)
        firestore = FirebaseFirestore.getInstance()

        val sectionName = arguments?.getString("sectionName")
        val date = arguments?.getString("date")
        val newsUID = arguments?.getString("newsUID")

        if (sectionName != null && date != null && newsUID != null) {
            loadNewsDetails(sectionName, date, newsUID)
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

                    // 뉴스 URL 활성화 (클릭 시 브라우저로 이동)
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
