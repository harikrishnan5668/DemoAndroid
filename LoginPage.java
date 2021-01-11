package com.example.projectunitconverter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {
    Button forget;
    Button signup;
    Button home;
    ProgressBar progressBar;
    private TextInputEditText usernames,passwords;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        mAuth=FirebaseAuth.getInstance();
        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        usernames=(TextInputEditText)findViewById(R.id.usernames) ;
        passwords=(TextInputEditText)findViewById(R.id.passwords);
        forget=(Button)findViewById(R.id.buttonlog);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Forget.class);
                startActivity(intent);
            }
        });
        signup=(Button)findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });
        home=(Button)findViewById(R.id.login);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();

            }
        });

    }
    public void  loginUser()
    {

        String username=usernames.getText().toString().trim();
        String password=passwords.getText().toString().trim();
        if(username.isEmpty())
        {
            usernames.setError("Email id is required");
            usernames.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches())
        {
            usernames.setError("Provide valid email");
            usernames.requestFocus();
            return;
        }
        if(password.isEmpty())
        {
            passwords.setError("Password is required");
            passwords.requestFocus();
            return;
        }
        if(password.length()<6)
        {
            passwords.setError("Password is week");
            passwords.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(getApplicationContext(), HomePages.class);
                    startActivity(intent);
                    progressBar.setVisibility(View.GONE);

                }
                else
                {
                    Toast.makeText(LoginPage.this, "Login failed", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }


}