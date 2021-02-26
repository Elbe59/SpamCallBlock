package com.alioptak.spamcallblock;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceFragment;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;


public class DataBaseActivity  extends AppCompatActivity {
    private File mFile = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        mFile = new File(Environment.getExternalStorageDirectory().getPath()+"/Android/data" + "com.alioptak.spamcallblock" +"/files" +"database.sql");
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // addPreferencesFromResource(R.xml.my_preference_screen); // changer nom xm
        }
    }
}
