package com.example.smartwastemanagement2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WasteSubmissionAdapter extends RecyclerView.Adapter<WasteSubmissionAdapter.ViewHolder> {

    private List<WasteSubmission> submissionsList;
    private Context context;
    private OnSubmissionClickListener listener;

    public interface OnSubmissionClickListener {
        void onViewDetailsClick(int position);
        void onPayNowClick(int position);

        void onViewDeleteClick(int position);
    }

    public WasteSubmissionAdapter(Context context, List<WasteSubmission> submissionsList, OnSubmissionClickListener listener) {
        this.context = context;
        this.submissionsList = submissionsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_waste_submission, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WasteSubmission submission = submissionsList.get(position);
        
        // Set data to views
        holder.textReferenceId.setText(submission.getReferenceId());
        holder.textWasteType.setText(submission.getWasteType());
        holder.textWeight.setText(submission.getWeight() + " kg");
        holder.textPickupDate.setText(submission.getPickupDate());
        holder.textPickupTime.setText(submission.getPickupTime());
        
        // Set status with potential payment date
        String statusText = submission.getStatus();
        if ("Paid".equals(statusText)) {
            // Format the payment date
            long paidAt = submission.getPaidAt();
            if (paidAt > 0) {
                String paymentDate = formatDate(paidAt);
                statusText = statusText + " on " + paymentDate;
            } else {
                // If no timestamp but status is Paid, just say "Paid"
                statusText = "Paid";
            }
        }
        holder.textStatus.setText(statusText);
        
        // Set status background color based on status
        if ("Paid".equals(submission.getStatus())) {
            holder.textStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            holder.buttonPayNow.setVisibility(View.GONE);
        } else if ("Pending".equals(submission.getStatus())) {
            holder.textStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark));
            holder.buttonPayNow.setVisibility(View.VISIBLE);
        } else {
            holder.textStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            holder.buttonPayNow.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Format a timestamp to a readable date string
     */
    private String formatDate(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }

    @Override
    public int getItemCount() {
        return submissionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textReferenceId, textStatus, textWasteType, textWeight, textPickupDate, textPickupTime;
        Button buttonDetails, buttonPayNow, buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // Initialize views
            textReferenceId = itemView.findViewById(R.id.textReferenceId);
            textStatus = itemView.findViewById(R.id.textStatus);
            textWasteType = itemView.findViewById(R.id.textWasteType);
            textWeight = itemView.findViewById(R.id.textWeight);
            textPickupDate = itemView.findViewById(R.id.textPickupDate);
            textPickupTime = itemView.findViewById(R.id.textPickupTime);
            buttonDetails = itemView.findViewById(R.id.buttonDetails);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonPayNow = itemView.findViewById(R.id.buttonPayNow);

            // Set click listeners
            buttonDetails.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onViewDetailsClick(position);
                    }
                }
            });

            // Set click listeners
            buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onViewDeleteClick(position);
                    }
                }
            });
            
            buttonPayNow.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onPayNowClick(position);
                    }
                }
            });
        }
    }
}
