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

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {


    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2019-01-01&latitude=30.70&longitude=104.05&maxradiuskm=800";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        //Not to show the empty view in the first
        TextView emptyView = (TextView)findViewById(R.id.empty_view);
        emptyView.setVisibility(View.GONE);

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);

        //Check the network
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //If there is network and the network can be connected
        if(networkInfo != null && networkInfo.isConnected()) {
            EarthquakeAsyncTask earthquakeAsynctask = new EarthquakeAsyncTask();
            //AsyncTask.execute方法会将参数传到doInBackground中执行
            earthquakeAsynctask.execute(USGS_REQUEST_URL);
        }
        else {
            //if there is no network or the network can't be connected
            //set the progressBar divisible and emptyView visible
            progressBar.setVisibility(View.GONE);
            emptyView.setText("No Internet Connection");
            emptyView.setVisibility(View.VISIBLE);
        }

    }

    //将EarthquakeAsyncTask 声明为EarthquakeActivity的内部类，用于在后台线程上执行网络请求
    private class EarthquakeAsyncTask extends AsyncTask<String, Void, ArrayList<Earthquake>> {
        /**
         * 此方法在后台线程上激活（调用），因此我们可以执行
         * 诸如做出网络请求等长时间运行操作。
         *
         * 因为不能从后台线程更新 UI，所以我们仅返回
         * {@link ArrayList<Earthquake>} 对象作为结果。
         */
        @Override
        protected ArrayList<Earthquake> doInBackground(String... urls) {
            //如果不存在任何url或者第一个url为空
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            //Perform the HTTP request for earthquake data and process the response
            //Not use hard code Utils.fetchEarthquakeData(USGS.REQUEST.URL) here
            //instead use the urls[0] to access the url in the urls array.
            //pass the USGS.REQUEST.URL into the execute() method when we want execute the async task.
            ArrayList<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(urls[0]);
            //返回的值会传到UI线程中作为onPostExecute()的输入参数
            return earthquakes;
        }

        /**
         * 此方法是在完成后台工作后，在主 UI 线程上
         * 激活的。
         *
         * 可以在此方法内修改 UI。我们输入 {@link ArrayList<Earthquake>} 对象
         * （该对象从 doInBackground() 方法返回），并更新屏幕上的视图。
         */
        @Override
        protected void onPostExecute(final ArrayList<Earthquake> earthquakes) {

            // Find a reference to the {@link ListView} in the layout
            ListView earthquakeListView = (ListView) findViewById(R.id.list);
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);

            if (earthquakes != null && !earthquakes.isEmpty()) {

                EarthquakeAdapter adapter = new EarthquakeAdapter(EarthquakeActivity.this, earthquakes);
                // Set the adapter on the {@link ListView}
                // so the list can be populated in the user interface
                earthquakeListView.setAdapter(adapter);

                earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Earthquake currentEarthquake = earthquakes.get(i);

                        //set the Intent to each listItem
                        Uri uri = Uri.parse(currentEarthquake.getUrl());
                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(websiteIntent);
                    }
                });
                //if the earthquake array list is not null then set the progressBar divisible
                progressBar.setVisibility(View.GONE);
            }
            else {
                //if the earthquake array list null then set the emptyView visible and progressBar divisible
                earthquakeListView.setEmptyView(findViewById(R.id.empty_view));
                progressBar.setVisibility(View.GONE);

            }

        }
    }

}
