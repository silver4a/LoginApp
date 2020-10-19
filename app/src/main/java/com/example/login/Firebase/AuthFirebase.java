
package com.example.login.Firebase;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.login.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

public class AuthFirebase extends DatabaseFirebase{
    //Singetlon
    private static AuthFirebase instance;
    //Params
    private Context context;
    protected FirebaseAuth mAuth;
    protected FirebaseUser user;
    ProgressDialog progressDialog;

    public AuthFirebase(Context context){
        super(context);
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this.context);
    }

    public static synchronized AuthFirebase getInstance(Context context){
        if(instance == null){
            instance = new AuthFirebase(context);
        }
        return instance;
    }

    //Register user.
    public void register(final String email, String password, final Map<String,Object> mapUser,final boolean verification){
        final String TAG = "Firebase register";
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //FirebaseUser user = mAuth.getCurrentUser();
                            write("Users/" + getUid(), mapUser);
                            updateUserBasic(mapUser.get("names").toString());
                            if (verification) {
                                sendEmailVerification();
                                signOut();
                            }
                            Toast.makeText(context,
                                    "Usuario creado con éxito" +
                                            (verification ? ", confirme su cuenta en su correo electrónico" : ""),
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(user);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastException(e);
            }
        });
    }

    //LogIn user.
    public void signUp(String email, String password, final boolean verification, final OnSignUpResponse onSigUpResponse){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        updateUserInfo();
                        if (task.isSuccessful()) {
                            if(verification && !user.isEmailVerified()){
                                signOut();
                                Toast.makeText(context, "Verifica tu cuenta en tu correo electronico", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                                onSigUpResponse.onSucess(user);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastException(e);
                showProgressDialog(false);
                //onSigUpResponse.onFail();
            }
        });
    }

    //Interface fragments.
    public interface OnSignUpResponse{
        void onSucess(FirebaseUser user);
        //void onFail();
    }

    //Check if user is signUp -> response trough a interface.
    public void isSingUp(OnSignUpResponse onSignUpResponse){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            onSignUpResponse.onSucess(user);
        }
    }

    //SignOut user.
    public void signOut(){
        mAuth.signOut();
    }

    //Get map from user information.
    public Map<String,String> getUserInformation(FirebaseUser user){

        Map<String,String> info = new HashMap<>();
        if (user != null) {
            info.put("name",user.getDisplayName());
            info.put("email",user.getEmail());
            info.put("urimg",user.getPhotoUrl().getPath());
            info.put("uid",user.getUid());
            info.put("verification",String.valueOf(user.isEmailVerified()));
            return info;
        }
        return info;
    }

    //Update instance FirebaseUser.
    private void updateUserInfo(){
        user = FirebaseAuth.getInstance().getCurrentUser();
    }


    //Update name user.
    public void updateUserBasic(String newName){
        final String TAG = "updateUser";
        updateUserInfo();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }

    //Update email user.
    public void updateEmail(String newEmail){
        final String TAG = "updateEmail";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        user.updateEmail(newEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                        }
                    }
                });
    }

    //Update password user.
    public void updatePassword(String newPassword){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String TAG = "Password update";

        assert user != null;
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });
    }

    //Send email verification.
    public void sendEmailVerification(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        auth.setLanguageCode("es");
        final String TAG = "verification send!";
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

    //Send email refractor password.
    public void passwordRefractor(String emailAddress){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String TAG = "Password Refractor";

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Revise su correo electronico", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Este correo no se encuentra registrado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Remove user from auth server.
    public void deleteUser(){
        final String TAG = "Delete user "+getName();
        updateUserInfo();
        assert user != null;
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    }
                });
    }

    //Getters
    public String getName() {
        try {
            updateUserInfo();
            return user.getDisplayName();
        }catch (Exception e){
            return "";
        }

    }
    public String getEmail(){ updateUserInfo(); return user.getEmail();}
    public Uri getPhotoUrl(){ updateUserInfo(); return user.getPhotoUrl();}
    public String getUid(){ updateUserInfo(); return user.getUid();}
    public String getUrlDatabase(){
        return "Users/"+getUid();
    }
    public FirebaseUser getUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    public void getImg(ImageView imageView){
        try {
            updateUserInfo();
            String url = user.getPhotoUrl().toString();
            //String url = "https://firebasestorage.googleapis.com/v0/b/pruebas-aa804.appspot.com/o/images%2FLana.jpg?alt=media&token=0df4f177-c5b0-4f1c-9ea6-f1895450a0be";
            System.out.println("url: " + url);
            Picasso.with(context).load(url).into(imageView);
        }catch (Exception e){}
    }

    //Setters
    public void setUserFirebase(FirebaseUser user){
        this.user = user;
    }

    //Response login.
    private void updateUI(FirebaseUser user) {
        System.out.println("user: "+getName());
        System.out.println("email: "+getEmail());
        System.out.println("url: "+getPhotoUrl());
        System.out.println("uid: "+getUid());
    }

    //Re-login
    public void relogin(final ReloginResponse reloginResponse){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String provider = "";
        if (user.getProviderData().size() > 0) {
            //Prints Out google.com for Google Sign In, prints facebook.com for Facebook
             provider = user.getProviderData().get(user.getProviderData().size() - 1).getProviderId();
            System.out.println("Provider: -> "+provider);
            AuthCredential credential = null;
            if(provider.contains("password")) {
                credential = EmailAuthProvider
                        .getCredential("jananava44@gmail.com", "123456");
            }else{
                return;
            }
            user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("Usuario re-autenticado");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(e instanceof FirebaseNetworkException) {
                        Toast.makeText(context, "Problemas de red", Toast.LENGTH_SHORT).show();
                        System.out.println("Problemas de red");
                    }
                    else {
                        System.out.println("Error al reautenticar -> " + e);
                        toastException(e);
                        signOut();
                        reloginResponse.onFail(e);
                    }
                }
            });
        }
        //        System.out.println("prov -> "+user.);
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.

        // Prompt the user to re-provide their sign-in credentials
    }
    public interface ReloginResponse{
        void onFail(Exception e);
    }
    //Methods utilitys
    public void showProgressDialog(boolean show,String title,String msj){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this.context);
        }
        try {
            if (!show)
                progressDialog.dismiss();
            else {
                progressDialog.setTitle(title);
                progressDialog.setMessage(msj);
                progressDialog.setCancelable(true);
                progressDialog.show();
            }
        }catch (Exception e){
            System.out.println("Error showing progressDialog -> "+e.getMessage());
        }
    }
    public void showProgressDialog(boolean show){
        showProgressDialog(show,"","");
    }
    public void alertDialog(AlertDialog.Builder builder,String title, String hint, final DialogResponse dialogResponse) {

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(hint);
        builder.setView(input);
        builder.setTitle(title);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                dialogResponse.onSucess(m_Text);
            }
        });

        builder.setCancelable(false);
        builder.show();
    }
    public interface DialogResponse {
        void onSucess(String value);
    }
    //Exceptions
    public void toastException(Exception e){
        String msj = "";
        if(e instanceof FirebaseAuthUserCollisionException) {
            msj = "Este correo electrónico ya se encuentra registrado";
        }
        else if(e instanceof FirebaseNetworkException){
            msj = "No hay conexión a internet";
        }
        else if(e instanceof FirebaseAuthInvalidCredentialsException){
            msj = "Verifique su contraseña";
        }else if(e instanceof FirebaseAuthInvalidUserException){
            if(((FirebaseAuthInvalidUserException) e).getErrorCode().equals("ERROR_USER_DISABLED"))
                msj = "La cuenta ha sido deshabilitada";
            else
                msj = "El correo electrónico ingresado no existe";
        }
        else{
            msj = "Ha ocurrido un error, intentelo nuevamente";
            System.out.println("Exception -> "+e);
        }
        Toast.makeText(context, msj, Toast.LENGTH_SHORT).show();
    }

}