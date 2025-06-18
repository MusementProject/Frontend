package com.example.musementfrontend.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.util.MediaUploadUtil;

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
    private List<Concert> concerts;

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
                pickFileLauncher.launch(new String[]{"image/jpeg","application/pdf"})
        );

        btnUpload.setOnClickListener(v -> {
            Concert sel = (Concert) spinnerConcerts.getSelectedItem();
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
        // 1) Получаем API
        APIService api = APIClient.getClient().create(APIService.class);

        // 2) Достаём из Intent родительской Activity токен и userId
        Intent host = requireActivity().getIntent();
        String token = host.getStringExtra("accessToken");
        long userId = host.getLongExtra("userId", -1L);
        if (token == null || userId < 0) {
            Toast.makeText(requireContext(),
                    "Не переданы данные пользователя", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3) Запрос к серверу
        api.getUserConcerts(token, userId)
                .enqueue(new Callback<List<Concert>>() {
                    @Override
                    public void onResponse(Call<List<Concert>> call,
                                           Response<List<Concert>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            concerts = resp.body();
                            // 4) Обновляем Spinner
                            ArrayAdapter<Concert> a = new ArrayAdapter<>(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    concerts
                            );
                            a.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );
                            spinnerConcerts.setAdapter(a);
                        } else {
                            Toast.makeText(requireContext(),
                                    "Не удалось загрузить концерты: " + resp.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Concert>> call, Throwable t) {
                        Toast.makeText(requireContext(),
                                "Ошибка сети: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}