package com.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.happening.happening.R;

/**
 * Created by Ravish on 10/24/2014.
 */
public class Dialog {

    public static android.app.AlertDialog createSimpleDialog(String title, String message, Context context) {

        android.app.AlertDialog.Builder builder = newDialog(context, title, message);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();;
        return dialog;


    }


    private static android.app.AlertDialog.Builder newDialog(Context context, String title, String message) {

        android.app.AlertDialog.Builder dialogBuilder =  new android.app.AlertDialog.Builder(context);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);

        return dialogBuilder;
    }

 }
