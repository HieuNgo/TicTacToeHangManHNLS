package com.hn.tictactoehangmanhnls;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.widget.AdapterView;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;
import android.util.Log;

public class PictureDialogFragment extends DialogFragment {
    private Spinner pictureSpinner;
    private String picture= "blank";
    private AssetManager assetManager;

    public Dialog onCreateDialog(Bundle bundle) {
        // create the dialog
        // load image as Drawable
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        View pictureDialogView =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_picture, null);
        builder.setView(pictureDialogView); // add GUI to dialog

        // set the AlertDialog's message
        builder.setTitle(R.string.title_picture_dialog);


        // configure widthSeekBar
        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        pictureSpinner = (Spinner) pictureDialogView.findViewById(
                R.id.pictureSpinner);
        pictureSpinner.setOnItemSelectedListener(pictureChanged);

        // add Set Line Width Button

        builder.setPositiveButton(R.string.button_picture,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDoodleFragment().getDoodleView().clear();
                        try {
                            setPicture();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
        );


        return builder.create(); // return dialog
    }

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
        fragment.getTag();
        Toast.makeText(getContext(), fragment.getTag(), Toast.LENGTH_SHORT).show();


        if (fragment != null)
            fragment.setDialogOnScreen(false);
    }

    private final OnItemSelectedListener pictureChanged = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            picture = parent.getItemAtPosition(position).toString();
            Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener : " + picture,
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    public void setPicture() throws IOException {
        AssetManager assets = getActivity().getAssets();

        Log.d("ADebugTag", "Value: " + picture);

        if (picture.equals("Three")){
            InputStream open = assets.open("coloringPhotos/three.png");
            Bitmap b = BitmapFactory.decodeStream(open);
            b.setDensity(Bitmap.DENSITY_NONE);
            b=Bitmap.createScaledBitmap(b, 1000, 1400, false);
            getDoodleFragment().getDoodleView().setImage(b);
            Toast.makeText(getContext(), "Picture has changed to a 3x3", Toast.LENGTH_SHORT).show();


        }
        else if (picture.equals("Four")){
            InputStream open = assets.open("coloringPhotos/four.png");
            Bitmap b = BitmapFactory.decodeStream(open);
            b=Bitmap.createScaledBitmap(b, 1000, 1400, false);
            b.setDensity(Bitmap.DENSITY_NONE);
            getDoodleFragment().getDoodleView().setImage(b);
            Toast.makeText(getContext(), "Picture has been changed to a 4x4", Toast.LENGTH_SHORT).show();

        }
        if (picture.equals("Five")){
            InputStream open = assets.open("coloringPhotos/five.png");
            Bitmap b = BitmapFactory.decodeStream(open);
            b=Bitmap.createScaledBitmap(b, 1000, 1400, false);
            b.setDensity(Bitmap.DENSITY_NONE);
            getDoodleFragment().getDoodleView().setImage(b);
            Toast.makeText(getContext(), "Picture has been changed to a 5x5", Toast.LENGTH_SHORT).show();


        }


    }
}