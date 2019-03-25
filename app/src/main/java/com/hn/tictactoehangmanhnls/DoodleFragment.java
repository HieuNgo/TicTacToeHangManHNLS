package com.hn.tictactoehangmanhnls;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class DoodleFragment extends Fragment {
    private DoodleView doodleView; // handles touch events and draws
    private ColorDialogFragment colorFrag;
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private boolean dialogOnScreen = false;

    // value used to determine whether user shook the device to erase
    private static final int ACCELERATION_THRESHOLD = 100000;

    // used to identify the request for using external storage, which
    // the save image feature needs
    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;

    // called when Fragment's view needs to be created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view =
                inflater.inflate(R.layout.fragment_doodle, container, false);

        setHasOptionsMenu(true); // this fragment has menu items to display

        // get reference to the DoodleView
        doodleView = (DoodleView) view.findViewById(R.id.doodleView);

        // initialize acceleration values
        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;
        return view;
    }

    // start listening for sensor events
    @Override
    public void onResume() {
        super.onResume();
        enableAccelerometerListening(); // listen for shake event
    }

    // enable listening for accelerometer events
    private void enableAccelerometerListening() {
        // get the SensorManager
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(
                        Context.SENSOR_SERVICE);

        // register to listen for accelerometer events
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    // stop listening for accelerometer events
    @Override
    public void onPause() {
        super.onPause();
        disableAccelerometerListening(); // stop listening for shake
    }

    // disable listening for accelerometer events
    private void disableAccelerometerListening() {
        // get the SensorManager
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(
                        Context.SENSOR_SERVICE);

        // stop listening for accelerometer events
        sensorManager.unregisterListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    // event handler for accelerometer events
    private final SensorEventListener sensorEventListener =
            new SensorEventListener() {
                // use accelerometer to determine whether user shook device
                @Override
                public void onSensorChanged(SensorEvent event) {
                    // ensure that other dialogs are not displayed
                    if (!dialogOnScreen) {
                        // get x, y, and z values for the SensorEvent
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        // save previous acceleration value
                        lastAcceleration = currentAcceleration;

                        // calculate the current acceleration
                        currentAcceleration = x * x + y * y + z * z;

                        // calculate the change in acceleration
                        acceleration = currentAcceleration *
                                (currentAcceleration - lastAcceleration);

                        // if the acceleration is above a certain threshold
                        //here is what happens when the device is shook
                        //ACCELERATION_THRESHOLD
                        if (acceleration > 50000)
                            changeColor(doodleView.getDrawingColor());
                    }
                }

                // required method of interface SensorEventListener
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            };

    // confirm whether image should be erase
    private void confirmErase() {
        EraseImageDialogFragment fragment = new EraseImageDialogFragment();
        fragment.show(getFragmentManager(), "erase dialog");
    }

    public void changeColor(int currentColor){
        if(currentColor == doodleView.getDrawingColor1()) {
            doodleView.setDrawingColor(doodleView.getDrawingColor2());
            Toast.makeText(getContext(), "Color changed to Player2", Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentColor == doodleView.getDrawingColor2()) {
            doodleView.setDrawingColor(doodleView.getDrawingColor1());
            Toast.makeText(getContext(), "Color changed to Player1", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    // displays the fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.doodle_fragment_menu, menu);
    }

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // switch based on the MenuItem id
        switch (item.getItemId()) {
            case R.id.color:
                ColorDialogFragment colorDialog = new ColorDialogFragment();
                colorDialog.show(getFragmentManager(), "color dialog");
                return true; // consume the menu event
            case R.id.line_width:
                LineWidthDialogFragment widthDialog =
                        new LineWidthDialogFragment();
                widthDialog.show(getFragmentManager(), "line width dialog");
                return true; // consume the menu event
            case R.id.delete_drawing:
                confirmErase(); // confirm before erasing image
                return true; // consume the menu event

            case R.id.picture:
                PictureDialogFragment pictureDialog = new PictureDialogFragment();
                pictureDialog.show(getFragmentManager(), "Picture Dialog");
                return true;
            case R.id.change_game:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.doodleFragment, new MainActivityFragment(), "Quiz Fragment");
                ft.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    // requests for the permission needed for saving the image if

    // called by the system when the user either grants or denies the
    // permission for saving an image
   // @Override
   /* public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        // switch chooses appropriate action based on which feature
        // requested permission
        switch (requestCode) {
            case SAVE_IMAGE_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    doodleView.saveImage(); // save the image
                return;
        }
    }
    */

    // returns the DoodleView
    public DoodleView getDoodleView() {
        return doodleView;
    }

    // indicates whether a dialog is displayed
    public void setDialogOnScreen(boolean visible) {
        dialogOnScreen = visible;
    }
}
