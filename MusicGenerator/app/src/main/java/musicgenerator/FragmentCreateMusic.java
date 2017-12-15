package musicgenerator;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import static android.R.attr.key;

/**
 * Created by Jussuf on 12.06.17.
 */

public class FragmentCreateMusic extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    // Attribute
    private View view;
    Button btn_a,btn_b,btn_c,btn_d,btn_e,btn_f,btn_g, btn_rec;
    private int sound_a,sound_b,sound_c,sound_d,sound_e,sound_f,sound_g,
                sound_h, sound_i, sound_j , sound_k, sound_l, sound_m, sound_n,
                sound_o,sound_p,sound_q,sound_r,sound_s,sound_t,sound_u;
    private SoundPool soundPool;
    private Context context;
    private PlayMusic play;
    SharedPreferences prefs;
    static int song_id = 0;
    static int song_id_intern = 0;




    // Recording
    private int id_music = 0;
    private boolean recording;
    String songname = "mySong";
    String savedSongname;
    FileOutputStream outputStream;
    double startRecordTime;

    // Konstruktoren
    public FragmentCreateMusic() {
        // define default values for the notes

    }

    // Lifecycle Methoden
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_createmusic, container, false);

        // Ref all Buttons of the TONLEITER
        btn_a = (Button) view.findViewById(R.id.a_btn);
        btn_b = (Button) view.findViewById(R.id.b_btn);
        btn_c = (Button) view.findViewById(R.id.c_btn);
        btn_d = (Button) view.findViewById(R.id.d_btn);
        btn_e = (Button) view.findViewById(R.id.e_btn);
        btn_f = (Button) view.findViewById(R.id.f_btn);
        btn_g = (Button) view.findViewById(R.id.g_btn);
        int [] A;

        btn_rec = (Button) view.findViewById(R.id.magic_btn);

        recording = false;

        soundPool = new SoundPool.Builder().setMaxStreams(5).build();
        // load all sounds from the raw folder
        context = getActivity();

        A = loadSounds(context);

        onSharedPreferenceChanged(null, "");

        // Play sound on button click
        playSoundOnClick(btn_a,0,context,A);
        playSoundOnClick(btn_b,1,context,A);
        playSoundOnClick(btn_c,2,context,A);
        playSoundOnClick(btn_d,3,context,A);
        playSoundOnClick(btn_e,4,context,A);
        playSoundOnClick(btn_f,5,context,A);
        playSoundOnClick(btn_g,6,context,A);

        // Recording Action
        btn_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recording = !recording;
                // Start Recording
                if (recording){
                    // Change Button to Rec
                    Log.d("StartRec","Alive1");
                    btn_rec.setText("Recording");
                    btn_rec.setBackgroundResource(R.drawable.magic_button_recording);
                    // Open new File
                    try {
                        song_id_intern = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("songId_intern", 0);
                        songname = "mySong"+song_id_intern;
                        song_id_intern++;
                        SharedPreferences.Editor edit = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                        edit.putInt("songId_intern", song_id_intern);
                        edit.commit();
                        outputStream = context.openFileOutput(songname, Context.MODE_PRIVATE);
                        outputStream.write(String.valueOf(id_music).getBytes());
                        startRecordTime = System.currentTimeMillis();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Stop Recording
                else {
                    Log.d("StopRec","Alive1");
                    // Change Button to Magic
                    btn_rec.setText("MAGIC");
                    btn_rec.setBackgroundResource(R.drawable.magic_button_selector);
                    // Close File
                    try {
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Autosave
                    SharedPreferences checkPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    boolean autosave = checkPref.getBoolean("autosave_on", false);
                    if (autosave){
                        Log.d("autosave","autosave checked");
                        song_id = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("songId", 0);
                        savedSongname = "mySong" + song_id;
                        song_id++;
                        SharedPreferences.Editor edit = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                        edit.putInt("songId", song_id);
                        edit.commit();
                        saveFileToPrefs();
                    }
                    // User Input Dialog Songname
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Enter Songname");
                        final EditText input = new EditText(context);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);
                        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                savedSongname = input.getText().toString();
                                saveFileToPrefs();
                            }
                        });
                        builder.setNeutralButton("Default", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                song_id = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("songId", 0);
                                savedSongname = "mySong" + song_id;
                                song_id++;
                                SharedPreferences.Editor edit = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                                edit.putInt("songId", song_id);
                                edit.commit();
                                saveFileToPrefs();
                            }
                        });
                        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                context.deleteFile(songname);
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
            }
        });
        return view;
    }


    //I believe you just need to register/unregister the Listener in your PreferenceFragment and it will work.

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSharedPreferences("Inst",Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getActivity().getSharedPreferences("Inst",Context.MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    // Helper Methoden

    // Play Sound and makeFileString
    public void playSoundOnClick(Button btn, final int sound, final Context context, final int[] A){
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                prefs = context.getSharedPreferences("Inst",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                int Instrument = prefs.getInt("Instrument",0);

                soundPool.play(A[sound+((Integer)(A.length)/3)*Instrument],1,1,0,0,1);


                //Log.d("PLAY",String.valueOf(sound));
                //soundPool.play(sound,1,1,0,0,1);

                // Save played sound to soundfile if Recording
                if (recording){
                    try {
                        outputStream.write(makeFileString(String.valueOf(sound)).getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Method to create string with relative timestamp + played sound
    public String makeFileString( String string){
        String Filestring;
        double currentTime = System.currentTimeMillis();
        double relativeTime = currentTime-startRecordTime;
        Filestring = "\n" + String.valueOf(relativeTime) + "\n" + string + "\n";
        return Filestring;
    }

    // Method to Save file to shared Preferences and rename Song
    public void saveFileToPrefs(){
        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String songList = prefs.getString("songList","");
        String songListIntern  = prefs.getString("songListIntern", "");
        songList += ","+ savedSongname;
        songListIntern += ","+ songname;
        String[] realList = songList.split(",");
        for(int i = 0; i < realList.length; i++){
            Log.d("sharedtest",realList[i]);
        }
        editor.putString("songList", songList);
        editor.putString("songListIntern", songListIntern);
        editor.commit();
        // Rename Song
        //File oldfile = context.getFileStreamPath(songname);
        //File newfile = context.getFileStreamPath(savedSongname);
        //oldfile.renameTo(newfile);
    }

    // Method to play song
    public void playSong (String songname){
        //PlayMusic pm = new PlayMusic(context);
        //pm.execute(songname);
    }


    private int[] loadSounds(Context context_){
        Log.d("loading","select new instrument");

                sound_a = soundPool.load(context_, R.raw.a_piano, 1);
                sound_b = soundPool.load(context_, R.raw.b_piano, 1);
                sound_c = soundPool.load(context_, R.raw.c_piano, 1);
                sound_d = soundPool.load(context_, R.raw.d_piano, 1);
                sound_e = soundPool.load(context_, R.raw.e_piano, 1);
                sound_f = soundPool.load(context_, R.raw.f_piano, 1);
                sound_g = soundPool.load(context_, R.raw.g_piano, 1);

                sound_h = soundPool.load(context_, R.raw.bass01, 1);
                sound_i = soundPool.load(context_, R.raw.bass_acid01, 1);
                sound_j = soundPool.load(context_, R.raw.bass_acid02, 1);
                sound_k = soundPool.load(context_, R.raw.bass_hard01, 1);
                sound_l = soundPool.load(context_, R.raw.bass_hard02, 1);
                sound_m = soundPool.load(context_, R.raw.rave_bass01, 1);
                sound_n = soundPool.load(context_, R.raw.rave_bass04, 1);

                sound_o = soundPool.load(context_, R.raw.g1, 1);
                sound_p = soundPool.load(context_, R.raw.g2, 1);
                sound_q = soundPool.load(context_, R.raw.g3, 1);
                sound_r = soundPool.load(context_, R.raw.g4, 1);
                sound_s = soundPool.load(context_, R.raw.g5, 1);
                sound_t = soundPool.load(context_, R.raw.g6, 1);
                sound_u = soundPool.load(context_, R.raw.g7, 1);

            int[] A = {sound_a,sound_b,sound_c,sound_d,sound_e,sound_f,sound_g,
                       sound_h,sound_i,sound_j,sound_k,sound_l,sound_m,sound_n,
                       sound_o,sound_p,sound_q,sound_r,sound_s,sound_t,sound_u};

            return A;

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("change!!",String.valueOf(key));

    }


}

