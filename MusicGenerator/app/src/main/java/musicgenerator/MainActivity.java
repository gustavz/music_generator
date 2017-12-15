package musicgenerator;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import musicgenerator.mnist.Classifier;
import musicgenerator.mnist.FragmentMNIST;
import musicgenerator.mnist.TensorFlowClassifier;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Attribute
    FragmentManager fragmentManager;
    private static final int PIXEL_WIDTH = 28;

    private SharedPreferences prefs;
    private FragmentCreateMusic frag;
    FloatingActionButton fab;



    public List<Classifier> mClassifiers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hard delete all shared prefs
        //SharedPreferences.Editor editor = this.getPreferences(getApplicationContext().MODE_PRIVATE).edit();
        //editor.clear();
        //editor.commit();

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) { getFragmentManager().beginTransaction()
                .add(R.id.container, new FragmentCreateMusic()).commit(); }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences("Inst",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int Instrument = prefs.getInt("Instrument",0);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        updatefab(Instrument);
        
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prefs = getSharedPreferences("Inst",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                int Instrument = prefs.getInt("Instrument",0);


                if (Instrument==0){
                    Instrument = 1;
                    Log.d("soundreste", String.valueOf(Instrument));

                }
                else if (Instrument ==1)
                {
                    Instrument = 2;
                    Log.d("soundreste", String.valueOf(Instrument));
                }
                else {
                    Instrument = 0;
                }

                editor.putInt("Instrument", Instrument);
                editor.commit();

                Log.d("song changed","Instrument");
                updatefab(Instrument);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // load the tensorflow model
        loadModel();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void updatefab(int i){

        if (i == 0){
            fab.setImageDrawable(getResources().getDrawable(R.drawable.piano));
        }
        else if (i == 1){
            fab.setImageDrawable(getResources().getDrawable(R.drawable.drum));
        }
        else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.guitar));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        fragmentManager = getFragmentManager();

        if (id == R.id.nav_createMusic) {
            // Handle the Magic
            FragmentCreateMusic fragmentCreateMusic = new FragmentCreateMusic();
            fragmentManager.beginTransaction().replace(R.id.container,
                    fragmentCreateMusic).commit();

        } else if (id == R.id.nav_midiSamples) {
            FragmentMidiSamples fragmentMidiSamples = new FragmentMidiSamples();
            fragmentManager.beginTransaction().replace(R.id.container,
                    fragmentMidiSamples).commit();

        } else if (id == R.id.nav_settings) {
            FragmentSettings fragmentSettings = new FragmentSettings();
            fragmentManager.beginTransaction().replace(R.id.container,
                    fragmentSettings).commit();
        }

        else if (id == R.id.nav_soundBook){
            FragmentSoundbook fragmentSoundbook = new FragmentSoundbook();
            fragmentManager.beginTransaction().replace(R.id.container,
                    fragmentSoundbook).commit();
        }

        else if (id == R.id.nav_mnist){
            FragmentMNIST fragmentmnist = new FragmentMNIST();
            fragmentManager.beginTransaction().replace(R.id.container,
                    fragmentmnist).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //creates a model object in memory using the saved tensorflow protobuf model file
    //which contains all the learned weights
    public void loadModel() {
        //The Runnable interface is another way in which you can implement multi-threading other than extending the
        // //Thread class due to the fact that Java allows you to extend only one class. Runnable is just an interface,
        // //which provides the method run.
        // //Threads are implementations and use Runnable to call the method run().
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //add 2 classifiers to our classifier arraylist
                    //the tensorflow classifier and the keras classifier
                   mClassifiers.add(
                            TensorFlowClassifier.create(getAssets(), "TensorFlow",
                                    "opt_mnist_convnet-tf.pb", "labels.txt", PIXEL_WIDTH,
                                    "input", "output", true));
                    mClassifiers.add(
                            TensorFlowClassifier.create(getAssets(), "Keras",
                                    "opt_mnist_convnet-keras.pb", "labels.txt", PIXEL_WIDTH,
                                    "conv2d_1_input", "dense_2/Softmax", false));
                } catch (final Exception e) {
                    //if they aren't found, throw an error!
                    throw new RuntimeException("Error initializing classifiers!", e);
                }
            }
        }).start();
    }



}




// Rainas TF Example shitfuck
/*
    private static final String MODEL_FILE = "file:///android_asset/name_of_file.pb";
    private static final String INPUT_NODE = "??x"; //input defined from tensorflow
    private static final String OUTPUT_NODE = "??y"; //output defined from tensorflow

    private static final int [] INPUT_SIZE = {1,27*27}; //size input data

    private TensorFlowInferenceInterface tfInterface; //tensorflow interface

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //init tensorflow
        tfInterface = new TensorFlowInferenceInterface();
        tfInterface.initializeTensorFlow(getAssets(), MODEL_FILE);

        float[] input = {1,2,3,4,5}; //input array
        float[] output;
        output = performTfFloat(input);
    }

    public float[] performTfFloat(float[] input){
        //calculates output (return value) for desired input
        //if input/output isn't float, just change the fillNode<Datatype> and readNodeFloat<Datatype>

        //fill input values
        tfInterface.fillNodeFloat(INPUT_NODE, INPUT_SIZE, input); //corresponds to feed_dict in python

        //run nn
        tfInterface.runInference(new String[] {OUTPUT_NODE}); //corresponds to sess.run() in python

        //read output
        float[] resu = {0,0};
        tfInterface.readNodeFloat(OUTPUT_NODE, resu);

        return resu;
    }
 */
