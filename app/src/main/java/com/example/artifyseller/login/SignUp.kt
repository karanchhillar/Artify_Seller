package com.example.artifyseller.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.artifyseller.MainActivity
import com.example.artifyseller.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val email : EditText = findViewById(R.id.email)
        val password : EditText = findViewById(R.id.password)
        val confirmPassword : EditText = findViewById(R.id.confirm_password)
        val signUpButton : Button = findViewById(R.id.sign_up_button)

        val auth = FirebaseAuth.getInstance()

        signUpButton.setOnClickListener {
            // isko bahar bnao toh nhi chlta
            val emailInput = email.text.toString()
            val passwordInput = password.text.toString()
            val confirmPasswordInput = confirmPassword.text.toString()

            if (TextUtils.isEmpty(emailInput) || TextUtils.isEmpty(passwordInput) || TextUtils.isEmpty(confirmPasswordInput)) {
                Toast.makeText(this, "Cannot be Empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (passwordInput.length < 6) {
                Toast.makeText(this, "Password should be more than 6 letters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (passwordInput != confirmPasswordInput) {
                Toast.makeText(this, "Both are not same", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(emailInput , passwordInput)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this, "New Id created", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    else{
                        Toast.makeText(this, "${task.exception}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }
}