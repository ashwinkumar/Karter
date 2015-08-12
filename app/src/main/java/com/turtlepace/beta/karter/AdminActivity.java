package com.turtlepace.beta.karter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by akumar on 08/08/15.
 */
public class AdminActivity extends Activity {
    private Button mButton;
    private EditText mTextId;
    private EditText mTextPassword;
    private DBHelper myDb;
    private final static String DRIVER_ID ="com.turtlepace.beta.karter.DRIVER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DBHelper(this);
        int numberofRows = myDb.numberOfRows();
        if (numberofRows == 1) {
            callActivity();
            // Call next activity
        } else if (numberofRows > 1) {
            myDb.deleteTable();
        } else {
            mButton = (Button) findViewById(R.id.registerbutton);
            mTextId = (EditText) findViewById(R.id.editTextID);

            mTextPassword = (EditText) findViewById(R.id.editTextPassword);
            mButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!myDb.validatePassword(mTextPassword.getText().toString())) {
                        new AlertDialog.Builder(AdminActivity.this)
                                .setTitle("ERROR")
                                .setMessage("Invalid User ID or Password")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        myDb.insertUser(mTextId.getText().toString(), mTextPassword.getText().toString());
                        callActivity();
                    }

                    // TODO Auto-generated method stub
                }
            });
        }
    }

    private void callActivity() {
        Intent intent = new Intent(this,StaticInfoActivity.class);
        intent.putExtra(DRIVER_ID,  myDb.getUserId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blank, menu);
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

}
