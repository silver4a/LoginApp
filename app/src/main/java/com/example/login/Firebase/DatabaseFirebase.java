package com.example.login.Firebase;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;

public class DatabaseFirebase{
    public static DatabaseFirebase instance;
    private Context context;
    private Map<String,ValueEventListener> mapValueEvents = new HashMap<String,ValueEventListener>();
    //Databse
    public static DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReference();

    public DatabaseFirebase(Context context) {
        this.context = context;
    }
    //getInstance
    public static synchronized DatabaseFirebase getInstance(Context context){
        if(instance == null){
            instance = new DatabaseFirebase(context);
        }
        return instance;
    }


    public void write(String child, Object value){
        databaseReference.child(child).setValue(value);
    }

    public void read(final String child, final boolean stream, final DatabaseResponse databaseResponse){

         final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseResponse.onResponse(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                databaseResponse.onFail(error);
            }
        };
        if(!stream)
            databaseReference.child(child).addListenerForSingleValueEvent(valueEventListener);
        else {
            databaseReference.child(child).addValueEventListener(valueEventListener);
            mapValueEvents.put(child, valueEventListener);
        }
    }

    public void deleteStreamEvent(ValueEventListener valueEventListener){
        databaseReference.removeEventListener(valueEventListener);
    }

    public interface DatabaseResponse{
        void onResponse(DataSnapshot dataSnapshot);
        void onFail(DatabaseError databaseError);
    }

    public ValueEventListener getValueEventListener(String key){
        return mapValueEvents.get(key);
    }
}
