package project.capstone6.acne_diagnosis

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    var firebaseAuth: FirebaseAuth? = null
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

//        val myNewsList = generateNewsList(6)

//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        recyclerView.adapter = MyRecyclerView(myNewsList)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.setHasFixedSize(true)

    }

    private fun generateNewsList(size: Int): List<HomePageModel> {

        val list = ArrayList<HomePageModel>()

        for (i in 1 until size) {
            val drawable = when (i % 5) {
                0 -> R.drawable.panoxyl_acne_creamy_wash
                1 -> R.drawable.medical_
                2 -> R.drawable.la_roche_acne_treatment
                3 -> R.drawable.differin_adapalene_gel
                else -> R.drawable.cerave_facial_moisturizing_lotion
            }

            val adsTitle = when (i % 5) {
                0 -> getString(R.string.adsTitle1)
                1 -> getString(R.string.adsTitle2)
                2 -> getString(R.string.adsTitle3)
                3 -> getString(R.string.adsTitle4)
                else -> getString(R.string.adsTitle5)
            }

            val adsContent = when (i % 3) {
                0 -> getString(R.string.adsContent1)
                1 -> getString(R.string.adsContent2)
                2 -> getString(R.string.adsContent3)
                3 -> getString(R.string.adsContent4)
                else -> getString(R.string.adsContent5)
            }

            val item = HomePageModel(drawable, adsTitle, adsContent)

            list += item
        }

        return list
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_result -> {
            val intent = Intent(this, Result::class.java)
            startActivity(intent)
            true
        }
        R.id.action_analyze -> {
            val intent = Intent(this, TakeSelfie::class.java)
            startActivity(intent)
            true
        }
        R.id.action_exit -> {
            logOut()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    // menu option to log out
    fun logOut() {

        startActivity(Intent(applicationContext, LoginActivity::class.java))
        firebaseAuth = FirebaseAuth.getInstance()
        //val currentUser: FirebaseUser? = firebaseAuth!!.currentUser

        // get google sign in status
        GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        )
            .signOut()
            .addOnSuccessListener { startActivity(Intent(this, LoginActivity::class.java)) }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Sign out failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        finish()

        // Facebook login status check
        firebaseAuth!!.signOut()
        LoginManager.getInstance().logOut()
        finish()
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