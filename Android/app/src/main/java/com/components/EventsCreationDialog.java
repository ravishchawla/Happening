package com.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.happening.happening.EventsActivity;
import com.happening.happening.R;
import com.node.App;
import com.node.Service;

import org.json.JSONArray;

/**
 * Created by Ravish on 10/30/2014.
 */
public class EventsCreationDialog implements Respondable<Object>{
    private EventsActivity context;

    public  EventsCreationDialog(EventsActivity _context) {

        context = _context;
        final View view = getView(context, R.layout.activity_add_event);

        AlertDialog dialog;
                dialog = createAddDialog(view);
        dialog.show();
    }

    public EventsCreationDialog(EventsActivity _context, int pos) {

        context = _context;
        final View view = getView(context, R.layout.activity_eventinfo);

        AlertDialog dialog;
        dialog = createInfoDialog(view, pos);
        dialog.show();
    }

    private AlertDialog createAddDialog(final View view) {

        AlertDialog.Builder builder = createDialog(view);



    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            addEvent(view);
        }
    });

    return builder.create();

}

    private AlertDialog createInfoDialog(final View view, final int pos) {
        AlertDialog.Builder builder = createDialog(view);
        if(!EventsActivity.isCheckedOnAllEvents)
        {
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    delete(view, pos);
                }
            });
        }

        try {

            ((TextView) view.findViewById(R.id.infoEventName)).setText(EventsActivity.jsonList.get(pos).getString("eventname"));
            ((TextView) view.findViewById(R.id.infoEventDate)).setText(EventsActivity.jsonList.get(pos).getString("date"));
            ((TextView) view.findViewById(R.id.infoEventLocation)).setText(EventsActivity.jsonList.get(pos).getString("location"));
            ((TextView) view.findViewById(R.id.infoventTime)).setText(EventsActivity.jsonList.get(pos).getString("time"));

                    }
        catch(Exception exe) {

        }
        return builder.create();
    }

    private AlertDialog.Builder createDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setCancelable(false);

        return builder;

    }


    private View getView(Activity context, int layout) {

        LayoutInflater inflater = context.getLayoutInflater();
        return inflater.inflate(layout, null);

    }


    private void addEvent(View view) {


        String name = ((EditText)view.findViewById(R.id.addEventName)).getText().toString();
        String location = ((EditText)view.findViewById(R.id.addEventLocation)).getText().toString();
        String date = ((EditText)view.findViewById(R.id.addventDate)).getText().toString();
        String time = ((EditText)view.findViewById(R.id.addventTime)).getText().toString();
        Boolean timeS = ((Switch)view.findViewById(R.id.addEventSwitch)).isChecked();

        if(timeS) {
            time += " PM";
        }
        else
            time += " AM";

        if(name.length() == 0 || location.length() == 0 || date.length() == 0 || time.length() == 0) {
            Toast.makeText(context, "Fields Incomplete", Toast.LENGTH_LONG).show();
        }
        else {
                Service.addEvent(name, location, date, time, this);
                Toast.makeText(App.get(), "The Event has been successfully added", Toast.LENGTH_LONG).show();
        }
    }

    private void delete(View view, int pos) {
        try {
            String id =  EventsActivity.jsonList.get(pos).getString("_id");
            Service.deleteEvent(id, this);
            Toast.makeText(App.get(), "The Event has been successfully deleted", Toast.LENGTH_LONG).show();
        }
        catch(Exception exe) {
            Log.wtf("service exception", exe);
            Toast.makeText(App.get(), "The Event was not deleted", Toast.LENGTH_LONG).show();
        }
    }

    public Object respond(JSONArray json, int requestCode) {

        EventsActivity.getInstance().getData();
        return true;
    }

}
