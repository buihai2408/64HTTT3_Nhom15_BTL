package vn.edu.tlu.cse.haibui.appdoctruyen_btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyFavoriteActivity extends AppCompatActivity {
    RecyclerView rvComics;
    ArrayList<Comic> comics;
    ComicAdapter comicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Danh Sách Yêu Thích");
        setContentView(R.layout.activity_my_favorite);

        // Lấy userId từ SharedPreferences
        int userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("userId", -1);
        
        // Lấy danh sách truyện yêu thích của user
        DbHelper db = new DbHelper(this);
        comics = db.getFavoriteComics(userId);

        // Sử dụng ID đúng của RecyclerView từ layout file
        rvComics = findViewById(R.id.favorite_rv);
        comicAdapter = new ComicAdapter(comics, userId, this);
        rvComics.setAdapter(comicAdapter);
        rvComics.setLayoutManager(new LinearLayoutManager(MyFavoriteActivity.this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Tạo intent để quay về MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Xóa tất cả các activity trên stack
        startActivity(intent);
        finish();
    }
}