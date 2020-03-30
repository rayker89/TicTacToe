package com.example.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    private var myAuth: FirebaseAuth? = null
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        myAuth = FirebaseAuth.getInstance()
    }

    fun LoginEvent (view: View) {

        LoginToFirebase(user_email.text.toString(), user_password.text.toString())

    }

    fun LoginToFirebase (email : String, password : String) {

        myAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    var currentUser = myAuth!!.currentUser
                    if(currentUser!=null) {
                        myRef.child("Users").child(SplitString(currentUser.email.toString())).child("Request").setValue(currentUser.uid)
                    }
                    Toast.makeText(applicationContext, "Successifful login!", Toast.LENGTH_SHORT ).show()
                    LoadMain()
                } else {
                    var error = task.exception?.message
                    Toast.makeText(applicationContext, "Error!" + error, Toast.LENGTH_LONG ).show()
                }
            }

    }

    override fun onStart() {
        super.onStart()
        LoadMain()

    }

    fun LoadMain () {
        var currentUser = myAuth!!.currentUser

        if(currentUser != null) {
            var intent = Intent(this@Login, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)

            startActivity(intent)

        }


    }

    fun SplitString (str:String): String {
        var split = str.split("@")
        return split[0]

    }
}
