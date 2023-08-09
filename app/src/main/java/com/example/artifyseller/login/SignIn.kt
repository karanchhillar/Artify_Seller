package com.example.artifyseller.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.artifyseller.MainActivity
import com.example.artifyseller.R
import com.example.artifyseller.UserInformation
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class SignIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private  lateinit var googleSignInClient: GoogleSignInClient
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val email : EditText = findViewById(R.id.email_editText)
        val password : EditText = findViewById(R.id.password_editText2)
        val signInButton : Button = findViewById(R.id.sign_in_button)
        val googleButton : ImageButton = findViewById(R.id.sign_in_with_google)
        val newUserText : TextView = findViewById(R.id.new_user_text)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


        signInButton.setOnClickListener {
            val emailInput = email.text.toString()
            val passwordInput = password.text.toString()

            if (TextUtils.isEmpty(emailInput) || TextUtils.isEmpty(passwordInput) ){
                Toast.makeText(this, "Cannot be Empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(emailInput , passwordInput)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful){
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    else{
                        Toast.makeText(this, "${task.exception}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        newUserText.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }



//         sign in with google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this , gso)

        googleSignInClient.signOut()


        googleButton.setOnClickListener{
            signInGoogle()
        }

    }
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }else{
                Toast.makeText(this , task.exception.toString() , Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken , null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    val intent : Intent = Intent(this , UserInformation::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this , it.exception.toString() , Toast.LENGTH_SHORT).show()
                }
            }
    }
}