package ch.hesso.parkovkaalex.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.example.lavdrim.myapplication.backend.userApi.model.User;

public class RegisterActivity extends AppCompatActivity {
    // private UserDataSource ua;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    //  ua = new UserDataSource(this);
        session = new SessionManager(getApplicationContext());
    }

    public void sendRegButtonRegister(View view) throws ExecutionException, InterruptedException {

        long id = 0;
        boolean createUser = true;


        //get input from editText
        EditText editText1 = (EditText) findViewById(R.id.regLogin);
        String regLogin = editText1.getText().toString();

        EditText editText2 = (EditText) findViewById(R.id.regFirstname);
        String regFirstname = editText2.getText().toString();

        EditText editText3 = (EditText) findViewById(R.id.regLastname);
        String regLastname = editText3.getText().toString();

        EditText editText4 = (EditText) findViewById(R.id.regAddress);
        String regAddress = editText4.getText().toString();

        EditText editText5 = (EditText) findViewById(R.id.regPhone);
        String regPhone = editText5.getText().toString();

        EditText editText6 = (EditText) findViewById(R.id.regEmail);
        String regEmail = editText6.getText().toString();

        EditText editText7 = (EditText) findViewById(R.id.regPassword);
        String regPassword = editText7.getText().toString();


        //check input (mby)
        if(!regLogin.isEmpty() && !regFirstname.isEmpty() && !regLastname.isEmpty() && !regAddress.isEmpty()
                && !regPhone.isEmpty() && !regEmail.isEmpty() && !regPassword.isEmpty()) {

            List<User> users = new EndPointAsyncTaskUser().execute().get();
            if (users != null)
            {
                for (User user : users) {
                    if (user.getLogin().toLowerCase().equals(regLogin.toLowerCase())) {
                        createUser = false;
                        AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
                        myAlert.setMessage("User already exists!")
                                .setPositiveButton("Continue..", new DialogInterface.OnClickListener() {
                                    @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setTitle("Register Error!")
                                .setIcon(R.mipmap.ic_error)
                                .create();
                        myAlert.show();
                        break;
                    }
                }
            }

            if(createUser) {
                User newUser = new User();

                newUser.setLogin(regLogin);
                newUser.setFirstname(regFirstname);
                newUser.setLastname(regLastname);
                newUser.setAddress(regAddress);
                newUser.setPhone(regPhone);
                newUser.setEmail(regEmail);
                newUser.setPassword(regPassword);

                new EndPointAsyncTaskUser(newUser).execute();
                //newUser.setIduser((int) ua.createUser(newUser));

                session.createLoginSession(String.valueOf(newUser.getId()), newUser.getLogin(), newUser.getFirstname(), newUser.getLastname(), newUser.getAddress(), newUser.getEmail(),newUser.getPhone(), newUser.getPassword());

                //open login screen again if successfull
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }

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
    }
}
