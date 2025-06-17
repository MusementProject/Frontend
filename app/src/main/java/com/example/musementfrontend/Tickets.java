package com.example.musementfrontend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dialogs.UploadTicketDialogFragment;
import com.example.musementfrontend.pojo.Ticket;
import com.example.musementfrontend.util.TicketsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.InputStream;
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
    private List<Ticket> tickets = new ArrayList<>();
    private TicketsAdapter adapter;
    private enum Action {UPLOAD, REPLACE}
    private Action pendingAction;
    private Ticket pendingTicket;

    private final ActivityResultLauncher<String[]> pickFile =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) return;
                try {
                    InputStream in = getContentResolver().openInputStream(uri);
                    byte[] data = new byte[in.available()];
                    in.read(data);
                    RequestBody rb = RequestBody.create(data,
                            MediaType.parse(getContentResolver().getType(uri)));
                    MultipartBody.Part part = MultipartBody.Part
                            .createFormData("file", "ticket", rb);
                    if (pendingAction == Action.UPLOAD) {
                        // concertId сохраняется при вызове onUpload
                    } else if (pendingAction == Action.REPLACE) {
                        performReplace(part, pendingTicket);
                    }
                } catch (Exception e) {}
            });

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_tickets);
        api = APIClient.getClient().create(APIService.class);
        token = "Bearer " + getIntent().getStringExtra("accessToken");

        RecyclerView rv = findViewById(R.id.rvTickets);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TicketsAdapter(this, tickets, this);
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddTicket);
        fab.setOnClickListener(v -> {
            pendingAction = Action.UPLOAD;
            UploadTicketDialogFragment dlg = UploadTicketDialogFragment.newInstance();
            dlg.show(getSupportFragmentManager(), "upload");
        });

        loadTickets();
    }

    private void performReplace(MultipartBody.Part part, Ticket ticket) {
        api.replaceTicket(token, ticket.getId(), part)
                .enqueue(new Callback<Ticket>() {
                    @Override
                    public void onResponse(Call<Ticket> call, Response<Ticket> response) {
                        if (response.isSuccessful()) {
                            loadTickets();
                        }
                    }
                    @Override
                    public void onFailure(Call<Ticket> call, Throwable t) {
                        Toast.makeText(Tickets.this, "Replace failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadTickets() {
        api.getTickets(token).enqueue(new Callback<List<Ticket>>() {
            @Override public void onResponse(Call<List<Ticket>> c,
                                             Response<List<Ticket>> r) {
                if (r.isSuccessful() && r.body()!=null) {
                    tickets.clear(); tickets.addAll(r.body());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(Call<List<Ticket>> c, Throwable t) {}
        });
    }

    @Override public void onUpload(long concertId, MultipartBody.Part filePart) {
        RequestBody cid = RequestBody.create(
                String.valueOf(concertId), MediaType.parse("text/plain"));
        api.uploadTicket(token, cid, filePart)
                .enqueue(new Callback<Ticket>() {
                    @Override public void onResponse(Call<Ticket> c, Response<Ticket> r) {
                        if (r.isSuccessful()) loadTickets();
                    }
                    @Override public void onFailure(Call<Ticket> c, Throwable t) {}
                });
    }

    @Override public void onDelete(Ticket t) {
        api.deleteTicket(token, t.getId())
                .enqueue(new Callback<Void>() {
                    @Override public void onResponse(Call<Void> c, Response<Void> r) {
                        if (r.isSuccessful()) loadTickets();
                    }
                    @Override public void onFailure(Call<Void> c, Throwable t) {}
                });
    }

    @Override public void onReplace(Ticket t) {
        pendingAction = Action.REPLACE;
        pendingTicket = t;
        pickFile.launch(new String[]{"image/jpeg","application/pdf"});
    }

    @Override public void onPreview(Ticket t) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(t.getFileUrl())));
    }
}