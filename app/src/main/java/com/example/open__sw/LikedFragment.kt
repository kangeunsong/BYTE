package com.example.open__sw

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView

class LikedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liked, container, false)

        view.findViewById<TextView>(R.id.Politics).setOnClickListener {
            openSection("Politics")
        }
        view.findViewById<TextView>(R.id.Economy).setOnClickListener {
            openSection("Economy")
        }
        view.findViewById<TextView>(R.id.Society).setOnClickListener {
            openSection("Society")
        }
        view.findViewById<TextView>(R.id.LifeNCulture).setOnClickListener {
            openSection("Life / Culture")
        }
        view.findViewById<TextView>(R.id.World).setOnClickListener {
            openSection("World")
        }
        view.findViewById<TextView>(R.id.ITNScience).setOnClickListener {
            openSection("IT / Science")
        }

        return view
    }

    private fun openSection(sectionName: String) {
        val intent = Intent(requireContext(), LikedSectionActivity::class.java)
        intent.putExtra("sectionName", sectionName)
        startActivity(intent)
    }
}