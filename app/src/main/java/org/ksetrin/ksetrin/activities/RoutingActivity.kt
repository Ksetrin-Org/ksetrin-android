package org.ksetrin.ksetrin.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth

class RoutingActivity : Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { true }
        val sharedPreferences = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            //val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        if (sharedPreferences.getBoolean("loggedIn", false)){}
        else{}
        finish()
    }
}