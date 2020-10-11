package com.example.login.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.login.Firebase.AuthFirebase
import com.example.login.Firebase.AuthGoogle
import com.example.login.Fragments.LoginActivity.LoginFragment
import com.example.login.Fragments.MainActivity.MainFragment
import com.example.login.R
import com.google.firebase.auth.FirebaseUser
import java.util.*


class LoginActivity : AppCompatActivity() {
    //Fragments
    lateinit var loginFragment: LoginFragment
    lateinit var mainFragment: MainFragment
    lateinit var authFirebase: AuthFirebase

    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Auth instance
        authFirebase = AuthFirebase.getInstance(this)
        //Fragments
        loginFragment = LoginFragment.newInstance(object : LoginFragment.OnFragmentLoginResponse {
            override fun onSucess(firebaseUser: FirebaseUser) {
                println("User: ${firebaseUser.displayName}")
                println("Email: ${firebaseUser.email}")
                println("Phone: ${firebaseUser.phoneNumber}")
                println("Uid: ${firebaseUser.uid}")
                println("UrlPhoto: ${firebaseUser.photoUrl}")
                println("Provider: ${firebaseUser.providerId}")
                println("Verify: ${firebaseUser.isEmailVerified}")
                mainFragment = MainFragment.newInstance(firebaseUser)
                callMainActivity()
            }

        })
        callFragment(loginFragment)
        progressDialog = ProgressDialog(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vprogressDialog(true,"","Iniciando sesión")
        AuthGoogle.getInstance(this).activityRespose(requestCode, resultCode, data)
    }

    private fun callMainActivity(){
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun vprogressDialog(view:Boolean,title:String = "",msj:String = ""){
        try {
            if (!view)
                progressDialog.dismiss()
            else {
                progressDialog.setTitle(title)
                progressDialog.setMessage(msj)
                progressDialog.setCancelable(true)
                progressDialog.show()
            }
        }catch (e:Exception){

        }
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