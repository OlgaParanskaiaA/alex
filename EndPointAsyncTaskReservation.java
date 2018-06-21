package ch.hesso.parkovkaalex.android;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lavdrim.myapplication.backend.reservationApi.ReservationApi;
import com.example.lavdrim.myapplication.backend.reservationApi.model.Reservation;
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

public class EndPointAsyncTaskReservation extends AsyncTask<Void, Void, List<Reservation>> {
    private static ReservationApi reservationApi = null;
    private static final String TAG = EndPointAsyncTaskReservation.class.getName();
    private Context context;
    private Reservation reservation;
    private Long id;

    private boolean insert = false;
    private boolean getall= false;
    private boolean get = false;
    private boolean update = false;
    private boolean delete = false;

    EndPointAsyncTaskReservation(){getall=true;}

    EndPointAsyncTaskReservation(Long id, Reservation reservation) {
        update = true;
        this.id = id;
        this.reservation = reservation;
    }

    EndPointAsyncTaskReservation(Long id, String s) {
        this.id = id;
        if (s.equals("get")){
            get=true;
        }else if (s.equals("delete")){
            delete = true;
        }
    }

    EndPointAsyncTaskReservation(Reservation reservation) {
        insert = true;
        this.reservation = reservation;
    }

    @Override
    protected  List<Reservation> doInBackground(Void... params){
        if(reservationApi == null) { // Only do this once
            ReservationApi.Builder builder = new ReservationApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://parkovkaalex.android-1492529578093.appspot.com/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            reservationApi = builder.setApplicationName("ReservationLibrary").build();
            reservationApi = builder.build();
        }
        try {
            // Call here the wished methods on the Endpoints
            // For instance insert
            if(reservation != null && insert == true){
                reservationApi.insert(reservation).execute();
            }

            if(reservation != null && getall == true){
                reservationApi.list().execute().getItems();
            }

            if(reservation != null && get == true){
                reservationApi.get(id).execute(); //id einfügen
            }

            if(reservation != null && update == true){
                reservationApi.update(id, reservation).execute(); //id einfügen
            }

            if(delete == true){
                reservationApi.remove(id).execute();
            }

            insert = false;
            getall= false;
            get = false;
            update = false;
            delete = false;
            // and for instance return the list of all users
            return reservationApi.list().execute().getItems();

        } catch (IOException e){
            return new ArrayList<Reservation>();
        }
    }

    //This method gets executed on the UI thread - The UI can be manipulated directly inside
    //of this method
    @Override
    protected void onPostExecute(List<Reservation> result){

        if(result != null) {
            for (Reservation reservation : result) {
                Log.i(TAG, "ID Provider: " + reservation.getIdprovider() + " ID Tenant: "
                        + reservation.getIdtenant());

            }
        }
    }
}
