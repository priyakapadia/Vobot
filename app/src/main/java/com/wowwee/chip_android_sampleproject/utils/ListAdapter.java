package com.wowwee.chip_android_sampleproject.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wowwee.chip_android_sampleproject.R;

/**
 * The ListAdapter class simply creates the view on the menu page. It customizes the list so that it displays text overlaying an image.
*/

public class ListAdapter extends ArrayAdapter<String> {

    private final Activity Context;
    private final String[] ListItemsName;
    private final Integer[] ImageName;

    public ListAdapter(Activity context, String[] content,
                       Integer[] ImageName) {
        super(context, R.layout.list_items, content);
        this.Context = context;
        this.ListItemsName = content;
        this.ImageName = ImageName;
    }

    public View getView(int position, View view, ViewGroup parent) {

        //Inflate the view for the list
        LayoutInflater inflater = Context.getLayoutInflater();
        View ListViewSingle = inflater.inflate(R.layout.list_items, null, true);

        TextView ListViewItems = (TextView) ListViewSingle.findViewById(R.id.textView1);
        ImageView ListViewImage = (ImageView) ListViewSingle.findViewById(R.id.imageView1);

        //Iterate through the array of text and images to create the list
        ListViewItems.setText(ListItemsName[position]);
        ListViewImage.setImageResource(ImageName[position]);
        return ListViewSingle;

    }

}