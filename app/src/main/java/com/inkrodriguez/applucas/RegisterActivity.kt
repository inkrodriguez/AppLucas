package com.inkrodriguez.applucas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.inkrodriguez.applucas.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnRegisterAccount = binding.btnRegisterAccount

        btnRegisterAccount.setOnClickListener {
            registerUserFirebase()
        }



    }

    fun registerUserFirebase(){
        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.text.toString()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this, "User successfully created!", Toast.LENGTH_SHORT).show()
                registerUserSQL()
            } else {
                Toast.makeText(this, "Failed to create user!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun registerUserSQL(){
        lifecycleScope.launch {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            var user = UserEntity("$email", "$password", "", "")
            AppDataBase(this@RegisterActivity).getUserDao().insertUser(user)
            finish()
        }
    }
}