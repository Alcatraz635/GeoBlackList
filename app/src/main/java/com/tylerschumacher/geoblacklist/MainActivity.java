package com.tylerschumacher.geoblacklist;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.tylerschumacher.geoblacklist.Data.MySQLiteHelper;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {
    //Get contect
    Context context = this;
    //String variables
    String CURRENT_LOCATION = "";
    public String numberOne = "";
    public String numberTwo = "";
    public String numberThree = "";
    public String numberFour = "";
    public String numberFive = "";
    //Buttons for the Main Activity
    Button markButton;
    Button locationsButton;
    Button numbersButton;
    Button changeLocationButton;
    //View Variables
    EditText currentLocation;
    EditText text;
    EditText locationText;
    ListView lvitem;
    //Arrays
    ArrayAdapter<String> listAdapter;
    ArrayList<String> locations;
    //SQLite
    MySQLiteHelper sqLiteHelper = new MySQLiteHelper(this);
    //Create Google API Client
    private GoogleApiClient mGoogleApiClient;
    // Saved instance error tag
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;



    private static MainActivity instance;
    public MainActivity getInstance(){
        return instance;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                                   .addApi(Drive.API)
                                   .addScope(Drive.SCOPE_FILE)
                                   .addConnectionCallbacks(this)
                                   .addOnConnectionFailedListener(this)
                                   .build();
        setUpView();
        instance = this;

    }
    public void setUpView()    {
        markButton = (Button) findViewById(R.id.mark_button);
        changeLocationButton = (Button) findViewById(R.id.changeLocationButton);
        lvitem = (ListView) findViewById(R.id.listView);
        locationText = (EditText) findViewById(R.id.currentLocationText);
        locations = new ArrayList<String>();
        locations.clear();
        listAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,locations);
        lvitem.setAdapter(listAdapter);
        currentLocation = (EditText) findViewById(R.id.currentLocationText);
        currentLocation.setText(CURRENT_LOCATION);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //no inspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        responseToButton();

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        // The good stuff goes here.
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    //Methods for getting blocked numbers
    public String getNumberOne(){
        return numberOne;
    }
    public String getNumberTwo(){
        return numberTwo;
    }
    public String getNumberThree(){
        return numberThree;
    }
    public String getNumberFour(){
        return numberFour;
    }
    public String getNumberFive(){
        return numberFive;
    }

    //Response to button selects
    public void responseToButton() {
        final Context context = this;

        //Change current location
        changeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = sqLiteHelper.numberOfRows(); i > 0; i--) {
                    Cursor data = sqLiteHelper.getData(i);
                    data.moveToFirst();
                    String string = data.getString(1);
                    locations.add(0, string);
                    listAdapter.notifyDataSetChanged();
                }


            }
        });

        lvitem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override


            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                locationText.setText(text);
                CURRENT_LOCATION = text;
                Cursor data = sqLiteHelper.getData(position + 1);
                data.moveToFirst();
                numberOne = data.getString(2);
                numberTwo = data.getString(3);
                numberThree = data.getString(4);
                numberFour = data.getString(5);
                numberFive = data.getString(6);
                Toast.makeText(getApplicationContext(), "Current Location Changed", Toast.LENGTH_LONG).show();
            }
        });
        markButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MarkLocationActivity.class);
                startActivity(intent);
            }
        });
    }



    //Creates a dialog for an error message
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    // Called from ErrorDialogFragment when the dialog is dismissed.
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    // A fragment to display an error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                                                                this.getActivity(), REQUEST_RESOLVE_ERROR);
        }
    }


}