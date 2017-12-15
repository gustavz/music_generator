package com.nico.gustav.raimund.musicgenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getActivity;
import static android.os.SystemClock.sleep;

/**
 * Created by raimund on 08.07.17.
 */

public class PlayMusic extends AsyncTask<String, Void, Void> {

    private Context context_;
    public FragmentSoundbook parent_;
    private Context context;
    private MainActivity main;

    private SharedPreferences prefs,prefs2;

    private int Instrument;

    {
        Instrument = 0;
    }

    public void setInstrument(int instrument){
        this.Instrument = instrument;
    }

    public int getInstrument(){
        return this.Instrument;
    }

    private List<Double> time_list_;
    private List<Integer> sound_list_;
    private int music_id_ = 0;

    private int sound_a,sound_b,sound_c,sound_d,sound_e,sound_f,sound_g;
    private SoundPool soundPool;

    public PlayMusic(Context context, FragmentSoundbook parent){
        context_ = context;
        parent_ = parent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(5).build();
        }
        else{
            soundPool= new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        parent_.queued_++;
        time_list_ = new ArrayList<Double>();
        sound_list_ = new ArrayList<Integer>();
        try {
            FileInputStream inputStream = context_.openFileInput(params[0]);

            //read
            if(inputStream != null){
                InputStreamReader inputReader = new InputStreamReader(inputStream);
                BufferedReader buffReader = new BufferedReader(inputReader);

                String curr = "";
                Log.d("PlayMusic", "Start reading");
                try {
                    curr = buffReader.readLine();
                    if (curr != null) {
                        try {
                            music_id_ = Integer.parseInt(curr);
                        } catch (Exception e) {
                            Log.d("PlayMusic", e.getMessage());
                        }


                    }
                    Log.d("PlayMusic", "Got mode: " + music_id_);
                    boolean time = true;
                    while ((curr = buffReader.readLine()) != null) {
                        if(curr.isEmpty()){
                            Log.d("PlayMusic", "curr is empty");
                            continue;
                        }
                        Log.d("PlayMusic", curr);
                        try {
                            if (time) {
                                //Log.d("PlayMusic", "double "+curr);
                                time_list_.add(Double.parseDouble(curr));
                            } else {
                                //Log.d("PlayMusic", "int "+curr);
                                sound_list_.add(Integer.parseInt(curr));
                            }
                        }catch(Exception e){
                            Log.d("PlayMusic", e.getMessage());
                        }
                        time = !time;
                    }

                }catch(IOException e){
                    //Toast.makeText(context_, e.getMessage(), Toast.LENGTH_LONG).show();
                    return null;
                }
            }else{
                //Toast.makeText(context_, "File == null!", Toast.LENGTH_LONG).show();
                Log.d("PlayMusic", "File == null");
                return null;
            }

        }catch(FileNotFoundException e){
            //Toast.makeText(context_, "File not found!", Toast.LENGTH_LONG).show();
            Log.d("PlayMusic", "File not found!");
            return null;
        }

        for(int i = 0; i < time_list_.size(); i++){
            Log.d("PlayMusic", "time: "+time_list_.get(i) + " sound: " + sound_list_.get(i));
        }

        if(time_list_.size()==0){
            return null;
        }

        //play back
        double time_start = System.currentTimeMillis();
        double song_time = time_list_.get(time_list_.size()-1);
        double curr_time = System.currentTimeMillis() - time_start;
        Log.d("PlayMusic", "song_time: " + song_time + " curr_time: " + curr_time);
        int index = 0;

        loadSounds(context_);

        while(index < time_list_.size()){
            if(parent_.cancle_){
                soundPool.release();
                return null;
            }
            if(curr_time > time_list_.get(index)){
                //play sound
                Log.d("PlayMusic", "play: " + sound_list_.get(index));
                soundPool.play(sound_list_.get(index), 1, 1, 0, 0, 1);
                index++;
            }

            //sleep(1);
            //Log.d("PlayMusic", "time: " + curr_time);
            curr_time = System.currentTimeMillis() - time_start;

            if(isCancelled()){
                soundPool.release();
                return null;
            }
        }
        Log.d("PlayMusic", "done");
        sleep(1000);

        soundPool.release();
        return null;
    }

    public void onPostExecute(Void result){
        parent_.queued_--;
        parent_.playing_ = false;
    }

   public void loadSounds(Context context_){
       Log.d("loading","select new instrument");
        if(music_id_ == 0){

            prefs = context_.getSharedPreferences("Inst",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            int Instrument = prefs.getInt("Instrument",0);


            prefs2 = context_.getSharedPreferences("Inst2",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor2 = prefs2.edit();

            if(Instrument == 0) {

                sound_a = soundPool.load(context_, R.raw.a_piano, 1);
                sound_b = soundPool.load(context_, R.raw.b_piano, 1);
                sound_c = soundPool.load(context_, R.raw.c_piano, 1);
                sound_d = soundPool.load(context_, R.raw.d_piano, 1);
                sound_e = soundPool.load(context_, R.raw.e_piano, 1);
                sound_f = soundPool.load(context_, R.raw.f_piano, 1);
                sound_g = soundPool.load(context_, R.raw.g_piano, 1);

                Log.d("Piano", String.valueOf(Instrument));

            }

            else if(Instrument == 1) {

                sound_a = soundPool.load(context_, R.raw.bass01, 1);
                sound_b = soundPool.load(context_, R.raw.bass_acid01, 1);
                sound_c = soundPool.load(context_, R.raw.bass_acid02, 1);
                sound_d = soundPool.load(context_, R.raw.bass_hard01, 1);
                sound_e = soundPool.load(context_, R.raw.bass_hard02, 1);
                sound_f = soundPool.load(context_, R.raw.rave_bass01, 1);
                sound_g = soundPool.load(context_, R.raw.rave_bass02, 1);
                Log.d("Bass", String.valueOf(Instrument));

            }

            else if(Instrument == 2) {
                sound_a = soundPool.load(context_, R.raw.g1, 1);
                sound_b = soundPool.load(context_, R.raw.g2, 1);
                sound_c = soundPool.load(context_, R.raw.g3, 1);
                sound_d = soundPool.load(context_, R.raw.g4, 1);
                sound_e = soundPool.load(context_, R.raw.g5, 1);
                sound_f = soundPool.load(context_, R.raw.g6, 1);
                sound_g = soundPool.load(context_, R.raw.g7, 1);
            }


        }

    }
}
