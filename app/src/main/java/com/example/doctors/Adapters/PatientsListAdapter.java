package com.example.doctors.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctors.Models.Patient;
import com.example.doctors.R;

import java.util.List;

public class PatientsListAdapter extends RecyclerView.Adapter<PatientsListAdapter.PatientViewHolder> {

    private Context mContext;
    private List<Patient> patientsList;

    public PatientsListAdapter(Context mContext, List<Patient> patientsList) {
        this.mContext = mContext;
        this.patientsList = patientsList;
    }

    public class PatientViewHolder extends RecyclerView.ViewHolder
    {

        TextView PatientName;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            PatientName = itemView.findViewById(R.id.PatientName);
        }
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_item,parent,false);
        PatientViewHolder patientViewHolder = new PatientViewHolder(view);
        return patientViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position)
    {
        Patient patient = patientsList.get(position);
        holder.PatientName.setText(patient.getName());
    }

    @Override
    public int getItemCount() {
        return patientsList.size();
    }

}
