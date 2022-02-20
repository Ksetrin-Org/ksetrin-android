package org.ksetrin.ksetrin.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class RoutingActivity : Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { true }
        val sharedPreferences = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("loggedIn", false)){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else{
            //val intent = Intent(this, LoginActivity::class.java)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}