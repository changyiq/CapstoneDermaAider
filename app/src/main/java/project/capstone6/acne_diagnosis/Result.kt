package project.capstone6.acne_diagnosis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import project.capstone6.acne_diagnosis.Intro.IntroActivity
import project.capstone6.acne_diagnosis.databinding.ActivityResultBinding
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class Result : AppCompatActivity() {

    // properties declaration
    private lateinit var binding3: ActivityResultBinding
    private lateinit var btnAgain: Button
    private lateinit var btnExit: Button
    private lateinit var skinProblem: TextView
    private lateinit var hybirdLink1: TextView
    private lateinit var hybirdLink2: TextView
    private lateinit var picPath: String
    private lateinit var symptom: String
    private lateinit var linkList: List<String>
    private lateinit var linkList2: List<String>
    private lateinit var receivedImage: ByteArray
    private lateinit var resultFromResponse: String
    private lateinit var tv6: TextView

    var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding3 = ActivityResultBinding.inflate(LayoutInflater.from(this))
        setContentView(binding3.root)

        // initializing
        btnAgain = binding3.btnAgain
        btnExit = binding3.btnExit
        hybirdLink1 = binding3.medicalRtv1
        hybirdLink2 = binding3.medicalRtv2
        symptom = ""
        linkList = listOf()
        linkList2 = listOf()
        skinProblem = binding3.skinProblem
        resultFromResponse = ""
        receivedImage = byteArrayOf()
        tv6 = binding3.tv6

        //Get intent obj
        val intent = getIntent()
        if (intent != null) {
            picPath = intent.getStringExtra(TakeSelfie.EXTRA_FULLDIRECTORY).toString()
            if (intent.getByteArrayExtra("ImageFile") != null) {
                receivedImage = intent.getByteArrayExtra("ImageFile")!!
            }
        }

        btnAgain.setOnClickListener {
            val intent3 = Intent(this, TakeSelfie::class.java)
            startActivity(intent3)
        }

        // Initialise Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth!!.currentUser

        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")

        // get current logged in user
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        btnExit.setOnClickListener {
            logOut()
        }

        handleSSLHandshake()
        // pass image to analyze
        getUser()
        if (receivedImage.isNotEmpty()) {
            getResultFromVolley(receivedImage)
            loadResult()
        } else {
            loadResult()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getUser() {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email.toString()
        if (user != null && email != null) {
            binding3.tv2.text = "Hi $email"
        } else {
            binding3.tv2.text = "Please login"
        }

    }

    @SuppressLint("SetTextI18n")
    private fun loadResult() {

        Log.e("In the loadresult-------", resultFromResponse)

        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")

        // get current logged in user
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        // retriveing old result for exsiting user
        myRef.child(uid.toString()).get().addOnSuccessListener {
            if (it.child("result").exists() && it.child("image").exists()) {
                if (!myRef.child(uid!!).child("result").equals("")) {
                    myRef.child(uid.toString()).child("result").get().addOnSuccessListener {
                        if (it.exists()) {
                            skinProblem.text = "Acne and Rosacea Photos"
                            tv6.visibility = View.INVISIBLE
                            // code to get the response from api and filter the keyword of the symptom and provide user
                            // medical resources
                            for (sym in SymptomEnum.values()) {
                                val temp = sym.symptom
                                if (skinProblem.text.toString().contains(temp)) {
                                    Log.e("5.5-------", "5.5")
                                    symptom = sym.symptom
                                    //Get column from the table
                                    // if exists, then fetch the data and update the UI
                                    myRef.child(uid.toString()).get().addOnSuccessListener {
                                        if (it.exists()) {
                                            Log.e("6-------", "6")
                                            if (it.child("image").exists()) {
                                                //symptom = SymptomEnum.AR.symptom
                                                myRef.child(uid.toString()).child("result")
                                                    .setValue(symptom)
                                                if (getWebsite(symptom).isNotEmpty()) {
                                                    if (getWebsite(symptom).size > 1) {
                                                        Log.e("6-------", getWebsite(symptom)[0])
                                                        hybirdLink1.text = setTextHtml(
                                                            "<a href=${
                                                                getWebsite(symptom)[0]
                                                            }>${getTitle(symptom)[0]}</a>"
                                                        )
                                                        hybirdLink2.text = setTextHtml(
                                                            "<a href=${
                                                                getWebsite(symptom)[1]
                                                            }>${getTitle(symptom)[1]}</a>"
                                                        )

                                                        // pass the url info based on the clicked link
                                                        val intent =
                                                            Intent(this, Website::class.java)
                                                        hybirdLink1.setOnClickListener() {
                                                            intent.putExtra(
                                                                "URL",
                                                                getWebsite(symptom)[0]
                                                            )
                                                            startActivity(intent)
                                                        }
                                                        hybirdLink2.setOnClickListener() {
                                                            intent.putExtra(
                                                                "URL",
                                                                getWebsite(symptom)[1]
                                                            )
                                                            startActivity(intent)
                                                        }
                                                    } else {
                                                        hybirdLink1.text = setTextHtml(
                                                            "<a href=${getWebsite(symptom)[0]}>${
                                                                getTitle(symptom)[0]
                                                            }</a>"
                                                        )
                                                        // pass the url info based on the clicked link
                                                        val intent =
                                                            Intent(this, Website::class.java)
                                                        hybirdLink1.setOnClickListener() {
                                                            intent.putExtra(
                                                                "URL",
                                                                getWebsite(symptom)[0]
                                                            )
                                                            startActivity(intent)
                                                        }
                                                    }
                                                } else {
                                                    // if no symptom found, then no medical resources provided, set it to ...
                                                    hybirdLink1.isClickable = false
                                                    hybirdLink2.isClickable = false
                                                    hybirdLink1.text = "..."
                                                    hybirdLink2.text = "..."
                                                }

                                            } else {
                                                tv6.visibility = View.VISIBLE
                                                skinProblem.text = "You have not made any analysis"
                                                Toast.makeText(
                                                    this,
                                                    "You have not made any analysis",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            Toast.makeText(this, "Invalid user", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                                } else {
                                    //Toast.makeText(this, "Cannot be diagnosed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    tv6.visibility = View.VISIBLE
                    skinProblem.text = "You have not made any analysis"
                    Toast.makeText(
                        this,
                        "You have not made any analysis",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (!it.child("result").exists()) {
                Log.w("no result no assessment", "NOOOOOOOO")
                tv6.visibility = View.VISIBLE
                skinProblem.text = "You have not made any analysis"
                Toast.makeText(
                    this,
                    "You have not made any analysis",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.w("No result then add result into firebase-------", "check")
                // Retrieving result value from textView which is from api
                // resultFromResponse = skinProblem.text.toString()

                resultFromResponse = "Acne and Rosacea Photos"

                myRef.child(uid!!).child("result").setValue(resultFromResponse)
                // code to get the response from api and filter the keyword of the symptom and provide user
                // medical resources
                tv6.visibility = View.INVISIBLE
                for (sym in SymptomEnum.values()) {
                    val temp = sym.symptom
                    Log.e("1-------", "1")
                    if (resultFromResponse.contains(temp)) {
                        Log.e("2-------", "2")
                        symptom = sym.symptom
                        //Get column from the table
                        // if exists, then fetch the data and update the UI
                        myRef.child(uid.toString()).get().addOnSuccessListener {
                            if (it.exists()) {
                                if (it.child("image").exists()) {
                                    Log.e("3-------", "3")
                                    //symptom = SymptomEnum.AR.symptom
                                    myRef.child(uid.toString()).child("result").setValue(symptom)
                                    if (getWebsite(symptom).isNotEmpty()) {
                                        if (getWebsite(symptom).size > 1) {
                                            hybirdLink1.text = setTextHtml(
                                                "<a href=${getWebsite(symptom)[0]}>${
                                                    getTitle(symptom)[0]
                                                }</a>"
                                            )
                                            Log.e("Link of 2------->", hybirdLink1.text.toString())
                                            hybirdLink2.text = setTextHtml(
                                                "<a href=${getWebsite(symptom)[1]}>${
                                                    getTitle(symptom)[1]
                                                }</a>"
                                            )

                                            // pass the url info based on the clicked link
                                            val intent = Intent(this, Website::class.java)
                                            hybirdLink1.setOnClickListener() {
                                                intent.putExtra(
                                                    "URL",
                                                    getWebsite(symptom)[0]
                                                )
                                                startActivity(intent)
                                            }
                                            hybirdLink2.setOnClickListener() {
                                                intent.putExtra(
                                                    "URL",
                                                    getWebsite(symptom)[1]
                                                )
                                                startActivity(intent)
                                            }
                                        } else {
                                            hybirdLink1.text = setTextHtml(
                                                "<a href=${getWebsite(symptom)[0]}>${
                                                    getTitle(symptom)[0]
                                                }</a>"
                                            )
                                            Log.e("Link of 1------->", hybirdLink1.text.toString())
                                            // pass the url info based on the clicked link
                                            val intent = Intent(this, Website::class.java)
                                            hybirdLink1.setOnClickListener() {
                                                intent.putExtra("URL", getWebsite(symptom)[0])
                                                startActivity(intent)
                                            }
                                        }
                                    } else {
                                        // if no symptom found, then no medical resources provided, set it to ...
                                        hybirdLink1.isClickable = false
                                        hybirdLink2.isClickable = false
                                        hybirdLink1.text = "..."
                                        hybirdLink2.text = "..."
                                    }

                                } else {
                                    skinProblem.text = "You have not made any analysis"
                                }
                            } else {
                                Toast.makeText(this, "Invalid user", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Cannot be diagnosed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }

    fun setTextHtml(html: String): Spanned {
        val result: Spanned =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(html)
            }
        return result
    }

    // get website links based on the symptom using enum classes
    private fun getWebsite(sym: String): List<String> {
        when (sym) {
            SymptomEnum.AD.symptom -> {
                linkList = listOf(MedicalResourcesEnum.AD.website)
            }
            SymptomEnum.AM.symptom -> {
                linkList = listOf(MedicalResourcesEnum.AM.website)
            }
            SymptomEnum.AR.symptom -> {
                linkList =
                    listOf(MedicalResourcesEnum.AR1.website, MedicalResourcesEnum.AR2.website)
            }
            SymptomEnum.BD.symptom -> {
                linkList = listOf(MedicalResourcesEnum.BD.website)
            }
            SymptomEnum.CI.symptom -> {
                linkList = listOf(MedicalResourcesEnum.CI.website)
            }
            SymptomEnum.EC.symptom -> {
                linkList = listOf(MedicalResourcesEnum.EC.website)
            }
            SymptomEnum.EDE.symptom -> {
                linkList = listOf(MedicalResourcesEnum.EDE.website)
            }
            SymptomEnum.HAIR.symptom -> {
                linkList = listOf(MedicalResourcesEnum.HAIR.website)
            }
            SymptomEnum.HPV.symptom -> {
                linkList = listOf(MedicalResourcesEnum.HPV.website)
            }
            SymptomEnum.PI.symptom -> {
                linkList = listOf(MedicalResourcesEnum.PI.website)
            }
            SymptomEnum.CTD.symptom -> {
                linkList = listOf(MedicalResourcesEnum.CTD.website)
            }
            SymptomEnum.MM.symptom -> {
                linkList =
                    listOf(MedicalResourcesEnum.MM1.website, MedicalResourcesEnum.MM2.website)
            }
            SymptomEnum.NAIL.symptom -> {
                linkList = listOf(MedicalResourcesEnum.NAIL.website)
            }
            SymptomEnum.CD.symptom -> {
                linkList = listOf(MedicalResourcesEnum.CD.website)
            }
            SymptomEnum.PSO.symptom -> {
                linkList = listOf(MedicalResourcesEnum.PSO.website)
            }
            SymptomEnum.SLD.symptom -> {
                linkList = listOf(MedicalResourcesEnum.SLD.website)
            }
            SymptomEnum.SK.symptom -> {
                linkList = listOf(MedicalResourcesEnum.SK.website)
            }
            SymptomEnum.SD.symptom -> {
                linkList = listOf(MedicalResourcesEnum.SD.website)
            }
            SymptomEnum.TRC.symptom -> {
                linkList = listOf(MedicalResourcesEnum.TRC.website)
            }
            SymptomEnum.UH.symptom -> {
                linkList = listOf(MedicalResourcesEnum.UH.website)
            }
            SymptomEnum.VP.symptom -> {
                linkList = listOf(MedicalResourcesEnum.VP.website)
            }
            SymptomEnum.VT.symptom -> {
                linkList = listOf(MedicalResourcesEnum.VT.website)
            }
            SymptomEnum.WM.symptom -> {
                linkList = listOf(MedicalResourcesEnum.WM.website)
            }
        }
        return linkList
    }

    // get website titles based on the symptom using enum classes
    private fun getTitle(sym: String): List<String> {
        when (sym) {
            SymptomEnum.AD.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.AD.title)
            }
            SymptomEnum.AM.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.AM.title)
            }
            SymptomEnum.AR.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.AR1.title, MedicalResourcesEnum.AR2.title)
            }
            SymptomEnum.BD.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.BD.title)
            }
            SymptomEnum.CI.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.CI.title)
            }
            SymptomEnum.EC.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.EC.title)
            }
            SymptomEnum.EDE.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.EDE.title)
            }
            SymptomEnum.HAIR.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.HAIR.title)
            }
            SymptomEnum.HPV.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.HPV.title)
            }
            SymptomEnum.PI.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.PI.title)
            }
            SymptomEnum.CTD.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.CTD.title)
            }
            SymptomEnum.MM.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.MM1.title, MedicalResourcesEnum.MM2.title)
            }
            SymptomEnum.NAIL.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.NAIL.title)
            }
            SymptomEnum.CD.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.CD.title)
            }
            SymptomEnum.PSO.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.PSO.title)
            }
            SymptomEnum.SLD.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.SLD.title)
            }
            SymptomEnum.SK.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.SK.title)
            }
            SymptomEnum.SD.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.SD.title)
            }
            SymptomEnum.TRC.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.TRC.title)
            }
            SymptomEnum.UH.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.UH.title)
            }
            SymptomEnum.VP.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.VP.title)
            }
            SymptomEnum.VT.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.VT.title)
            }
            SymptomEnum.WM.symptom -> {
                linkList2 = listOf(MedicalResourcesEnum.WM.title)
            }
        }
        return linkList2
    }

    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    fun handleSSLHandshake() {
        try {
            val trustAllCerts: Array<TrustManager> =
                arrayOf<TrustManager>(object : X509TrustManager {
                    val acceptedIssuers: Array<Any?>?
                        get() = arrayOfNulls(0)

                    override fun checkClientTrusted(
                        certs: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun checkServerTrusted(
                        certs: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        TODO("Not yet implemented")
                    }
                })
            val sc: SSLContext = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
            HttpsURLConnection.setDefaultHostnameVerifier(object : HostnameVerifier {
                override fun verify(arg0: String?, arg1: SSLSession?): Boolean {
                    return true
                }
            })
        } catch (ignored: java.lang.Exception) {
        }
    }

    // sending request to get the result response from api
    fun getResultFromVolley(image: ByteArray) {

        Log.e("9-------", "9")
        val url2: String = "https://10.0.2.2:5001/api/Image"

        // converting to image encoded string
        val imageString = Base64.encodeToString(image, Base64.DEFAULT)

        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")

        // get current logged in user
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        //fetching image result from server
        val request2: StringRequest = object : StringRequest(
            Method.POST, url2,
            Response.Listener { response ->
                // Process the json
                try {
                    Log.e("10-------", "10")
                    // pass value on UI textView from received result
                    skinProblem.text = "Acne and Rosacea Photos"
                    resultFromResponse = "Acne and Rosacea Photos"
                    myRef.child(uid.toString()).child("result").setValue(resultFromResponse)
//                    skinProblem.text = response.toString()
                    /**
                     * WAITING FOR CONFIGURE
                     */
                } catch (e: Exception) {
                    Toast.makeText(this, "Exception: $e", Toast.LENGTH_LONG).show()
                }

            }, Response.ErrorListener { volleyError ->
                Toast.makeText(
                    this,
                    "Some error occurred -> $volleyError",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("Volley Error-----------", "${volleyError.cause}")
                Log.e("Volley Error-----------", "${volleyError.message}")

            }) {
            //adding parameters to send
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val parameters: MutableMap<String, String> = HashMap()
                parameters["image"] = imageString
                return parameters
            }
        }

        // Add the volley post request to the request queue
        VolleySingleton.getInstance(this).addToRequestQueue(request2)
    }

    fun logOut() {
        startActivity(Intent(applicationContext, LoginActivity::class.java))

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
        val currentUser = firebaseAuth!!.currentUser
        firebaseAuth!!.signOut()
        LoginManager.getInstance().logOut()
        finish()
    }

    //process menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.resultmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_products -> {
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
            true
        }

        R.id.action_picture -> {
            val intent = Intent(this, TakeSelfie::class.java)
            startActivity(intent)
            true
        }
        R.id.action_intro -> {
            val intent = Intent(this, IntroActivity::class.java)
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

}
