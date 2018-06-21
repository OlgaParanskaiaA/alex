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
import java.util.concurrent.ExecutionException;


public class Update_delete_parkovkaalex extends AppCompatActivity {

    private SessionManager session;
    private HashMap<String, String> user;
    private List<parkovkaalex> fulllistparkovkaalexs;
    private parkovkaalex choosenspot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete__parkovkaalex);

        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();

        if(session.checkLogin()){
            finish();
        }

        //get parking spot from cloud
        Bundle extras = getIntent().getExtras();
        extras.getLong("parkovkaalexid");

        try {
            fulllistparkovkaalexs = new EndPointAsyncTaskparkovkaalex().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        for (parkovkaalex s : fulllistparkovkaalexs){
            if (s.getId().equals(extras.getLong("parkovkaalexid"))){
                choosenspot=s;
            }
        }

        EditText t1 = (EditText) findViewById(R.id.updateparkPriceDay);
        t1.setText(String.valueOf(choosenspot.getPrice()));
        EditText t2 = (EditText) findViewById(R.id.updateparkAddress);
        t2.setText(choosenspot.getAddress());
        EditText t3 = (EditText) findViewById(R.id.updateparkPLZCity);
        t3.setText(choosenspot.getLocation());
    }


    public void sendUpdateParkSaveButton(View view){
    //get input from editText
        EditText editText1 = (EditText) findViewById(R.id.updateparkPriceDay);
        String updateParkPrice = editText1.getText().toString();

        EditText editText2 = (EditText) findViewById(R.id.updateparkAddress);
        String updateParkAddress = editText2.getText().toString();

        EditText editText3 = (EditText) findViewById(R.id.updateparkPLZCity);
        String updateParkLocation = editText3.getText().toString();

        getCoordinatesOfAddress(updateParkAddress, updateParkLocation);
        //check entries?
        Double [] coordinates = getCoordinatesOfAddress(updateParkAddress, updateParkLocation);

        //save in db

        if(!updateParkPrice.isEmpty() && !updateParkAddress.isEmpty() && !updateParkLocation.isEmpty()) {

            parkovkaalex newparkovkaalex = new parkovkaalex();

            newparkovkaalex.setId(choosenspot.getId());
            newparkovkaalex.setIduser(Long.parseLong(user.get(SessionManager.KEY_ID)));
            newparkovkaalex.setAddress(updateParkAddress);
            newparkovkaalex.setLocation(updateParkLocation);
            newparkovkaalex.setPrice(Double.parseDouble(updateParkPrice));
            newparkovkaalex.setCoordinatex(coordinates[0]);
            newparkovkaalex.setCoordinatey(coordinates[1]);


            //save in cloud
            new EndPointAsyncTaskparkovkaalex(newparkovkaalex.getId(), newparkovkaalex).execute();

            //open login screen again if successfull
            Intent intent = new Intent(this, Map.class);
            startActivity(intent);

            finish();

        }else{
            AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
            myAlert.setMessage("Please add in all values!!")
                    .setPositiveButton("Continue..", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setTitle("Empty Update Error!")
                    .setIcon(R.mipmap.ic_error)
                    .create();
            myAlert.show();
        }

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
