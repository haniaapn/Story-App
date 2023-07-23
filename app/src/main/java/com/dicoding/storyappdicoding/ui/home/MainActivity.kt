package com.dicoding.storyappdicoding.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyappdicoding.R
import com.dicoding.storyappdicoding.databinding.ActivityMainBinding
import com.dicoding.storyappdicoding.ui.LoadingStateAdapter
import com.dicoding.storyappdicoding.ui.StoryAdapter
import com.dicoding.storyappdicoding.ui.ViewModelFactory
import com.dicoding.storyappdicoding.ui.add_story.AddStoryActivity
import com.dicoding.storyappdicoding.ui.login.LoginActivity
import com.dicoding.storyappdicoding.ui.map.MapsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getModelFactory(this)
        val viewModel: MainViewModel by viewModels { factory }
        mainViewModel = viewModel

        supportActionBar?.title = getString(R.string.list_story)

        binding.fabAdd.setOnClickListener { navigateToAddStory() }

        val adapter = StoryAdapter()

        mainViewModel.stories().observe(this){
            adapter.submitData(lifecycle, it)
        }
        binding.rvListStory.layoutManager = LinearLayoutManager(this)
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
    }

    override fun onResume() {
        super.onResume()
        val adapter = StoryAdapter()
        mainViewModel.stories().observe(this){
            adapter.submitData(lifecycle, it)
        }
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
    }

    private fun navigateToAddStory() {
        val intent = Intent(this, AddStoryActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.action_logout -> {
                mainViewModel.deleteUser()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            R.id.action_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)

    }
}