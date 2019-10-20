package com.example.android.quakereport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
    /**
     *
     * @param context of the app
     * @param earthquakes is the list of the earthquake, which is the data sources of the adapter
     */
    public EarthquakeAdapter(Context context, ArrayList<Earthquake> earthquakes){
        super(context, 0, earthquakes);

    }

    /**
     * Returns a list item view that displays information about the earthquake at the given position
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        //Check if there is an existing list item view(called convertView) that we can reuse.
        //otherwise, if convertView is null, then inflate a new list item layout
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_list_item, parent, false);
        }
        //Find the earthquake at the given position in the list of earthquakes
        Earthquake currentEarthquake = getItem(position);

        //Find the TextView with View ID magnitude
        TextView magnitude_text_view = (TextView)listItemView.findViewById(R.id.magnitude_text_view);
        magnitude_text_view.setText(currentEarthquake.getMagnitude());

        TextView location_text_view = (TextView)listItemView.findViewById(R.id.location_text_view);
        location_text_view.setText(currentEarthquake.getLocation());


        Date dateObject = new Date(currentEarthquake.getTimeInMilliseconds());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM DD, YYYY");
        String date = dateFormat.format(dateObject);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        String time = timeFormat.format(dateObject);

        TextView date_text_view = (TextView) listItemView.findViewById(R.id.date_text_view);
        date_text_view.setText(date);

        TextView time_text_view =(TextView) listItemView.findViewById(R.id.time_text_view);
        time_text_view.setText(time);

        return listItemView;
    }
}
