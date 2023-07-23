package com.dicoding.storyappdicoding.ui.login

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.storyappdicoding.R
import com.dicoding.storyappdicoding.data.remote.Result
import com.dicoding.storyappdicoding.data.remote.response.LoginResponse
import com.dicoding.storyappdicoding.databinding.ActivityLoginBinding
import com.dicoding.storyappdicoding.model.UserModel
import com.dicoding.storyappdicoding.model.UserPreference
import com.dicoding.storyappdicoding.ui.ViewModelFactory
import com.dicoding.storyappdicoding.ui.home.MainActivity
import com.dicoding.storyappdicoding.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    private lateinit var progressBar: ProgressBar
    private lateinit var loginButton: Button
    private lateinit var registerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        progressBar = binding.progressBar
        loginButton = binding.btnLogin
        registerTextView = binding.tvRegister

        val factory: ViewModelFactory = ViewModelFactory.getModelFactory(this)
        val viewModel: LoginViewModel by viewModels { factory }
        loginViewModel = viewModel

        binding.edLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    binding.tilEmail.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    binding.tilPassword.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        loginButton.setOnClickListener { performLogin() }
        registerTextView.setOnClickListener { navigateToRegister() }

        observeLoginResult()

        ObjectAnimator.ofFloat(binding.ivLogoLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun performLogin() {
        val email = binding.edLoginEmail.text.toString().trim()
        val password = binding.edLoginPassword.text.toString().trim()

        if (validateInput(email, password)) {
            loginViewModel.login(email, password)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        val edLoginEmail = binding.edLoginEmail
        val edLoginPassword = binding.edLoginPassword

        if (email.isEmpty()) {
            edLoginEmail.error = getString(R.string.error_empty_email)
            edLoginEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edLoginEmail.error = getString(R.string.error_valid_email)
            edLoginEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            edLoginPassword.error = getString(R.string.error_empty_password)
            edLoginEmail.requestFocus()
            return false
        }

        if (password.length < 8){
            edLoginPassword.error = getString(R.string.error_valid_password)
            edLoginEmail.requestFocus()
            return false
        }

        return true
    }

    private fun observeLoginResult() {
        loginViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    handleLoginSuccess(result.data)
                }
                is Result.Error -> {
                    showLoading(false)
                    handleLoginError(result.error)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        loginButton.isEnabled = !isLoading
        registerTextView.isEnabled = !isLoading
    }

    private fun handleLoginSuccess(loginResponse: LoginResponse) {
        val userPreference = UserPreference(this)
        val loginResult = loginResponse.loginResult
        val userModel = UserModel(
            name = loginResult.name,
            token = loginResult.token,
            userId = loginResult.userId,
            isLogin = true
        )
        userPreference.setUser(userModel)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun handleLoginError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}
