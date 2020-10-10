package com.example.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.example.login.Firebase.AuthFirebase
import com.example.login.Fragments.LoginFragment

class MainActivity : AppCompatActivity()  {
    lateinit var loginFragment: LoginFragment
    lateinit var authFirebase:AuthFirebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Auth instance
        authFirebase = AuthFirebase.getInstance(this)
        //Fragments
        loginFragment = LoginFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout,loginFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}