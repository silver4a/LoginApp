package com.example.login.Firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UIFirebase {
    public static UIFirebase instance;

    Context context;
    AuthFirebase.OnSignUpResponse onSignUpResponse;
    public static final int RC_SIGN_IN = 363;


    public static synchronized UIFirebase getInstance(Context context){
        if(instance == null){
            instance = new UIFirebase(context);
        }
        return instance;
    }

    public UIFirebase(Context context) {
        this.context = context;
    }

    public List<AuthUI.IdpConfig> getPhoneProvider(){
        //Providers
        return Collections.singletonList(
                new AuthUI.IdpConfig.PhoneBuilder().build());
    }

    public List<AuthUI.IdpConfig> getFacebookProvider(){
        //Providers
        return Collections.singletonList(
                new AuthUI.IdpConfig.FacebookBuilder().build());
    }

    public void signIn(String provider,AuthFirebase.OnSignUpResponse onSignUpResponse){
        List<AuthUI.IdpConfig> providers;
        switch (provider){
            case "phone":
                 providers = getPhoneProvider();
                break;
            case "facebook":
                providers = getFacebookProvider();
                break;
            default:
                return;
        }
        //Assign response interface.
        this.onSignUpResponse = onSignUpResponse;
        // Create and launch sign-in intent
        Activity activity = (Activity)context;
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public void activityRespose(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                onSignUpResponse.onSucess(user);

            } else {
                AuthFirebase.getInstance(context).showProgressDialog(false);
                Toast.makeText(context, "No se pudo acceder", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
