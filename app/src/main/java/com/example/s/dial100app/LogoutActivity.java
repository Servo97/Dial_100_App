package com.example.s.dial100app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogoutActivity extends AppCompatActivity {
    TextView user1;
    Button btnlogout;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user1=findViewById(R.id.user_tv);
        try{
            assert user != null;
            user1.setText(user.getEmail());
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        btnlogout=findViewById(R.id.logout_button);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user==null) {
                    startActivity(new Intent(LogoutActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }
}
