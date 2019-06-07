package com.example.anuradha.musicmania;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    TextView newacc;
    Button enter;
    private FirebaseAuth mAuth;
    String pass, mail;

    private static final String TAG = "AUTH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pass = "";
        mail = "";
        email = findViewById(R.id.id_email);
        password = findViewById(R.id.id_password);

        load();
        newacc = findViewById(R.id.id_newacc);
        enter = findViewById(R.id.id_enter);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(email.getText().toString(), password.getText().toString());

            }
        });

        newacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(email.getText().toString(), password.getText().toString());
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if(user==null)
            return;
        else{
            saveData();
            Intent toNav = new Intent(LoginActivity.this, NavActivity.class);
            Toast.makeText(LoginActivity.this, "Authentication Success.",
                    Toast.LENGTH_SHORT).show();
            startActivity(toNav);
        }
    }

    private void signIn(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            pass = password;
                            mail = email;
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    private void createAccount(final String email, final String password) {
        if(!isFieldValid(email, password))
            Toast.makeText(this, "Invalid email and/or password", Toast.LENGTH_SHORT).show();
        else {
            // [START create_user_with_email]
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                pass = password;
                                mail = email;
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                        }
                    });
        }
    }

    private boolean isFieldValid(String e, String p){
        if(e.equals("")||p.equals(""))
            return false;
        else if(!e.contains("@") || (e.indexOf(".com") == -1 && e.indexOf(".org") == -1 && e.indexOf(".net") == -1))
            return false;
        return true;
    }

    public void saveData(){
        JSONObject data = new JSONObject();
        try {
            data.put("email", mail);
            data.put("password", pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try{
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput("info.json",MODE_PRIVATE));
            out.write(data.toString());
            out.close();
        }catch (Exception e){

        }
    }

    public void load(){
        try {
            BufferedReader red = new BufferedReader(new InputStreamReader(openFileInput("info.json")));
            JSONObject json = new JSONObject(red.readLine());

            email.setText(json.getString("email"));
            password.setText(json.getString("password"));
        }catch (Exception w){

        }
    }

}
