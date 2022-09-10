package com.abhash.firebasechat

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val txtHaveAccount:TextView = findViewById(R.id.txtHaveAccount)
        val email:TextView=findViewById(R.id.txtEmail)
        val password:TextView=findViewById(R.id.txtPassword)
        val btnSelectPhoto:Button=findViewById(R.id.btnSelectPhoto)
        txtHaveAccount.setOnClickListener{
            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        val btnRegister: Button =findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener{
            performRegister(email,password)
        }

        btnSelectPhoto.setOnClickListener{
            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
    }

    var selectPhotoUri: Uri?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode==Activity.RESULT_OK && data!=null){
            selectPhotoUri=data.data
            //val inputStream=uri?.let { contentResolver.openInputStream(it) }
            //val drawable=Drawable.createFromStream(inputStream,uri.toString())
            val bitmap=MediaStore.Images.Media.getBitmap(contentResolver,selectPhotoUri)
            //val bitmapDrawable=BitmapDrawable(bitmap)
            val btnSelectPhoto:Button=findViewById(R.id.btnSelectPhoto)
            //btnSelectPhoto.setBackgroundDrawable(bitmapDrawable)
            //btnSelectPhoto.background=drawable
            //val imageButton:ImageButton=findViewById(R.id.imageButton)
            //imageButton.setImageBitmap(bitmap)
            val circleImage:CircleImageView=findViewById(R.id.circleImage)
            circleImage.setImageBitmap(bitmap)
            btnSelectPhoto.alpha=0f
        }
    }

    private fun performRegister(email:TextView, password:TextView){
        val email=email.text.toString()
        val pass=password.text.toString()
        if(email.isEmpty() || pass.isEmpty()){
            Toast.makeText(baseContext, "Email and Password cannot be empty",
                Toast.LENGTH_LONG).show()
            return
        }

        // Initialize Firebase Auth
        val auth: FirebaseAuth = Firebase.auth
        auth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Log.d("MainActivity", "createUserWithEmail:success")
                    Toast.makeText(baseContext, "Authentication Success.",
                        Toast.LENGTH_SHORT).show()
                    uploadImageToFirebaseStorage()
                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadImageToFirebaseStorage(){
        if(selectPhotoUri== null) return
        val filename=UUID.randomUUID().toString()
        val ref=FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectPhotoUri!!)
            .addOnSuccessListener {
                Toast.makeText(baseContext, "Upload Successful",
                    Toast.LENGTH_SHORT).show()
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{
                Toast.makeText(baseContext, "Upload UnSuccessful",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid=FirebaseAuth.getInstance().uid ?:""
        val ref=FirebaseDatabase.getInstance("https://fir-chat-b74bb-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("/users/$uid")
        val username:TextView=findViewById(R.id.txtName)
        val user=User(uid,username.text.toString(),profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(baseContext, "Saved to Database",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(baseContext, "Upload UnSuccessful",
                    Toast.LENGTH_SHORT).show()
            }
    }
}

class User(val uid:String, val username:String, val profileImageUrl:String)