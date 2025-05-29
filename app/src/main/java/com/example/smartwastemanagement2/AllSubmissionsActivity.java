package com.example.smartwastemanagement2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllSubmissionsActivity extends AppCompatActivity implements WasteSubmissionAdapter.OnSubmissionClickListener {

    private RecyclerView recyclerView;
    private LinearLayout emptyStateLayout;
    private Button buttonAddWasteEmpty, btnDelete;
    private DBHelper dbHelper;
    private SessionManager sessionManager;
    private List<WasteSubmission> submissionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_submissions);

        // Initialize database helper and session manager
        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewSubmissions);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        buttonAddWasteEmpty = findViewById(R.id.buttonAddWasteEmpty);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        // Set up empty state button
        buttonAddWasteEmpty.setOnClickListener(v -> {
            Intent intent = new Intent(AllSubmissionsActivity.this, AddWasteActivity.class);
            startActivity(intent);
        });

        // Load submissions
        loadSubmissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload submissions when coming back to this screen
        loadSubmissions();
    }

    /**
     * Load waste submissions from database
     */
    private void loadSubmissions() {
        String userEmail = sessionManager.getUserEmail();
        submissionsList = dbHelper.getUserWasteSubmissions(userEmail);

        if (submissionsList != null && !submissionsList.isEmpty()) {
            // We have submissions, show the list
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);

            // Create and set adapter
            WasteSubmissionAdapter adapter = new WasteSubmissionAdapter(this, submissionsList, this);
            recyclerView.setAdapter(adapter);
        } else {
            // No submissions, show empty state
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewDetailsClick(int position) {
        // Show details of the selected submission
        WasteSubmission submission = submissionsList.get(position);
        Toast.makeText(this, "Details for: " + submission.getReferenceId(), Toast.LENGTH_SHORT).show();
        // In a real app, you might navigate to a details page:
        // Intent intent = new Intent(this, SubmissionDetailsActivity.class);
        // intent.putExtra("referenceId", submission.getReferenceId());
        // startActivity(intent);
    }

    @Override
    public void onViewDeleteClick(int position) {
        WasteSubmission submission = submissionsList.get(position);
        boolean deleted = dbHelper.deleteWasteSubmissionByReferenceId(submission.getReferenceId());
        if (deleted) {
            Toast.makeText(this, "Submission deleted successfully", Toast.LENGTH_SHORT).show();
            loadSubmissions();
        }
    }

    @Override
    public void onPayNowClick(int position) {
        WasteSubmission submission = submissionsList.get(position);
        
        // Check if already paid
        if ("Paid".equals(submission.getStatus())) {
            Toast.makeText(this, "This submission has already been paid", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Navigate to payment screen
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("referenceId", submission.getReferenceId());
        startActivity(intent);
    }
}
