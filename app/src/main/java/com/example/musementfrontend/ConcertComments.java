package com.example.musementfrontend;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.pojo.Comment;
import com.example.musementfrontend.util.CommentAdapter;
import com.example.musementfrontend.util.UtilButtons;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConcertComments extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<Comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert_comments);
        UtilButtons.Init(this);

        recyclerView = findViewById(R.id.recycler_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        comments = createDummyComments();

        adapter = new CommentAdapter(comments);
        recyclerView.setAdapter(adapter);

    }

    private List<Comment> createDummyComments() {
        List<Comment> list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setUsername("Julia");
            user.setNickname("Julia");
            Comment comment = new Comment(
                    user,
                    "Это пример комментария номер " + i,
                    new Date(),
                    null,
                    null);
            list.add(comment);
        }
        return list;
    }
}
