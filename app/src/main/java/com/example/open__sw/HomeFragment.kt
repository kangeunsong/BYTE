package com.example.open__sw

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.open__sw.adapter.DataAdapter
import com.example.open__sw.databinding.FragmentHomeBinding
import com.example.open__sw.model.DataModel
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var firestore: FirebaseFirestore
    private val binding get() = _binding!!
    private lateinit var adapter: DataAdapter
    private var sectionEG: String? = "Politics"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sectionEG = arguments?.getString("sectionEG")
        adapter = DataAdapter(arrayListOf())
        binding.recycler.adapter = adapter

        binding.recycler.apply {
            this.adapter = adapter
            set3DItem(true)
            setAlpha(true)
            setOrientation(RecyclerView.VERTICAL)
            setIntervalRatio(0.7f)
        }

        val currentDate = getCurrentDate()
        sectionEG?.let {
            fetchNewsData(currentDate, it)
        }
    }

    private fun fetchNewsData(date: String, sectionName: String) {
        val newsRef = firestore.collection("TodayNews").document(date).collection(sectionName)

        newsRef.get().addOnSuccessListener { documents ->
            val newsList = ArrayList<DataModel>()
            for (document in documents) {
                val title = document.getString("title") ?: ""
                val summary = document.getString("summary") ?: ""
                val newsURL = document.getString("newsURL") ?: ""
                val imgURL = document.getString("imgURL") ?: ""
                val newsUID = document.id // Firestore document ID를 newsUID로 사용

                // DataModel에 sectionName과 date를 포함시켜 생성
                newsList.add(DataModel(title, summary, newsURL, imgURL, newsUID, sectionName, date))
            }
            adapter.updateData(newsList)
        }.addOnFailureListener { exception ->
            // 에러 처리
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
