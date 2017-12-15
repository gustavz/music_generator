package com.nico.gustav.raimund.musicgenerator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;


/**
 * Created by Jussuf on 12.06.17.
 */

public class FragmentSettings extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences prefs;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Delete Soundbook
        Preference database = findPreference(getString(R.string.set_soundbook_size_key));
        database.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Soundbook")
                        .setMessage("Are you sure to delete the soundbook? There are some massiv hits in it")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
                                // Delete Files
                                String[] songArrayIntern = prefs.getString("songListIntern", "").split(",");
                                for (int i = 0; i < songArrayIntern.length; i++) {
                                    getActivity().deleteFile(songArrayIntern[i]);
                                }

                                // Delete Prefs
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.clear();
                                editor.commit();

                                Toast.makeText(getActivity(), "Soundbook has been burned."+"\n"+"Heil Hydra!", Toast.LENGTH_LONG).show();
                                setSummariesForEditText();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }
        });

        setSummariesForEditText();

    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getActivity().getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getActivity().getApplicationContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(this.getClass().getSimpleName(), "OnPreferenceChanged");
        // Sollte sich einer der Werte ändern, sollen auch die Summaries neu angezeigt werden.
        if (key.equals(getString(R.string.set_GenreNet_key))) {
            setSummariesForEditText();
        }
    }

    private void setSummariesForEditText() {
        String[] keys = {
                getString(R.string.set_GenreNet_key)
        };
        String[] defaults = {
                getString(R.string.set_GenreNet_default_entry),
        };

        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getActivity().getApplicationContext());
        for (int i = 0; i < keys.length; i++) {
            Preference preference = findPreference(keys[i]);
            String summary = String.format("%s", sharedPreferences.getString(keys[i], defaults[i]));
            preference.setSummary(summary);
        }
    }
}