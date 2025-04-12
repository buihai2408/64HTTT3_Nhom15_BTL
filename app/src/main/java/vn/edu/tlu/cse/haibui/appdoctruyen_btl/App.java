package vn.edu.tlu.cse.haibui.appdoctruyen_btl;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

public class App extends Application {
    private static ArrayList<Comic> comics;
    private static User currentUser;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static ArrayList<Comic> getComic(Context context) {
        if (comics == null) {
            DbHelper db = new DbHelper(context);
            comics = db.getAllComic();
        }
        return comics;
    }

    public static void setComics(ArrayList<Comic> newComics) {
        comics = newComics;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static Context getContext() {
        return context;
    }
}