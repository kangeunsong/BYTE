package com.example.open__sw

import com.example.open__sw.databinding.ItemCarouselBinding
import com.example.open__sw.databinding.ItemEmptyCarouselBinding
import alirezat775.lib.carouselview.CarouselAdapter
import android.view.LayoutInflater
import android.view.ViewGroup

class SampleAdapter : CarouselAdapter() {

    private val EMPTY_ITEM = 0
    private val NORMAL_ITEM = 1

    var onClick: OnClick? = null

    fun setOnClickListener(onClick: OnClick?) {
        this.onClick = onClick
    }

    fun addItems(items: List<SampleModel>) {
        getItems().addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItems()[position]) {
            is EmptySampleModel -> EMPTY_ITEM
            else -> NORMAL_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == NORMAL_ITEM) {
            val binding = ItemCarouselBinding.inflate(inflater, parent, false)
            MyViewHolder(binding)
        } else {
            val binding = ItemEmptyCarouselBinding.inflate(inflater, parent, false)
            EmptyMyViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        when (holder) {
            is MyViewHolder -> {
                val model = getItems()[position] as SampleModel
                holder.binding.newsTitle.text = model.title
                holder.binding.newsSummary.text = model.context

                // 하트 이미지 상태 설정
                holder.binding.likeButton.setImageResource(
                    if (model.islikeFilled) R.drawable.ic_like_filled
                    else R.drawable.ic_like_empty
                )
            }
            is EmptyMyViewHolder -> {
                val model = getItems()[position] as EmptySampleModel
                holder.binding.itemEmptyText.text = model.getText()
            }
        }
    }

    inner class MyViewHolder(val binding: ItemCarouselBinding) : CarouselViewHolder(binding.root) {
        init {
            binding.likeButton.setOnClickListener {
                val position = adapterPosition
                val model = getItems()[position] as SampleModel

                // 하트 상태를 토글
                model.islikeFilled = !model.islikeFilled

                // UI 갱신
                notifyItemChanged(position)
                onClick?.click(model)
            }
        }
    }

    inner class EmptyMyViewHolder(val binding: ItemEmptyCarouselBinding) : CarouselViewHolder(binding.root)

    interface OnClick {
        fun click(model: SampleModel)
    }
}
