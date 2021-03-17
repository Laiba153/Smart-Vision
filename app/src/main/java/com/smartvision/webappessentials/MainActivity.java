package com.smartvision.webappessentials;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public boolean doubleBackToExitPressedOnce = false;
    private InterstitialAd interstitial;
    private NavigationView navigationView;
    public Timer AdTimer;
    private boolean open_from_push = false;

    boolean state;
    // GCM
    public static final String PROPERTY_REG_ID = "notifyId";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    SharedPreferences preferences;
    String reg_cgm_id;
    static final String TAG = "MainActivity";
    private boolean first_fragment = false;
    private double latitude;
    private double longitude;

    SpaceNavigationView spacenavigationView;

    @Override
    protected void onPause() {
        super.onPause();
        if (AdTimer != null) {
            AdTimer.cancel();
            AdTimer = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    //ADD FLASH CODE HERE

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.onOffFlashLight:
                try
                {
                    Torch();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (getString(R.string.rtl_version).equals("true")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (drawer.isDrawerOpen(GravityCompat.START)) {
//                    drawer.closeDrawer(GravityCompat.START);
//                } else {
//                    drawer.openDrawer(GravityCompat.START);
//                }
//            }
//        });


        // Go to first fragment
        Intent intent = getIntent();
        if (intent.getExtras() != null && intent.getExtras().getString("link", null) != null && !intent.getExtras().getString("link", null).equals("")) {
            open_from_push = true;
            String url = null;
            if (intent.getExtras().getString("link").contains("http")) {
                url = intent.getExtras().getString("link");
            } else {
                url = "http://" + intent.getExtras().getString("link");
            }

            Bundle bundle = new Bundle();
            bundle.putString("type", "url");
            bundle.putString("url", url);
            Fragment fragment = new FragmentWebInteractive();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, "FragmentWebInteractive").commit();
            first_fragment = true;

        } else if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString("type", getString(R.string.home_type));
            bundle.putString("url", getString(R.string.home_url));
            Fragment fragment = new FragmentWebInteractive();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, "FragmentWebInteractive").commit();
            first_fragment = true;
        }

        if (preferences.getBoolean("pref_geolocation_update", true)) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                // create class object
                GPSTracker gps = new GPSTracker(MainActivity.this);

                // check if GPS enabled
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    int appVersion = getAppVersion(this);
                    Log.i(TAG, "Saving regId on app version " + appVersion);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("latitude", "" + latitude);
                    editor.putString("longitude", "" + longitude);
                    editor.putString(PROPERTY_APP_VERSION, ""+appVersion);
                    editor.commit();


                    Log.d("GPS", "Latitude: " + latitude + ", Longitude: " + longitude);


                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    if (preferences.getBoolean("pref_gps_remember", false)) {
                        gps.showSettingsAlert();
                    }
                }
            } else {
                // Request permission to the user
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1
                );
            }
        }

        //SpaceNavigation Bar Fragment  Code

        spacenavigationView = findViewById(R.id.space);
        
        final Fragment fragment_text;
        final Fragment fragment_color;
        final Fragment fragment_search;
        final Fragment fragment_objectdetect;

        spacenavigationView = findViewById(R.id.space);
        fragment_text = new TextFragment();
        fragment_color = new ColorDetectionFragment();
        fragment_search = new SearchObjectTFragment();
        fragment_objectdetect = new ObjectDetectionFragment();

        setFragment(fragment_text);
        spacenavigationView.initWithSaveInstanceState(savedInstanceState);
        spacenavigationView.addSpaceItem(new SpaceItem("", R.mipmap.color_detect));
        spacenavigationView.addSpaceItem(new SpaceItem("", R.mipmap.text_detect));
        spacenavigationView.addSpaceItem(new SpaceItem("", R.mipmap.object_detect));
        spacenavigationView.addSpaceItem(new SpaceItem("", R.mipmap.search_object));

        spacenavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            public void onCentreButtonClick() {
                setFragment(fragment_text);
                spacenavigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex) {
//                    switch (position) {
//                        case 0:
//                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_chronograph).commit();
//                    }
                    case 0:
                        setFragment(fragment_text);
                        break;
                    case 1:
                        setFragment(fragment_color);
                        break;
                    case 2:
                        setFragment(fragment_objectdetect);
                        break;
//                    case 3:
//                        setFragment(fragment_search);
//                        return;
                    default:
                        setFragment(fragment_search);
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });


        // Save token on server
        sendRegistrationIdToBackend();
    }

        private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    public void Torch() {

            Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener()
            {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(MainActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();
                try {
                    if (!state) {
                        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                        String cameraId = null;
                        try
                        {
                            cameraId = cameraManager.getCameraIdList()[0];
                            cameraManager.setTorchMode(cameraId, true);
                            state = true;
                        }
                        catch (CameraAccessException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                        {
                        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                        String cameraId;
                        try
                        {
                            cameraId = cameraManager.getCameraIdList()[0];
                            cameraManager.setTorchMode(cameraId, false);
                            state = false;
                        }
                        catch (CameraAccessException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
                @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                Toast.makeText(MainActivity.this, "Camera Permission Required", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                Toast.makeText(MainActivity.this, "Permission Denied for accessing Camera", Toast.LENGTH_SHORT).show();
            }
        }).check();
    }

    @Override

    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        spacenavigationView.onSaveInstanceState(outState);
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
//    private void showSettingsDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("Need Permissions");
//        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
//        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//                openSettings();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.show();
//
//    }
//
//    // navigating user to app settings
//    private void openSettings() {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package", getPackageName(), null);
//        intent.setData(uri);
//        startActivityForResult(intent, 101);
//    }

//    private void openCamera() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, 100);
//    }
//}

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        Fragment webviewfragment = getSupportFragmentManager().findFragmentByTag("FragmentWebInteractive");
        if (webviewfragment instanceof FragmentWebInteractive) {
            if (((FragmentWebInteractive) webviewfragment).canGoBack()) {
                ((FragmentWebInteractive) webviewfragment).GoBack();


                return;
            }
        }

        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        } else {
            if (first_fragment == false) {
                super.onBackPressed();
            }
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1500);


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        String tag = null;
        first_fragment = false;

        if (id == R.id.home) {
            Bundle bundle = new Bundle();
            bundle.putInt("item_position", 0);
            bundle.putString("type", getString(R.string.home_type));
            bundle.putString("url", getString(R.string.home_url));
            fragment = new FragmentWebInteractive();
            fragment.setArguments(bundle);
            tag = "FragmentWebInteractive";
            first_fragment = true;

        } else if (id == R.id.about_us) {

            Bundle bundle = new Bundle();
            bundle.putInt("item_position", 1);
            bundle.putString("type", getString(R.string.about_us_type));
            bundle.putString("url", getString(R.string.about_us_url));
            fragment = new FragmentWebInteractive();
            fragment.setArguments(bundle);
            tag = "FragmentWebInteractive";

        //else if (id == R.id.contacts) {
//            fragment = new FragmentContacts();
//            tag = "FragmentContacts";
        }
        else if(id == R.id.share_button)
        {
            switch (item.getItemId()) {
                case R.id.share_button:
                    try {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        String sAux = getString(R.string.share_text) + "\n";
                        sAux = sAux + getString(R.string.share_link) + "\n";
                        i.putExtra(Intent.EXTRA_TEXT, sAux);
                        startActivity(Intent.createChooser(i, "Choose one"));
                    } catch (Exception e) { //e.toString();
                    }
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }

        }

        else if (id == R.id.nav_1) {
            Intent i = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }

        else if (id == R.id.nav_profile_login)
        {
            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);
            return true;
        }

        else if(id == R.id.rate_us)
        {
            rateMe();
        }
        else if(id == R.id.colordetect)
        {
            Bundle bundle = new Bundle();
            fragment = new ColorDetectionFragment();
            fragment.setArguments(bundle);
            tag = "Color Detection";
        }
        else if(id == R.id.textdetect)
        {
            Bundle bundle = new Bundle();
            fragment = new TextFragment();
            fragment.setArguments(bundle);
            tag = "Text Detection";
        }
        else if(id == R.id.objectdetect)
        {
            Bundle bundle = new Bundle();
            fragment = new ObjectDetectionFragment();
            fragment.setArguments(bundle);
            tag = "Object Detection";
        }
        else if(id == R.id.searchObject)
        {
            Bundle bundle = new Bundle();
            fragment = new SearchObjectTFragment();
            fragment.setArguments(bundle);
            tag = "Search Object";
        }
        else if (id == R.id.nav_4) {
            Intent i = new Intent(getBaseContext(), TalkbackActivity.class);
            startActivity(i);
            return true;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, tag).addToBackStack(null).commit();

        setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void SetItemChecked(int position) {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(position).setChecked(true);
    }

    //Rate me Function
    public void rateMe() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + "com.facebook.katana")));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     * REFRESH TOKEN TO THE SERVER
     */
    private void sendRegistrationIdToBackend() {
        Log.d(TAG, "Start update data to server...");
        String latitude = preferences.getString("latitude", null);
        String longitude = preferences.getString("longitude", null);
        String appVersion = preferences.getString("appVersion", null);
        String token = preferences.getString("fcm_token", null);
        // Register FCM Token ID to server
        if (token != null) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair(getString(R.string.db_field_token), token));
            nameValuePairs.add(new BasicNameValuePair(getString(R.string.db_field_latitude), "" + latitude));
            nameValuePairs.add(new BasicNameValuePair(getString(R.string.db_field_longitude), "" + longitude));
            nameValuePairs.add(new BasicNameValuePair(getString(R.string.db_field_appversion), "" + appVersion));
            new HttpTask(null, MainActivity.this, getString(R.string.server_url), nameValuePairs, false).execute();
        }
    }
}
