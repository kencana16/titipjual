package com.kencana.titipjual

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.kencana.titipjual.data.UserPreferences
import com.kencana.titipjual.ui.auth.AuthActivity
import com.kencana.titipjual.utils.startNewActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val userPreferences = UserPreferences(this)

        userPreferences.isLoggedIn.asLiveData().observe(
            this,
            Observer {
//                val activity = if (it == false) AuthActivity::class.java else TabMainActivity::class.java
                val activity = if (it == false) AuthActivity::class.java else MainActivity::class.java
                lifecycleScope.launch {
                    delay(2000L)
                    startNewActivity(activity)
                }
            }
        )
    }
}
