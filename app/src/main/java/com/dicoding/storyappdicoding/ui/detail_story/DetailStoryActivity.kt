package com.dicoding.storyappdicoding.ui.detail_story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.dicoding.storyappdicoding.databinding.ActivityDetailStoryBinding
import com.dicoding.storyappdicoding.ui.ViewModelFactory
import com.dicoding.storyappdicoding.data.remote.Result
import com.dicoding.storyappdicoding.data.remote.response.ListStoryItem
import com.dicoding.storyappdicoding.data.remote.response.Story

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var detailViewModel: DetailViewModel

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getModelFactory(this)
        val viewModel: DetailViewModel by viewModels { factory }
        detailViewModel = viewModel

        val storyItem = intent.getParcelableExtra<ListStoryItem>("story")
        if (storyItem != null) {
            detailViewModel.getDetailStory(storyItem.id)
        }
        observeDetailStory()

    }

    private fun observeDetailStory() {
        detailViewModel.detailStoryResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    val detailStory = result.data.story
                    displayStory(detailStory)
                }
                is Result.Error -> {
                    showLoading(false)
                    showError(result.error)
                }
            }
        }
    }

    private fun displayStory(detailStory: Story) {
        binding.apply {
            tvDetailName.text = detailStory.name
            tvDetailDescription.text = detailStory.description
            Glide.with(this@DetailStoryActivity)
                .load(detailStory.photoUrl)
                .into(ivDetailPhoto)

        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}