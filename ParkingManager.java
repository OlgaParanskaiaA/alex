package ch.hesso.parkovkaalex.android;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Lavdrim on 27.04.2017.
 */

public class ParkingManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref2";

    // User name (make variable public to access from outside)
    public static final String KEY_IDparkovkaalex = "idparkovkaalex";
    public static final String KEY_IDUSER = "iduser";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_PRICE = "price";
    public static final String KEY_COORDINATEX = "coordinatex";
    public static final String KEY_COORDINATEY = "coordinatey";

    // Constructor
    public ParkingManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */

    public void saveparkovkaalex(String idparkovkaalex, String iduser, String address, String location, String price, String coordinatex, String coordinatey){
        editor.putString(KEY_IDparkovkaalex, idparkovkaalex);
        editor.putString(KEY_IDUSER, iduser);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_LOCATION, location);
        editor.putString(KEY_PRICE, price);
        editor.putString(KEY_COORDINATEX, coordinatex);
        editor.putString(KEY_COORDINATEY, coordinatey);

        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getParkingDetails(){
        HashMap<String, String> parkovkaalex = new HashMap<String, String>();
        // parkovkaalex.android
        parkovkaalex.put(KEY_IDparkovkaalex, pref.getString(KEY_IDparkovkaalex, null));
        parkovkaalex.put(KEY_IDUSER, pref.getString(KEY_IDUSER, null));
        parkovkaalex.put(KEY_ADDRESS, pref.getString(KEY_ADDRESS, null));
        parkovkaalex.put(KEY_LOCATION, pref.getString(KEY_LOCATION, null));
        parkovkaalex.put(KEY_PRICE, pref.getString(KEY_PRICE, null));
        parkovkaalex.put(KEY_COORDINATEX, pref.getString(KEY_COORDINATEX, null));
        parkovkaalex.put(KEY_COORDINATEY, pref.getString(KEY_COORDINATEY, null));

        // return user
        return parkovkaalex;
    }

    public void updateparkovkaalex(){
        editor.clear();
        editor.commit();
    }

}
