package com.example.doctors.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

public class LoginActivity extends AppCompatActivity {

    Button Login_btn;
    MaterialEditText Email_str, Password_str;

    FirebaseAuth Auth=FirebaseAuth.getInstance();
    DatabaseReference DoctorsDBreference, PatientsDBreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email_str=findViewById(R.id.EmailLogin_STR);
        Password_str=findViewById(R.id.PassLogin_STR);

        Login_btn = findViewById(R.id.Login_BTN);
        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailEntered = Email_str.getText().toString();
                String passwordEntered = Password_str.getText().toString();

                if (TextUtils.isEmpty(emailEntered) || TextUtils.isEmpty(passwordEntered))
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.all_fileds_are_required), Toast.LENGTH_SHORT).show();
                else
                {
                    Auth.signInWithEmailAndPassword(emailEntered, passwordEntered)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        checkUserType();
                                    }
                                    else
                                        {
                                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
                                        }
                                }
                            });
                }
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
                        intent = new Intent(LoginActivity.this, DoctorActivity.class);
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
                        intent = new Intent(LoginActivity.this, PatientActivity.class);
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
