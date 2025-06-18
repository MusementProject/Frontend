package com.example.musementfrontend.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.util.IntentKeys;
import com.example.musementfrontend.util.MediaUploadUtil;
import com.example.musementfrontend.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadTicketDialogFragment extends DialogFragment {

    public interface Listener {
        void onUpload(long concertId, MultipartBody.Part filePart);
    }

    private Listener listener;
    private Spinner spinnerConcerts;
    private Button btnPickFile, btnUpload;
    private Uri pickedUri;
    private List<ConcertDTO> concerts;

    private final ActivityResultLauncher<String[]> pickFileLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    pickedUri = uri;
                    btnUpload.setEnabled(true);
                }
            });

    public static UploadTicketDialogFragment newInstance() {
        return new UploadTicketDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
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
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_upload_ticket, null);
        spinnerConcerts = view.findViewById(R.id.spinnerConcerts);
        btnPickFile    = view.findViewById(R.id.btnPickFile);
        btnUpload      = view.findViewById(R.id.btnUpload);
        btnUpload.setEnabled(false);

        loadConcerts();

        btnPickFile.setOnClickListener(v ->
                pickFileLauncher.launch(new String[]{"image/*","application/pdf"})
        );

        btnUpload.setOnClickListener(v -> {
            ConcertDTO sel = (ConcertDTO) spinnerConcerts.getSelectedItem();
            long concertId = sel.getId();

            MultipartBody.Part part =
                    null;
            try {
                part = MediaUploadUtil.getMultipartBodyPart(requireContext(), pickedUri, "file", "ticket");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            listener.onUpload(concertId, part);
            dismiss();
        });

        builder.setView(view)
                .setNegativeButton("Отмена", (d,w)-> dismiss());
        return builder.create();
    }

    private void loadConcerts() {
        User user = Util.getUser(requireActivity().getIntent());
        if (user == null) {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + user.getAccessToken();
        long userId       = user.getId();

        APIService api = APIClient.getClient().create(APIService.class);
        api.getUserConcerts(authHeader, userId)
                .enqueue(new Callback<List<ConcertDTO>>() {
                    @Override
                    public void onResponse(Call<List<ConcertDTO>> call,
                                           Response<List<ConcertDTO>> resp) {
                        if (resp.isSuccessful()) {
                            List<ConcertDTO> list = resp.body();
                            // <-- вот тут лог:
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
                                        "Нет концертов для показа",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(),
                                    "Не удалось загрузить концерты: " + resp.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<ConcertDTO>> call, Throwable t) {
                        Log.e("UploadTicketDlg", "Ошибка сети при loadConcerts", t);
                        Toast.makeText(requireContext(),
                                "Ошибка сети: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }




}