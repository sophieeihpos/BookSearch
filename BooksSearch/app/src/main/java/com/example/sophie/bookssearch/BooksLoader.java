package com.example.sophie.bookssearch;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Sophie on 2/19/2017.
 */

public class BooksLoader extends AsyncTaskLoader {

    private String url;
    public BooksLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i("BooksLoader","onStartLoading");
        forceLoad();
    }
    @Override
    public ArrayList<Book> loadInBackground() {
        Log.i("BooksLoader","loadInBackground");
        if (url == null) {
            return null;
        }
        Log.i("BooksLoader",url);
        ArrayList<Book> books =Utils.extractBooks(url);
        return books;
    }

}
