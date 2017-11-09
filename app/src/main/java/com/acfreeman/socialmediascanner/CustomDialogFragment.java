package com.acfreeman.socialmediascanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Andrew on 9/25/2017.
 */

public class CustomDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    String title;
    String uri;
    String name;
    String action;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mArgs = getArguments();
        title = mArgs.getString("dialog_title");
        uri = mArgs.getString("uri");
        name = mArgs.getString("name");
        action = mArgs.getString("action");
        if(action!=null)
            setButtons();
    }

    String positive;
    String negative;

    private void setButtons() {
        switch (action){
            case "socialAdd":
                positive = "Yes";
                negative = "No";
                break;
            case "delete":
                positive = "Ok";
                negative = "Cancel";
                break;
            case "saveContact":
                positive = "Ok";
                negative = "Cancel";
                break;
            case "appInstall":
                positive = "Install";
                negative = "Not now";
                break;
            case "acknowledge":
                positive = "Ok";
                break;
            default:
                positive = "Ok";
                negative = "Cancel";
                break;
        }
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(title)
                    .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogPositiveClick(CustomDialogFragment.this);
                        }
                    })
                    .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogNegativeClick(CustomDialogFragment.this);
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

