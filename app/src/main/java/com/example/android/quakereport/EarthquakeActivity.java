package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
    //使用下滑刷新接口
public class EarthquakeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    private static String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=";
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        //Not to show the empty view in the first
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        emptyView.setVisibility(View.GONE);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        updateUrl();


        //Check the network
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //If there is network and the network can be connected
        if (networkInfo != null && networkInfo.isConnected()) {
            EarthquakeActivity.EarthquakeAsyncTask earthquakeAsynctask = new EarthquakeActivity.EarthquakeAsyncTask();
            //AsyncTask.execute方法会将参数传到doInBackground中执行
            earthquakeAsynctask.execute(USGS_REQUEST_URL);
        } else {
            //if there is no network or the network can't be connected
            //set the progressBar divisible and emptyView visible
            progressBar.setVisibility(View.GONE);
            emptyView.setText("No Internet Connection");
            emptyView.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        //设置刷新监听器
        swipeRefreshLayout.setOnRefreshListener(this);
        //设置进度圆圈的背景颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

    }

    @Override
    public void onRefresh() {
        //update USGS_REQUEST_URL
        updateUrl();
        EarthquakeActivity.EarthquakeAsyncTask earthquakeAsyncTask = new EarthquakeActivity.EarthquakeAsyncTask();
        //AsyncTask.execute方法会将参数传到doInBackground中执行
        earthquakeAsyncTask.execute(USGS_REQUEST_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
         * <p>
         * 可以在此方法内修改 UI。我们输入 {@link ArrayList<Earthquake>} 对象
         * （该对象从 doInBackground() 方法返回），并更新屏幕上的视图。
         */
        @Override
        protected void onPostExecute(final ArrayList<Earthquake> earthquakes) {
            //收到数据后结束下拉刷新动作
            swipeRefreshLayout.setRefreshing(false);

            EarthquakeAdapter adapter = new EarthquakeAdapter(EarthquakeActivity.this, earthquakes);

            // Find a reference to the {@link ListView} in the layout
            ListView earthquakeListView = (ListView) findViewById(R.id.list);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            TextView emptyView = (TextView)findViewById(R.id.empty_view);
            if (earthquakes != null && !earthquakes.isEmpty()) {


                // Set the adapter on the {@link ListView}
                // so the list can be populated in the user interface
                earthquakeListView.setAdapter(adapter);

                earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                emptyView.setVisibility(View.INVISIBLE);


            } else {
                //收到数据后结束下拉刷新动作
                swipeRefreshLayout.setRefreshing(false);


                // Set the adapter on the {@link ListView}
                // so the list can be populated in the user interface
                earthquakeListView.setAdapter(adapter);
                //if the earthquake array list null then set the emptyView visible and progressBar divisible
                emptyView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }

        }
    }

    private void updateUrl(){

        USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=";


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //获取最小震级和之前天数对应偏好设置的值
        String minMagnitude = sharedPreferences.getString(getString(R.string.setting_min_magnitude_key), getString(R.string.setting_min_magnitude_default));
        String before_day = sharedPreferences.getString(getString(R.string.setting_date), getString(R.string.setting_date_default));

        //add the date，获取之前某段时间的方法
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, - Integer.parseInt(before_day));
        Date day = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String starttime = format.format(day);
        USGS_REQUEST_URL += starttime;

        //先将USGS_REQUEST_URL 常量的值修改为 基准 URI
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        //在此baseUri之上进行添加，
        Uri.Builder uriBuilder = baseUri.buildUpon();
        //添加最小震级参数
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        //获取位置偏好的参数
        String location = sharedPreferences.getString(getString(R.string.setting_location),"world");

        //如果是位置偏好是世界范围的则不添加位置偏好到url中
        if (!location.equals("world")){
            String[] part = location.split(",");
            String latitude = part[0];
            String longitude = part[1];
            String maxradiuskm = "800";
            uriBuilder.appendQueryParameter("latitude", latitude);
            uriBuilder.appendQueryParameter("longitude", longitude);
            uriBuilder.appendQueryParameter("maxradiuskm", maxradiuskm);
        }

        USGS_REQUEST_URL = uriBuilder.toString();

    }
}
