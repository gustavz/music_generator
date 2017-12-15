package com.nico.gustav.raimund.musicgenerator;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Jussuf on 12.06.17.
 */

public class SubFragmentCreateMusicPianoPad extends Fragment{

    // Attribute
    private View view;
    Button btn_a,btn_b,btn_c,btn_d,btn_e,btn_f,btn_g;
    private int sound_a,sound_b,sound_c,sound_d,sound_e,sound_f,sound_g;
    private SoundPool soundPool;
    private Context context;

    MediaRecorder recorder = new MediaRecorder();


    // Konstruktoren
    public SubFragmentCreateMusicPianoPad(){
    }

    // Lifecycle Methoden
    @RequiresApi(api = Build.VERSION_CODES.M) // wegen getContext()
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.subfragment_createmusic_pianopad,container,false);

        // Ref all Buttons of the TONLEITER
        btn_a = (Button) view.findViewById(R.id.a_btn);
        btn_b = (Button) view.findViewById(R.id.b_btn);
        btn_c = (Button) view.findViewById(R.id.c_btn);
        btn_d = (Button) view.findViewById(R.id.d_btn);
        btn_e = (Button) view.findViewById(R.id.e_btn);
        btn_f = (Button) view.findViewById(R.id.f_btn);
        btn_g = (Button) view.findViewById(R.id.g_btn);

        // if statement because of android version AUFHAXX for soundpool
        // soundpool Must be build BEFORE sounds are loaded with context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(5).build();
        }
        else{
            soundPool= new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        }

        // load all sounds from the raw folder
        context = getActivity();
        sound_a = soundPool.load(context,R.raw.a_piano,1);
        sound_b = soundPool.load(context,R.raw.b_piano,1);
        sound_c = soundPool.load(context,R.raw.c_piano,1);
        sound_d = soundPool.load(context,R.raw.d_piano,1);
        sound_e = soundPool.load(context,R.raw.e_piano,1);
        sound_f = soundPool.load(context,R.raw.f_piano,1);
        sound_g = soundPool.load(context,R.raw.g_piano,1);

        // Play sound on button click
        playSoundOnClick(btn_a,sound_a);
        playSoundOnClick(btn_b,sound_b);
        playSoundOnClick(btn_c,sound_c);
        playSoundOnClick(btn_d,sound_d);
        playSoundOnClick(btn_e,sound_e);
        //playSoundOnClick(btn_f,sound_f);
        //playSoundOnClick(btn_g,sound_g);

        return view;
    }

    // Helper Methoden
    public void playSoundOnClick(Button btn, final int sound){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(sound,1,1,0,0,1);
            }
        });
    }
}
