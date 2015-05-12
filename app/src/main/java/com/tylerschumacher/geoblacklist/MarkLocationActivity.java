package com.tylerschumacher.geoblacklist;


import android.app.Dialog;
import android.content.IntentSender;
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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.tylerschumacher.geoblacklist.Data.MySQLiteHelper;

import java.util.ArrayList;



public class MarkLocationActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    // Saved instance error tag
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    //Tag constant
    private static final String TAG = "Location Activity: ";
    //Buttons
    Button markButton;
    Button blockButton;
    //Text Fields
    EditText t1;
    EditText t2;
    EditText t3;
    EditText t4;
    EditText t5;
    //Variables for SQL Query
    public String address;
    String number_one;
    String number_two;
    String number_three;
    String number_four;
    String number_five;
    //Create Google API Client
    private GoogleApiClient mGoogleApiClient;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    //Constants to fill list view
    private ListView lvItem;
    private ArrayList<String> itemArray;
    private ArrayAdapter<String> itemAdapter;
    //Get database
    MySQLiteHelper sqLiteHelper = new MySQLiteHelper(this);


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_location);
        mGoogleApiClient = new GoogleApiClient
                                       .Builder(this)
                                   .addApi(Places.GEO_DATA_API)
                                   .addApi(Places.PLACE_DETECTION_API)
                                   .addConnectionCallbacks(this)
                                   .addOnConnectionFailedListener(this)
                                   .build();
        setUpView();

    }
    //Set up the view
    private void setUpView()    {
        lvItem = (ListView)this.findViewById(R.id.locationListView);
        itemArray = new ArrayList<String>();
        itemArray.clear();

        itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,itemArray);
        lvItem.setAdapter(itemAdapter);

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

        //noinspection SimplifiableIfStatement
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
    //Handle failed connection to google client
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }
    //Response to buttons
    public void responseToButton() {
        final PendingResult<PlaceLikelihoodBuffer> result;
        markButton = (Button) findViewById(R.id.mark_button);
        blockButton = (Button) findViewById(R.id.blockButton);
        t1 = (EditText) findViewById(R.id.editText);
        t2 = (EditText) findViewById(R.id.editText2);
        t3 = (EditText) findViewById(R.id.editText3);
        t4 = (EditText) findViewById(R.id.editText4);
        t5 = (EditText) findViewById(R.id.editText5);

        markButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                                                                      .getCurrentPlace(mGoogleApiClient, null);
                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                    @Override
                    public void onResult(PlaceLikelihoodBuffer likelyPlaces) {

                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            itemArray.add(0, placeLikelihood.getPlace().getName().toString());
                            itemAdapter.notifyDataSetChanged();
                        }
                        likelyPlaces.release();
                    }
                });

            }
        });
        blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Long newRowId = sqLiteHelper.insertAddress( address, t1.getText().toString(),t2.getText().toString(),t3.getText().toString(),t4.getText().toString(),t5.getText().toString());
                String toastText = String.valueOf(newRowId) + " " + address + " " + t1.getText().toString() + " " + t2.getText().toString()+ " " + t3.getText().toString()+ " " +t4.getText().toString()+ " " +t5.getText().toString();
                Toast.makeText(getApplicationContext(), toastText , Toast.LENGTH_LONG).show();
            }



        });

        lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                address = parent.getItemAtPosition(position).toString();
                String toastText = "Location set to " + address + ", please enter numbers to block";
                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();

            }
        });
    }

    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                                                                this.getActivity(), REQUEST_RESOLVE_ERROR);
        }
    }


}