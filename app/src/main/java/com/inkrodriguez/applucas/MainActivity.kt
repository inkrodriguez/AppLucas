package com.inkrodriguez.applucas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.inkrodriguez.applucas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        const val PREFS_KEY = "myPrefs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        var btnLogin = binding.btnLogin

        btnLogin.setOnClickListener {
            var email = binding.editEmail.text.toString()
            var password = binding.editPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        // Save user data and navigate to next activity
                        saveData(email, password)
                        nextActivity()

                        // Log the login event with Firebase Analytics
                        val bundle = Bundle().apply {
                            putString(FirebaseAnalytics.Param.METHOD, email)
                        }
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                    } else {
                        Toast.makeText(this, "Email or password is invalid!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun nextActivity() {
        val intent = Intent(this, HomeActivity::class.java)
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
}
