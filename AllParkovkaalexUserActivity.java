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
import android.widget.AdapterView;
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


public class AllparkovkaalexsUserActivity extends AppCompatActivity {

    private ArrayList<String> data = new ArrayList<>();
    private SessionManager session;
    private ParkingManager parkManager;
    private HashMap<String, String> user;
    private List<parkovkaalex> fulllist;
    private List<parkovkaalex> fillteredlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_parkovkaalexs_user);

        session = new SessionManager(getApplicationContext());

        if (session.checkLogin()) {
            finish();
        }

        parkManager = new ParkingManager(getApplicationContext());
        user = session.getUserDetails();

        fulllist=new ArrayList<>();
        fillteredlist=new ArrayList<>();

        try {
            fulllist = new EndPointAsyncTaskparkovkaalex().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (fulllist==null){
            return;
        }
        for (int i=0; i<fulllist.size(); i++){
            if(fulllist.get(i).getIduser().equals(Long.parseLong(user.get(SessionManager.KEY_ID)))){
                fillteredlist.add(fulllist.get(i));
            }
        }


        ListView lv = (ListView) findViewById(R.id.listview);
        generateListContent();
        lv.setAdapter(new MyListAdaper(this, R.layout.list_view, data));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parkovkaalex parking1 = new parkovkaalex();
                //Update
                for(int i = 0; i < fulllist.size(); i++){
                    if(fillteredlist.get(position).getId().equals(fulllist.get(i).getId())){
                         parking1 = fulllist.get(i);
                    }
                }

                parkManager.saveparkovkaalex(String.valueOf(parking1.getId()), String.valueOf(parking1.getIduser()), parking1.getAddress(), parking1.getLocation(), String.valueOf(parking1.getPrice()), String.valueOf(parking1.getCoordinatex()), String.valueOf(parking1.getCoordinatey()));

                Intent i = new Intent(getApplicationContext(), Update_delete_parkovkaalex.class);
                i.putExtra("parkovkaalexid", parking1.getId());
                startActivity(i);
            }
        });

    }

    private void generateListContent() {
        for(int i = 0; i < fillteredlist.size(); i++) {
            data.add(fillteredlist.get(i).getAddress().toString());
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
            ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.carImage = (ImageView) convertView.findViewById(R.id.carImage);
                viewHolder.parkingAddress = (TextView) convertView.findViewById(R.id.parkingAddress);
                viewHolder.button = (Button) convertView.findViewById(R.id.btnInfo);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDeleteparkovkaalex(v,position);
                }
            });
            mainViewholder.parkingAddress.setText(getItem(position));

            return convertView;
        }
    }
    public class ViewHolder {

        ImageView carImage;
        TextView parkingAddress;
        Button button;
    }

    public void sendDeleteparkovkaalex(View view, final int position){

        new AlertDialog.Builder(this)
                .setTitle("Deleting parking spot")
                .setMessage("Are you sure you want to delete this parking spot?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        parkovkaalex parkovkaalex1 = new parkovkaalex();
                        //Update
                        for(int i = 0; i < fulllist.size(); i++){
                            if(fulllist.get(position).getId().equals(fulllist.get(i).getId())){
                                parkovkaalex1 = fulllist.get(i);
                            }
                        }
                        //parkManager.updateparkovkaalex();
                        try {
                            List<Reservation> reservations = new EndPointAsyncTaskReservation().execute().get();
                            //cascade delete reservations if provider deletes parking spot
                            if(reservations != null) {
                                for (Reservation reservation : reservations) {
                                    if (reservation.getIdprovider().equals(parkovkaalex1.getId())  || reservation.getIdtenant().equals(Long.parseLong(user.get(SessionManager.KEY_ID)))) {
                                        new EndPointAsyncTaskReservation(reservation.getId(), "delete").execute();
                                    }
                                }
                            }
                            new EndPointAsyncTaskparkovkaalex(parkovkaalex1.getId(), "delete").execute();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        Intent i = new Intent(getApplicationContext(), AllparkovkaalexsUserActivity.class);
                        startActivity(i);
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}
