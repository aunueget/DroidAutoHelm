package com.example.bluedrive;

/**
 * Created with IntelliJ IDEA.
 * User: dell
 * Date: 11/6/13
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.*;

import java.util.Set;

public class SettingsActivity extends Activity {
    // Debugging
    private static final String TAG = "SettingsActivity";
    private static final boolean D = true;
    private EditText tolerance;
    private EditText pid2Motor;
    private EditText autoTuneNoise;
    private EditText dervSmooth;
    private EditText compassSmooth;
    private CheckBox useGPS;
    private CheckBox useTolerance;
    private BluetoothChatService blueComm;
    private Button exitSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState=savedInstanceState;
        blueComm=((BlueHelmApp)this.getApplication()).getBlueComm();
        // Setup the window
        //        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.settings);
        exitSettings = (Button)findViewById(R.id.exitSettings);
        dervSmooth = (EditText)findViewById(R.id.derivativeSmooth);
        tolerance = (EditText)findViewById(R.id.toleranceValue);
        pid2Motor = (EditText)findViewById(R.id.PID2MI);
        autoTuneNoise = (EditText)findViewById(R.id.noise4Tuner);
        useGPS= (CheckBox)findViewById(R.id.useGPSCheckBox);
        useTolerance= (CheckBox)findViewById(R.id.UseToleranceCheckBox);
        compassSmooth = (EditText)findViewById(R.id.compassSmooth);

        exitSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        tolerance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if(tolerance.getText().length()>3 || !isNumeric(tolerance.getText().toString())){
                        Toast.makeText(tolerance.getContext(), "Tolerance value is a 3 digit positive number", Toast.LENGTH_SHORT)
                                .show();
                        tolerance.setText("");
                    }
                    else if(Integer.parseInt(tolerance.getText().toString())<0){
                        Toast.makeText(tolerance.getContext(), "Tolerance value is a 3 digit positive number", Toast.LENGTH_SHORT)
                                .show();
                        tolerance.setText("");
                    }
                }
            }
        });
        pid2Motor.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if(pid2Motor.getText().length()>3 || !isNumeric(pid2Motor.getText().toString())){
                        Toast.makeText(pid2Motor.getContext(), "Pid2Motor value is a 3 digit positive number", Toast.LENGTH_SHORT)
                                .show();
                        pid2Motor.setText("");
                    }
                    else if(Integer.parseInt(pid2Motor.getText().toString())<0){
                        Toast.makeText(pid2Motor.getContext(), "Pid2Motor value is a 3 digit positive number", Toast.LENGTH_SHORT)
                                .show();
                        pid2Motor.setText("");
                    }
                    else{
                        if(blueComm!=null && pid2Motor.getText().length()>0){
                            blueComm.sendMessage("%M"+pid2Motor.getText()+";",(Activity)pid2Motor.getRootView().getContext());
                        }
                    }
                }
                //do job here owhen Edittext lose focus
            }
        });
        autoTuneNoise.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (autoTuneNoise.getText().length() > 3 || !isNumeric(autoTuneNoise.getText().toString())) {
                        Toast.makeText(autoTuneNoise.getContext(), "AutoTuneNoise value is a 3 digit positive number", Toast.LENGTH_SHORT)
                                .show();
                        autoTuneNoise.setText("");
                    } else if (Integer.parseInt(autoTuneNoise.getText().toString()) < 0) {
                        Toast.makeText(autoTuneNoise.getContext(), "AutoTuneNoise value is a 3 digit positive number", Toast.LENGTH_SHORT)
                                .show();
                        autoTuneNoise.setText("");
                    } else {
                        if (blueComm != null && autoTuneNoise.getText().length() > 0) {
                            blueComm.sendMessage("%N" + autoTuneNoise.getText() + ";", (Activity) autoTuneNoise.getRootView().getContext());
                        }
                    }
                }
            }
            //do job here owhen Edittext lose focus
        });
        useGPS.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View checkBoxView, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    ((CheckBox)checkBoxView).setChecked(!((CheckBox)checkBoxView).isChecked());
                    if (((CheckBox)checkBoxView).isChecked()) {
                        Toast.makeText(checkBoxView.getContext(), "UseGPS checked", Toast.LENGTH_SHORT)
                                .show();
                        //TODO: send message to micro controller
                        //TODO: set micro controller to respond to message with a toast on android
                    }
                    else{
                        Toast.makeText(checkBoxView.getContext(), "UseGPS Unchecked", Toast.LENGTH_SHORT)
                                .show();
                    }
                    return true;
                }
                return false;
            }
        });
        useTolerance.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View checkBoxView, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    ((CheckBox)checkBoxView).setChecked(!((CheckBox)checkBoxView).isChecked());
                    ((CheckBox)checkBoxView).clearAnimation();

                    if (((CheckBox)checkBoxView).isChecked()) {
                        Toast.makeText(checkBoxView.getContext(), "Tolerance checked", Toast.LENGTH_SHORT)
                                .show();
                        //TODO: send message to micro controller
                        //TODO: set micro controller to respond to message with a toast on android
                        if(blueComm!=null && tolerance.getText().length()>0){
                            blueComm.sendMessage("%T"+tolerance.getText()+";",(Activity)tolerance.getRootView().getContext());
                        }
                    }
                    else{
                        if(blueComm!=null){
                            blueComm.sendMessage("%T0;",(Activity)useTolerance.getRootView().getContext());
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        compassSmooth.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if(compassSmooth.getText().length()>3 || !isNumeric(compassSmooth.getText().toString())){
                        Toast.makeText(compassSmooth.getContext(), "compassSmooth value is a 3 digit positive number", Toast.LENGTH_SHORT)
                                .show();
                        compassSmooth.setText("");
                    }
                    else if(Integer.parseInt(compassSmooth.getText().toString())<0){
                        Toast.makeText(compassSmooth.getContext(), "compassSmooth value is a 3 digit positive number", Toast.LENGTH_SHORT)
                                .show();
                        compassSmooth.setText("");
                    }
                    else{
                        if(blueComm!=null && compassSmooth.getText().length()>0){
                            blueComm.sendMessage("%C"+compassSmooth.getText()+";",(Activity)compassSmooth.getRootView().getContext());
                        }
                    }
                }
            }
            //do job here owhen Edittext lose focus
        });
        dervSmooth.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if(dervSmooth.getText().length()>3 || !isNumeric(dervSmooth.getText().toString())){
                        Toast.makeText(dervSmooth.getContext(), "dervSmooth value is a 3 digit positive number", Toast.LENGTH_SHORT)
                                .show();
                        dervSmooth.setText("");
                    }
                    else if(Integer.parseInt(dervSmooth.getText().toString())<0){
                        Toast.makeText(dervSmooth.getContext(), "dervSmooth value is a 3 digit positive number", Toast.LENGTH_SHORT)
                                .show();
                        dervSmooth.setText("");
                    }
                    else{
                        if(blueComm!=null && dervSmooth.getText().length()>0){
                            blueComm.sendMessage("%D"+dervSmooth.getText()+";",(Activity)dervSmooth.getRootView().getContext());
                        }
                    }
                }
            }
            //do job here owhen Edittext lose focus
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

//    // The on-click listener for all devices in the ListViews
//    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
//        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
//
//            // Get the device MAC address, which is the last 17 chars in the View
//            String info = ((TextView) v).getText().toString();
//            String address = info.substring(info.length() - 17);
//
//            // Create the result Intent and include the MAC address
//            Intent intent = new Intent();
//            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
//
//            // Set result and finish this Activity
//            setResult(Activity.RESULT_OK, intent);
//            finish();
//        }
//    };

    public static boolean isNumeric(String str)
    {
        try
        {
            int integerTest = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
