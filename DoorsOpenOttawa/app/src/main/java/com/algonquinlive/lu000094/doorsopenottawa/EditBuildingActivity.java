package com.algonquinlive.lu000094.doorsopenottawa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquinlive.lu000094.doorsopenottawa.model.Building;
import com.algonquinlive.lu000094.doorsopenottawa.model.eHttpMethod;
import com.algonquinlive.lu000094.doorsopenottawa.model.mRequest;
import com.algonquinlive.lu000094.doorsopenottawa.model.mUriProvider;

import static com.algonquinlive.lu000094.doorsopenottawa.model.mStringProvider.string_bulding_Address;
import static com.algonquinlive.lu000094.doorsopenottawa.model.mStringProvider.string_bulding_Description;
import static com.algonquinlive.lu000094.doorsopenottawa.model.mStringProvider.string_bulding_Id;
import static com.algonquinlive.lu000094.doorsopenottawa.model.mStringProvider.string_bulding_Name;
/**
 * Created by shiva on 2016-12-12.
 */

public class EditBuildingActivity extends FragmentActivity {
    private String buildingName;
    private String buildingAdress;
    private String buildingDescription;
    private ProgressBar progressBar;

    private EditText txtName;
    private EditText txtAddress;
    private EditText txtDescription;
    private  Building aBuilding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newbuilding);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        Bundle bundle = getIntent().getExtras();
        aBuilding=new Building();
        aBuilding.setBuildingId(bundle.getInt(string_bulding_Id));

        txtName = (EditText) findViewById(R.id.txtName);
        txtAddress = (EditText) findViewById(R.id.txtAddress);
        txtDescription = (EditText) findViewById(R.id.txtDescription);

        buildingName=bundle.getString(string_bulding_Name);
        txtName.setText(buildingName);
        buildingAdress = bundle.getString(string_bulding_Address);
        txtAddress.setText(buildingAdress);
        buildingDescription = bundle.getString(string_bulding_Description);
        txtDescription.setText(buildingDescription);

        Button btnSave = (Button) findViewById(R.id.btnAdd);
        btnSave.setText("Save");
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updatePlanet(mUriProvider.REST_URI_EDIT);
            }
        });

        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setText("Delete Building");
        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                deleteBuilding(mUriProvider.REST_URI_EDIT);
            }
        });


    }
    private void deleteBuilding(String uri) {
        mRequest myRequest = new mRequest();
        myRequest.setMethod( eHttpMethod.DELETE );
        // DELETE the planet with Id 8
        myRequest.setUri( uri + Integer.toString(aBuilding.getBuildingId()));
        MyTask2 deleteTask = new MyTask2();
        deleteTask.execute( myRequest );
    }

    private void updatePlanet(String uri) {
        buildingName= txtName.getText().toString();
        buildingAdress= txtAddress.getText().toString();
        buildingDescription = txtDescription.getText().toString();

        aBuilding.setName(buildingName);
        aBuilding.setAddress(buildingAdress);
        aBuilding.setDescription(buildingDescription);
        aBuilding.setImage("tmp.png");

        mRequest myRequest = new mRequest();
        myRequest.setMethod(eHttpMethod.PUT);
        myRequest.setUri(uri+Integer.toString(aBuilding.getBuildingId()));
        //myRequest.setUri(uri);

        myRequest.setParam("address", aBuilding.getAddress());
        myRequest.setParam("description", aBuilding.getDescription());
        myRequest.setParam("name", aBuilding.getName());
        myRequest.setParam("image", aBuilding.getImage());

        MyTask editTast = new MyTask();
        editTast.execute(myRequest);
    }
    private class MyTask extends AsyncTask<mRequest, String, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(mRequest... params) {

            String content = HttpManager.postDataWithParams(params[0]);

            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);

            if (result == null || result.contains("null")) {
                Toast.makeText(EditBuildingActivity.this, "Failed To edit a building", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(EditBuildingActivity.this, "Building updated successfully", Toast.LENGTH_LONG).show();
            }
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        }
    }
    private class MyTask2 extends AsyncTask<mRequest, String, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(mRequest... params) {

            String content = HttpManager.postDataWithParams(params[0]);

            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);

            if (result == null || result.contains("null")) {
                Toast.makeText(EditBuildingActivity.this, "Failed To delete building", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(EditBuildingActivity.this, "Building deleted successfully", Toast.LENGTH_LONG).show();
            }
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        }
    }
}
