package com.algonquinlive.lu000094.doorsopenottawa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquinlive.lu000094.doorsopenottawa.model.Building;
import com.algonquinlive.lu000094.doorsopenottawa.model.eHttpMethod;
import com.algonquinlive.lu000094.doorsopenottawa.model.mRequest;
import com.algonquinlive.lu000094.doorsopenottawa.model.mUriProvider;


/**
 * Created by lu000094 on 12/14/16.
 */

public class NewBuildingActivity extends FragmentActivity {
    private String buildingName;
    private String buildingAdress;
    private String buildingDescription;
    private ProgressBar progressBar;

    private static final String TAG ="tag" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newbuilding);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        Log.d(TAG, "onCreate: "+"new POST building view");

        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                   addBuilding();
            }
        });

        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               Intent i = new Intent(getApplicationContext(),MainActivity.class);
               startActivity(i);
           }
       });
        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setVisibility(View.GONE);
    }

    private void addBuilding(){
        final EditText txtName = (EditText) findViewById(R.id.txtName);
        buildingName= txtName.getText().toString();
        final EditText txtAddress = (EditText) findViewById(R.id.txtAddress);
        buildingAdress = txtAddress.getText().toString();
        final EditText txtDescription = (EditText) findViewById(R.id.txtDescription);
        buildingDescription = txtDescription.getText().toString();

        createPlanet(mUriProvider.REST_URI);
    }

    private void createPlanet(String uri) {
        Building aBuilding = new Building();
        aBuilding.setName(buildingName);
        aBuilding.setAddress(buildingAdress);
        aBuilding.setDescription(buildingDescription);
        aBuilding.setImage("tmp.png");


        mRequest myRequest = new mRequest();
        myRequest.setMethod(eHttpMethod.POST);
        myRequest.setUri(uri);
        myRequest.setParam("name", aBuilding.getName());
        myRequest.setParam("address", aBuilding.getAddress());
        myRequest.setParam("description", aBuilding.getDescription());
        myRequest.setParam("image", aBuilding.getImage());

        MyTask postTask = new MyTask();
        postTask.execute(myRequest);
    }

    private class MyTask extends AsyncTask<mRequest, String, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(mRequest... params) {

            String content = HttpManager.getDataWithParams(params[0]);

            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);
            if (result == null) {
                Toast.makeText(NewBuildingActivity.this, "Failed To add a building", Toast.LENGTH_LONG).show();
                return;
            }else{
                Toast.makeText(NewBuildingActivity.this, "Building added succsessfully", Toast.LENGTH_LONG).show();
//                try {
//                    JSONObject jsonResponse = new JSONObject(result);
//                    JSONArray buildingArray = jsonResponse.getJSONArray("buildings");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }
}