package org.ksetrin.ksetrin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import org.ksetrin.ksetrin.R
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var phoneNumberEditText : TextInputEditText
    private lateinit var sendOtpButton : MaterialButton
    private lateinit var otpEditText : TextInputEditText
    private lateinit var confirmOtpButton : MaterialButton
    private lateinit var resendOtpButton : MaterialButton
    private lateinit var linearLayout1 : LinearLayout
    private lateinit var linearLayout2 : LinearLayout


    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        initCallbacks()
        initListeners()
    }

    private fun initViews() {
        phoneNumberEditText = findViewById(R.id.loginActivityPhoneInput)
        sendOtpButton = findViewById(R.id.loginActivitySendOtpButton)
        confirmOtpButton = findViewById(R.id.loginActivityConfirmOtpButton)
        resendOtpButton = findViewById(R.id.loginActivityResendOtpButton)
        otpEditText = findViewById(R.id.loginActivityOtpInput)
        linearLayout1 = findViewById(R.id.loginActivityLinearLayout1)
        linearLayout2 = findViewById(R.id.loginActivityLinearLayout2)
    }

    private fun initCallbacks() {
        auth = FirebaseAuth.getInstance()
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                println(p0)
                println("On Verification Complete")
                signInWithPhoneAuthCredential(p0)
            }
            override fun onVerificationFailed(p0: FirebaseException) {
                println(p0)
                println("On Verification Failed")
            }
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                println("On Code Sent")
                storedVerificationId = verificationId
                resendToken = token
                goNextScreen()
            }
        }
    }

    private fun initListeners() {
        sendOtpButton.setOnClickListener {
            startPhoneNumberVerification(phoneNumberEditText.text.toString())
        }
        resendOtpButton.setOnClickListener {
            resendVerificationCode(storedVerificationId, resendToken)
        }

        confirmOtpButton.setOnClickListener {
            verifyPhoneNumberWithCode(storedVerificationId, otpEditText.text.toString())
        }
    }

    private fun goNextScreen() {
        linearLayout1.visibility = View.GONE
        linearLayout2.visibility = View.VISIBLE
    }

    private fun goPreviousScreen() {
        linearLayout1.visibility = View.VISIBLE
        linearLayout2.visibility = View.GONE
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        println(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    println("signInWithCredential:success")
                    val user = task.result?.user
                    println(user)

                } else {
                    println("signInWithCredential:failure")
                }
            }
    }

    private fun resendVerificationCode (phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken?) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
        if (token != null) {
            optionsBuilder.setForceResendingToken(token)
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    override fun onBackPressed() {
        if (linearLayout2.visibility == View.VISIBLE) {
            goPreviousScreen()
        } else {
            super.onBackPressed()
        }
    }
}