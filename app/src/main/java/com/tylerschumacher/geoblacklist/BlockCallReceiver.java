package com.tylerschumacher.geoblacklist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

public class BlockCallReceiver extends BroadcastReceiver{

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Get instance of the Main Activity and blocked numbers
        MainActivity mainActivity = new MainActivity();
        String number = mainActivity.getInstance().getNumberOne();
        String number2 = mainActivity.getInstance().getNumberTwo();
        String number3 = mainActivity.getInstance().getNumberThree();
        String number4 = mainActivity.getInstance().getNumberFour();
        String number5 = mainActivity.getInstance().getNumberFive();

        //Check if the bundle is null
        Bundle myBundle = intent.getExtras();
        if (myBundle != null)
        {
            System.out.println("--------Not null-----");
            //Check the phone state
            try
            {
                if (intent.getAction().equals("android.intent.action.PHONE_STATE"))
                {
                    String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                    System.out.println("--------in state-----");
                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
                    {
                        // Incoming call
                        String incomingNumber =intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        System.out.println("--------------my number---------"+incomingNumber);

                        // Check if the incoming number is blocked & get the TelephonyManager.
                        if (incomingNumber.equals(number) || incomingNumber.equals(number2) || incomingNumber.equals(number3) || incomingNumber.equals(number4) || incomingNumber.equals(number5)) {
                            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                            // Get the getITelephony() method
                            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
                            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

                            // Ignore that the method is supposed to be private
                            methodGetITelephony.setAccessible(true);

                            // Invoke getITelephony() to get the ITelephony interface
                            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

                            // Get the endCall method from ITelephony
                            Class<?> telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
                            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

                            // Invoke endCall()
                            methodEndCall.invoke(telephonyInterface);
                        }

                    }

                }
            }
            catch (Exception ex)
            { // In case anything goes wrong
                ex.printStackTrace();
            }
        }
    }
}