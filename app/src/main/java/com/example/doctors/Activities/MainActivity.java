package com.example.doctors.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.doctors.Models.Doctor;
import com.example.doctors.Models.Patient;
import com.example.doctors.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button LoginBtn, SignupBtn;

    FirebaseAuth Auth=FirebaseAuth.getInstance();
    FirebaseUser FirebaseCurrentUser;
    DatabaseReference DoctorsDBreference, PatientsDBreference;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseCurrentUser=  Auth.getCurrentUser();
        if(FirebaseCurrentUser!=null) //user already connected
        {
            checkIfDoctor();
            checkIfPatient();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginBtn= findViewById(R.id.loginChoiceBTN);
        SignupBtn= findViewById(R.id.signupChoiceBTN);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(LoginIntent);
            }
        });

        SignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUpIntent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(SignUpIntent);
            }
        });

    }


    private void checkUserType()
    {
        checkIfDoctor();
        checkIfPatient();
    }

    private void checkIfDoctor() {
        FirebaseUser firebaseUser=Auth.getCurrentUser();
        String userid= firebaseUser.getUid();
        DoctorsDBreference= FirebaseDatabase.getInstance().getReference("Users").child("Doctors");
        DoctorsDBreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren())
                {
                    Doctor doctor= data.getValue(Doctor.class);

                    if (doctor.getId().equals(userid)) {
                        Intent intent;
                        intent = new Intent(MainActivity.this, DoctorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkIfPatient() {
        FirebaseUser firebaseUser=Auth.getCurrentUser();
        String userid= firebaseUser.getUid();
        PatientsDBreference= FirebaseDatabase.getInstance().getReference("Users").child("Patients");
        PatientsDBreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren())
                {
                    Patient patient= data.getValue(Patient.class);

                    if (patient.getId().equals(userid)) {
                        Intent intent;
                        intent = new Intent(MainActivity.this, PatientActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}