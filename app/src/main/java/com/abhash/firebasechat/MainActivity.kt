package com.abhash.firebasechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val txtHaveAccount:TextView = findViewById(R.id.txtHaveAccount)
        val email:TextView=findViewById(R.id.txtEmail)
        val password:TextView=findViewById(R.id.txtPassword)
        txtHaveAccount.setOnClickListener{
            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        val btnRegister: Button =findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener{
            performRegister(email,password)
        }
    }

    fun performRegister(email:TextView,password:TextView){
        val email=email.text.toString()
        val pass=password.text.toString()
        if(email.isEmpty() || pass.isEmpty()){
            Toast.makeText(baseContext, "Email and Password cannot be empty",
                Toast.LENGTH_LONG).show()
            return
        }

        // Initialize Firebase Auth
        var auth: FirebaseAuth = Firebase.auth
        auth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Log.d("MainActivity", "createUserWithEmail:success")
                    Toast.makeText(baseContext, "Authentication Success.",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}