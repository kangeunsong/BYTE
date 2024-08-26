package com.example.open__sw

import com.example.open__sw.databinding.ItemCarouselBinding
import com.example.open__sw.databinding.ItemEmptyCarouselBinding
import alirezat775.lib.carouselview.CarouselAdapter
import android.view.LayoutInflater
import android.view.ViewGroup

class SampleAdapter : CarouselAdapter() {

    private val EMPTY_ITEM = 0
    private val NORMAL_ITEM = 1

    private var vh: CarouselViewHolder? = null
    var onClick: OnClick? = null

    fun setOnClickListener(onClick: OnClick?) {
        this.onClick = onClick
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
                vh = holder
                val model = getItems()[position] as SampleModel
                holder.binding.itemText.text = model.getId().toString()
            }
            is EmptyMyViewHolder -> {
                vh = holder
                val model = getItems()[position] as EmptySampleModel
                holder.binding.itemEmptyText.text = model.getText()
            }
        }
    }

    inner class MyViewHolder(val binding: ItemCarouselBinding) : CarouselViewHolder(binding.root) {

        init {
            binding.itemText.setOnClickListener { onClick?.click(getItems()[adapterPosition] as SampleModel) }
        }
    }

    inner class EmptyMyViewHolder(val binding: ItemEmptyCarouselBinding) : CarouselViewHolder(binding.root)

    interface OnClick {
        fun click(model: SampleModel)
    }
}
