package com.example.practice_phone_authentication

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.practice_phone_authentication.databinding.PhoneAuthBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*

import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: PhoneAuthBinding
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private var phoneNum: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PhoneAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
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

    fun generate_OTP(view: View) {
        phoneNum = binding.phone.text.toString()
        if (phoneNum.isNotEmpty()) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNum) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // Handle different cases of verification failure
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    // Invalid request
                }
                is FirebaseTooManyRequestsException -> {
                    // The SMS quota for the project has been exceeded
                }
                is FirebaseAuthMissingActivityForRecaptchaException -> {
                    // reCAPTCHA verification attempted with null Activity
                }
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            val otpIntent = Intent(this@LoginActivity, OTPactivity::class.java)
            otpIntent.putExtra("otpcr", verificationId)
            startActivity(otpIntent)
            finish()
        }
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
}
