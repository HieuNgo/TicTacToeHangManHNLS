// EraseImageDialogFragment.java
// Allows user to erase image
package com.hn.tictactoehangmanhnls;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

// class for the Erase Image dialog
public class EraseImageDialogFragment extends DialogFragment {
    // create an AlertDialog and return it
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        // set the AlertDialog's message
        builder.setMessage(R.string.message_erase);

        // add Erase Button
        builder.setPositiveButton(R.string.button_erase,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDoodleFragment().getDoodleView().clear(); // clear image
                    }
                }
        );

        // add cancel Button
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create(); // return dialog
    }

    // gets a reference to the TicTacToeFragment
    private TicTacToeFragment getDoodleFragment() {
        return (TicTacToeFragment) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }

    // tell TicTacToeFragment that dialog is now displayed
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        TicTacToeFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(true);
    }

    // tell TicTacToeFragment that dialog is no longer displayed
    @Override
    public void onDetach() {
        super.onDetach();
        TicTacToeFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(false);
    }
}
