package ch.hesso.parkovkaalex.android;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lavdrim.myapplication.backend.parkovkaalexApi.parkovkaalexApi;
import com.example.lavdrim.myapplication.backend.parkovkaalexApi.model.parkovkaalex;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 12.05.2017
 */

public class EndPointAsyncTaskparkovkaalex extends AsyncTask<Void, Void, List<parkovkaalex>> {
    private static parkovkaalexApi parkovkaalexApi = null;
    private static final String TAG = EndPointAsyncTaskparkovkaalex.class.getName();
    private Context context;
    private parkovkaalex parkovkaalex;
    private Long id;

    private boolean insert = false;
    private boolean getall= false;
    private boolean get = false;
    private boolean update = false;
    private boolean delete = false;

    EndPointAsyncTaskparkovkaalex(){ getall = true;}


    EndPointAsyncTaskparkovkaalex(Long id, parkovkaalex parkovkaalex) {
        update = true;
        this.id = id;
        this.parkovkaalex = parkovkaalex;
    }

    EndPointAsyncTaskparkovkaalex(Long id, String s) {
        this.id = id;
        if (s.equals("get")){
            get=true;
        }else if (s.equals("delete")){
            delete = true;
        }
    }

    EndPointAsyncTaskparkovkaalex(parkovkaalex parkovkaalex) {
        insert = true;
        this.parkovkaalex = parkovkaalex;
    }

    @Override
    protected  List<parkovkaalex> doInBackground(Void... params){
        if(parkovkaalexApi == null) { // Only do this once
            parkovkaalexApi.Builder builder = new parkovkaalexApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://parkovkaalex.android-1492529578093.appspot.com/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            parkovkaalexApi = builder.setApplicationName("ParkingLibrary").build();
            parkovkaalexApi = builder.build();
        }
        try {
            // Call here the wished methods on the Endpoints
            // For instance insert
            if(parkovkaalex != null && insert == true){
                parkovkaalexApi.insert(parkovkaalex).execute();
            }

            if(parkovkaalex != null && getall == true){
                parkovkaalexApi.list().execute().getItems();
            }

            if(parkovkaalex != null && get == true){
                parkovkaalexApi.get(id).execute(); //id einfügen
            }

            if(parkovkaalex != null && update == true){
                parkovkaalexApi.update(id, parkovkaalex).execute(); //id einfügen
            }

            if(delete == true){
                parkovkaalexApi.remove(id).execute();
            }

            insert = false;
            getall= false;
            get = false;
            update = false;
            delete = false;
            // and for instance return the list of all users
            return parkovkaalexApi.list().execute().getItems();

        } catch (IOException e){
            return new ArrayList<parkovkaalex>();
        }
    }

    //This method gets executed on the UI thread - The UI can be manipulated directly inside
    //of this method
    @Override
    protected void onPostExecute(List<parkovkaalex> result){

        if(result != null) {
            for (parkovkaalex parkovkaalex : result) {
                Log.i(TAG, "Address: " + parkovkaalex.getAddress() + " Location: "
                        + parkovkaalex.getLocation());

            }
        }
    }
}