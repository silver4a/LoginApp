package com.example.login.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.login.Firebase.AuthFirebase
import com.example.login.Fragments.MainActivity.MainFragment
import com.example.login.R
import java.lang.Exception

class MainActivity : AppCompatActivity()  {

    //Fragments
    lateinit var mainFragment:MainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //getIntent().getSerializableExtra("userReference")
        mainFragment = MainFragment.newInstance(AuthFirebase.getInstance(this).user)
        callFragment(mainFragment)
        relogin();

    }

    fun callLoginActivity(){
        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    //Functions.

    fun relogin(){
        Thread(Runnable {
            println("Relogeando. . . ");
            AuthFirebase.getInstance(this).relogin(object : AuthFirebase.ReloginResponse{
                override fun onFail(e: Exception?) {
                    callLoginActivity()
                    finish()
                }
            })
        }).start()
    }

    private fun callFragment(fragment: Fragment, fragmentback: Boolean = false){
        if(fragmentback){
            this.
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout_main, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit()
        }
        else{
            this.
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout_main, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    }
}