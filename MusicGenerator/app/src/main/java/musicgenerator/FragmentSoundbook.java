package musicgenerator;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.os.SystemClock.sleep;

/**
 * Created by Jussuf on 12.06.17.
 */

public class FragmentSoundbook extends ListFragment implements AdapterView.OnItemClickListener , AdapterView.OnItemLongClickListener {

    private View view;

    public ListView listView_;

    public int queued_ = 0;
    public boolean playing_ = false;

    public boolean cancle_ = false;

    int music_id_ = 0;
    private List<Double> time_list_;
    private List<Integer> sound_list_;


    public FragmentSoundbook(){}


    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


        String[] songArray = getActivity().getPreferences(getActivity().MODE_PRIVATE).getString("songList","").split(",");
        List<String> songList = new ArrayList<String>();
        for(int i = 0; i < songArray.length; i++){
            if(songArray[i].isEmpty()){
                continue;
            }
            songList.add(songArray[i]);
        }



        final StableArrayAdapter adapter = new StableArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, songList);
        setListAdapter(adapter);

        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);

    }

    public void onItemClick(AdapterView<?> parent, final View view, int position, long id){
        Log.d("Soundbook", "clicked queue: "+queued_);
        final String clickedSongName = (String) parent.getItemAtPosition(position);


        String[] songArrayUser = getActivity().getPreferences(getActivity().MODE_PRIVATE).getString("songList","").split(",");
        String[] songArrayIntern = getActivity().getPreferences(getActivity().MODE_PRIVATE).getString("songListIntern","").split(",");

        for(int i = 0; i < songArrayUser.length; i++){
            Log.d("Soundbook", songArrayUser[i]);
            Log.d("Soundbook", "compare: " + clickedSongName.compareTo(songArrayUser[i]));
            if(clickedSongName.compareTo(songArrayUser[i])==0) {
                //play music
                Log.d("Soundbook", "play: " + songArrayIntern[i]);
                if(!playing_) {
                    playing_ = true;
                    PlayMusic playMusic = new PlayMusic(getActivity(), this);
                    playMusic.execute(songArrayIntern[i]);
                }else{
                    //do not play
                    cancle_ = true;
                    sleep(100);
                    cancle_ = false;
                    PlayMusic playMusic = new PlayMusic(getActivity(), this);
                    playMusic.execute(songArrayIntern[i]);
                    Log.d("Soundbook", "nope, not gonna play more shit!");
                }
            }
        }



        Log.d("Soundbook", " clickedSong: " + clickedSongName);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Soundbook", "onItemLong");
        final String clickedSongName = (String) parent.getItemAtPosition(position);


        final String[] songArrayUser = getActivity().getPreferences(getActivity().MODE_PRIVATE).getString("songList","").split(",");


        final String[] songArrayIntern = getActivity().getPreferences(getActivity().MODE_PRIVATE).getString("songListIntern","").split(",");

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        String deleted = "";

        dialog.setTitle("Merge or delete " + clickedSongName + "?");
        dialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String file_intern = "";
                String file = "";

                for(int i = 0; i < songArrayUser.length; i++){
                    if(songArrayUser[i].compareTo(clickedSongName)!= 0){
                        file_intern += "," + songArrayIntern[i];
                        file += ","+ songArrayUser[i];
                    }else{
                        getActivity().deleteFile(songArrayIntern[i]);

                        sleep(100);
                        try {
                            getActivity().openFileInput(songArrayIntern[i]);
                        }catch(IOException e){
                            Log.d("Soundbook", "deleted!!!!");
                        }
                    }
                }
                SharedPreferences.Editor edit = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                edit.putString("songList", file);
                edit.putString("songListIntern", file_intern);
                edit.commit();

                getFragmentManager().beginTransaction().replace(R.id.container,
                        new FragmentSoundbook()).commit();


            }
        });
        dialog.setPositiveButton("Merge with another song!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder dialog2 = new AlertDialog.Builder(getActivity());
                dialog2.setTitle("Merging "+ clickedSongName + " with?");

                String[] songArray = getActivity().getPreferences(getActivity().MODE_PRIVATE).getString("songList","").split(",");
                List<String> songList = new ArrayList<String>();
                for(int i = 0; i < songArray.length; i++){
                    if(songArray[i].isEmpty()){
                        continue;
                    }
                    if(songArray[i].compareTo(clickedSongName) == 0){
                        continue;
                    }
                    songList.add(songArray[i]);
                }

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, songList);
                dialog2.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String item = adapter.getItem(which);
                        Log.d("Soundbook", item);

                        AlertDialog.Builder dialogFinal = new AlertDialog.Builder(getActivity());
                        dialogFinal.setTitle("You selected");
                        dialogFinal.setMessage(item);
                        dialogFinal.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mergeSong(item, clickedSongName);
                            }
                        });

                        dialogFinal.show();

                    }
                });

                dialog2.show();
            }
        });
        /*
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        */


        dialog.show();


        return false;
    }

    private class StableArrayAdapter extends ArrayAdapter<String>{
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> list){
            super(context, textViewResourceId, list);
            for(int i = 0; i < list.size();i++){
                mIdMap.put(list.get(i),i);
            }
        }

        @Override
        public long getItemId(int position){
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds(){
            return true;
        }
    }

    public void onPause(){
        super.onPause();
        cancle_ = true;
        //PlayMusic playMusic = new PlayMusic(getActivity());
        //playMusic.execute("irgendwaswasesdefinitivnichtgibt");
    }

    public void onResum(){
        super.onResume();
        cancle_ = false;
    }

    public void mergeSong(String name1, String name2){
        //find out intern name
        String[] songArrayUser = getActivity().getPreferences(getActivity().MODE_PRIVATE).getString("songList","").split(",");

        String[] songArrayIntern = getActivity().getPreferences(getActivity().MODE_PRIVATE).getString("songListIntern","").split(",");

        String name_int1 = "";
        String name_int2 = "";

        for(int i = 0; i < songArrayUser.length; i++){
            if(songArrayUser[i].compareTo(name1)==0){
                name_int1 = songArrayIntern[i];
            }
            if(songArrayUser[i].compareTo(name2)==0){
                name_int2 = songArrayIntern[i];
            }
        }
        time_list_ = new ArrayList<Double>();
        sound_list_ = new ArrayList<Integer>();

        if(!read(name_int1)){
            return;
        }

        List<Double> time_list_1 = time_list_;
        List<Integer> sound_list_1 = sound_list_;
        int music_id_1 = music_id_;

        if(!read(name_int2)){
            return;
        }

        String new_name = name1 + " and " + name2;
        OutputStream outputStream;
        try{
            outputStream = getActivity().openFileOutput(new_name, Context.MODE_PRIVATE);
            outputStream.write(String.valueOf(0).getBytes());
        }catch(IOException e){
            return;
        }

        //merge that shit
        int index1 = 0;
        int index2 = 0;
        while(index1+1 != time_list_1.size() || index2+1 != time_list_.size()){
            double time1, time2;
            if(index1+1 == time_list_1.size()){
                time1 = 932143214;
            }else {
                time1 = time_list_1.get(index1);
            }

            if(index2+1 == time_list_.size()){
                time2 = 932143214;
            }else {
                time2 = time_list_.get(index2);
            }
            String append = "\n";
            if(time1<time2){
                append += String.valueOf(time1) +"\n"+String.valueOf(sound_list_1.get(index1));
                index1++;
            }else{
                append += String.valueOf(time2) +"\n"+String.valueOf(sound_list_.get(index1));
                index2++;
            }
            Log.d("Soundbook", append);
            try{
                outputStream.write(append.getBytes());
            }catch(IOException e){

            }
        }
        try {
            outputStream.close();
        }catch(Exception e){

        }
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String songList = prefs.getString("songList","");
        String songListIntern  = prefs.getString("songListIntern", "");
        songList += ","+ new_name;
        songListIntern += ","+ new_name;
        String[] realList = songList.split(",");

        editor.putString("songList", songList);
        editor.putString("songListIntern", songListIntern);
        editor.commit();

        getFragmentManager().beginTransaction().replace(R.id.container,
                new FragmentSoundbook()).commit();

    }

    public boolean read(String name){
        try {
            FileInputStream inputStream = getActivity().openFileInput(name);

            //read
            if (inputStream != null) {
                InputStreamReader inputReader = new InputStreamReader(inputStream);
                BufferedReader buffReader = new BufferedReader(inputReader);

                String curr = "";
                Log.d("PlayMusic", "Start reading");
                try {
                    curr = buffReader.readLine();
                    if (curr != null) {
                        music_id_ = Integer.parseInt(curr);

                    }
                    Log.d("PlayMusic", "Got mode: " + music_id_);
                    boolean time = true;
                    while ((curr = buffReader.readLine()) != null) {
                        if (curr.isEmpty()) {
                            Log.d("PlayMusic", "curr is empty");
                            continue;
                        }
                        Log.d("PlayMusic", curr);
                        if (time) {
                            //Log.d("PlayMusic", "double "+curr);
                            time_list_.add(Double.parseDouble(curr));
                        } else {
                            //Log.d("PlayMusic", "int "+curr);
                            sound_list_.add(Integer.parseInt(curr));
                        }
                        time = !time;
                    }

                } catch (IOException e) {
                    //Toast.makeText(context_, e.getMessage(), Toast.LENGTH_LONG).show();
                    return false;
                }
            } else {
                //Toast.makeText(context_, "File == null!", Toast.LENGTH_LONG).show();
                Log.d("PlayMusic", "File == null");
                return false;
            }
        }catch(IOException e){
            return false;
        }
        return true;
    }
}
