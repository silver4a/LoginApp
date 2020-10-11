package com.example.login.Fragments.LoginActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.login.Activities.LoginActivity
import com.example.login.Activities.MainActivity
import com.example.login.Firebase.AuthFirebase
import com.example.login.Firebase.AuthGoogle
import com.example.login.R
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.login_layout.*
class LoginFragment : Fragment(),View.OnClickListener {

    lateinit var registerFragment: RegisterFragment
    lateinit var authFirebase: AuthFirebase
    lateinit var onFragmentLoginResponse: OnFragmentLoginResponse

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
        ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(onLoginResponse: OnFragmentLoginResponse?) = LoginFragment().apply {
            arguments = Bundle().apply {
                if (onLoginResponse != null) {
                    onFragmentLoginResponse = onLoginResponse
                }
            }
        }
    }

    //Created!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authFirebase = AuthFirebase.getInstance(context)
        register.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        forgot_password.setOnClickListener(this)
        google_login.setOnClickListener(this)
        registerFragment = RegisterFragment.newInstance()

        //Verifica si ya el usuario se encuentra logeado.
        authFirebase.isSingUp(object : AuthFirebase.OnSignUpResponse{
            override fun onSucess(user: FirebaseUser?) {
                user?.let { onFragmentLoginResponse.onSucess(it) }
            }

        })
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.register -> {
                (getActivity() as LoginActivity?)?.callFragment(registerFragment,true)
            }
            R.id.btn_login ->{
                val txt_email = l_email.text.toString();
                val txt_password = l_password.text.toString()
                if(txt_email.isEmpty()) {
                    Toast.makeText(context, "Ingrese su email", Toast.LENGTH_SHORT).show()
                }
                else if(!txt_email.contains("@") && !txt_email.contains(".com")) {
                    Toast.makeText(context,"Ingrese un email valido",Toast.LENGTH_SHORT).show()
                }else if(txt_password.isEmpty() || txt_password.length < 5){
                    Toast.makeText(context,"Verifique su contraseña",Toast.LENGTH_SHORT).show()
                }else{
                    authFirebase.signUp(
                        l_email.text.toString(),
                        l_password.text.toString(),
                        true,object : AuthFirebase.OnSignUpResponse {
                            override fun onSucess(user: FirebaseUser?) {
                                user?.let { onFragmentLoginResponse.onSucess(it) }
                            }

                        })
                }
            }
            R.id.forgot_password ->{
                val txt_email:String = l_email.text.toString()
                if(txt_email.isEmpty()) {
                    Toast.makeText(context, "Ingrese su email", Toast.LENGTH_SHORT).show()
                }
                else if(!txt_email.contains("@") && !txt_email.contains(".com")) {
                    Toast.makeText(context,"Ingrese un email valido",Toast.LENGTH_SHORT).show()
                }else{
                    authFirebase.passwordRefractor(txt_email)
                }
            }
            R.id.google_login ->{
                Toast.makeText(context,"Iniciando con google. . .",Toast.LENGTH_SHORT).show()
                AuthGoogle.getInstance(context).signIn(object :AuthGoogle.SignInresponse{
                    override fun onSucess(user: FirebaseUser?) {
                        user?.let { onFragmentLoginResponse.onSucess(it) }
                    }
                })
            }
        }
    }
    //Methods



    interface OnFragmentLoginResponse{
        fun onSucess(firebaseUser: FirebaseUser)
    }
}