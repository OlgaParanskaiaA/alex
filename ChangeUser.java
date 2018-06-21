package ch.hesso.parkovkaalex.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.lavdrim.myapplication.backend.parkovkaalexApi.model.parkovkaalex;
import com.example.lavdrim.myapplication.backend.reservationApi.model.Reservation;
import com.example.lavdrim.myapplication.backend.userApi.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ChangeUser extends AppCompatActivity {

    private SessionManager session;
    //private UserDataSource ua;
    private HashMap<String, String> user;
    private List<parkovkaalex> parkovkaalexs;
    private List<Reservation> reservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user);

        session = new SessionManager(getApplicationContext());

        //ua = new UserDataSource(this);

        try {
            parkovkaalexs = new EndPointAsyncTaskparkovkaalex().execute().get();
            reservations = new EndPointAsyncTaskReservation().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        if(session.checkLogin()) {
            finish();
        }

        user = session.getUserDetails();

        EditText t1 = (EditText) findViewById(R.id.chgFirstname);
        t1.setText(user.get(SessionManager.KEY_FIRSTNAME));
        EditText t2 = (EditText) findViewById(R.id.chgLastname);
        t2.setText(user.get(SessionManager.KEY_LASTNAME));
        EditText t3 = (EditText) findViewById(R.id.chgAddress);
        t3.setText(user.get(SessionManager.KEY_ADDRESS));
        EditText t4 = (EditText) findViewById(R.id.chgEmail);
        t4.setText(user.get(SessionManager.KEY_EMAIL));
        EditText t5 = (EditText) findViewById(R.id.chgPhone);
        t5.setText(user.get(SessionManager.KEY_PHONE));
        EditText t6 = (EditText) findViewById(R.id.chgPassword);
        t6.setText(user.get(SessionManager.KEY_PASSWORD));
    }

    public void sendUpdateUserButton(View view){

        String chgId = user.get(SessionManager.KEY_ID);
        String chgLogin = user.get(SessionManager.KEY_LOGIN);

        //get input from editText
        EditText editText1 = (EditText) findViewById(R.id.chgFirstname);
        String chgFirstname = editText1.getText().toString();

        EditText editText2 = (EditText) findViewById(R.id.chgLastname);
        String chgLastname = editText2.getText().toString();

        EditText editText3 = (EditText) findViewById(R.id.chgAddress);
        String chgAddress = editText3.getText().toString();

        EditText editText4 = (EditText) findViewById(R.id.chgEmail);
        String chgEmail = editText4.getText().toString();

        EditText editText5 = (EditText) findViewById(R.id.chgPhone);
        String chgPhone = editText5.getText().toString();

        EditText editText6 = (EditText) findViewById(R.id.chgPassword);
        String chgPassword = editText6.getText().toString();

        User upUser = new User();
        upUser.setId(Long.parseLong(chgId));
        upUser.setLogin(chgLogin);
        upUser.setFirstname(chgFirstname);
        upUser.setLastname(chgLastname);
        upUser.setAddress(chgAddress);
        upUser.setEmail(chgEmail);
        upUser.setPhone(chgPhone);
        upUser.setPassword(chgPassword);

        new EndPointAsyncTaskUser(upUser.getId(), upUser).execute();

        //ua.updateUser(upUser);

        session.updateUser();
        session.createLoginSession(String.valueOf(upUser.getId()), upUser.getLogin(), upUser.getFirstname(), upUser.getLastname(), upUser.getAddress(), upUser.getEmail(), upUser.getPhone(), upUser.getPassword());

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void sendDeleteUserButton(View view){

        new AlertDialog.Builder(this)
                .setTitle("Deleting Account")
                .setMessage("Are you sure you want to delete your user account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            new EndPointAsyncTaskUser(Long.parseLong(user.get(SessionManager.KEY_ID)), "remove").execute();
                            if(parkovkaalexs != null) {
                                for (parkovkaalex parkovkaalex : parkovkaalexs) {
                                    if (parkovkaalex.getIduser() == Long.parseLong(user.get(SessionManager.KEY_ID))) {
                                        new EndPointAsyncTaskparkovkaalex(parkovkaalex.getId(), "remove").execute();
                                        new EndPointAsyncTaskparkovkaalex(parkovkaalex.getId(), "delete").execute();
                                    }
                                }
                            }
                            if(reservations != null) {
                                for (Reservation reservation : reservations) {
                                    if (reservation.getIdprovider() == Long.parseLong(user.get(SessionManager.KEY_ID)) || reservation.getIdtenant() == Long.parseLong(user.get(SessionManager.KEY_ID))) {
                                        new EndPointAsyncTaskReservation(reservation.getId(), "delete").execute();
                                    }
                                }
                            }
                            new EndPointAsyncTaskUser(Long.parseLong(user.get(SessionManager.KEY_ID)), "delete").execute();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //ua.deleteUser(Long.parseLong(user.get(SessionManager.KEY_ID)));
                        session.logoutUser();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}
