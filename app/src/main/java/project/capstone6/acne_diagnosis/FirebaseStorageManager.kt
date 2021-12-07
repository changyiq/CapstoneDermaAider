/*
 * Name: Xiaohong Deng
 * Student ID: 991517517
 * Assignment: Capstone Project - DermaAider APP
 * Dec 12, 2021
 *
 * Description of FirebaseStorageManager class:
 * This activity is to upload image to firebase
 *
 * @author dengxiao
* */

package project.capstone6.acne_diagnosis

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class FirebaseStorageManager {

    private val mStorageRef = FirebaseStorage.getInstance().reference
    private lateinit var mProgressDialog: ProgressDialog
    private var user: FirebaseUser? = null
    private lateinit var subDir: String

    //upload image into firebase cloud.
    fun uploadImage(context: Context, imageFileUri: Uri):String {
        mProgressDialog = ProgressDialog(context)
        mProgressDialog.setMessage("Please wait, image being upload")
        mProgressDialog.show()
        val date = Date()
        user = FirebaseAuth.getInstance().getCurrentUser();
        subDir = user?.uid + "/${date}/"

        //val uploadTask = mStorageRef.child("Test1/Pictures/${date}.png").putFile(imageFileUri)
        val uploadTask = mStorageRef.child(subDir+"Selifie.png").putFile(imageFileUri)
        uploadTask.addOnSuccessListener {
            Log.e("Frebase", "Image Upload success")
            mProgressDialog.dismiss()
            //val uploadedURL = mStorageRef.child("Test1/Pictures/${date}.png").downloadUrl
            val uploadedURL = mStorageRef.child(subDir+"Selifie.png").downloadUrl
            Log.e("Firebase", "Uploaded $uploadedURL")
        }.addOnFailureListener {
            Log.e("Frebase", "Image Upload fail")
            mProgressDialog.dismiss()
        }
        return subDir
    }
}