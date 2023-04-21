package com.inkrodriguez.applucas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.inkrodriguez.applucas.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnRegisterAccount = binding.btnRegisterAccount

        val db = DatabaseHelper(this)

        btnRegisterAccount.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            db.insert(email, password, lastposition = 0)
        }

    }
}