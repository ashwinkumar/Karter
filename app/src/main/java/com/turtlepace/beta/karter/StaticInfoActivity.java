package com.turtlepace.beta.karter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


public class StaticInfoActivity extends Activity {
    private final static String DRIVER_ID ="com.turtlepace.beta.karter.DRIVER_ID";
    private TextView mDriverId;
    private Switch mSwitch;
    private boolean onlineStatus;
    private SMSBroadcastReceiver mSmsReceiver = new SMSBroadcastReceiver() {
         @Override
            public void onReceive(Context context, Intent intent) {
                if(!onlineStatus){
                    return;
                }
                String msg = intent.getStringExtra("get_msg");

                msg = msg.replace("\n", "");
                String sPickUp = msg.substring(msg.indexOf("(") + 1, msg.indexOf(")"));
                String sDropDown = msg.substring(msg.lastIndexOf("("), msg.lastIndexOf(")"));


                Intent i = new Intent();
                i.setClassName("com.turtlepace.beta.karter", "com.turtlepacStatice.beta.karter.StaticMapActivity");
                MyApplication myApp = MyApplication.getInstance();
                myApp.setPickUpLatLng(sPickUp);
                myApp.setDropLatLng(sDropDown);
                context.startActivity(i);
            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_info);
        String sDriverId;
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                sDriverId= null;
            } else {
                sDriverId= extras.getString(DRIVER_ID);
            }
        }
        else{
            sDriverId= (String) savedInstanceState.getSerializable(DRIVER_ID);
        }
        mDriverId =(TextView)findViewById(R.id.textViewDriverID);
        if(sDriverId!= null) {
            mDriverId.setText(sDriverId);
        }
        else{
            new AlertDialog.Builder(StaticInfoActivity.this)
                    .setTitle("FATAL")
                    .setMessage("Invalid Driver Details")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            System.exit(0);
        }
        mSwitch = (Switch)findViewById(R.id.switchStatus);
        mSwitch.setChecked(false);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    onlineStatus = true;
                } else {
                    onlineStatus =false;
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_static_info, menu);
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSmsReceiver);
    }
    @Override
    protected  void onResume(){

        super.onResume();

        if(onlineStatus) {
            IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");

            this.registerReceiver(mSmsReceiver, intentFilter);
        }
        else{
            this.unregisterReceiver(mSmsReceiver);
        }

    }

    @Override
    protected void onPause(){
        unregisterReceiver(mSmsReceiver);
        super.onPause();

    }
}
