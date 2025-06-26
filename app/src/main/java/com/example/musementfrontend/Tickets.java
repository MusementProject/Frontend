package com.example.musementfrontend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dialogs.UploadTicketDialogFragment;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.pojo.Ticket;
import com.example.musementfrontend.util.IntentKeys;
import com.example.musementfrontend.util.TicketsAdapter;
import com.example.musementfrontend.util.UtilButtons;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tickets extends AppCompatActivity
        implements UploadTicketDialogFragment.Listener, TicketsAdapter.Listener {

    private APIService api;
    private String token;
    private final List<Ticket> tickets = new ArrayList<>();
    private TicketsAdapter adapter;
    private Action pendingAction;
    private Ticket pendingTicket;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_tickets);

        UtilButtons.Init(this);
        Bundle arguments = getIntent().getExtras();
        User user = null;
        if (arguments != null) {
            user = (User) arguments.get(IntentKeys.getUSER_KEY());
        }
        api = APIClient.getClient().create(APIService.class);
        assert user != null;
        token = user.getAccessToken();

        RecyclerView rv = findViewById(R.id.rvTickets);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TicketsAdapter(this, tickets, this);
        rv.setAdapter(adapter);

        MaterialButton btnAddTicket = findViewById(R.id.btnAddTicket);
        btnAddTicket.setOnClickListener(v -> {
            pendingAction = Action.UPLOAD;
            UploadTicketDialogFragment dlg = UploadTicketDialogFragment.newInstance();
            dlg.show(getSupportFragmentManager(), "upload");
        });

        loadTickets();
    }

    private void performReplace(long ticketId, MultipartBody.Part part) {
        api.replaceTicket(token, ticketId, part)
                .enqueue(new Callback<Ticket>() {
                    @Override
                    public void onResponse(@NonNull Call<Ticket> call, @NonNull Response<Ticket> r) {
                        if (r.isSuccessful()) loadTickets();
                        else
                            Toast.makeText(Tickets.this,
                                    "Replace failed",
                                    Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Ticket> call, @NonNull Throwable t) {
                        Toast.makeText(Tickets.this,
                                "Replace failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadTickets() {
        api.getTickets("Bearer " + token)
                .enqueue(new Callback<List<Ticket>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(
                            @NonNull Call<List<Ticket>> c,
                            @NonNull Response<List<Ticket>> r) {
                        if (r.isSuccessful() && r.body() != null) {
                            tickets.clear();
                            tickets.addAll(r.body());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Ticket>> c, @NonNull Throwable t) {
                    }
                });
    }

    @Override
    public void onUpload(long concertId, MultipartBody.Part filePart) {
        RequestBody cid = RequestBody.create(
                String.valueOf(concertId), MediaType.parse("text/plain"));
        api.uploadTicket("Bearer " + token, cid, filePart)
                .enqueue(new Callback<Ticket>() {
                    @Override
                    public void onResponse(@NonNull Call<Ticket> c, @NonNull Response<Ticket> r) {
                        if (r.isSuccessful()) {
                            loadTickets();
                        } else {
                            Toast.makeText(Tickets.this,
                                    "Upload failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Ticket> c, @NonNull Throwable t) {
                        Toast.makeText(Tickets.this,
                                "Upload failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDelete(Ticket ticket) {
        String authHeader = "Bearer " + token;
        api.deleteTicket(authHeader, ticket.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> r) {
                        if (r.isSuccessful()) {
                            loadTickets();
                        } else {
                            Toast.makeText(Tickets.this,
                                    "An error occurred while deleting",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(Tickets.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPreview(Ticket t) {
        Uri uri = Uri.parse(t.getFileUrl());
        Intent img = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "image/*")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(img, "View Ticket Image"));

    }

    private enum Action {UPLOAD}
}
