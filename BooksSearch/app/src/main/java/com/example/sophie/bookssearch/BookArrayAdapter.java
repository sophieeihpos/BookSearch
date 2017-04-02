package com.example.sophie.bookssearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sophie on 2/19/2017.
 */

public class BookArrayAdapter extends ArrayAdapter {

    public BookArrayAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.listItemTitle);
        TextView author = (TextView) convertView.findViewById(R.id.listItemAuthor);

        Book book = (Book) getItem(position);
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        return convertView;

    }
}
