package com.algonquinlive.lu000094.doorsopenottawa;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquinlive.lu000094.doorsopenottawa.model.Building;
import com.algonquinlive.lu000094.doorsopenottawa.model.eHttpMethod;
import com.algonquinlive.lu000094.doorsopenottawa.model.mRequest;
import com.algonquinlive.lu000094.doorsopenottawa.model.mUriProvider;
import com.algonquinlive.lu000094.doorsopenottawa.parsers.BuildingJSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.algonquinlive.lu000094.doorsopenottawa.model.mStringProvider.string_bulding_Address;
import static com.algonquinlive.lu000094.doorsopenottawa.model.mStringProvider.string_bulding_Description;
import static com.algonquinlive.lu000094.doorsopenottawa.model.mStringProvider.string_bulding_Id;
import static com.algonquinlive.lu000094.doorsopenottawa.model.mStringProvider.string_bulding_Name;

/**
 *  This app for the Doors Open Ottawa (Links to an external site.) event with the City of Ottawa
 *  This app has two activities. The first activity (MainActivity) displays the list of buildings from my RESTful API server.
 *  The section activity (DetailActivity) displays the detailed information for a selected building.
 *  @author Wenjuan Lu (lu000094@algonquinlive.com)
 */


public class MainActivity extends ListActivity implements AdapterView.OnItemLongClickListener {

    public static final String BUILDING_EXTRA = "BUILDING_EXTRA";

    private ProgressBar pb;
    private List tasks;

    private List buildingList;
    private SwipeRefreshLayout mySwipeToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        getListView().setOnItemLongClickListener(this);
        tasks = new ArrayList();

        if (isOnline()) {
            requestBuildingData(mUriProvider.REST_URI);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }

        //Swipe down to refresh List
        mySwipeToRefresh=(SwipeRefreshLayout) findViewById(R.id.swipetorefresh);
        mySwipeToRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        mySwipeToRefresh.setProgressViewOffset(true,1,5);
                        mySwipeToRefresh.setColorSchemeColors(3543);
                        requestBuildingData(mUriProvider.REST_URI);
                        mySwipeToRefresh.setRefreshing(false);
                    }
                }

        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage("Full name: Wenjuan Lu\nUsername: lu000094")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
        if (item.getItemId() == R.id.action_add_building) {
            if (isOnline()) {
                Intent myIntent = new Intent(this,NewBuildingActivity.class);
                startActivity(myIntent);
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }
        if(item.isCheckable()){
            // which sort menu item did the user pick?
            switch( item.getItemId() ) {
                case R.id.action_sort_name_asc:
                    Collections.sort( buildingList, new Comparator<Building>() {
                        @Override
                        public int compare(Building lhs, Building rhs ) {
                            Log.i( "PLANETS", "Sorting planets by name (a-z)" );
                            return lhs.getName().compareTo( rhs.getName() );
                        }
                    });
                    break;

                case R.id.action_sort_name_dsc:
                    Collections.sort( buildingList, Collections.reverseOrder(new Comparator<Building>() {
                        @Override
                        public int compare( Building lhs, Building rhs ) {
                            Log.i( "PLANETS", "Sorting planets by name (z-a)" );
                            return lhs.getName().compareTo( rhs.getName() );
                        }
                    }));
                    break;
            }
            // remember which sort option the user picked
            item.setChecked( true );
            // re-fresh the list to show the sort order
            //((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
            updateDisplay();
        }
        return false;
    }


    private void requestBuildingData(String uri) {
        MyTask2 task = new MyTask2();

        mRequest myRequest = new mRequest();
        myRequest.setUri(uri);
        myRequest.setMethod(eHttpMethod.GET);

        task.execute(myRequest);
    }

    protected void updateDisplay() {
        //Use BuildingAdapter to display data
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MainActivity.BUILDING_EXTRA, (Building) buildingList.get(position));
        startActivity(intent);
    }

    private class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected String doInBackground(String... params) {

            String content = HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            Log.d("DOOR-OPEN-OTTAWA", "result=" + result);
            buildingList = BuildingJSONParser.parseFeed(result);

            updateDisplay();
        }
    }
    private class MyTask2 extends AsyncTask<mRequest, String, String> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected String doInBackground(mRequest ... params) {

            String content = HttpManager.getDataWithParams(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            Log.d("DOOR-OPEN-OTTAWA", "result=" + result);
            buildingList = BuildingJSONParser.parseFeed(result);
//            for (int i=0;i<140;i++)
//            {
//                buildingList.remove(0);
//            }
            updateDisplay();
        }
    }



    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Building selectedBuilding = (Building) buildingList.get(position);

        String name = selectedBuilding.getName().toString();
        String dess = selectedBuilding.getDescription().toString();
        String address = selectedBuilding.getAddress().toString();
        int bId = selectedBuilding.getBuildingId();


        Intent intent = new Intent(getApplicationContext(), EditBuildingActivity.class);
        intent.putExtra(string_bulding_Address, address);
        intent.putExtra(string_bulding_Name, name);
        intent.putExtra(string_bulding_Description, dess);
        intent.putExtra(string_bulding_Id,bId);
        startActivity(intent);
        return false;
    }
}
