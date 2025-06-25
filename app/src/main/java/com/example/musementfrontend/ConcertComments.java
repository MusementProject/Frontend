package com.example.musementfrontend;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.pojo.Comment;
import com.example.musementfrontend.util.CommentAdapter;
import com.example.musementfrontend.util.IntentKeys;
import com.example.musementfrontend.util.Util;
import com.example.musementfrontend.util.UtilButtons;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConcertComments extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private TextView errorText;

    private long concertId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert_comments);
        UtilButtons.Init(this);

        recyclerView = findViewById(R.id.recycler_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        concertId = getIntent().getLongExtra(IntentKeys.getCONCERT_ID(),-1);
        user = Util.getUser(getIntent());
        errorText = findViewById(R.id.errorText);

        loadComments(concertId);
    }

    private void loadComments(long concertId) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<List<Comment>> call = apiService.getConcertComments("Bearer " + user.getAccessToken(), user.getId(), concertId);

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful()) {
                    List<Comment> comments = response.body();
                    adapter = new CommentAdapter(comments);
                    recyclerView.setAdapter(adapter);
                } else {
                    showError("Вы не идёте на этот концерт. Чат недоступен.");
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                showError("Сетевая ошибка: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        recyclerView.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(message);
    }
}
