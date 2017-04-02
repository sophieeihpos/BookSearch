package com.example.sophie.bookssearch;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Sophie on 2/19/2017.
 */

public final class Utils {

    public static final String LOG_TAG = Utils.class.getSimpleName();
    public static final ArrayList<Book> books = new ArrayList<>();
    public static final String NO_AUTHOR="AUTHOR UNKNOWN";
    public static final String NO_WEBLINK="NO WEB LINK";
    private Utils() {
    }

    public static ArrayList<Book> extractBooks(String requestUrl) {

        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        ArrayList<Book> books = extractItemsFromJson(jsonResponse);
        return books;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        Log.e(LOG_TAG,"makeHttpRequest");
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<Book> extractItemsFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }
        books.clear();
        try {

            JSONObject response= new JSONObject(bookJSON);
            JSONArray items= response.getJSONArray("items");
            for(int i=0;i<items.length();i++){
                JSONObject ithitem=(JSONObject)  items.get(i);
                JSONObject volumeInfo = (JSONObject) ithitem.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                String url=NO_WEBLINK;
                if(volumeInfo.has("infoLink")){
                    url= volumeInfo.getString("infoLink");
                }

                String author=NO_AUTHOR;
                if(volumeInfo.has("authors")){
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    for(int j=0;j<authors.length();j++){
                        if(j==0){
                            author=authors.getString(j);
                        }else {
                            author=author+", "+authors.getString(j);
                        }

                    }
                }

                books.add(new Book(title,author,url));
            }

            return books;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the books JSON results", e);
        }
        return null;
    }

}
