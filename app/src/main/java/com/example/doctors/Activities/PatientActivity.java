package com.example.doctors.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctors.Adapters.DoctorsListAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class PatientActivity extends AppCompatActivity {

    RecyclerView DoctorsRecycleView;
    ImageButton signOutBTN;
    CheckBox AvailableDoctorsCheckbox;
    TextView patient_name;

    private DoctorsListAdapter doctorsListAdapter;
    private List<Doctor> allDoctorList, availableDoctorsList;

    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_screen);

        patient_name = findViewById(R.id.hello_patient_name);
        DatabaseReference PatientsReference = FirebaseDatabase.getInstance().getReference("Users").child("Patients");
        PatientsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    Patient patient = snapshot1.getValue(Patient.class);
                    if(patient.getId().equals(currentUser.getUid()))
                        patient_name.setText(patient.getName());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DoctorsRecycleView = findViewById(R.id.DoctorsRecycleView);
        DoctorsRecycleView.setHasFixedSize(true);
        DoctorsRecycleView.setLayoutManager(new LinearLayoutManager((this)));

        AvailableDoctorsCheckbox= findViewById(R.id.availbleDoctorCheckbox);
        readAllDoctorsList();
        AvailableDoctorsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(compoundButton.isChecked())
                {
                    readAvailableDoctorsList();
                }
                else
                {
                    readAllDoctorsList();
                }
            }
        });

        signOutBTN = findViewById(R.id.SignOutBTN);
        signOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(PatientActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void readAllDoctorsList() {
        DatabaseReference DBreference = FirebaseDatabase.getInstance().getReference("Users").child("Doctors");
        DBreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allDoctorList = new ArrayList<>();
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    Doctor doctor = snapshot1.getValue(Doctor.class);
                    allDoctorList.add(doctor);
                }

                doctorsListAdapter = new DoctorsListAdapter(PatientActivity.this, allDoctorList);
                DoctorsRecycleView.setAdapter(doctorsListAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void readAvailableDoctorsList(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Doctors");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                availableDoctorsList = new ArrayList<Doctor>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Doctor doctor = snapshot.getValue(Doctor.class);
                    if(doctor.getStatus().equals("available"))
                        availableDoctorsList.add(doctor);
                }
                doctorsListAdapter = new DoctorsListAdapter(PatientActivity.this, availableDoctorsList);
                DoctorsRecycleView.setAdapter(doctorsListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
