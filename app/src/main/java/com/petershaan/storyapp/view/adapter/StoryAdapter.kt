package com.petershaan.storyapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petershaan.storyapp.data.remote.database.StoryEntity
import com.petershaan.storyapp.databinding.ItemStoryBinding
import com.petershaan.storyapp.utils.withDateFormat

class StoryAdapter: PagingDataAdapter<StoryEntity, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.storyData(story)
        }
    }

    inner class ViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun storyData(data: StoryEntity) {
            binding.apply {
                root.setOnClickListener {
                    onItemClickCallback?.onItemClicked(data)
                }
                Glide.with(itemView)
                    .load(data.photoUrl)
                    .into(ivItemPhoto)
                tvItemName.text = data.name
                tvDate.text = data.createdAt?.withDateFormat()
            }
        }
    }

    fun setOnItemCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryEntity)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id

            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem

            }
        }
    }
}