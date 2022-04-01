package org.ksetrin.ksetrin.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.ktx.initialize
import org.ksetrin.ksetrin.R

class RoutingActivity : Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { true }
        val options = FirebaseOptions.Builder()
            .setProjectId(getString(R.string.project_id))
            .setApplicationId(getString(R.string.google_app_id))
            .setApiKey(getString(R.string.google_api_key))
            .build()
        Firebase.initialize(this, options, "secondary")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}