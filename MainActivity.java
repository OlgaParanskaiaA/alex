package ch.hesso.parkovkaalex.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        TextView lblLogin = (TextView) findViewById(R.id.lblLogin);
        TextView lblFirstname = (TextView) findViewById(R.id.lblFirstname);
        TextView lblLastname = (TextView) findViewById(R.id.lblLastname);
        TextView lblAddress = (TextView) findViewById(R.id.lblAddress);
        TextView lblEmail = (TextView) findViewById(R.id.lblEmail);
        TextView lblPhone = (TextView) findViewById(R.id.lblPhone);


        if(session.checkLogin()) {
            finish();
        }

        HashMap<String, String> user = session.getUserDetails();

        String login = user.get(SessionManager.KEY_LOGIN);
        String firstname = user.get(SessionManager.KEY_FIRSTNAME);
        String lastname = user.get(SessionManager.KEY_LASTNAME);
        String address = user.get(SessionManager.KEY_ADDRESS);
        String email = user.get(SessionManager.KEY_EMAIL);
        String phone = user.get(SessionManager.KEY_PHONE);

        lblLogin.setText(Html.fromHtml("Username: <b> " + login + "</b>"));
        lblFirstname.setText(Html.fromHtml("Firstname: <b> " + firstname + "</b>"));
        lblLastname.setText(Html.fromHtml("Lastname: <b> " + lastname + "</b>"));
        lblAddress.setText(Html.fromHtml("Address: <b> " + address + "</b>"));
        lblEmail.setText(Html.fromHtml("Email: <b> " + email + "</b>"));
        lblPhone.setText(Html.fromHtml("Phone: <b> " + phone + "</b>"));
    }

    public void sendMapButton(View view){
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
    }

    public void sendChangeButton(View view){
        Intent ch = new Intent(this, ChangeUser.class);
        startActivity(ch);
    }

    public void sendLogoutButton(View view){
        session.logoutUser();
    }
}
