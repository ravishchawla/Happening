package com.happening.happening;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.components.Dialog;
import com.components.Respondable;
import com.node.Service;

import org.json.JSONArray;


public class createAccount extends Activity implements Respondable<Object> {

    Button signUp;
    EditText email, userName, passWord, passWordConfirm;
    createAccount instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        instance = this;

        signUp = (Button) findViewById(R.id.signUpSubmitButton);
        email = (EditText) findViewById(R.id.addEventName);
        userName = (EditText) findViewById(R.id.addEventLocation);
        passWord = (EditText) findViewById(R.id.addventDate);
        passWordConfirm = (EditText) findViewById(R.id.addventTime);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _email = email.getText().toString();
                String _username = userName.getText().toString();
                String _passwd = passWord.getText().toString();
                String _passwdConfirm = passWordConfirm.getText().toString();

                if (!_email.matches(".+@[a-z]+[.](com|edu|org|gov|batman)")) {
                    AlertDialog dialog = Dialog.createSimpleDialog("Wrong email", "The email format is incorrect", createAccount.this);
                    return;
                }

                if (_email.length() == 0 || _username.length() == 0 || _passwd.length() == 0 || _passwdConfirm.length() == 0) {
                    AlertDialog dialog = Dialog.createSimpleDialog("Missing Fields", "Please enter all information", createAccount.this);
                    return;
                }

                if (!(_passwd.equals(_passwdConfirm))) {
                    AlertDialog dialog = Dialog.createSimpleDialog("", "Passwords entered do not match", createAccount.this);
                    return;
                }
                Service.signupUser(_username, _email, _passwd, getInstance());
            }
        });
    }

    private createAccount getInstance() {
        if (instance == null) {
            instance = this;
        }

        return instance;

    }

    public Object respond(JSONArray json, int requestCode) {

        AlertDialog.Builder builder = new AlertDialog.Builder(createAccount.this);
        AlertDialog dialog = builder.setTitle("Registration Complete").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (userName == null) {
                    userName = (EditText) findViewById(R.id.addEventLocation);
                }

                if (passWord == null) {
                    passWord = (EditText) findViewById(R.id.addventDate);
                }

                Boolean verify = Service.loginUser(userName.getText().toString(), passWord.getText().toString(), LoginActivity.getInstance());
            }
        }).create();

        dialog.show();


        return null;

    }
}
