package com.example.smartwastemanagement2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying services in the search results.
 */
public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private List<Service> serviceList;
    private List<Service> filteredList;
    private Context context;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        this.filteredList = new ArrayList<>(serviceList);
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_item, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = filteredList.get(position);
        
        holder.serviceName.setText(service.getName());
        holder.serviceDescription.setText(service.getDescription());
        holder.serviceIcon.setImageResource(service.getIconResourceId());
        
        holder.itemView.setOnClickListener(v -> {
            try {
                // Navigate to the corresponding activity
                Class<?> activityClass = Class.forName(service.getActivityClass());
                Intent intent = new Intent(context, activityClass);
                context.startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    /**
     * Filter the list of services based on query.
     *
     * @param query The search query
     */
    public void filter(String query) {
        filteredList.clear();
        
        if (query.isEmpty()) {
            filteredList.addAll(serviceList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            
            for (Service service : serviceList) {
                if (service.getName().toLowerCase().contains(lowerCaseQuery) ||
                        service.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(service);
                }
            }
        }
        
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for service items.
     */
    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName;
        TextView serviceDescription;
        ImageView serviceIcon;

        ServiceViewHolder(View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.serviceName);
            serviceDescription = itemView.findViewById(R.id.serviceDescription);
            serviceIcon = itemView.findViewById(R.id.serviceIcon);
        }
    }
}
