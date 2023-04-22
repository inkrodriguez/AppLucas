package com.inkrodriguez.applucas

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.ktx.Firebase
import com.inkrodriguez.applucas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences

    companion object {
        const val PREFS_KEY = "myPrefs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

        var btnLogin = binding.btnLogin
        var btnRegister = binding.btnRegister

        btnLogin.setOnClickListener {
            var email = binding.editEmail.text.toString()
            var password = binding.editPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        trackLogin()
                        saveData(email, password)
                        login()
                    } else {
                        Toast.makeText(this, "Email or password is invalid!", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    when(exception) {
                        is FirebaseAuthInvalidUserException -> {
                            Toast.makeText(this, "Invalid user!", Toast.LENGTH_SHORT).show()
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show()
                        }
                        is FirebaseNetworkException -> {
                            Toast.makeText(this, "No network available!", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this, "Authentication failed!", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "signInWithEmailAndPassword failed", exception)
                        }
                    }
                }
        }

        btnRegister.setOnClickListener {
            register()
        }


    }

    private fun login() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun register(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun saveData(email: String, password: String) {
        val sharedPref = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("email", email)
            putString("password", password)
            apply()
        }
    }

    private fun trackLogin(){
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.METHOD, "email")
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.LOGIN, params)
    }


}
