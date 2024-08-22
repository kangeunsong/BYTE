package com.example.open__sw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels

class HomeFragment : Fragment() {

    private val sharedViewModel: ShareViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val newsTitleTextView: TextView = view.findViewById(R.id.news_title)

        // ViewModel의 selected 값을 관찰하고, 해당 값을 TextView에 설정
        sharedViewModel.selected.observe(viewLifecycleOwner) { selected ->
            newsTitleTextView.text = selected
        }

        return view
    }


}