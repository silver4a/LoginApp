package com.example.login.Fragments.MainActivity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.login.Activities.MainActivity
import com.example.login.Firebase.AuthFirebase
import com.example.login.Firebase.DatabaseFirebase
import com.example.login.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.main_layout.*
import java.lang.Exception


lateinit var userFirebase: FirebaseUser
lateinit var authFirebase: AuthFirebase
lateinit var urlDatabase : String

class MainFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(firebaseUser: FirebaseUser) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    userFirebase = firebaseUser
                }
            }
    }

    //********************************************************************************

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authFirebase = AuthFirebase.getInstance(context)
        urlDatabase = authFirebase.getUrlDatabase()
        assignVales()
        btn_logOut.setOnClickListener(this)

    }

    //AssignValues
    private fun assignVales(){
        authFirebase.getImg(img_user)
        welcome_msj.setText("Welcome ${userFirebase.displayName?.split(" ")?.get(0)}")
        user_uid.setText("UID: " + userFirebase.uid.toString())
        email_user.setText("Email: ${userFirebase.email}")
        name_user.setText("Nombre: ${userFirebase.displayName}")
        username_user.setText("Usuario: ")

        //Create builder from dialog.
        val builder = android.app.AlertDialog.Builder(context)

        Handler(Looper.getMainLooper()).postDelayed({
            DatabaseFirebase.getInstance(context).read(
                urlDatabase,
                false,
                object : DatabaseFirebase.DatabaseResponse {
                    override fun onResponse(dataSnapshot: DataSnapshot) {
                        //OnSucess!
                        println(urlDatabase);
                        val username: String = dataSnapshot.child("username").value.toString()
                        println("username: $username")
                        if (username.equals("null") || username.isEmpty()) {
                            authFirebase.alertDialog(builder,"", "Nombre de usuario",
                                object : AuthFirebase.DialogResponse {

                                    override fun onSucess(value: String?) {
                                        authFirebase.write(urlDatabase + "/username", value)
                                        username_user.setText("Usuario: $value")
                                    }
                                })
                        } else {
                            username_user.setText("Usuario: $username")
                        }
                    }

                    override fun onFail(databaseError: DatabaseError?) {
                        try {
                            Toast.makeText(
                                context,
                                "Ocurrió un error al recuperar los datos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }catch (e:Exception){
                            println("Error de recuperacion de datos -> "+e);
                        }
                    }
                })
        }, 1500)
    }


    //OnClick
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_logOut -> {
                Toast.makeText(context, "Ha cerrado la sesión", Toast.LENGTH_SHORT).show()
                authFirebase.signOut()
                val activity = context as Activity
                (getActivity() as MainActivity?)?.callLoginActivity()
                activity.finish()
            }
        }
    }
}