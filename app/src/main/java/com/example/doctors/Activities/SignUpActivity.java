package com.example.doctors.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doctors.Models.Doctor;
import com.example.doctors.Models.Patient;
import com.example.doctors.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUpActivity extends AppCompatActivity {

    MaterialEditText Email_str, Username_str, Pass1_str, Pass2_str;
    CheckBox Doctor_checkbox, Patient_checkbox;
    Button Signup_btn;
    String UsernameST, UserType = null;

    FirebaseAuth Auth=FirebaseAuth.getInstance();
    DatabaseReference DBreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Email_str=findViewById(R.id.EmailSignUp_STR);
        Username_str=findViewById(R.id.Username_STR);
        Pass1_str=findViewById(R.id.Pass1SignUp_STR);
        Pass2_str=findViewById(R.id.Pass2SignUp_STR);
        Signup_btn=findViewById(R.id.SignUp_BTN);
        Doctor_checkbox= findViewById(R.id.DoctorChoise);
        Patient_checkbox = findViewById(R.id.PatientChoise);

        Signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String EmailST = Email_str.getText().toString();
                UsernameST = Username_str.getText().toString();
                String Password1ST = Pass1_str.getText().toString();
                String Password2ST = Pass2_str.getText().toString();

                if(Doctor_checkbox.isChecked())
                    UserType = "doctor";
                else if(Patient_checkbox.isChecked())
                    UserType = "patient";

                if(TextUtils.isEmpty(EmailST)||TextUtils.isEmpty(UsernameST)||TextUtils.isEmpty(Password1ST)||TextUtils.isEmpty(Password2ST) || UserType == null)
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.all_fileds_are_required), Toast.LENGTH_SHORT).show();
                else
                if(Password1ST.length()<6)
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.password_must_be_length), Toast.LENGTH_SHORT).show();
                else
                if (Password1ST.equals(Password2ST))
                    SignUp(EmailST,Password1ST);
                else
                    Toast.makeText(SignUpActivity.this,getResources().getString(R.string.passwords_not_match) , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SignUp(String email, String password){
        Auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete( Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser=Auth.getCurrentUser();
                    assert firebaseUser != null;
                    String userid= firebaseUser.getUid();

                    //check which checkbox signed and create appropriate new user object
                    if(UserType == "doctor")
                    {
                        DBreference= FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(userid);
                        Doctor newDoctor = new Doctor(userid,UsernameST,"available",System.currentTimeMillis());
                        createNewDoctor(newDoctor);
                    }
                    else if(UserType == "patient")
                    {
                        DBreference= FirebaseDatabase.getInstance().getReference("Users").child("Patients").child(userid);
                        Patient newPatient = new Patient(userid,UsernameST,System.currentTimeMillis());
                        createNewPatient(newPatient);
                    }
                }
                else
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewDoctor(Doctor doctor)
    {
        DBreference.setValue(doctor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this,getResources().getString(R.string.signup_success), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, DoctorActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewPatient(Patient patient)
    {
        DBreference.setValue(patient).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this,getResources().getString(R.string.signup_success), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, PatientActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
