package com.hn.tictactoehangmanhnls;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

// class for the Select Color dialog
public class ColorDialogFragment extends DialogFragment {
    private SeekBar alphaSeekBar1;
    private SeekBar redSeekBar1;
    private SeekBar greenSeekBar1;
    private SeekBar blueSeekBar1;
    private SeekBar alphaSeekBar2;
    private SeekBar redSeekBar2;
    private SeekBar greenSeekBar2;
    private SeekBar blueSeekBar2;
    private View colorView1;
    private View colorView2;
    private int color1;
    private int color2;


    // create an AlertDialog and return it
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        // create dialog
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        View colorDialogView = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_color, null);
        builder.setView(colorDialogView); // add GUI to dialog

        // set the AlertDialog's message
        builder.setTitle(R.string.title_color_dialog);

        // get the color SeekBars and set their onChange listeners
        alphaSeekBar1 = (SeekBar) colorDialogView.findViewById(
                R.id.alphaSeekBar1);
        redSeekBar1 = (SeekBar) colorDialogView.findViewById(
                R.id.redSeekBar1);
        greenSeekBar1 = (SeekBar) colorDialogView.findViewById(
                R.id.greenSeekBar1);
        blueSeekBar1 = (SeekBar) colorDialogView.findViewById(
                R.id.blueSeekBar1);
        colorView1 = colorDialogView.findViewById(R.id.colorView1);

        // register SeekBar event listeners
        alphaSeekBar1.setOnSeekBarChangeListener(colorChangedListener);
        redSeekBar1.setOnSeekBarChangeListener(colorChangedListener);
        greenSeekBar1.setOnSeekBarChangeListener(colorChangedListener);
        blueSeekBar1.setOnSeekBarChangeListener(colorChangedListener);

        // use current drawing color to set SeekBar values
        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        color1 = doodleView.getDrawingColor();
        alphaSeekBar1.setProgress(Color.alpha(color1));
        redSeekBar1.setProgress(Color.red(color1));
        greenSeekBar1.setProgress(Color.green(color1));
        blueSeekBar1.setProgress(Color.blue(color1));

        // get the color SeekBars and set their onChange listeners
        alphaSeekBar2 = (SeekBar) colorDialogView.findViewById(
                R.id.alphaSeekBar2);
        redSeekBar2 = (SeekBar) colorDialogView.findViewById(
                R.id.redSeekBar2);
        greenSeekBar2 = (SeekBar) colorDialogView.findViewById(
                R.id.greenSeekBar2);
        blueSeekBar2 = (SeekBar) colorDialogView.findViewById(
                R.id.blueSeekBar2);
        colorView2 = colorDialogView.findViewById(R.id.colorView2);

        // register SeekBar event listeners
        alphaSeekBar2.setOnSeekBarChangeListener(colorChangedListener);
        redSeekBar2.setOnSeekBarChangeListener(colorChangedListener);
        greenSeekBar2.setOnSeekBarChangeListener(colorChangedListener);
        blueSeekBar2.setOnSeekBarChangeListener(colorChangedListener);

        // use current drawing color to set SeekBar values
        final DoodleView doodleView2 = getDoodleFragment().getDoodleView();
        color2 = doodleView2.getDrawingColor();
        alphaSeekBar2.setProgress(Color.alpha(color2));
        redSeekBar2.setProgress(Color.red(color2));
        greenSeekBar2.setProgress(Color.green(color2));
        blueSeekBar2.setProgress(Color.blue(color2));

        // add Set Color Button
        builder.setPositiveButton(R.string.button_set_color,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doodleView.setDrawingColor(color1);
                        doodleView.setDrawingColor1(color1);
                        doodleView.setDrawingColor2(color2);
                    }
                }
        );

        return builder.create(); // return dialog
    }

    public int getColor1(){
        return color1;
    }

    public int getColor2(){
        return color2;
    }

    // gets a reference to the DoodleFragment
    private DoodleFragment getDoodleFragment() {
        return (DoodleFragment) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }

    // tell DoodleFragment that dialog is now displayed
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       DoodleFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(true);
    }

    // tell DoodleFragment that dialog is no longer displayed
    @Override
    public void onDetach() {
        super.onDetach();
       DoodleFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(false);
    }

    // OnSeekBarChangeListener for the SeekBars in the color dialog
    private final OnSeekBarChangeListener colorChangedListener =
            new OnSeekBarChangeListener() {
                // display the updated color
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {

                    if (fromUser) { // user, not program, changed SeekBar progress
                        color1 = Color.argb(alphaSeekBar1.getProgress(),
                                redSeekBar1.getProgress(), greenSeekBar1.getProgress(),
                                blueSeekBar1.getProgress());
                        colorView1.setBackgroundColor(color1);

                        color2 = Color.argb(alphaSeekBar2.getProgress(),
                                redSeekBar2.getProgress(), greenSeekBar2.getProgress(),
                                blueSeekBar2.getProgress());
                        colorView2.setBackgroundColor(color2);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {} // required

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {} // required
            };
}
