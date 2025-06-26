package com.example.musementfrontend;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.AddCommentRequestDTO;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.pojo.Comment;
import com.example.musementfrontend.util.CommentAdapter;
import com.example.musementfrontend.util.IntentKeys;
import com.example.musementfrontend.util.Util;
import com.example.musementfrontend.util.UtilButtons;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConcertComments extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private TextView errorText;
    private EditText commentText;
    private Button send;

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
        commentText = findViewById(R.id.comment_input);
        send = findViewById(R.id.send_button);
        send.setOnClickListener(this::sendComment);

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

    public void sendComment(View v){
        if (commentText.getText().toString().isEmpty()){
            return;
        }
        String text = commentText.getText().toString();
        AddCommentRequestDTO request = new AddCommentRequestDTO();
        request.setUserId(user.getId());
        request.setConcertId(concertId);
        request.setMessage(text);
        request.setTime(new Date());

        APIService api = APIClient.getClient().create(APIService.class);
        Call<Comment> call = api.addComment("Bearer " + user.getAccessToken(), request);

        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    Comment addedComment = response.body();
                    if (adapter != null && addedComment != null) {
                        adapter.addComment(addedComment);
                        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                    }
                    Toast.makeText(v.getContext(), "Comment is added", Toast.LENGTH_SHORT).show();
                    commentText.setText("");
                } else {
                    Toast.makeText(v.getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(v.getContext(), "Network failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
