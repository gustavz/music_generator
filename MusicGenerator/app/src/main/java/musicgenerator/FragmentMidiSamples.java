package musicgenerator;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jussuf on 12.06.17.
 */

public class FragmentMidiSamples extends Fragment {

    private View view;

    public FragmentMidiSamples(){}

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_midisamples, container, false);
        return view;
    }
}
