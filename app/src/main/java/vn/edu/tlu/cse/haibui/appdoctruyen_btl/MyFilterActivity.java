package vn.edu.tlu.cse.haibui.appdoctruyen_btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyFilterActivity extends AppCompatActivity {
    RecyclerView rvComics;
    ArrayList<Comic> comics_filter;
    ComicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_my_filter);

        // Lấy userId từ SharedPreferences
        int userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("userId", -1);

        rvComics = findViewById(R.id.filter_rv);
        Intent intent = getIntent();
        comics_filter = (ArrayList<Comic>) intent.getSerializableExtra("comics");
        String filter_query = intent.getStringExtra("filter_query");

        // Truyền thêm userId và context vào constructor
        adapter = new ComicAdapter(comics_filter, userId, this);
        rvComics.setAdapter(adapter);
        adapter.getFilter().filter("");
        rvComics.setLayoutManager(new LinearLayoutManager(MyFilterActivity.this, LinearLayoutManager.VERTICAL, false));
        rvComics.addItemDecoration(new DividerItemDecoration(MyFilterActivity.this, LinearLayoutManager.VERTICAL));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}