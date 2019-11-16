package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    String locationOffset;
    String primaryLocation;

    private final String LOCATION_SEPARATOR = " of ";

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

        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        String magnitude = decimalFormat.format(currentEarthquake.getMagnitude());

        //Find the TextView with View ID magnitude
        TextView magnitude_text_view = (TextView)listItemView.findViewById(R.id.magnitude_text_view);
        magnitude_text_view.setText(magnitude);

        //在设置某个视图背景颜色的时候，要传入解析后的颜色,而不是传入颜色资源id否则会报错
        //magnitude_text_view.setBackgroundColor(R.color.magnitude1);

        //GradientDrawable is a drawable with color gradient for button,background.
        GradientDrawable magnitudeCircle = (GradientDrawable)magnitude_text_view.getBackground();

        double magnitude_double = Double.parseDouble(magnitude);

        //setColor() need a parameter of color(int argb) not the resource id
        magnitudeCircle.setColor(getMagnitudeColor(magnitude_double));

        String originalLocation = currentEarthquake.getLocation();
        // The string split() method search the sequence of characters in the given string.
        // It returns true if sequence of char values are found in this string otherwise returns false.
        if (originalLocation.contains(LOCATION_SEPARATOR)){
            //The string split() method breaks a given string around matches of the given regular expression.
            String[] parts = originalLocation.split(LOCATION_SEPARATOR);
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];
        } else {
            //Need to know the Context before use the string getString() method.
            locationOffset = getContext().getString(R.string.near_the);
            primaryLocation = originalLocation;
        }

        TextView location_offset_text_view = (TextView)listItemView.findViewById(R.id.location_offset);
        location_offset_text_view.setText(locationOffset);

        TextView primary_location_text_view = (TextView)listItemView.findViewById(R.id.primary_location);
        primary_location_text_view.setText(primaryLocation);

        //Create a new Date instance with the timeInMillisecond of earthquake
        Date dateObject = new Date(currentEarthquake.getTimeInMilliseconds());

        //Set the format of data string
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日, YYYY");
        String date = dateFormat.format(dateObject);

        //Set the format of time string
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        String time = timeFormat.format(dateObject);

        TextView date_text_view = (TextView) listItemView.findViewById(R.id.date_text_view);
        date_text_view.setText(date);

        TextView time_text_view =(TextView) listItemView.findViewById(R.id.time_text_view);
        time_text_view.setText(time);

        //Delete these code and use the method of ListView ->setOnItemClickListener() to set the
        //intent of the every list_item
        /*
        final Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

        LinearLayout linearLayout = (LinearLayout)listItemView.findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                getContext().startActivity(websiteIntent);
            }
        });*/

        return listItemView;
    }

    public int getMagnitudeColor (double magnitude){
        //注意floor返回的是double型
        //round返回的是int型
        int magnitude_integer = (int)Math.floor(magnitude);
        int magnitudeColorResourceId;
        switch(magnitude_integer){
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        //ContextCompat is a helper for accessing features in Context
        //getColor() Returns a color associated with a particular resource ID
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);

    }
}
