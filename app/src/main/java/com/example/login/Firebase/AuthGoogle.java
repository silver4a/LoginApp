package com.example.login.Firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.login.Firebase.AuthFirebase;
import com.example.login.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;
import java.util.concurrent.Executor;

public class AuthGoogle {

    public static AuthGoogle instance;
    private Context context;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 897;
    public String TAG = "GoogleSignIn";
    private FirebaseAuth mAuth;

    public AuthGoogle(Context context){
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public static synchronized AuthGoogle getInstance(Context context){
        if(instance == null){
            instance = new AuthGoogle(context);
        }
        return instance;
    }

    SignInresponse signInresponse;

    public void signIn(SignInresponse signInresponse) {
        this.signInresponse = signInresponse;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Activity activity = (Activity) context;
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public interface SignInresponse{
        void onSucess(FirebaseUser user);
    }

    public void activityRespose(int requestCode, int resultCode, Intent data){
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("Error google Auth", e.getMessage());
                Toast.makeText(context, "Fallo de incio de sesion", Toast.LENGTH_SHORT).show();
                // ...
            }
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(context, "Success Google", Toast.LENGTH_SHORT).show();
                            AuthFirebase.getInstance(context).user = mAuth.getCurrentUser();
                            signInresponse.onSucess(mAuth.getCurrentUser());
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Fallo al ingresar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
