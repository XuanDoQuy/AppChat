package com.example.chatfoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "AAA";
    EditText edtEmail, edtPassword;
    Button btnLogin, btnGoSignUp;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FindView();

        btnGoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if (accountIsInvalid(email, password)) {
                    Login(email, password);
                }
            }
        });
    }

    private void FindView() {
        edtEmail = findViewById(R.id.editTextEmailLogin);
        edtPassword = findViewById(R.id.editTextPasswordLogin);
        btnLogin = findViewById(R.id.buttonLogin);
        btnGoSignUp = findViewById(R.id.buttonGoSignUp);
    }

    private void Login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Login successful");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, task.getException().toString());
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean accountIsInvalid(String email, String password) {
        if (email.equals("") || password.equals("")) {
            Toast.makeText(this, "Please fill all field", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must have at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
