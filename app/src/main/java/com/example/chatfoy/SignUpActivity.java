package com.example.chatfoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatfoy.model.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {
    EditText edtEmail, edtPassword, edtRePass, edtFullName;
    Spinner spinner;
    Button btnCreatAcc;
    TextView txtGoLogin;
    Toolbar toolbar;
    ArrayList<String> listSex;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef = db.collection("users");
    String sex;
    private static final String TAG = "AAA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FindView();
        LoadSpinner();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txtGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCreatAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                String retypePassword = edtRePass.getText().toString();
                String fullName = edtFullName.getText().toString();
                if (accountIsInvalid(password, retypePassword, email, fullName)) {
                    CreatAccount(email, password, fullName);
                }
            }
        });

    }

    private boolean accountIsInvalid(String password, String retypePassword, String email, String fullName) {
        if (email.equals("") || fullName.equals("") || password.equals("") || retypePassword.equals("")) {
            Toast.makeText(this, "Please fill all information", Toast.LENGTH_SHORT).show();
        }
        if (!password.equals(retypePassword)) {
            Toast.makeText(this, "Retype Password incorrect!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must have at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void CreatAccount(final String email, String password, final String fullName) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Created Account");
                            UpdateDatabase(firebaseAuth.getCurrentUser().getUid(), email, fullName);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                        }
                    }
                });
    }

    private void UpdateDatabase(String userId, String email, String fullName) {

        User user = new User(userId, email, fullName, "default", sex, false);
        userRef.document(user.getUserId()).set(user);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void FindView() {
        edtEmail = findViewById(R.id.editTextEmailSignUp);
        edtPassword = findViewById(R.id.editTextPasswordSignUp);
        edtRePass = findViewById(R.id.editTextCheckPasswordSignUp);
        edtFullName = findViewById(R.id.editTextFullNameSignUp);
        spinner = findViewById(R.id.spinner);
        btnCreatAcc = findViewById(R.id.buttonCreatAccount);
        txtGoLogin = findViewById(R.id.textViewGoLogin);
        toolbar = findViewById(R.id.toolbarSignUp);
    }

    private void LoadSpinner() {
        listSex = new ArrayList<>();
        listSex.add("Nam");
        listSex.add("Nữ");
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listSex);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sex = listSex.get(position);
                Log.d(TAG, "onItemSelected: " + sex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sex = "Nam";
            }
        });
    }

}
