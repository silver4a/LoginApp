package com.example.login.Fragments.LoginActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.login.Firebase.AuthFirebase
import com.example.login.R
import kotlinx.android.synthetic.main.register_layout.*

class RegisterFragment : Fragment() {

    lateinit var authFirebase: AuthFirebase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RegisterFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authFirebase = AuthFirebase.getInstance(context)

        //Register click
        btn_register.setOnClickListener(View.OnClickListener {
            val txt_name = r_names.text.toString()
            val txt_lastname = r_names.text.toString()
            val txt_ide = r_usuario.text.toString()
            val txt_email = r_email.text.toString()
            val txt_password = r_password.text.toString()
            val txt_re_password = r_confirm_password.text.toString()
            //Blank sides
            if(txt_name.isEmpty() || txt_lastname.isEmpty() || txt_ide.isEmpty()){
                Toast.makeText(context,"Complete toda la informacion",Toast.LENGTH_SHORT).show()
            }
            else {
                //Validation
                if (txt_email.isEmpty()) {
                    Toast.makeText(context, "Ingrese su email", Toast.LENGTH_SHORT).show()
                } else if (!txt_email.contains("@") && !txt_email.contains(".com")) {
                    Toast.makeText(context, "Ingrese un email valido", Toast.LENGTH_SHORT).show()
                } else if (txt_password.isEmpty() || txt_password.length < 5) {
                    Toast.makeText(
                        context,
                        "Su contraseña debe tener al menos 5 caracteres",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (txt_password != txt_re_password) {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT)
                        .show()
                } else {

                    //Map data
                    val mapUser = mapOf<String, String>(
                        "names" to r_names.text.toString(),
                        "lastnames" to r_lastnames.text.toString(),
                        "email" to r_email.text.toString(),
                        "username" to r_usuario.text.toString()
                    )
                    authFirebase.register(
                        r_email.text.toString(),
                        r_password.text.toString(),
                        mapUser,
                        true
                    )
                }
            }
        })
    }
}