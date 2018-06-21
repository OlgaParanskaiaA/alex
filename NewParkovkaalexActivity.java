package ch.hesso.parkovkaalex.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.lavdrim.myapplication.backend.parkovkaalexApi.model.parkovkaalex;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;



public class NewparkovkaalexActivity extends AppCompatActivity {

    //private parkovkaalexDataSource pa;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_parkovkaalex);

        //pa = new parkovkaalexDataSource(this);

        session = new SessionManager(getApplicationContext());

        if(session.checkLogin()){
            finish();
        }
    }

    public void sendNewParkSaveButton(View view){
        //get input from editText
        EditText editText1 = (EditText) findViewById(R.id.newparkPriceDay);
        String newparkPriceDay = editText1.getText().toString();

        EditText editText2 = (EditText) findViewById(R.id.newparkAddress);
        String newparkAddress = editText2.getText().toString();

        EditText editText3 = (EditText) findViewById(R.id.newparkPLZCity);
        String newparkPLZCity = editText3.getText().toString();

        getCoordinatesOfAddress(newparkAddress, newparkPLZCity);
        //check entries?
        Double [] coordinates = getCoordinatesOfAddress(newparkAddress, newparkPLZCity);

        HashMap<String, String> user = session.getUserDetails();
        //save in db

        if(!newparkPriceDay.isEmpty() && !newparkAddress.isEmpty() && !newparkPLZCity.isEmpty()) {

                parkovkaalex parkovkaalex = new parkovkaalex();

                parkovkaalex.setIduser(Long.parseLong(user.get(SessionManager.KEY_ID)));
                parkovkaalex.setAddress(newparkAddress);
                parkovkaalex.setLocation(newparkPLZCity);
                parkovkaalex.setPrice(Double.parseDouble(newparkPriceDay));
                parkovkaalex.setCoordinatex(coordinates[0]);
                parkovkaalex.setCoordinatey(coordinates[1]);

                new EndPointAsyncTaskparkovkaalex(parkovkaalex).execute();

        }else{
            AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
            myAlert.setMessage("Please add in all values!!")
                    .setPositiveButton("Continue..", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setTitle("Empty Register Error!")
                    .setIcon(R.mipmap.ic_error)
                    .create();
            myAlert.show();
        }

        // Refresh map activity upon close of dialog box
        Intent refresh = new Intent(this, Map.class);
        startActivity(refresh);
        finish();
    }

    public Double [] getCoordinatesOfAddress(String address, String plz){
        Double [] coordi = new Double[2];
        double latitude = 0.0;
        double longitude = 0.0;
        String addresswithplz = address +" "+ plz;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(addresswithplz, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses.size() > 0) {
            latitude = addresses.get(0).getLatitude();
            longitude = addresses.get(0).getLongitude();
        }

        coordi[0] = latitude;
        coordi[1] = longitude;

        return coordi;
    }

}
