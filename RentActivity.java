package ch.hesso.parkovkaalex.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavdrim.myapplication.backend.parkovkaalexApi.model.parkovkaalex;
import com.example.lavdrim.myapplication.backend.reservationApi.model.Reservation;
import com.example.lavdrim.myapplication.backend.userApi.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class RentActivity extends AppCompatActivity {

    private SessionManager session;
    private ParkingManager parking;
    private List<User> users;
    private List<parkovkaalex> park;
    private User infouser;
    private HashMap<String, String> user;
    private Bundle extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);

        extras = getIntent().getExtras();

        session = new SessionManager(getApplicationContext());

        if(session.checkLogin()){
            finish();
        }

        parking = new ParkingManager(getApplicationContext());
        user = session.getUserDetails();
        try {
            users = new EndPointAsyncTaskUser().execute().get();
            park = new EndPointAsyncTaskparkovkaalex().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //get logged user from cloud
        for (User u : users){
            if (u.getId().equals(Long.parseLong(user.get(SessionManager.KEY_ID)))){
                infouser=u;
            }
        }

        TextView t1 = (TextView) findViewById(R.id.showprovider);
        t1.setText(infouser.getFirstname() + " " + infouser.getLastname());
        TextView t2 = (TextView) findViewById(R.id.showaddress);
        t2.setText(extras.getString("address"));
        TextView t3 = (TextView) findViewById(R.id.showlocation);
        t3.setText(extras.getString("plzort"));
        TextView t4 = (TextView) findViewById(R.id.showprice);
        t4.setText(String.valueOf(extras.getDouble("price")));

    }

    public void sendRentButton(View view){

        Reservation reservation = new Reservation();

        reservation.setIdparkovkaalex(extras.getLong("idparkovkaalex"));
        reservation.setIdprovider(extras.getLong("iduser"));
        reservation.setIdtenant(infouser.getId());
        reservation.setDays(1);
        reservation.setFullprice(extras.getDouble("price"));
        reservation.setIsrented(1);

        new EndPointAsyncTaskReservation(reservation).execute();

        Toast.makeText(getApplicationContext(),
                "Reservation confirmed!",
                Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
        finish();
    }
}
