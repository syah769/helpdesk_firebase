package com.example.syahril.yourtaskapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends BaseActivity {

    private EditText email;
    private EditText pass;
    private Button btnReg;

    private TextView login;

    private FirebaseAuth mAuth;
    private ConstraintLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();

        setupView();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });




        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail=email.getText().toString().trim();
                String mPass=pass.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)){
                    email.setError("Required Field");
                    return;
                }

                if (TextUtils.isEmpty(mPass)){
                    pass.setError("Required Field");
                    return;
                }

                showProgressDialog();

                mAuth.createUserWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dismissProgressDialog();
                        if (task.isSuccessful()){
                            showSnackBar(root,"Successful");
                        } else {
                            showSnackBar(root,"Please try again later.");
                        }
                    }
                });



            }
        });
    }

    public void setupView(){
        email = findViewById(R.id.email_reg);
        pass = findViewById(R.id.password_reg);

        btnReg = findViewById(R.id.reg_btn);

        login=findViewById(R.id.login_txt);

        root=findViewById(R.id.root);

    }
}
