package com.example.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.login.Firebase.AuthFirebase
import com.example.login.Fragments.LoginFragment
import com.example.login.Fragments.MainFragment
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity()  {
    //Fragments
    lateinit var loginFragment: LoginFragment
    lateinit var mainFragment: MainFragment
    lateinit var authFirebase:AuthFirebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Auth instance
        authFirebase = AuthFirebase.getInstance(this)
        //Fragments
        loginFragment = LoginFragment.newInstance(object : LoginFragment.OnFragmentLoginResponse{
            override fun onSucess(firebaseUser: FirebaseUser) {
                Toast.makeText(applicationContext,"Welcome "
                        +firebaseUser.displayName +
                        " from MainActivity",Toast.LENGTH_SHORT).show()
                mainFragment = MainFragment.newInstance(firebaseUser)
                callFragment(mainFragment)

            }

        })
        callFragment(loginFragment)
    }

    private fun callFragment(fragment:Fragment, fragmentback:Boolean = false){
        if(fragmentback){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit()
        }
        else{
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    }
}