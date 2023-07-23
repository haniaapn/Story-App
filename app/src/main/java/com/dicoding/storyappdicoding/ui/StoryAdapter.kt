package com.dicoding.storyappdicoding.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyappdicoding.data.remote.response.ListStoryItem
import com.dicoding.storyappdicoding.databinding.ItemStoryBinding
import com.dicoding.storyappdicoding.ui.detail_story.DetailStoryActivity

class StoryAdapter: PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    class ViewHolder(private val binding : ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem){
            binding.tvItemName.text = data.name
            binding.tvItemDescription.text = data.description
            Glide.with(itemView).load(data.photoUrl).into(binding.ivItemPhoto)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra("story", data)
                itemView.context.startActivity(intent)
            }
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if(data != null){
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}