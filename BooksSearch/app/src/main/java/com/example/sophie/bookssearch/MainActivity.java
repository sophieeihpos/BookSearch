package com.example.sophie.bookssearch;

import android.app.Activity;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;

import android.app.LoaderManager;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<Book>>{

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String GOOGLEBOOKS_REQUEST_URL_PART1 =
            "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String GOOGLEBOOKS_REQUEST_URL_PART2="&orderBy=relevance&maxResults=20";
    private static final int BOOKSLOADER_ID=1;
    private BookArrayAdapter bookArrayAdapter;
    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(LOG_TAG,"onCreate");

        final ProgressBar progressBar=(ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.GONE);

        bookArrayAdapter = new BookArrayAdapter(getBaseContext(), new ArrayList<Book>());
        final ListView booksListView = (ListView) findViewById(R.id.listView);
        final TextView emptyView = (TextView) findViewById(R.id.emptyView);

        booksListView.setEmptyView(emptyView);
        booksListView.setAdapter(bookArrayAdapter);

        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Book currentBook = (Book) bookArrayAdapter.getItem(i);
                String url = currentBook.getUrl();
                if (url == Utils.NO_WEBLINK) {
                    Toast.makeText(MainActivity.this, R.string.no_weblink, Toast.LENGTH_SHORT).show();
                } else {
                    intent.setData(Uri.parse(currentBook.getUrl()));
                    startActivity(intent);
                }
            }
        });

        Button button = (Button) findViewById(R.id.button);

        final LoaderManager loaderManager = getLoaderManager();
        if(savedInstanceState!=null){
            progressBar.setVisibility(View.VISIBLE);
            bookArrayAdapter.clear();
            emptyView.setText("");
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                loaderManager.initLoader(BOOKSLOADER_ID, null, MainActivity.this);
                Log.i(LOG_TAG,"initLoader");
            }else {
                emptyView.setText(R.string.no_internet_connection);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }else {
            emptyView.setText(R.string.user_instructions);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                bookArrayAdapter.clear();
                emptyView.setText("");
                EditText editText= (EditText) findViewById(R.id.editText);
                editText.clearFocus();
                searchText=editText.getText().toString().trim();
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    loaderManager.restartLoader(BOOKSLOADER_ID, null, MainActivity.this);
                    Log.i(LOG_TAG,"restartLoader");
                }else {
                    emptyView.setText(R.string.no_internet_connection);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                hideKeyboard(MainActivity.this, v);
            }
        });

    }

    @Override
    public android.content.Loader<ArrayList<Book>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG,"onCreateLoader");
        String url;
        if(searchText==null){
            url=null;
        }else{
            url = GOOGLEBOOKS_REQUEST_URL_PART1+searchText+GOOGLEBOOKS_REQUEST_URL_PART2;
        }
        return new BooksLoader(this,url);

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, ArrayList<Book> books) {
        Log.i(LOG_TAG,"onLoaderFinished");
        bookArrayAdapter.clear();
        if (books != null && !books.isEmpty()) {
            bookArrayAdapter.addAll(books);
        }
        TextView emptyView = (TextView) findViewById(R.id.emptyView);
        emptyView.setText(R.string.no_results);
        ProgressBar progressBar=(ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Book>> loader) {
        Log.i(LOG_TAG,"onLoaderReset");
        bookArrayAdapter.clear();
    }


    public static void hideKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}

