package com.dicoding.storyappdicoding.ui.register

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyappdicoding.R
import com.dicoding.storyappdicoding.data.remote.Result
import com.dicoding.storyappdicoding.databinding.ActivityRegisterBinding
import com.dicoding.storyappdicoding.ui.ViewModelFactory
import com.dicoding.storyappdicoding.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val factory: ViewModelFactory = ViewModelFactory.getModelFactory(this)
        val viewModel: RegisterViewModel by viewModels { factory }
        registerViewModel = viewModel

        observeRegisterResult()

        binding.btnRegister.setOnClickListener { performRegister() }
        binding.tvLogin.setOnClickListener { navigateToLogin() }

        ObjectAnimator.ofFloat(binding.ivLogoRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun performRegister() {
        val name = binding.edRegisterName.text.toString().trim()
        val email = binding.edRegisterEmail.text.toString().trim()
        val password = binding.edRegisterPassword.text.toString().trim()

        if (validateInput(name, email, password)){
            registerViewModel.register(name, email, password)
        }

    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        val edRegisterName = binding.edRegisterName
        val edRegisterEmail = binding.edRegisterEmail
        val edRegisterPassword = binding.edRegisterPassword

        if (name.isEmpty()) {
            edRegisterName.error = getString(R.string.error_empty_name)
            edRegisterName.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            edRegisterEmail.error = getString(R.string.error_empty_email)
            edRegisterEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edRegisterEmail.error = getString(R.string.error_valid_email)
            edRegisterEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            edRegisterPassword.error = getString(R.string.error_empty_password)
            edRegisterPassword.requestFocus()
            return false
        }

        if (password.length < 8){
            edRegisterPassword.error = getString(R.string.error_valid_password)
            edRegisterPassword.requestFocus()
            return false
        }

        return true
    }

    private fun observeRegisterResult() {
        registerViewModel.registerResult.observe(this){ result ->
            when (result) {
                is Result.Success -> {
                    showLoading(false)
                    handleRegisterSuccess()
                }
                is Result.Error -> {
                    showLoading(true)
                    val errorMessage = result.error
                    handleRegisterError(errorMessage)
                }
                Result.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
        binding.tvLogin.isEnabled = !isLoading
    }

    private fun handleRegisterSuccess() {
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun handleRegisterError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        showLoading(false)
    }
}
