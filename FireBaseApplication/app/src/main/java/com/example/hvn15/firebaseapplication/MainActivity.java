package com.example.hvn15.firebaseapplication;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button mFirebaseBtn;
    private DatabaseReference mDatabase;
    private EditText mNameField;
    private EditText mEmailField;
    private TextView mNameView;
    private TextView mGetOneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseBtn = (Button) findViewById(R.id.firebase_btn);
        //this is a reference to where we start in the database, in this case we start at the very start,
        //you could add a .child('example') if you wish to start at another place in the database,
        //but in our case we want to start at the very beginning.
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mNameField = (EditText) findViewById(R.id.name_field);
        mEmailField = (EditText) findViewById(R.id.email_field);
        mGetOneView = (TextView) findViewById(R.id.getOne_view);

        //the value event listener is being used for listening for anything happening
        //on the firebase database, and in case something happens you can do as you wish
        //at the time the event happens
        mDatabase.addValueEventListener(new ValueEventListener() {
            //ondatachange listens for if any change is happening to the data,
            //examples are if something new has been added, if something has been removed,
            //if something has been updated etc.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String name = "";
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        name += "EMAIL: " + child.child("Email").getValue().toString();
                        name += " - NAME: " + child.child("Name").getValue().toString() + "\n";
                    }
                    //in our case we loop through the data, and add the 2 childs and their value (email and name) into
                    //a string, and after the looping is complete we put the string (name) intp the textview called mGetOneView
                    if(dataSnapshot.getChildrenCount() > 0){
                        //if there is more than 0 children (meaning database isnt empty,
                        // then we add the string to the textview
                        mGetOneView.setText(name);
                    } else {
                        //if there is zero children (database is empty) then we add that error message in the textview
                        mGetOneView.setText("the database is empty");
                    }

                } catch (Exception e) {
                    //if something went wrong, then we add the error inside the same textview
                    mGetOneView.setText(e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //if we were not allowed to do anything with the database, then we end up here.
                //Reason we're not allowed would be that we are not logged into a user (go to authenticate
                // and login to make it work), in that case the error message will be Permission Denied
                mGetOneView.setText(databaseError.toString());
            }
        });


        mFirebaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create child in root
                //assign value to that child
                String name = mNameField.getText().toString().toString();
                String email = mEmailField.getText().toString().toString();

                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put("Name", name);
                dataMap.put("Email", email);
                //the two edittext are being put to a hashmap and the email is being set to be
                // a unique child id, that itself also has two child: name and email.
                //.child(email) creates the child id, setValue(dataMap) creates the two child's belonging to the id.
                    mDatabase.child(email).setValue(dataMap);


            }
        });
    }

    public void goToAuthenticate(View view) {
        Intent intent = new Intent(getBaseContext(), AuthenticationActivity.class);
        startActivity(intent);
        //changes the activity so we go from main to authentication on the app
    }
}
