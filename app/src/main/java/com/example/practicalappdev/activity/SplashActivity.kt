package com.example.practicalappdev.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.practicalappdev.R
import com.example.practicalappdev.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_SCREEN_DURATION:Long = 2000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {


        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.ivImage.startAnimation(animation)

        Handler().postDelayed(Runnable {
            // Start the main activity
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
            finish() // Finish the splash screen activity
        }, SPLASH_SCREEN_DURATION)

    }
}