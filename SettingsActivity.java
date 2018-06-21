package ch.hesso.parkovkaalex.android;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsPrefFragment()).commit();
    }

    public static float getMarkerColor(Context c) {
        float hueValue = BitmapDescriptorFactory.HUE_RED;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        int value = Integer.valueOf(sharedPref.getString("color_marker", "0"));
        switch (value) {
            case 1:
                hueValue = BitmapDescriptorFactory.HUE_GREEN;
                break;
            case 2:
                hueValue = BitmapDescriptorFactory.HUE_YELLOW;
                break;
            case 3:
                hueValue = BitmapDescriptorFactory.HUE_ORANGE;
                break;
        }

        return hueValue;
    }

    public static float getMarkerAlpha(Context c) {
        float alpha = 1f;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        int value = Integer.valueOf(sharedPref.getString("color_alpha", "0"));
        switch (value) {
            case 1:
                alpha = 0.3f;
                break;
            case 2:
                alpha = 0.7f;
                break;
            case 3:
                alpha = 1f;
                break;
        }
        return alpha;
    }

    public static class SettingsPrefFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }
    }
}
