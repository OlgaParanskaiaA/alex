package ch.hesso.parkovkaalex.android;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.lavdrim.myapplication.backend.parkovkaalexApi.model.parkovkaalex;
import com.example.lavdrim.myapplication.backend.reservationApi.model.Reservation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Map extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final int REQUEST_LOCATION_PERM = 59;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private String provider;
    private Location mLastLocation;
    private Location closestLocation;
    private List<parkovkaalex> fulllist;
    private List<Reservation> fulllistReservation;
    //private parkovkaalexDataSource pa;
    //private ReservationDataSource ra;
    private SessionManager session;
    private HashMap<String, String> user;


    //actionbar ini
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.my_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //actionbar selection
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_new:
                intent = new Intent(this, NewparkovkaalexActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_change:
                intent = new Intent(this, AllparkovkaalexsUserActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_info:
                intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_reservation:
                intent = new Intent(this, AllReservationActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERM);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mMap.clear();
        try {
            loadMap();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void loadMap() throws ExecutionException, InterruptedException {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //session
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        if (session.checkLogin()) {
            finish();
        }

        //get all parkovkaalexs and reservations of db
        //pa = new parkovkaalexDataSource(this);
        //ra = new ReservationDataSource(this);
        loadparkovkaalex();

        //get location of phone
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
            myAlert.setMessage("Could not access your location!")
                    .setPositiveButton("Continue..", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setTitle("Location error!")
                    .setIcon(R.mipmap.ic_error)
                    .create();
            myAlert.show();
        }
        mLastLocation = locationManager.getLastKnownLocation(provider);
    }

    //lets you allow location access in app instead of going to phone settings
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERM: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        loadMap();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {

                    AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
                    myAlert.setMessage("Could not access your location!")
                            .setPositiveButton("Continue..", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setTitle("Location error!")
                            .setIcon(R.mipmap.ic_error)
                            .create();
                    myAlert.show();
                }
                return;
            }
        }
    }

    //loads DB
    public void loadparkovkaalex() throws ExecutionException, InterruptedException {
        fulllist = new EndPointAsyncTaskparkovkaalex().execute().get();
        fulllistReservation = new EndPointAsyncTaskReservation().execute().get();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //loop all and add all markers of every entry with condition:
        // -User logged in is not user of the parkovkaalex
        // -parkovkaalex is not rented already

        if (fulllist!=null){
            Marker [] markers=new Marker[fulllist.size()];

            //loop all parkovkaalex.android entries
            for (int i=0; i<fulllist.size(); i++){
                boolean checkConditions=true;
                //check if logged user is NOT the owner of the parkovkaalex.android
                if (fulllist.get(i).getIduser().equals(Long.parseLong(user.get(SessionManager.KEY_ID)))){
                    checkConditions=false;
                }
                if (fulllistReservation!=null){
                    //loop all reservations
                    for (int j = 0; j < fulllistReservation.size(); j++) {
                        //check if the parkovkaalexid of the parkovkaalex.android we are trying to add with outer loop is not the same
                        //as any entry in the reservation DB
                        if (fulllistReservation.get(j).getIdparkovkaalex().equals(fulllist.get(i).getId()) ) {
                            checkConditions=false;
                            break;
                        }
                    }
                }
                if (checkConditions){
                    markers[i] = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(fulllist.get(i).getCoordinatex(), fulllist.get(i).getCoordinatey()))
                            .title(fulllist.get(i).getAddress())
                            .alpha(SettingsActivity.getMarkerAlpha(this))
                            .icon(BitmapDescriptorFactory.defaultMarker(SettingsActivity.getMarkerColor(this))));
                    markers[i].setTag(0);
                }
            }
        }
        if (mLastLocation!=null){
            Marker currentposition = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .title("You")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .alpha(SettingsActivity.getMarkerAlpha(this)));
            currentposition.setTag(0);

            calculateClosest();
            if (closestLocation!=null){
                Marker closest = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(closestLocation.getLatitude(), closestLocation.getLongitude()))
                        .title("you shouldnt see this marker :)"));
                closest.setTag(0);
                closest.setVisible(false);

                builder.include(closest.getPosition());
            }

            //the include method will calculate the min and max bound.
            builder.include(currentposition.getPosition());

            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

            //mMap.setOnMarkerClickListener(this);
            mMap.animateCamera(cu);
        }
            // Set a listener for marker click.
         mMap.setOnMarkerClickListener(this);


    }


    //calculate the closest parking spot to phone coordinates
    public Location calculateClosest(){

        closestLocation=new Location("");

        float smallestDistance = -1;
        ArrayList<Location> locations = new ArrayList<>();
        //create a list of all possible locations available
        for (int i=0; i<fulllist.size(); i++){
            boolean checkConditions=true;
            Location temp = new Location("");
            if (fulllistReservation!=null) {
                for (int j = 0; j < fulllistReservation.size(); j++) {
                    if (fulllistReservation.get(j).getIdparkovkaalex().equals(fulllist.get(i).getId())) {
                        checkConditions = false;
                        break;
                    }
                }
            }
            if (fulllist.get(i).getIduser().equals(Long.parseLong(user.get(SessionManager.KEY_ID)))){
                checkConditions=false;
            }
            if(checkConditions){
                temp.setLatitude(fulllist.get(i).getCoordinatex());
                temp.setLongitude(fulllist.get(i).getCoordinatey());
                locations.add(temp);
            }
        }
        //algo which calculates closes from list
        for(Location location : locations){
            float distance = mLastLocation.distanceTo(location);
            if(smallestDistance == -1 || distance < smallestDistance){
                closestLocation = location;
                smallestDistance = distance;
            }
        }
        return closestLocation;
    }


    /* Called when the user clicks a marker.
     *
     * */
    //WORKS!!
     public boolean onMarkerClick(final Marker marker) {

     // Retrieve the data from the marker.
        String addressClicked =  marker.getTitle();

         for (int i=0; fulllist.size()>i; i++){
             if (fulllist.get(i).getAddress().equalsIgnoreCase(addressClicked)){
                 parkovkaalex spotchoosen=fulllist.get(i);
                 Intent intent = new Intent(this, RentActivity.class);
                 intent.putExtra("idparkovkaalex", spotchoosen.getId());
                 intent.putExtra("iduser", spotchoosen.getIduser());
                 intent.putExtra("address", spotchoosen.getAddress());
                 intent.putExtra("plzort", spotchoosen.getLocation());
                 intent.putExtra("price", spotchoosen.getPrice());
                 startActivity(intent);
                //open intend with the address
             }
         }
         // Return false to indicate that we have not consumed the event and that we wish
         // for the default behavior to occur (which is for the camera to move such that the
         // marker is centered and for the marker's info window to open, if it has one).
         return false;
     }

}
