// OTPactivity.kt
package com.example.practice_phone_authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.practice_phone_authentication.databinding.OtpTestBinding
import com.google.firebase.auth.*

class OTPactivity : AppCompatActivity() {

    private lateinit var binding: OtpTestBinding
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private var authId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OtpTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        authId = intent.getStringExtra("otpcr")!!
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    send_home()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    fun send_home() {
        val loginIntent = Intent(this, MainActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        if (currentUser != null) {
            send_home()
            finish()
        }
    }

    fun verify_otp(view: View) {
        val otp: String = binding.otp.text.toString()
        if (otp.isNotEmpty()) {
            val credential = PhoneAuthProvider.getCredential(authId, otp)
            signInWithPhoneAuthCredential(credential)
        }
    }
}