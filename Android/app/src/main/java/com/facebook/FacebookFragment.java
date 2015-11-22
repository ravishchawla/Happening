package com.facebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.components.Dialog;
import com.components.Navigation;
import com.components.Respondable;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.happening.happening.*;
import com.happening.happening.LoginActivity;
import com.node.App;
import com.node.Service;
import com.node.Storage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FacebookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FacebookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FacebookFragment extends Fragment implements Respondable<Object> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static FacebookFragment instance;
    private ProgressDialog progressIndicator;

    private OnFragmentInteractionListener mListener;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
        }
    };

    private UiLifecycleHelper uiLifecycleHelper;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FacebookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FacebookFragment newInstance(String param1, String param2) {
        FacebookFragment fragment = new FacebookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        instance = fragment;
        return fragment;
    }

    public FacebookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        uiLifecycleHelper = new UiLifecycleHelper(getActivity(), callback);
        uiLifecycleHelper.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.activity_login, container, false);

        LoginButton button = (LoginButton)view.findViewById(R.id.facebookLoginButton);
        button.setFragment(this);
        button.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "user_birthday", "user_education_history", "user_hometown", "user_photos", "user_relationships", "user_relationship_details", "user_work_history"));

        Button login = (Button) view.findViewById(R.id.loginButton);
        TextView signUp = (TextView) view.findViewById(R.id.loginEmailButton);
        TextView skip = (TextView)view.findViewById(R.id.loginSkip);



        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView user = (EditText) view.findViewById(R.id.loginUsername);
                TextView password = (EditText) view.findViewById(R.id.loginPassword);

                String userName = user.getText().toString();
                String passWord = password.getText().toString();

                if (userName.length() == 0 || passWord.length() == 0) {
                    AlertDialog dialog = Dialog.createSimpleDialog("Missing fields", "Please enter your Username or Password", LoginActivity.getInstance());
                    return;
                }
                LoginActivity.getInstance().progressIndicator = ProgressDialog.show(getActivity(), "Please wait", "Loging in...", true);
                LoginActivity.getInstance().progressIndicator.setCancelable(false);
                Boolean verify = Service.loginUser(userName, passWord, LoginActivity.getInstance());
                if (verify == true) {

                } else {
                    //AlertDialog dialog = Dialog.createSimpleDialog("Login", "Login was unsuccessful", getApplicationContext());

                    // dialog.show();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent events = new Intent(App.get(), createAccount.class);
                startActivity(events);


            }
        });


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent events = new Intent(App.get(), EventsActivity.class);
                events.putExtra("skip", true);
                startActivity(events);
            }
        });


        return view;
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exe) {
        if(state.isOpened()) {
            Log.wtf("facebook service", "Logged in");
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if(response != null) {
                        try {
                            String email = (String)user.getProperty("email");

                            getInstance().progressIndicator = ProgressDialog.show(getActivity(), "Please wait", "Loging in...", true);
                            getInstance().progressIndicator.setCancelable(false);


                            Storage.get().addKeyValue("facebookAccessToken", session.getAccessToken());
                            Service.loginWithFacebook("facebook:" + email, getInstance());
                        }
                        catch (Exception exe) {
                            Toast.makeText(App.get(), exe.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }).executeAsync();

        }
        else {
            Log.wtf("facebook service", "logged out");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public FacebookFragment getInstance() {
        if(instance == null) {
            instance = new FacebookFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();


        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiLifecycleHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiLifecycleHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiLifecycleHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiLifecycleHelper.onSaveInstanceState(outState);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public Object respond(JSONArray jsonArr, int requestCode) {
        try {
            JSONObject json = jsonArr.getJSONObject(0);

            if(json.has("msg")) {
                throw new Exception("Invalid user found");
            }

            Service.setSessionId(json.getString("token"));
            Service.setUsername(json.getString("username"));
            //Transition to events activity
            getInstance().progressIndicator.dismiss();
            Navigation.get().navigateLogin(com.happening.happening.LoginActivity.getInstance(), null);
        }

        catch(Exception exe) {
            Toast.makeText(App.get(), "Facebook Login error", Toast.LENGTH_LONG).show();
            Log.wtf("service fragment ", exe.toString());
        }
        return null;
    }

}
