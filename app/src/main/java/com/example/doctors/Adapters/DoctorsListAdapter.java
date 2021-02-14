package com.example.doctors.Adapters;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctors.Models.Doctor;
import com.example.doctors.Models.Patient;
import com.example.doctors.Notification.OreoNotification;
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

public class DoctorsListAdapter extends RecyclerView.Adapter<DoctorsListAdapter.DoctorViewHolder> {

    private Context mContext;
    private List<Doctor> doctorsList;
    private CountDownTimer timer;

    public DoctorsListAdapter(Context mContext, List<Doctor> doctorsList) {
        this.mContext = mContext;
        this.doctorsList = doctorsList;
    }

    public class DoctorViewHolder extends RecyclerView.ViewHolder
    {

        TextView DoctorName, AvailableText, NotAvailableText;
        ImageView AvailableImage, NotAvailableImage;
        Button WaitingListBTN, MakeAppointmentBTN, CancleAppointmentBTN;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            DoctorName = itemView.findViewById(R.id.DoctorName);
            AvailableText=  itemView.findViewById(R.id.availableText);
            NotAvailableText=  itemView.findViewById(R.id.notAvailableText);
            AvailableImage=  itemView.findViewById(R.id.availableImage);
            NotAvailableImage=  itemView.findViewById(R.id.notAvailableImage);
            WaitingListBTN=  itemView.findViewById(R.id.waitingListBTN);
            MakeAppointmentBTN=  itemView.findViewById(R.id.makeAppointmentBTN);
            CancleAppointmentBTN=  itemView.findViewById(R.id.cancleAppointmentBTN);
        }
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_item,parent,false);
        DoctorViewHolder doctorViewHolder = new DoctorViewHolder(view);
        return doctorViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position)
    {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Doctor doctor = doctorsList.get(position);
        holder.DoctorName.setText(doctor.getName());

        if(doctor.getTimeLastAppointment()-System.currentTimeMillis() < 0)  //Doc available
        {
            holder.MakeAppointmentBTN.setVisibility(View.VISIBLE);
            holder.AvailableText.setVisibility(View.VISIBLE);
            holder.AvailableImage.setVisibility(View.VISIBLE);
            holder.NotAvailableText.setVisibility(View.INVISIBLE);
            holder.NotAvailableImage.setVisibility(View.INVISIBLE);
            holder.WaitingListBTN.setBackground(mContext.getDrawable(R.drawable.btn_block_background));
            holder.WaitingListBTN.setEnabled(false);
            doctor.setStatus("available");
            FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(doctor.getId()).child("status").setValue("available");
        }
        else //Doc not available
        {
            holder.AvailableText.setVisibility(View.INVISIBLE);
            holder.AvailableImage.setVisibility(View.INVISIBLE);
            holder.NotAvailableText.setVisibility(View.VISIBLE);
            holder.NotAvailableImage.setVisibility(View.VISIBLE);
            holder.WaitingListBTN.setBackground(mContext.getDrawable(R.drawable.btn_background));
            holder.WaitingListBTN.setEnabled(true);
            doctor.setStatus("not available");
            FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(doctor.getId()).child("status").setValue("not available");
        }

        DatabaseReference PatientsListReference = FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(doctor.getId()).child("PatientsList");
        PatientsListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isRegister= false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Patient patient = dataSnapshot.getValue(Patient.class);
                    if(patient.getId().equals(currentUser.getUid()))
                        isRegister=true;
                }
                if(isRegister)
                {
                    holder.MakeAppointmentBTN.setVisibility(View.INVISIBLE);
                    holder.CancleAppointmentBTN.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.MakeAppointmentBTN.setVisibility(View.VISIBLE);
                    holder.CancleAppointmentBTN.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.WaitingListBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder waitingListDialogBuilder = new AlertDialog.Builder(mContext);
                View dialogWaitingListView  = LayoutInflater.from(mContext).inflate(R.layout.dialog_waiting_list, null);

                RecyclerView WaitingListRecyclerView = dialogWaitingListView.findViewById(R.id.PatientsWaitingListRecycleView);
                WaitingListRecyclerView.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                WaitingListRecyclerView.setLayoutManager(layoutManager);

                DatabaseReference  DoctorsReference = FirebaseDatabase.getInstance().getReference("Users").child("Doctors");
                DoctorsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Doctor doctor1  =  snapshot.getValue(Doctor.class);
                            if(doctor1.getId().equals(doctor.getId()))
                            {
                                DatabaseReference DBreference = FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(doctor.getId()).child("PatientsList");
                                DBreference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        PatientsListAdapter waitingListAdapter;
                                        List<Patient> PatientsWaitingList = new ArrayList<>();

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Patient patient = snapshot.getValue(Patient.class);
                                            PatientsWaitingList.add(patient);
                                        }

                                        waitingListAdapter = new PatientsListAdapter(mContext, PatientsWaitingList);
                                        WaitingListRecyclerView.setAdapter(waitingListAdapter);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                AlertDialog waitingListDialog = waitingListDialogBuilder.create();
                waitingListDialog.setView(dialogWaitingListView);
                waitingListDialog.setCanceledOnTouchOutside(false);
                waitingListDialog.setTitle(mContext.getString(R.string.Doctor_waiting_list)  + doctor.getName());

                Button okBTN = dialogWaitingListView.findViewById(R.id.OKBTN);
                okBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        waitingListDialog.dismiss();
                    }
                });
                waitingListDialog.show();
            }
        });


        holder.MakeAppointmentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference CurrentDocRef = FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(doctor.getId());
                DatabaseReference PatTimeRef = FirebaseDatabase.getInstance().getReference("Users").child("Patients").child(currentUser.getUid()).child("timeStartAppointment");

                if(System.currentTimeMillis() - doctor.getTimeLastAppointment() > 0) //Doc is available
                {
                    sendNotification();

                    PatTimeRef.setValue(System.currentTimeMillis());

                    addPatientToWaitingList(doctor);
                    CurrentDocRef.child("timeLastAppointment").setValue(System.currentTimeMillis()+120000);
                    doctor.setTimeLastAppointment(System.currentTimeMillis()+120000);
//                    PatTimeRef.setValue(System.currentTimeMillis());
                    CurrentDocRef.child("status").setValue("not available");
                    doctor.setStatus("not available");
                }
                else if(System.currentTimeMillis() - doctor.getTimeLastAppointment() < 0) // not available
                {
                    PatTimeRef.setValue(doctor.getTimeLastAppointment());

                    addPatientToWaitingList(doctor);
                    CurrentDocRef.child("timeLastAppointment").setValue(doctor.getTimeLastAppointment()+120000);
                    doctor.setTimeLastAppointment(doctor.getTimeLastAppointment()+120000);

                    DatabaseReference CurrentPatRef = FirebaseDatabase.getInstance().getReference("Users").child("Patients").child(currentUser.getUid());
                    CurrentPatRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Patient patient = snapshot.getValue(Patient.class);
                            long currentTime = System.currentTimeMillis();
                            long whenMyTurnTime = patient.getTimeStartAppointment() - currentTime;

                            timer = new CountDownTimer(whenMyTurnTime, 1000) {
                                @Override
                                public void onTick(long l) {
                                }

                                @Override
                                public void onFinish() {
                                    sendNotification();
                                    removePatientFromList(doctor);
                                }
                            }.start();

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

                Toast.makeText(mContext ,mContext.getString(R.string.Registration_appointment) , Toast.LENGTH_SHORT).show();
            }
        });

        holder.CancleAppointmentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timer != null)
                {
                    timer.cancel();
                }
                removePatientFromList(doctor);
                doctor.setStatus("available");
                FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(doctor.getId()).child("status").setValue("available");
                DatabaseReference PatTimeRef = FirebaseDatabase.getInstance().getReference("Users").child("Patients").child(currentUser.getUid()).child("timeStartAppointment");
                PatTimeRef.setValue(System.currentTimeMillis());
                Toast.makeText(mContext ,mContext.getString(R.string.Registration_canceled) , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorsList.size();
    }

    private void addPatientToWaitingList(Doctor doctor)
    {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference PatientDBReference = FirebaseDatabase.getInstance().getReference("Users").child("Patients").child(currentUser.getUid());
        PatientDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference  DoctorsReference = FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(doctor.getId()).child("PatientsList").child(currentUser.getUid());
                DoctorsReference.setValue(snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removePatientFromList(Doctor doctor)
    {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference PatientsListRef = FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(doctor.getId()).child("PatientsList");
        PatientsListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Patient patient = snapshot.getValue(Patient.class);
                    if(patient.getId().equals(currentUser.getUid()))
                        snapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void sendNotification()
    {
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoNotification oreoNotification = new OreoNotification(mContext);
        Notification.Builder builder = oreoNotification.getOreoNotification("Doctors", mContext.getString(R.string.your_turn_now), defaultSound);
        oreoNotification.getManager().notify(1, builder.build());
    }
}
