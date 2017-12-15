package com.nico.gustav.raimund.musicgenerator;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Jussuf on 12.06.17.
 */

public class SubFragmentCreateMusicMagicButton extends Fragment {

    // Attribute
    Button btnMagic;
    View view;


    // Konstruktoren
    public SubFragmentCreateMusicMagicButton(){
    }

    // Lifecycle Methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.subfragment_createmusic_magicbutton,container,false);
        btnMagic = (Button) view.findViewById(R.id.magic_btn);
        return view;
    }
}
