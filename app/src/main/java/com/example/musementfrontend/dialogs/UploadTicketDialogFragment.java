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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.R;
import com.example.musementfrontend.pojo.Concert;
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
            long concertId = sel.getId_concert();
            RequestBody rb = new RequestBody() {
                @Override
                public MediaType contentType() {
                    return MediaType.parse(Objects.requireNonNull(requireContext().getContentResolver().getType(pickedUri)));
                }

                @Override
                public void writeTo(@NonNull BufferedSink sink) throws IOException {
                    try (InputStream in = requireContext().getContentResolver().openInputStream(pickedUri)) {
                        byte[] buffer = new byte[8192];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            sink.write(buffer, 0, read);
                        }
                    }
                }
            };
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", "ticket", rb);
            listener.onUpload(concertId, part);
            dismiss();
        });

        builder.setView(view)
                .setNegativeButton("Отмена", (d,w)-> dismiss());
        return builder.create();
    }

    private void loadConcerts() {
        // TODO: заменить на реальное API
        APIService api = APIClient.getClient().create(APIService.class);
        String token = "Bearer " + requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .getString("accessToken", "");

        api.getUserConcerts() // вызываем эндпоинт для списка концертов
                .enqueue(new Callback<List<Concert>>() {
                    @Override public void onResponse(Call<List<Concert>> c,
                                                     Response<List<Concert>> r) {
                        if (r.isSuccessful() && r.body()!=null) {
                            concerts = r.body();
                            ArrayAdapter<Concert> adapter = new ArrayAdapter<>(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    concerts);
                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item);
                            spinnerConcerts.setAdapter(adapter);
                        }
                    }
                    @Override public void onFailure(Call<List<Concert>> c, Throwable t) {}
                });
    }
}