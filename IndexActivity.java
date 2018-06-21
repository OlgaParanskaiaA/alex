package ch.hesso.parkovkaalex.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.lavdrim.myapplication.backend.userApi.model.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class IndexActivity extends AppCompatActivity {

    public static final String LOGIN_PATH = ".parkovkaalex.android.After_loginActivity";
    //private UserDataSource ua;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        //ua = new UserDataSource(this);

        //User Session Manager
        session = new SessionManager(getApplicationContext());

    }

    public void sendRegisterButton(View view) {

        Intent intent1 = new Intent(this, RegisterActivity.class);
        startActivity(intent1);

    }

    public void loginButtonPushed(View view) throws ExecutionException, InterruptedException {
        //get username and login from textbox
        EditText editText1 = (EditText) findViewById(R.id.loginUsername);
        String username = editText1.getText().toString();

        EditText editText2 = (EditText) findViewById(R.id.loginPassword);
        String password = editText2.getText().toString();

        if (checkLogin(username, password)) {
            //access
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();
        } else {
            AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
            myAlert.setMessage("Invalid User or Password, try again!")
                    .setPositiveButton("Continue..", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setTitle("Logon Error!")
                    .setIcon(R.mipmap.ic_error)
                    .create();
            myAlert.show();

        }
    }

    public boolean checkLogin(String username, String password) throws ExecutionException, InterruptedException {
        List<User> users = new EndPointAsyncTaskUser().execute().get();
        if (users != null)
        {
            for(User user : users) {
                if (username.toLowerCase().equals(user.getLogin().toLowerCase()) && password.equals(user.getPassword())) {
                    session.createLoginSession(String.valueOf(user.getId()), user.getLogin(), user.getFirstname(), user.getLastname(), user.getAddress(), user.getEmail(), user.getPhone(), user.getPassword());
                    return true;
                }
            }
        }
        return false;
    }
}