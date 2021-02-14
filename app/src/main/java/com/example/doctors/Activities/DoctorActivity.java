package com.example.doctors.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctors.Adapters.PatientsListAdapter;
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

public class DoctorActivity extends AppCompatActivity {

    RecyclerView WaitingPatientRecycleView, CurrentPatientRecycleView;
    LinearLayout firstLayout;
    TextView NoPatientText, DocName;
    ImageButton signOutBTN;
    PatientsListAdapter CurrentPatientAdapter;
    List<Patient> CurrentPatient = new ArrayList<>();
    List<Patient> PatientsWaitingList;

    FirebaseAuth mAuth= FirebaseAuth.getInstance();;
    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_screen);

        DocName = findViewById(R.id.hello_doctor_name);
        DatabaseReference PatientsReference = FirebaseDatabase.getInstance().getReference("Users").child("Doctors");
        PatientsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    Doctor doctor = snapshot1.getValue(Doctor.class);
                    if(doctor.getId().equals(currentUser.getUid()))
                        DocName.setText(doctor.getName());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        firstLayout= findViewById(R.id.FirstLayout);
        NoPatientText= findViewById(R.id.NoPatientText);
        WaitingPatientRecycleView = findViewById(R.id.PatientRecycleView);
        WaitingPatientRecycleView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        WaitingPatientRecycleView.setLayoutManager(layoutManager);

        CurrentPatientRecycleView = findViewById(R.id.CurrentPatientRecycleView);
        CurrentPatientRecycleView.setHasFixedSize(true);
        CurrentPatientRecycleView.setLayoutManager(new LinearLayoutManager((this)));

        readPatients();

        signOutBTN = findViewById(R.id.SignOutBTN);
        signOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(DoctorActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void readPatients()
    {
        DatabaseReference DBreference = FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(currentUser.getUid());
        DBreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("PatientsList")) {
                    firstLayout.setVisibility(View.VISIBLE);
                    WaitingPatientRecycleView.setVisibility(View.VISIBLE);
                    NoPatientText.setVisibility(View.INVISIBLE);

                    updatePatientFromWaitingList();

                    PatientsListAdapter waitingListAdapter;
                    PatientsWaitingList = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.child("PatientsList").getChildren()) {
                        Patient patient = snapshot.getValue(Patient.class);
                        PatientsWaitingList.add(patient);
                    }
                    //display current patients
                    CurrentPatient.add(PatientsWaitingList.get(PatientsWaitingList.size() - 1));
                    CurrentPatientAdapter = new PatientsListAdapter(DoctorActivity.this, CurrentPatient);
                    CurrentPatientRecycleView.setAdapter(CurrentPatientAdapter);
                    //display waiting list
                    PatientsWaitingList.remove(PatientsWaitingList.size() - 1); //remove the current patient from waitingList
                    waitingListAdapter = new PatientsListAdapter(DoctorActivity.this, PatientsWaitingList);
                    WaitingPatientRecycleView.setAdapter(waitingListAdapter);
                }
                else
                {
                    firstLayout.setVisibility(View.INVISIBLE);
                    WaitingPatientRecycleView.setVisibility(View.INVISIBLE);
                    NoPatientText.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updatePatientFromWaitingList() {
        DatabaseReference PatientsListRef = FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(currentUser.getUid()).child("PatientsList");
        PatientsListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Patient patient = snapshot.getValue(Patient.class);
                    if (patient.getTimeStartAppointment() + 120000 < System.currentTimeMillis())
                        snapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}