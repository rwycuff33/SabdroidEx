package com.sabdroidex.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.sabdroidex.R;
import com.sabdroidex.utils.RawReader;


public class NewVersionDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        OnClickListener clickListener = new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        };
        
        String versionInfo = RawReader.readTextRaw(getActivity().getApplicationContext(), R.raw.version_info);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.new_version);
        builder.setPositiveButton(android.R.string.ok, clickListener);
        builder.setMessage(versionInfo);
                
        return builder.create();
    }
}
