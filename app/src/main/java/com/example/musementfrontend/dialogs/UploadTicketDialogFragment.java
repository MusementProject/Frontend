package com.example.musementfrontend.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.R;
import com.example.musementfrontend.dto.ConcertDTO;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.util.MediaUploadUtil;
import com.example.musementfrontend.util.Util;

import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadTicketDialogFragment extends DialogFragment {

    private Listener listener;
    private Spinner spinnerConcerts;
    private Button btnUpload;
    private Uri pickedUri;
    private final ActivityResultLauncher<String[]> pickFileLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    pickedUri = uri;
                    btnUpload.setEnabled(true);
                }
            });
    private List<ConcertDTO> concerts;

    public static UploadTicketDialogFragment newInstance() {
        return new UploadTicketDialogFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            listener = (Listener) context;
        } else {
            throw new IllegalStateException(
                    "Activity must implement UploadTicketDialogFragment.Listener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater()
                .inflate(R.layout.dialog_upload_ticket, null);
        spinnerConcerts = view.findViewById(R.id.spinnerConcerts);
        Button btnPickFile = view.findViewById(R.id.btnPickFile);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnUpload.setEnabled(false);

        loadConcerts();

        btnPickFile.setOnClickListener(v ->
                pickFileLauncher.launch(new String[]{"image/*", "application/pdf"})
        );

        btnUpload.setOnClickListener(v -> {
            ConcertDTO sel = (ConcertDTO) spinnerConcerts.getSelectedItem();
            long concertId = sel.getId();

            MultipartBody.Part part =
                    null;
            try {
                part = MediaUploadUtil.getMultipartBodyPart(
                        requireContext(), pickedUri, "file", "ticket");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            listener.onUpload(concertId, part);
            dismiss();
        });

        builder.setView(view)
                .setNegativeButton("Cancel",
                        (d, w) -> dismiss());
        return builder.create();
    }

    private void loadConcerts() {
        User user = Util.getUser(requireActivity().getIntent());
        if (user == null) {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + user.getAccessToken();
        long userId = user.getId();

        APIService api = APIClient.getClient().create(APIService.class);
        api.getUserConcerts(authHeader, userId)
                .enqueue(new Callback<List<ConcertDTO>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<List<ConcertDTO>> call,
                            @NonNull Response<List<ConcertDTO>> resp) {
                        if (resp.isSuccessful()) {
                            List<ConcertDTO> list = resp.body();

                            Log.d("UploadTicketDlg", "Fetched concerts count="
                                    + (list != null ? list.size() : "null")
                                    + " : " + list);
                            if (list != null && !list.isEmpty()) {
                                concerts = list;
                                ArrayAdapter<ConcertDTO> adapter = new ArrayAdapter<>(
                                        requireContext(),
                                        android.R.layout.simple_spinner_item,
                                        concerts
                                );
                                adapter.setDropDownViewResource(
                                        android.R.layout.simple_spinner_dropdown_item
                                );
                                spinnerConcerts.setAdapter(adapter);
                            } else {
                                Toast.makeText(requireContext(),
                                        "No concerts found",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(),
                                    "Failed to load concerts:"
                                            + resp.code(),

                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ConcertDTO>> call, @NonNull Throwable t) {
                        Log.e("UploadTicketDlg",
                                "Network error: " + t.getMessage(), t);
                        Toast.makeText(requireContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public interface Listener {
        void onUpload(long concertId, MultipartBody.Part filePart);
    }
}