package ch.hesso.parkovkaalex.android;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.example.lavdrim.myapplication.backend.userApi.UserApi;
import com.example.lavdrim.myapplication.backend.userApi.model.User;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * 12.05.2017
 */

public class EndPointAsyncTaskUser extends AsyncTask<Void, Void, List<User>>{
    private static UserApi userApi = null;
    private static final String TAG = EndPointAsyncTaskUser.class.getName();
    private Context context;
    private User user;
    private Long id;

    private boolean insert = false;
    private boolean getall= false;
    private boolean get = false;
    private boolean update = false;
    private boolean delete = false;

    EndPointAsyncTaskUser(){
        getall = true;
    }

    EndPointAsyncTaskUser(Long id, User user) {
        update = true;
        this.id = id;
        this.user = user;
    }

    EndPointAsyncTaskUser(Long id, String s) throws ExecutionException, InterruptedException {
        this.id = id;
        if (s.equals("get")){
            get=true;
        }else if (s.equals("delete")){
            delete = true;
        }
    }

    EndPointAsyncTaskUser(User user) {
        insert = true;
        this.user = user;
    }

    @Override
    protected  List<User> doInBackground(Void... params){
        if(userApi == null) { // Only do this once
            UserApi.Builder builder = new UserApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://parkovkaalex.android-1492529578093.appspot.com/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            userApi = builder.setApplicationName("UserLibrary").build();
            userApi = builder.build();
        }
        try {
            // Call here the wished methods on the Endpoints
            // For instance insert
            if(user != null && insert == true){
                userApi.insert(user).execute();
            }

            if(user != null && getall == true){
                userApi.list().execute().getItems();
            }

            if(user != null && get == true){
                userApi.get(id).execute();
            }

            if(user != null && update == true){
                userApi.update(id, user).execute();
            }

            if(delete == true){
                userApi.remove(id).execute();
            }

            insert = false;
            getall= false;
            get = false;
            update = false;
            delete = false;
            // and for instance return the list of all users
            return userApi.list().execute().getItems();

        } catch (IOException e){
            return new ArrayList<User>();
        }
    }

    //This method gets executed on the UI thread - The UI can be manipulated directly inside
    //of this method
    @Override
    protected void onPostExecute(List<User> result){

        if(result != null) {
            for (User user : result) {
                Log.i(TAG, "First name: " + user.getFirstname() + " Last name: "
                        + user.getLastname());

            }
        }
    }
}

