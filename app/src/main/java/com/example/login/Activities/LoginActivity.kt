package com.example.login.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.login.Firebase.AuthFirebase
import com.example.login.Firebase.AuthGoogle
import com.example.login.Firebase.UIFirebase
import com.example.login.Fragments.LoginActivity.LoginFragment
import com.example.login.Fragments.MainActivity.MainFragment
import com.example.login.R
import com.google.firebase.auth.FirebaseUser
import java.lang.Exception
import java.util.*


class LoginActivity : AppCompatActivity() {
    //Fragments
    lateinit var loginFragment: LoginFragment

    //Authentication lateinit instance.
    lateinit var authFirebase: AuthFirebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Auth instance
        authFirebase = AuthFirebase.getInstance(this)

        //Fragments
        loginFragment = LoginFragment.newInstance(object : AuthFirebase.OnSignUpResponse{
                override fun onSucess(firebaseUser: FirebaseUser) {
                    //USer information.
                    println("User: ${firebaseUser.displayName}")
                    println("Email: ${firebaseUser.email}")
                    println("Phone: ${firebaseUser.phoneNumber}")
                    println("Uid: ${firebaseUser.uid}")
                    println("UrlPhoto: ${firebaseUser.photoUrl}")
                    println("Provider: ${firebaseUser.providerId}")
                    println("Verify: ${firebaseUser.isEmailVerified}")

                    //Call main Activity
                    callMainActivity()
                }
        })
        callFragment(loginFragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //ProgressDialog
        authFirebase.showProgressDialog(true,"","Iniciando sesión")

        //Google authentication. . .
        AuthGoogle.getInstance(this).activityRespose(requestCode, resultCode, data)
        //Phone authentication.
        UIFirebase.getInstance(this).activityRespose(requestCode,resultCode,data)
    }

    //Functions of utillity.
    fun callMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun callFragment(fragment: Fragment, fragmentback: Boolean = false){
        if(fragmentback){
            this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout_login, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(fragment.toString())
                .commit()
        }
        else{
            this.
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout_login, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    }

}