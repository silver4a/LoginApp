package com.example.login.Fragments.MainActivity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.login.Activities.MainActivity
import com.example.login.Firebase.AuthFirebase
import com.example.login.Firebase.DatabaseFirebase
import com.example.login.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.main_layout.*


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
        urlDatabase = "Users/" + authFirebase.uid
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
        Handler(Looper.getMainLooper()).postDelayed({
            DatabaseFirebase.getInstance(context).read(
                urlDatabase,
                false,
                object : DatabaseFirebase.DatabaseResponse {
                    override fun onResponse(dataSnapshot: DataSnapshot) {
                        //OnSucess!
                        println(urlDatabase);
                        val username:String = dataSnapshot.child("username").value.toString()
                        println("username: $username")
                        if(username.equals("null") || username.isEmpty()){
                            usernameAlert(object : DialogResponse {
                                override fun onSucess(value: String?) {
                                    authFirebase.write(urlDatabase+"/username", value)
                                    username_user.setText("Usuario: $value")
                                }
                            })
                        }else{
                            username_user.setText("Usuario: $username")
                        }
                    }

                    override fun onFail(databaseError: DatabaseError?) {
                        Toast.makeText(
                            context,
                            "Ocurrió un error al recuperar los datos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }, 1500)
    }

    fun usernameAlert(dialogResponse: DialogResponse) {
        val builder = AlertDialog.Builder(
            context!!
        )
        // Set up the input
        val input = EditText(context)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setHint("Nombre de usuario")
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK") { dialog, which ->
            val m_Text = input.text.toString()
            dialogResponse.onSucess(m_Text)
        }
        builder.setCancelable(false)
        builder.show()
    }

    interface DialogResponse {
        fun onSucess(value: String?)
    }


    private fun callFragment(fragment: Fragment, fragmentback: Boolean = false){
        val fragmentManager: FragmentManager = activity!!.supportFragmentManager
        if(fragmentback){
            fragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout_login, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit()
        }
        else{
            fragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout_login, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
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