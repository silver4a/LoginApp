package com.example.login.Firebase;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Map;

public class AuthFirebase extends DatabaseFirebase{
    //Singetlon
    private static AuthFirebase instance;
    //Params
    private FirebaseAuth mAuth;
    private Context context;
    private FirebaseUser user;

    public AuthFirebase(Context context){
        super(context);
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    public static synchronized AuthFirebase getInstance(Context context){
        if(instance == null){
            instance = new AuthFirebase(context);
        }
        return instance;
    }

    public void register(final String email, String password, final Map<String,Object> mapUser,final boolean verification){
        final String TAG = "Firebase register";
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(context, "Usuario creado con exito", Toast.LENGTH_SHORT).show();
                            write("Users/"+getUid(),mapUser);
                            updateUserBasic(mapUser.get("names").toString());
                            if(verification)
                                sendEmailVerification();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Ha ocurrido un error, vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void singUp(String email, String password, final boolean verification){
        final String TAG = "Login Firebase";
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        updateUserInfo();
                        if (task.isSuccessful()) {
                            if(verification && !user.isEmailVerified()){
                                singOut();
                                Toast.makeText(context, "Verifica tu cuenta", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(context, "Iniciaste sesion", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            }
                        } else {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Verifica tus datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void singOut(){
        mAuth.signOut();
    }

    public Map<String,String> getUserInformation(){
        updateUserInfo();
        if (user != null) {
            Map<String,String> info = new HashMap<>();
            info.put("name",user.getDisplayName());
            info.put("email",user.getEmail());
            info.put("urimg",user.getPhotoUrl().getPath());
            info.put("uid",user.getUid());
            info.put("verification",String.valueOf(user.isEmailVerified()));
            return info;
        }
        return null;
    }

    //Actualiza la instancia.
    private void updateUserInfo(){
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    //Actualiza la informacion basica del usuario.
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

    //Actualiza el email del usuario.
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

    //Actualiza la contraseña del usuario.
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

    //Envia un mensaje de verificacion.
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

    //Envia un mensaje de restablecimiento contraseña.
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

    //Elimina un usuario
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

    //Response login.
    private void updateUI(FirebaseUser user) {
        System.out.println("user: "+getName());
        System.out.println("email: "+getEmail());
        System.out.println("url: "+getPhotoUrl());
        System.out.println("uid: "+getUid());
    }
}
