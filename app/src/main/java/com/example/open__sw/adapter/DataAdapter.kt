//Copyright 2021 Sparrow007 (Ankit kumar)
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.


package com.example.open__sw.adapter

import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.open__sw.NewsdetailFragment
import com.example.open__sw.model.DataModel
import com.example.open__sw.R

class DataAdapter(private var list: ArrayList<DataModel>) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.news_title)
        val image: ImageView = itemView.findViewById(R.id.image)
        val newsLink: TextView = itemView.findViewById(R.id.news_link)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.title.text = data.title
//        holder.newsLink.apply {
//            text = data.newsURL
//            Linkify.addLinks(this, Linkify.WEB_URLS)
//        }

        // 이미지 없는 기사인 경우
        if (data.imgURL.isNullOrEmpty()) {
            Glide.with(holder.image.context)
                .load(R.drawable.splashimg)
                .into(holder.image)
        } else {
            Glide.with(holder.image.context)
                .load(data.imgURL)
                .into(holder.image)
        }

        holder.image.setOnClickListener {
            val fragment = NewsdetailFragment().apply {
                arguments = Bundle().apply {
                    putString("sectionName", data.sectionName) // 전달할 데이터
                    putString("date", data.date)
                    putString("newsUID", data.newsUID)
                }
            }

            fragment.show((it.context as AppCompatActivity).supportFragmentManager, fragment.tag)
        }
    }

    fun updateData(list: ArrayList<DataModel>) {
        this.list = list
        notifyDataSetChanged()
    }
}
