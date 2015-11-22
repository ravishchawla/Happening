package com.components;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FriendPickerActivity;
import com.happening.happening.EventsActivity;
import com.happening.happening.R;
import com.node.App;

import org.json.JSONObject;

import java.util.List;
import java.util.Random;

/**
 * Created by Ravish on 10/29/2014.
 */
public class EventsAdapter extends ArrayAdapter<JSONObject>{

    private Context _context;
    private List<JSONObject> jsonData;

    public EventsAdapter(Context _context,  List<JSONObject> json) {
        super(_context, R.layout.events_list_item, json);
        this._context = _context;
        this.jsonData = json;
    }

    private static int[] modiArray = {R.drawable.modi, R.drawable.modi2, R.drawable.modi3};

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
                convertView = LayoutInflater.from(_context).inflate(R.layout.events_list_item, null);
    }
    ImageButton info = (ImageButton)convertView.findViewById(R.id.imageButtonInfo);
    info.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new EventsCreationDialog(EventsActivity.getInstance(), position);
                    }
                }

        );

        final ImageButton like = (ImageButton)convertView.findViewById(R.id.imageButtonLike);
        like.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(like.getTag().equals("1"))
                        {
                            like.setImageDrawable(App.get().getResources().getDrawable(R.drawable.ic_action_good_blue));
                            like.setTag("0");
                        }
                        else {
                            like.setImageDrawable(App.get().getResources().getDrawable(R.drawable.ic_action_good));
                            like.setTag("1");
                        }
                    }
                }
        );

        String mapLoc = "North Ave NW, Atlanta, GA 30332";
        String title = "Event Name";
        String description = "Description";
        String id = "id";
        try {
            final JSONObject json = jsonData.get(position);
            ImageView image = (ImageView)convertView.findViewById(R.id.eventsModi);
            //URL newurl = new URL("http://lorempixel.com/80/80/");
            //Bitmap bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            //image.setImageBitmap(bitmap);
            ((TextView) convertView.findViewById(R.id.eventsHeader)).setText(json.getString("eventname"));
            ((TextView) convertView.findViewById(R.id.eventsLocation)).setText("@" + json.getString("location"));
            mapLoc = json.getString("location");
            title = json.getString("eventname");
            description = json.getString("description");
            id = json.getString("_id");
        }

        catch (Exception exe) {
            Log.wtf("service events adapter", exe.getMessage());
        }


        final ImageButton location = (ImageButton)convertView.findViewById(R.id.imageButtonLocation);
        final String _mapLoc = mapLoc;
        final String _title = title;
        final String _description = description;
        final String _id = id;

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //souurce: http://stackoverflow.com/questions/2662531/launching-google-maps-directions-via-an-intent-on-android

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + _mapLoc));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.get().startActivity(intent);
            }
        });

        final ImageButton event = (ImageButton)convertView.findViewById(R.id.imageButtonEvent);
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.TITLE, _title);
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, _mapLoc);
                intent.putExtra(CalendarContract.Events.DESCRIPTION, "Happening");
                App.get().startActivity(intent);
            }
        });

       final ImageButton share = (ImageButton)convertView.findViewById(R.id.imageButtonShare);
       share.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                EventsActivity.getInstance().startFriendPickerActivity(_title, _description, _mapLoc, _id);
           }
       });

        Random gen = new Random();

        final ImageView modiPic = (ImageView)convertView.findViewById(R.id.eventsModi);
        modiPic.setImageDrawable(App.get().getResources().getDrawable(modiArray[gen.nextInt(modiArray.length)]));

        return convertView;
    }




}
