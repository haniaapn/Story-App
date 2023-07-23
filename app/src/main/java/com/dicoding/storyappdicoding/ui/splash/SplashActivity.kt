package com.dicoding.storyappdicoding.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyappdicoding.databinding.ActivitySplashBinding
import com.dicoding.storyappdicoding.ui.ViewModelFactory
import com.dicoding.storyappdicoding.ui.home.MainActivity
import com.dicoding.storyappdicoding.ui.login.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val factory: ViewModelFactory = ViewModelFactory.getModelFactory(this)
        val viewModel: SplashViewModel by viewModels { factory }
        splashViewModel = viewModel

        @Suppress("DEPRECATION")
        Handler().postDelayed({
            if (splashViewModel.getUser()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, SPLASH_DELAY)

    }

    companion object{
        private const val SPLASH_DELAY = 3000L
    }
}
