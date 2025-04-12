package vn.edu.tlu.cse.haibui.appdoctruyen_btl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DbHelper {
    String DATABASE_NAME = "COMIC.db";
    String tblname = "Truyen";
    String tblname2 = "TheLoai";
    String tblname3 = "User";
    String tblname4 = "FavoriteComics";
    String tblname5 = "LinkTruyen";
    Context context;

    public DbHelper(Context context) {
        this.context = context;
        createTheLoaiTable();
        createTruyenTable();
        createUserTable();
        createFavoriteTable();
        createLinkTruyenTable();
    }

    private void createTheLoaiTable() {
        SQLiteDatabase db = openDB();
        String query = "CREATE TABLE IF NOT EXISTS " + tblname2 +
                " (IdCategory INTEGER PRIMARY KEY, " +
                "NameCategory TEXT NOT NULL, " +
                "Description TEXT NOT NULL)";
        db.execSQL(query);
        closeDB(db);
    }

    private void createTruyenTable() {
        SQLiteDatabase db = openDB();
        String query = "CREATE TABLE IF NOT EXISTS " + tblname +
                " (Id INTEGER PRIMARY KEY, " +
                "Name TEXT NOT NULL, " +
                "Author TEXT, " +
                "IdCategory INTEGER NOT NULL, " +
                "Description TEXT, " +
                "isFavorite INTEGER NOT NULL, " +
                "imageLink TEXT, " +
                "numberOfChapter INTEGER, " +
                "FOREIGN KEY (IdCategory) REFERENCES " + tblname2 + "(IdCategory))";
        db.execSQL(query);
        closeDB(db);
    }

    private void createUserTable() {
        SQLiteDatabase db = openDB();
        String query = "CREATE TABLE IF NOT EXISTS " + tblname3 +
                " (idUser INTEGER PRIMARY KEY, " +
                "username TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "fullname TEXT, " +
                "email TEXT, " +
                "role TEXT DEFAULT 'user')";
        db.execSQL(query);
        closeDB(db);
    }

    private void createFavoriteTable() {
        SQLiteDatabase db = openDB();
        String query = "CREATE TABLE IF NOT EXISTS " + tblname4 +
                " (idUser INTEGER, idComic TEXT, PRIMARY KEY (idUser, idComic), " +
                "FOREIGN KEY (idUser) REFERENCES " + tblname3 + "(idUser), " +
                "FOREIGN KEY (idComic) REFERENCES " + tblname + "(Id))";
        db.execSQL(query);
        closeDB(db);
    }

    private void createLinkTruyenTable() {
        SQLiteDatabase db = openDB();
        String query = "CREATE TABLE IF NOT EXISTS " + tblname5 +
                " (Id INTEGER NOT NULL, " +
                "Chap INTEGER NOT NULL, " +
                "Link TEXT NOT NULL)";
        db.execSQL(query);
        closeDB(db);
    }

    public SQLiteDatabase openDB() {
        return context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    public void closeDB(SQLiteDatabase db) {
        db.close();
    }


    // ================= User methods =================

    public boolean addUser(User user) {
        SQLiteDatabase db = openDB();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("fullname", user.getFullname());
        values.put("email", user.getEmail());
        values.put("role", user.getRole());

        long result = db.insert(tblname3, null, values);
        closeDB(db);
        return result != -1;
    }

    public User getUser(String username, String password) {
        SQLiteDatabase db = openDB();
        Cursor cursor = db.query(tblname3, null, "username=? AND password=?",
                new String[]{username, password}, null, null, null);

        if (cursor.moveToFirst()) {
            int idUser = cursor.getInt(0);
            String fullname = cursor.getString(3);
            String email = cursor.getString(4);
            String role = cursor.getString(5);
            closeDB(db);
            return new User(idUser, username, password, fullname, email, role);
        }

        closeDB(db);
        return null;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = openDB();
        Cursor cursor = db.query(tblname3, null, "username=?",
                new String[]{username}, null, null, null);
        boolean exists = cursor.moveToFirst();
        closeDB(db);
        return exists;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = openDB();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("fullname", user.getFullname());
        values.put("email", user.getEmail());
        values.put("role", user.getRole());

        int result = db.update(tblname3, values, "idUser=?",
                new String[]{String.valueOf(user.getIdUser())});
        closeDB(db);
        return result > 0;
    }

    // ================= Comic methods =================

    public ArrayList<Comic> getAllComic() {
        ArrayList<Comic> tmp = new ArrayList<>();
        SQLiteDatabase db = openDB();
        Cursor cursor = db.query(tblname, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String author = cursor.getString(2);
            int IdCategory = cursor.getInt(3);
            String des = cursor.getString(4);
            int isFavorite = cursor.getInt(5);
            String imageLink = cursor.getString(6);
            int numbOfChap = cursor.getInt(7);
            tmp.add(new Comic(id, name, author, IdCategory, des, isFavorite, imageLink, numbOfChap));
        }

        closeDB(db);
        return tmp;
    }

    public ArrayList<ComicCategory> getAllCategory() {
        ArrayList<ComicCategory> tmp = new ArrayList<>();
        SQLiteDatabase db = openDB();
        Cursor cursor = db.query(tblname2, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            tmp.add(new ComicCategory(id, name));
        }

        closeDB(db);
        return tmp;
    }

    public String getCategoryOfComic(String id) {
        String tmp = "";
        SQLiteDatabase db = openDB();
        Cursor c = db.rawQuery("SELECT NameCategory FROM TheLoai WHERE IdCategory=?", new String[]{id});
        if (c.moveToFirst()) {
            tmp = c.getString(0);
        }
        closeDB(db);
        return tmp;
    }

    public int updateFarvorite(String id, int isFavorite) {
        SQLiteDatabase db = openDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put("isFavorite", isFavorite);
        int tmp = db.update(tblname, contentValues, "Id=?", new String[]{id});
        closeDB(db);
        return tmp;
    }

    public ArrayList<Chapter> getAllUrl(String id) {
        SQLiteDatabase db = openDB();
        ArrayList<Chapter> tmp = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT Chap, Link FROM LinkTruyen WHERE Id = ? GROUP BY Chap", new String[]{id});
        while (c.moveToNext()) {
            String chap = c.getString(0);
            String url = c.getString(1);
            tmp.add(new Chapter(Integer.parseInt(chap), url));
        }
        closeDB(db);
        return tmp;
    }

    // Thêm truyện vào danh sách yêu thích
    public boolean addFavoriteComic(int userId, String comicId) {
        SQLiteDatabase db = openDB();
        ContentValues values = new ContentValues();
        values.put("idUser", userId);
        values.put("idComic", comicId);

        long result = db.insert(tblname4, null, values);
        closeDB(db);
        return result != -1;
    }

    // Xóa truyện khỏi danh sách yêu thích
    public boolean removeFavoriteComic(int userId, String comicId) {
        SQLiteDatabase db = openDB();
        int result = db.delete(tblname4, "idUser=? AND idComic=?",
                new String[]{String.valueOf(userId), comicId});
        closeDB(db);
        return result > 0;
    }

    // Kiểm tra xem truyện có trong danh sách yêu thích không
    public boolean isFavoriteComic(int userId, String comicId) {
        SQLiteDatabase db = openDB();
        Cursor cursor = db.query(tblname4, null, "idUser=? AND idComic=?",
                new String[]{String.valueOf(userId), comicId}, null, null, null);
        boolean exists = cursor.moveToFirst();
        closeDB(db);
        return exists;
    }

    // Lấy danh sách truyện yêu thích của user
    public ArrayList<Comic> getFavoriteComics(int userId) {
        ArrayList<Comic> favorites = new ArrayList<>();
        SQLiteDatabase db = openDB();
        String query = "SELECT t.* FROM " + tblname + " t " +
                "INNER JOIN " + tblname4 + " f ON t.Id = f.idComic " +
                "WHERE f.idUser = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String author = cursor.getString(2);
            int IdCategory = cursor.getInt(3);
            String des = cursor.getString(4);
            int isFavorite = cursor.getInt(5);
            String imageLink = cursor.getString(6);
            int numbOfChap = cursor.getInt(7);
            favorites.add(new Comic(id, name, author, IdCategory, des, isFavorite, imageLink, numbOfChap));
        }

        closeDB(db);
        return favorites;
    }
}
