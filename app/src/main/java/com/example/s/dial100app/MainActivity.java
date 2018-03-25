package com.example.s.dial100app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.TotalCaptureResult;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.test.mock.MockPackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{
    ImageView imageView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button sendtext;
    private String message="";
    private String textlocation;

    private Button btnImage,btnVideo;
    private ProgressDialog mDialog;
    private StorageReference storageReference;
    private FirebaseApp app;
    private FirebaseStorage storage;
    private String urlsi="";
    private String urlsv="";

    Button btnShowLocation;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    TextView location_tv;
    private String postalCode = null;
    private Double latitude;
    private Double longitude;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    private MediaPlayer ring;
    Button dial100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.imgView);
        ring=MediaPlayer.create(MainActivity.this,R.raw.siren);

        mDrawerLayout=findViewById(R.id.drawerLayout);
        mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        navbar_trigger(item);
                        return true;
                    }
                }
        );

        getSupportActionBar().setTitle(R.string.app_name);
        radioGroup=findViewById(R.id.radio_group);
        sendtext = findViewById(R.id.sendtext);
        sendtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id=radioGroup.getCheckedRadioButtonId();
                if(id!=-1) {
                    radioButton = findViewById(id);
                    String distress = radioButton.getText().toString();
                    message = message + distress + "\n" + textlocation;
                    message = message+"\n\n"+urlsi;
                    message = message+"\n\n"+urlsv;
                    try {
                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "9819602059"));
                        smsIntent.putExtra("sms_body", message);
                        startActivity(smsIntent);
                        message="";
                        urlsi="";
                        urlsv="";
                        Toast.makeText(MainActivity.this, "Click on Send Icon\nto send the message", Toast.LENGTH_SHORT).show();
                    }catch(Exception e){
                        Toast.makeText(MainActivity.this, "SMS not sent.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });

        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this);
        btnShowLocation=findViewById(R.id.location_button);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
            }
        });

        dial100=findViewById(R.id.Dial100);
        dial100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:(+91)9819602059"));
                startActivity(callIntent);
            }
        });
        app=FirebaseApp.getInstance();
        storage=FirebaseStorage.getInstance(app);

        btnImage=findViewById(R.id.btnimage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AlbumSelectActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT,8);
                startActivityForResult(intent,Constants.REQUEST_CODE);
            }
        });
        btnVideo=findViewById(R.id.btnvideo);
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(Intent.createChooser(intent,"Select the Video of the incident"),101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.REQUEST_CODE && resultCode==RESULT_OK){
            ArrayList<Image> images=data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            Uri[] uri=new Uri[images.size()];
            for(int i=0;i<images.size();i++) {
                uri[i]=Uri.parse("file://"+images.get(i).path);
                storageReference = storage.getReference("photos");
                final StorageReference photoRef = storageReference.child(uri[i].getLastPathSegment());
                photoRef.putFile(uri[i]).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        String content = downloadUrl.toString();
                        if (content.length() > 0) {
                            urlsi=urlsi+content+"\n\n";
                        }
                        Toast.makeText(MainActivity.this,"Images successfully uploaded",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Images could not be uploaded",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        if(requestCode==101 && resultCode==RESULT_OK){
            StorageReference storageReference=storage.getReference("videos");
            Uri uri=data.getData();
            try {
                assert uri != null;
                StorageReference videoRef = storageReference.child("files/" + uri.getLastPathSegment());
                videoRef.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl=taskSnapshot.getDownloadUrl();
                        String content=downloadUrl.toString();
                        if(content.length()>0){
                            urlsv=urlsv+"\n\n"+"video link:\n"+content+"\n\n";
//                            mDialog.dismiss();
                        }
                    }
                }).addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                        mDialog.show(MainActivity.this,"Wait till Video is Uploaded","Uploading...",true);
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this,"Video couldn't be uploaded",Toast.LENGTH_SHORT).show();
                    }
                });
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void navbar_trigger(MenuItem menuItem){
        int id=menuItem.getItemId();
        String phno= PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("phno","");
        if(id==R.id.fake_call){
            Intent callintent=new Intent(Intent.ACTION_DIAL);
            if(!phno.equals("")) {
                callintent.setData(Uri.parse("tel: " + phno));
                startActivity(callintent);
            }
        }else if(id==R.id.sos_on){
            ring.start();
            ring.setLooping(true);
        }else if(id==R.id.sos_off){
            if(ring!=null) {
                ring.stop();
                ring.release();
            }
        }
    }

    private class GeoLocation extends AsyncTask<Double,Void,Address>{

        @Override
        protected Address doInBackground(Double... integers) {
            double latitude = integers[0];
            double longitude = integers[1];
            Geocoder geocoder;
            List<Address> addresses;
            geocoder=new Geocoder(MainActivity.this, Locale.getDefault());
            try{
                addresses = geocoder.getFromLocation(latitude,longitude,1);
                return addresses.get(0);
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Address s) {
            location_tv = findViewById(R.id.location_tv);
            if(s !=null){
                String address = s.getAddressLine(0);
                String address1 = s.getAddressLine(1);
                String city = s.getLocality();
                String state = s.getAdminArea();
                String country = s.getCountryName();
                postalCode = s.getPostalCode();
                String currentLocation;

                if(!TextUtils.isEmpty(address))
                {
                    currentLocation=address;

                    if (!TextUtils.isEmpty(address1))
                        currentLocation+="\n"+address1;

                    if (!TextUtils.isEmpty(city))
                    {
                        currentLocation+="\n"+city;

                        if (!TextUtils.isEmpty(postalCode))
                            currentLocation+=" - "+postalCode;
                    }
                    else
                    {
                        if (!TextUtils.isEmpty(postalCode))
                            currentLocation+="\n"+postalCode;
                    }

                    if (!TextUtils.isEmpty(state))
                        currentLocation+="\n"+state;

                    if (!TextUtils.isEmpty(country))
                        currentLocation+="\n"+country;
                    textlocation=currentLocation;
                    location_tv.setText(currentLocation);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!checkPermissions()){
            requestPermissions();
        }else{
            getLastLocation();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation(){
        mFusedLocationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()&&task.getResult()!=null){
                    mLastLocation = task.getResult();
                    latitude=mLastLocation.getLatitude();
                    longitude=mLastLocation.getLongitude();
                    new GeoLocation().execute(latitude,longitude);
                }
                else{
                    Log.w(TAG,"getLastLocation:exception",task.getException());
                    showSnackbar("No Location Detected");
                }
            }
        });
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    private void showSnackbar(final String text) {
        View container = findViewById(R.id.drawerLayout);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.hamburger_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.logout){
            Intent logoutActivity=new Intent(this,LogoutActivity.class);
            startActivity(logoutActivity);
            return true;
        }else if(id==R.id.about){
            Intent aboutActivity=new Intent(this,AboutActivity.class);
            startActivity(aboutActivity);
            return true;
        }else if(id==R.id.help){
            Intent helpActivity=new Intent (this,HelpActivity.class);
            startActivity(helpActivity);
            return true;
        }
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
