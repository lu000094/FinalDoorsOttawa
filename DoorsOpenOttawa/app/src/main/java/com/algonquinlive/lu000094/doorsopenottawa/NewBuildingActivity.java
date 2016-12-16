package com.algonquinlive.lu000094.doorsopenottawa;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
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
import com.algonquinlive.lu000094.doorsopenottawa.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


/**
 * Created by lu000094 on 12/14/16.
 */

public class NewBuildingActivity extends FragmentActivity {
    private String buildingName;
    private String buildingAdress;
    private String buildingDescription;
    private ProgressBar progressBar;

    private static final int SELECT_IMAGE = 1;
    private static final String TAG ="tag" ;
    private File photo;
    private String imageUrl;
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
        btnDelete.setText("Select image");
        //btnDelete.setVisibility(View.GONE);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkPermission();
            }
        });
    }

    private void checkPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void addBuilding(){
        final EditText txtName = (EditText) findViewById(R.id.txtName);
        buildingName= txtName.getText().toString();
        final EditText txtAddress = (EditText) findViewById(R.id.txtAddress);
        buildingAdress = txtAddress.getText().toString();
        final EditText txtDescription = (EditText) findViewById(R.id.txtDescription);
        buildingDescription = txtDescription.getText().toString();

        createBuilding(mUriProvider.REST_URI);
    }

    private Building aBuilding = null;
    private void createBuilding(String uri) {
        aBuilding = new Building();
        aBuilding.setName(buildingName);
        aBuilding.setAddress(buildingAdress);
        aBuilding.setDescription(buildingDescription);
        aBuilding.setImage(imageUrl);


        mRequest myRequest = new mRequest();
        myRequest.setMethod(eHttpMethod.POST);
        myRequest.setUri(uri);
        myRequest.setParam("name", aBuilding.getName());
        myRequest.setParam("address", aBuilding.getAddress());
        myRequest.setParam("description", aBuilding.getDescription());

        if (aBuilding.getImage() != null) {
            myRequest.setParam("image", "images/" + aBuilding.getImage());
        }else{
            myRequest.setParam("image", "images/image.jpg");
        }

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
            if (result == null || result.contains("null")) {
                Toast.makeText(NewBuildingActivity.this, "Failed To add a building", Toast.LENGTH_LONG).show();
                return;
            }else{
                Toast.makeText(NewBuildingActivity.this, "Building added succsessfully", Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    aBuilding.setBuildingId(jsonResponse.getInt("buildingId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class MyTask2 extends AsyncTask<mRequest, String, String> {

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
                Toast.makeText(NewBuildingActivity.this, "Failed To upload Photo for Your Building", Toast.LENGTH_LONG).show();
                return;
            }else{
                Toast.makeText(NewBuildingActivity.this, "" +
                        "Photo for Your Building is uploaded succsessfully", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK) {

                    if (data != null) {
                        try {
                            Uri mImageUri = data.getData();
                            imageUrl = mImageUri.getPath().toString();
                            aBuilding.setImage(imageUrl);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                            //mImage.setImageBitmap(bitmap);
                            photo = FileUtils.getFile(this,mImageUri);

                            if (aBuilding != null) {
                                if (aBuilding.getBuildingId() > 0)
                                {
                                    mRequest myRequest = new mRequest();
                                    myRequest.setMethod(eHttpMethod.POST);
                                    myRequest.setUri(mUriProvider.REST_URI + "/" + Integer.toString(aBuilding.getBuildingId()) + "/image");
                                    myRequest.setParam("id", Integer.toString(aBuilding.getBuildingId()));

                                    if (aBuilding.getImage() != null) {
                                        myRequest.setParam("image", "images/" + aBuilding.getImage());
                                    }else{
                                        myRequest.setParam("image", "images/image.jpg");
                                    }
                                    myRequest.setImage("imageFile", photo);

                                    MyTask2 uploadTask = new MyTask2();
                                    uploadTask.execute(myRequest);
                                }
                                else{
                                    Toast.makeText(NewBuildingActivity.this, "Please add a new building first", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Image Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(uri, proj, null, null, null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        return cursor.getString(column_index);
    }
}