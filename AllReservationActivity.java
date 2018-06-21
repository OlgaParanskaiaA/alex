package ch.hesso.parkovkaalex.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lavdrim.myapplication.backend.parkovkaalexApi.model.parkovkaalex;
import com.example.lavdrim.myapplication.backend.reservationApi.model.Reservation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class AllReservationActivity extends AppCompatActivity {

    private ArrayList<String> data = new ArrayList<String>();
    private SessionManager session;
    private ParkingManager parkManager;
    private HashMap<String, String> user;
    private List<Reservation> fillteredlist;
    private List<Reservation> fulllist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reservation);

        session = new SessionManager(getApplicationContext());

        if (session.checkLogin()) {
            finish();
        }

        parkManager = new ParkingManager(getApplicationContext());
        user = session.getUserDetails();
        fulllist=new ArrayList<>();
        fillteredlist=new ArrayList<>();
        try {
            fulllist = new EndPointAsyncTaskReservation().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (fulllist==null){
            return;
        }
        for (int i=0; i<fulllist.size(); i++){
            if (fulllist.get(i).getIdtenant().equals(Long.parseLong(user.get(SessionManager.KEY_ID)))){
                fillteredlist.add(fulllist.get(i));
            }
        }
        if (fillteredlist==null){
            return;
        }
        ListView lv = (ListView) findViewById(R.id.listviewreservation);
        generateListContent();
        lv.setAdapter(new AllReservationActivity.MyListAdaper(getApplicationContext(), R.layout.list_view_reservation, data));
    }

    private void generateListContent() {
        List<parkovkaalex> parkings = null;
        try {
            parkings = new EndPointAsyncTaskparkovkaalex().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        parkovkaalex temp = new parkovkaalex();
        for(int i = 0; i < fillteredlist.size(); i++) {
            if(fillteredlist.get(i).getIdtenant().equals(Long.parseLong(user.get(SessionManager.KEY_ID)))) {
                for(int j = 0; j < parkings.size(); j++){
                    if(fillteredlist.get(i).getIdparkovkaalex().equals(parkings.get(j).getId())) {
                        temp = parkings.get(j);
                        data.add(temp.getAddress().toString());
                    }
                }
            }
        }
    }

    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;
        private MyListAdaper(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            AllReservationActivity.ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                AllReservationActivity.ViewHolder viewHolder = new AllReservationActivity.ViewHolder();
                viewHolder.rentIamge = (ImageView) convertView.findViewById(R.id.rentImage);
                viewHolder.parkingAddress = (TextView) convertView.findViewById(R.id.rentingaddress);
                viewHolder.button = (Button) convertView.findViewById(R.id.btnRentDelete);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (AllReservationActivity.ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDeleteReservation(v,position);
                }
            });
            mainViewholder.parkingAddress.setText(getItem(position));

            return convertView;
        }
    }
    public class ViewHolder {

        ImageView rentIamge;
        TextView parkingAddress;
        Button button;
    }

    public void sendDeleteReservation(View view, final int position){
        new AlertDialog.Builder(this)
                .setTitle("Deleting Reservation")
                .setMessage("Are you sure you want to delete this reservation?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Reservation reservation = new Reservation();
                        //Update
                        for(int i = 0; i < fillteredlist.size(); i++){
                            if(fillteredlist.get(position).getId().equals(fillteredlist.get(i).getId())){
                                reservation = fillteredlist.get(i);
                            }
                        }
                        new EndPointAsyncTaskReservation(reservation.getId(), "delete").execute();

                        Intent i = new Intent(getApplicationContext(), AllReservationActivity.class);
                        startActivity(i);
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}

