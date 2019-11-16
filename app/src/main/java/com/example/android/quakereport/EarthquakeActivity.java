/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        EarthquakeAsyncTask earthquakeAsynctask = new EarthquakeAsyncTask();
        earthquakeAsynctask.execute("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2019-01-01&latitude=30.70&longitude=104.05&maxradiuskm=800");


    }

    private class EarthquakeAsyncTask extends AsyncTask<String, Void, ArrayList<Earthquake>> {
        @Override
        protected ArrayList<Earthquake> doInBackground(String... urls) {
            ArrayList<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(urls[0]);
            return earthquakes;
        }


        @Override
        protected void onPostExecute(final ArrayList<Earthquake> earthquakes) {
            // Find a reference to the {@link ListView} in the layout
            ListView earthquakeListView = (ListView) findViewById(R.id.list);

            EarthquakeAdapter adapter = new EarthquakeAdapter(EarthquakeActivity.this, earthquakes);
            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            earthquakeListView.setAdapter(adapter);

            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Earthquake currentEarthquake = earthquakes.get(i);

                    Uri uri = Uri.parse(currentEarthquake.getUrl());
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(websiteIntent);
                }
            });
        }
    }

}
