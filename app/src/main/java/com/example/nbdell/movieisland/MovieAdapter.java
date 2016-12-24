package com.example.nbdell.movieisland;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NB DELL on 11/29/2016.
 */

public class MovieAdapter extends BaseAdapter {

    List<Movie> movielist = new ArrayList<>();
    Context context;
    LayoutInflater inflter;

    public MovieAdapter(Context mcontext, List<Movie> movies) {
        movielist = movies;
        context = mcontext;
        inflter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return movielist.size();
    }

    @Override
    public Object getItem(int position) {
        return movielist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView moviePoster;
        if(convertView == null) {
            convertView =  inflter.inflate(R.layout.image_item, null);
            moviePoster = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(moviePoster);
        }
        else{
            moviePoster = (ImageView) convertView.getTag();
        }
        if( movielist.get(position) != null )
            Picasso.with(context).load(movielist.get(position).getImageFullURL()).into(moviePoster);

        return convertView;
    }

}
/*
public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    // A holder will hold the references
    // to your views.
    ViewHodler holder;

    if(convertView == null) {
        View rowView = inflater.inflate(R.layout.artist, parent, false);
        holder = new ViewHodler();
        holder.someTextField = (TextView) rowView.findViewById(R.id.sometext);
        holder.someImageView = (ImageView) rowView.findViewById(R.id.someimage);
        rowView.setTag(holder);
    }
    else {
        holder = (ViewHodler) convertView.getTag();
    }
    holder.someTextView.setText(somtexts.get(position));
    if (somearray.get(position) != null)
        Picasso.with(context)
            .load(somearray.get(position))
            .into(holder.someImageView);

    return convertView;
}

class ViewHodler {
    // declare your views here
    TextView someTextView;
    ImageView someImageView;
}
 */