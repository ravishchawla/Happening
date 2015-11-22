package com.facebook;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.WebDialog;
import com.happening.happening.EventsActivity;
import com.happening.happening.R;

public class FriendPickerActivity extends FragmentActivity {

    public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
    private FriendPickerFragment friendPickerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_friend_picker);

        Bundle args = getIntent().getExtras();
        FragmentManager manager = getSupportFragmentManager();
        Uri intentURI = getIntent().getData();
        Fragment fragmentToShow = null;



        if(FRIEND_PICKER.equals(intentURI)) {

            if(savedInstanceState == null) {
                friendPickerFragment = new FriendPickerFragment(args);
            }
            else {
                friendPickerFragment = (FriendPickerFragment)manager.findFragmentById(R.id.picker_fragment);
            }

            friendPickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
                @Override
            public void onError(PickerFragment<?> fragment, FacebookException error) {
                   FriendPickerActivity.this.onError(error);
                }
            });

            friendPickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener(){
                @Override
            public void onDoneButtonClicked(PickerFragment<?> fragment) {
                    finishActivity();
                }
            });

            fragmentToShow = friendPickerFragment;
        }
        else {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        manager.beginTransaction().replace(R.id.picker_fragment, fragmentToShow).commit();
    }

    private void onError(Exception exe) {
        onError(exe.getLocalizedMessage(), false);
}

    private void onError(String error, final boolean finishActivity) {

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Error").setMessage(error).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(finishActivity)
                    finishActivity();
            }
        }).create();

        dialog.show();
    }

    private void sendRequestDialog() {
        Bundle params = new Bundle();
        params.putString("message", "Learn how to make your Android apps social");

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(FriendPickerActivity.this,
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error != null) {
                            if (error instanceof FacebookOperationCanceledException) {
                                Toast.makeText(getApplicationContext().getApplicationContext(),
                                        "Request cancelled",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext().getApplicationContext(),
                                        "Network Error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            final String requestId = values.getString("request");
                            if (requestId != null) {
                                Toast.makeText(getApplicationContext().getApplicationContext(),
                                        "Request sent",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext().getApplicationContext(),
                                        "Request cancelled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                })
                .build();
        requestsDialog.show();
    }


    private void finishActivity() {

        if(FRIEND_PICKER.equals(getIntent().getData())) {
            if(friendPickerFragment != null) {
                Log.wtf("service friends", friendPickerFragment.getSelection().toString());
                EventsActivity.getInstance().setFriendsList(friendPickerFragment.getSelection());
            }
        }






        setResult(RESULT_OK, null);
        finish();
    }

    @Override
    protected void onStart() {

            super.onStart();
            if(FRIEND_PICKER.equals(getIntent().getData())) {
                try {
                    friendPickerFragment.loadData(false);

                }
            catch(Exception exe) {
                onError(exe);
            }
        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_picker, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_friend_picker, container, false);
            return rootView;
        }
    }
}
