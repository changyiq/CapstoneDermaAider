/*
 * Name: Xiaohong Deng
 * Student ID: 991517517
 * Assignment: Capstone Project - DermaAider APP
 * Dec 12, 2021
 *
 * Description of MainActivity class:
 * This activity is to start dermaAider APP and trigger the IntroActivity for first-time users
 *
 * @author dengxiao
* */

package project.capstone6.acne_diagnosis

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import project.capstone6.acne_diagnosis.Intro.IntroActivity

class MainActivity : AppCompatActivity() {

    private var userFirstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Checks whether it's the users first time
        loadData()

        //If it's the users first time, it will go to the introduction page. Otherwise, it will go to the taking picture page
        if (userFirstTime) {

            userFirstTime = false
            saveData()

            val i1 = Intent(this, IntroActivity::class.java)
            startActivity(i1)
            finish()
        } else {

            val i2 = Intent(this, TakeSelfie::class.java)
            startActivity(i2)
            finish()
        }
    }

    private fun saveData() {
        val sp = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE)
        sp.edit().apply {
            putBoolean("BOOLEAN_FIRST_TIME", userFirstTime)
            apply()
        }
    }

    private fun loadData() {
        val sp = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE)
        userFirstTime = sp.getBoolean("BOOLEAN_FIRST_TIME", true)
    }
}