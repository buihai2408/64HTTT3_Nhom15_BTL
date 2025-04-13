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
        
        // Kiểm tra và lọc theo thể loại
        if (filter_query != null && !filter_query.isEmpty()) {
            ArrayList<Comic> filteredComics = checkCategory(filter_query);
            comics_filter.clear();
            comics_filter.addAll(filteredComics);
        }
        
        adapter.notifyDataSetChanged();
        rvComics.setLayoutManager(new LinearLayoutManager(MyFilterActivity.this, LinearLayoutManager.VERTICAL, false));
        rvComics.addItemDecoration(new DividerItemDecoration(MyFilterActivity.this, LinearLayoutManager.VERTICAL));
    }

    private ArrayList<Comic> checkCategory(String filter_query) {
        ArrayList<Comic> result = new ArrayList<>();
        DbHelper db = new DbHelper(this);
        
        // Lấy danh sách tất cả thể loại
        ArrayList<ComicCategory> allCategories = db.getAllCategory();
        
        // Tạo map để dễ dàng tra cứu id của thể loại
        java.util.HashMap<String, Integer> categoryMap = new java.util.HashMap<>();
        for (ComicCategory category : allCategories) {
            categoryMap.put(category.getNameTag(), category.getId());
        }
        
        // Tách các thể loại từ chuỗi filter_query
        String[] selectedCategories = filter_query.split(",");
        
        // Lọc truyện theo thể loại
        for (Comic comic : comics_filter) {
            for (String categoryName : selectedCategories) {
                // Lấy ID của thể loại từ tên
                Integer categoryId = categoryMap.get(categoryName);
                if (categoryId != null && comic.getIdCategory() == categoryId) {
                    result.add(comic);
                    break;
                }
            }
        }
        
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}