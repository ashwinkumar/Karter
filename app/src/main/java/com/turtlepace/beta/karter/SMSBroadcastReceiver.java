package com.turtlepace.beta.karter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by akumar on 08/08/15.
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();
    final String CONTACT_NUMBER = "8554977473"; //ACP number
    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();


                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + phoneNumber + "; message: " + message);

                    if(phoneNumber.equals(CONTACT_NUMBER)) {
                        Intent in = new Intent("SmsMessage.intent.MAIN").
                                putExtra("get_msg", message);

                        //You can place your check conditions here(on the SMS or the sender)
                        //and then send another broadcast
                        context.sendBroadcast(in);
                    }

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }
}
