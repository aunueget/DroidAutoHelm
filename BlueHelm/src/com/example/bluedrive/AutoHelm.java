package com.example.bluedrive;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class AutoHelm extends Activity {

    private static final String TAG = AutoHelm.class.getSimpleName();
	public Location mLocation;
	public static final String PREFS_NAME = "AutohelmPrefsFile";
	public static final int TACK_PORT = 1;
	public static final int TACK_STARBOARD = 2;
	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	private LocationListener locationListener;
	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	public boolean changeBTdevice;
	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 31;
	private static final int REQUEST_ENABLE_BT = 33;
    private static final int SETTINGS_CHANGED = 34;
	public HandsOnImageView myCompass;
	public String blueInString;
	public int headingCount;
	public String mLastBTDeviceaddress;
	public boolean startedReadingHeading;
	IncomingHandler mHandler;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter;
	// Name of the connected device
	private String mConnectedDeviceName = null;

    // Array adapter for the conversation thread
	// private ArrayAdapter<String> mConversationArrayAdapter;
	// Member object for the chat services
	private BluetoothChatService mChatService;
	private boolean autohelmOn;
	// try tack
	private boolean tackStarboard;
	private boolean tackPort;
	// sounds
	public MediaPlayer mp;
	private SharedPreferences settings;
	private boolean receivedMCDesiredHeading;
	private boolean receivedMCCurrentState;
    public static final char ON_BRIGHT=255;
    public static final char TEMP_SIGNAL_OFF_SETTING=120;
    public static final char TEMP_SHUTDOWN_OFF_SETTING=120;
    public static final char BAT_HIGH_OFF_SETTING=80;
    public static final char BAT_LOW_OFF_SETTING=80;
    public static final char CURRENT_LIMIT_OFF_SETTING=120;
    public static final char OVER_CURRENT_OFF_SETTING=100;
    public Timer timer;
    public batLowTask blTask;
    public batHighTask bhTask;
    public currentLimitTask clTask;
    public tempTask tTask;

    /** A handle to the thread that's actually running the animation. */
	// private CompassThread compassThread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_helm);
        mHandler = new IncomingHandler(this);
        timer = new Timer();
        batLowTask blTask = new batLowTask();
        batHighTask bhTask= new batHighTask();
        currentLimitTask clTask = new currentLimitTask();
        tempTask tTask= new tempTask();
        mp = MediaPlayer.create(this, R.raw.warning);
        myCompass = (HandsOnImageView) findViewById(R.id.imageView1);
        ((ProgressBar) findViewById(R.id.progressBar1))
                .setVisibility(View.INVISIBLE);
        System.out.println("Setting the view");
        myCompass.invalidate();
        ((ImageView)findViewById(R.id.tempSignal)).setAlpha((int)TEMP_SIGNAL_OFF_SETTING);
        ((ImageView)findViewById(R.id.tempShutdownSignal)).setAlpha((int)TEMP_SHUTDOWN_OFF_SETTING);
        ((ImageView)findViewById(R.id.batHighSignal)).setAlpha((int)BAT_HIGH_OFF_SETTING);
        ((ImageView)findViewById(R.id.batLowSignal)).setAlpha((int)BAT_LOW_OFF_SETTING);
        ((ImageView)findViewById(R.id.currentLimitSignal)).setAlpha((int)CURRENT_LIMIT_OFF_SETTING);
        ((ImageView)findViewById(R.id.overCurrentSignal)).setAlpha((int)OVER_CURRENT_OFF_SETTING);
        this.headingCount = 0;
        changeBTdevice = false;
        this.blueInString = "";
        startedReadingHeading = false;
        this.autohelmOn = false;
        tackStarboard = false;
        tackPort = false;
        mChatService=null;
        settings = getSharedPreferences(PREFS_NAME, 0);
        mLastBTDeviceaddress = settings.getString("mLastBTDeviceaddress",
                mLastBTDeviceaddress);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Intent serverIntent = null;
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
        }
        else if (!mBluetoothAdapter.isEnabled()) {
            serverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(serverIntent, REQUEST_ENABLE_BT);

        } else {
            // try to connect to previos device
            if (mLastBTDeviceaddress != null
                    && mLastBTDeviceaddress.length() > 0) {
                ((ProgressBar) findViewById(R.id.progressBar1))
                        .setVisibility(View.VISIBLE);
                connectDevice(true, mLastBTDeviceaddress);
            }
        }
        receivedMCDesiredHeading = false;
        receivedMCCurrentState = false;
        locationListener = new MyLocationListener();
        setupGPScomms();
        myCompass.setGPSBearing(270);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_auto_helm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.connect:
			changeBTdevice = true;
			if (!mBluetoothAdapter.isEnabled()) {
				serverIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(serverIntent, REQUEST_ENABLE_BT);
			} else {
				// Launch the DeviceListActivity to see devices and do scan
				serverIntent = new Intent(this, DeviceListActivity.class);
				startActivityForResult(serverIntent,
						REQUEST_CONNECT_DEVICE_SECURE);

			}
			return true;
            case R.id.menu_settings:
                Intent changeSetttingsIntent=new Intent(this, SettingsActivity.class);
                //changeSetttingsIntent.pu,mChatService);
                startActivity(changeSetttingsIntent);
                break;
		default:
			return super.onOptionsItemSelected(item);
		}
        return false;//TODO: don't know what this does
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
        timer = new Timer();
        batLowTask blTask = new batLowTask();
        batHighTask bhTask= new batHighTask();
        currentLimitTask clTask = new currentLimitTask();
        tempTask tTask= new tempTask();
        mp = MediaPlayer.create(this, R.raw.warning);
		settings = getSharedPreferences(PREFS_NAME, 0);
		mLastBTDeviceaddress = settings.getString("mLastBTDeviceaddress",
				mLastBTDeviceaddress);

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		} else if(mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled()) {
			// try to connect to previos device
			if (mLastBTDeviceaddress != null
					&& mLastBTDeviceaddress.length() > 0) {
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.VISIBLE);
				connectDevice(true, mLastBTDeviceaddress);
			}
		}
		receivedMCDesiredHeading = false;
		receivedMCCurrentState = false;
		setupGPScomms();
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("mLastBTDeviceaddress", mLastBTDeviceaddress);

		// Commit the edits!
		editor.commit();
		LocationManager locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locman.removeUpdates(locationListener);
	}

	@Override
	public void onStop() {
		super.onStop();
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("mLastBTDeviceaddress", mLastBTDeviceaddress);

		// Commit the edits!
		editor.commit();
		LocationManager locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locman.removeUpdates(locationListener);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("mLastBTDeviceaddress", mLastBTDeviceaddress);

		// Commit the edits!
		editor.commit();
		LocationManager locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locman.removeUpdates(locationListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Intent serverIntent = null;
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				connectDevice(true, address);
				mLastBTDeviceaddress = address;
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				if (mChatService == null) {
					setupChat();
				}
				if (!changeBTdevice && mLastBTDeviceaddress != null
						&& mLastBTDeviceaddress.length() > 0) {
					connectDevice(true, mLastBTDeviceaddress);
				} else {
					changeBTdevice = false;
					// Launch the DeviceListActivity to see devices and do scan
					serverIntent = new Intent(this, DeviceListActivity.class);
					startActivityForResult(serverIntent,
							REQUEST_CONNECT_DEVICE_SECURE);
				}
			} else {

				// User did not enable Bluetooth or an error occurred
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.INVISIBLE);
				Toast.makeText(this, "BT not available", Toast.LENGTH_SHORT)
						.show();
				// finish();
			}
			break;
		}
	}

	public void tackPort(View myView) {
		tackStarboard = false;
		tackPort = true;
		confirmTack("PORT");
	}

	public void tackStarboard(View myView) {
		tackStarboard = true;
		tackPort = false;
		confirmTack("STARBOARD");
	}

	public void pauseAutohelm(View myView) {
		if (mChatService != null) {
			if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
				// Button pauseButton = (Button) findViewById(R.id.pausebutton);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.VISIBLE);
				if (this.autohelmOn) {
					mChatService.sendMessage("PP", this);
					this.autohelmOn = false;
				} else {
                    mChatService.sendMessage("**", this);
					this.autohelmOn = true;
				}
				System.out.println("pauseAutohelm");
			} else {
				Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void incrementHeading(View myView) {
		// mp.start();
		if (mChatService != null) {
			if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.VISIBLE);
			}
		}
		myCompass
				.setDesiredHeading(handleLoop(myCompass.getDesiredHeading() + 1));
		myCompass.invalidate();
		System.out.println("incrementHeading");
	}

	public void requestDiagMessage(View myView) {
    	if (mChatService != null) {
			if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
				// Button pauseButton = (Button) findViewById(R.id.pausebutton);
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.VISIBLE);
                mChatService.sendMessage("&", this);
				System.out.println("requestDiag");
			} else {
				Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
		}
	}
    public void decrementHeading(View myView) {
		// mp.start();
		if (mChatService != null) {
			if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.VISIBLE);
			}
		}
        myCompass
				.setDesiredHeading(handleLoop(myCompass.getDesiredHeading() - 1));
		myCompass.invalidate();
		System.out.println("decrementHeading");
	}
	private int handleLoop(int degrees) {
		if (degrees > 359) {
			degrees -= 360;
		} else if (degrees < 0) {
			degrees += 360;
		}
		return degrees;
	}

	private void connectDevice(boolean secure, String address) {
		if (mChatService == null) {
			setupChat();
		}
		// Get the device MAC address
		// String address = data.getExtras().getString(
		// DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		System.out.println("Device returned from selection as: " + address);
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, secure);
	}

	private void setupChat() {

		// Initialize the BluetoothChatService to perform bluetooth connections
        ((BlueHelmApp)this.getApplication()).setBlueComm( new BluetoothChatService(this, mHandler));
		mChatService = ((BlueHelmApp)this.getApplicationContext()).getBlueComm();
        //mChatService = new BluetoothChatService(this, mHandler);
	}

	static class IncomingHandler extends Handler {
		private final WeakReference<AutoHelm> mActivity;

		IncomingHandler(Context context) {
			mActivity = new WeakReference<AutoHelm>((AutoHelm) context);
		}

		@Override
		public void handleMessage(Message msg) {
			AutoHelm activity = mActivity.get();
			if (activity != null) {
				activity.handleMessage(msg);
			}
		}
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case MESSAGE_STATE_CHANGE:
			switch (msg.arg1) {
			case BluetoothChatService.STATE_CONNECTED:
				setStatus(getString(R.string.title_connected_to,
						mConnectedDeviceName));
				sendStartupMessage();
				// mConversationArrayAdapter.clear();
				break;
			case BluetoothChatService.STATE_CONNECTING:
				setStatus(R.string.title_connecting);
				break;
			case BluetoothChatService.STATE_LISTEN:
			case BluetoothChatService.STATE_NONE:
				setStatus(R.string.title_not_connected);
				break;
			}
			break;
		case MESSAGE_WRITE:
			// byte[] writeBuf = (byte[]) msg.obj;
			// construct a string from the buffer
			// String writeMessage = new String(writeBuf);
			// mConversationArrayAdapter.add("Me:  " + writeMessage);
			break;
		case MESSAGE_READ:
			byte[] readBuf = (byte[]) msg.obj;
			// construct a string from the valid bytes in the buffer
			String readMessage = new String(readBuf, 0, msg.arg1);
			readHeading(readMessage);
			break;
		case MESSAGE_DEVICE_NAME:
			// save the connected device's name
			mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
			Toast.makeText(getApplicationContext(),
					"Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT)
					.show();
			break;
		case MESSAGE_TOAST:
			Toast.makeText(getApplicationContext(),
					msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
			break;
		}
	}

	private final void setStatus(int resId) {
		// final ActionBar actionBar = getActionBar();
		// actionBar.setSubtitle(resId);
	}

	private final void setStatus(CharSequence subTitle) {
		// final ActionBar actionBar = getActionBar();
		// actionBar.setSubtitle(subTitle);
	}

	private void readHeading(String readMessage) {
		blueInString += readMessage;
		if (blueInString.length() > 60) {
			blueInString = blueInString.substring(blueInString.length() - 60);
		}
		if (blueInString.contains("P")) {
			if (!receivedMCCurrentState) {
				onAuthelmPause();
				receivedMCCurrentState = true;
			} else {
				if (tackPort || tackStarboard) {
					tackPort = false;
					tackStarboard = false;
				}
				onAuthelmPause();
			}
            if(blueInString.indexOf("P") + 1<blueInString.length()){
                blueInString = blueInString
                        .substring(blueInString.indexOf("P") + 1);
            }
            else{
                blueInString="";
            }
		}
		if (blueInString.contains("*")) {
			if (!receivedMCCurrentState) {
				onHelmAuto();
				receivedMCCurrentState = true;
			} else {
				if (tackPort || tackStarboard) {
					tackPort = false;
					tackStarboard = false;
				}
				onHelmAuto();
			}
            if(blueInString.indexOf("*") + 1<blueInString.length()){
                blueInString = blueInString
                        .substring(blueInString.indexOf("*") + 1);
            }
            else{
                blueInString="";
            }

		}
		if (blueInString.contains("-") || blueInString.contains("+")) {
			if (!receivedMCCurrentState) {
				onHelmAuto();
				receivedMCCurrentState = true;
			} else {
				autohelmOn = true;
				myCompass.setAutoOn(true);
				//((Button) findViewById(R.id.pausebutton)).setText("pause");
				((ProgressBar) findViewById(R.id.progressBar1))
						.setVisibility(View.VISIBLE);
			}
            if(blueInString.contains("-")){
                if(blueInString.indexOf("-") + 1<blueInString.length()){
                    blueInString = blueInString
                            .substring(blueInString.indexOf("-") + 1);
                }
                else{
                    blueInString="";
                }
            }
            else{
                if(blueInString.indexOf("+") + 1<blueInString.length()){
                    blueInString = blueInString
                            .substring(blueInString.indexOf("+") + 1);
                }
                else{
                    blueInString="";
                }
            }

        }
		int indexOfDollar = blueInString.indexOf("$");
		if (-1 != indexOfDollar && indexOfDollar + 4 < blueInString.length()) {
			String currHeading = blueInString.substring(indexOfDollar + 1,
					indexOfDollar + 4);
			if (Character.isDigit(currHeading.charAt(0))
					&& Character.isDigit(currHeading.charAt(1))
					&& Character.isDigit(currHeading.charAt(2))) {
				int currHeadingNum = Integer.parseInt(currHeading);
				if (receivedMCDesiredHeading
						&& currHeadingNum != myCompass.getDesiredHeading()) {
					((ProgressBar) findViewById(R.id.progressBar1))
							.setVisibility(View.VISIBLE);
					this.sendNewHeading();
				} else {
					if (isValidHeading(currHeadingNum)) {
                        myCompass.setDesiredHeading(currHeadingNum);
						receivedMCDesiredHeading = true;
					}
					((ProgressBar) findViewById(R.id.progressBar1))
							.setVisibility(View.INVISIBLE);
				}
			}
            if(indexOfDollar + 4<blueInString.length()){
                blueInString = blueInString.substring(indexOfDollar + 4);
            }
            else{
                blueInString="";
            }		}
		int indexOfAt = blueInString.indexOf("@");
		if (-1 != indexOfAt && indexOfAt + 4 < blueInString.length()) {
			String currHeading = blueInString.substring(indexOfAt + 1,
					indexOfAt + 4);
            Log.i(TAG, "Length of blueStr when reading heading: " + blueInString.length());
			if (Character.isDigit(currHeading.charAt(0))
					&& Character.isDigit(currHeading.charAt(1))
					&& Character.isDigit(currHeading.charAt(2))) {
				int redHeading = Integer.parseInt(currHeading);
				if (isValidHeading(redHeading)) {
					if (((MyLocationListener) locationListener) != null) {
                        int bearing=(int) ((MyLocationListener) locationListener).getBearing();
						myCompass.setGPSBearing(bearing);
					}
					myCompass.setDigitalCompass(Integer.parseInt(currHeading));
					myCompass.invalidate();
                    Log.i(TAG, "Read heading");
				}
            }
            if(indexOfAt + 4<blueInString.length()){
                blueInString = blueInString.substring(indexOfAt + 4);
            }
            else{
                blueInString="";
            }
        }
        int indexOfMod = blueInString.indexOf("%");
        int indexOfSemiColon = blueInString.indexOf(";");//changed from bar
        if (-1 != indexOfMod && indexOfSemiColon != -1 && indexOfMod<indexOfSemiColon) {
            String messageStr=blueInString.substring(indexOfMod,indexOfSemiColon);
    		Toast.makeText(getApplicationContext(),
					messageStr, Toast.LENGTH_LONG)
					.show();
            if(indexOfSemiColon + 1<blueInString.length()){
                blueInString = blueInString.substring(indexOfSemiColon + 1);
            }
            else{
                blueInString="";
            }            
        }
        int indexOfExcl = blueInString.indexOf("!");
        if (-1 != indexOfExcl && indexOfExcl + 3 < blueInString.length()) {
            String warningMessage = blueInString.substring(indexOfExcl + 1,
                    indexOfExcl + 3);
            Log.i(TAG, "We got inside of Excl read");
            if(warningMessage.equals("VO")){
                Log.i(TAG, "We got inside of VO read: str size:"+blueInString.length());
                if(!mp.isPlaying()){
                    mp.start();
                }
                ((ImageView)findViewById(R.id.batHighSignal)).setAlpha(ON_BRIGHT);
                if(bhTask!=null){
                    bhTask.cancel();
                }
                bhTask= new batHighTask();
                timer.schedule(bhTask,2000l);
            }
            else if(warningMessage.equals("VU")){
                Log.i(TAG,"We got inside of VU read");
                if(!mp.isPlaying()){
                    mp.start();
                }
                ((ImageView)findViewById(R.id.batLowSignal)).setAlpha(ON_BRIGHT);
                if(blTask!=null){
                    blTask.cancel();
                }
                blTask=new batLowTask();
                timer.schedule(blTask,2000l);
            }
            else if(warningMessage.equals("TW")){
                Log.i(TAG,"We got inside of TW read");
                if(!mp.isPlaying()){
                    mp.start();
                }
                ((ImageView)findViewById(R.id.tempSignal)).setAlpha(ON_BRIGHT);
                if(tTask!=null){
                    tTask.cancel();
                }
                tTask=new tempTask();
                timer.schedule(tTask,2000l);
            }
            else if(warningMessage.equals("TS")){
                Log.i(TAG,"We got inside of TS read");
                if(!mp.isPlaying()){
                    mp.start();
                }
                ((ImageView)findViewById(R.id.tempShutdownSignal)).setAlpha(ON_BRIGHT);
            }
            else if(warningMessage.equals("CL")){
                Log.i(TAG,"We got inside of CL read");
                ((ImageView)findViewById(R.id.currentLimitSignal)).setAlpha(ON_BRIGHT);
                if(clTask!=null){
                    clTask.cancel();
                }
                clTask=new currentLimitTask();
                timer.schedule(clTask, 2000l);
            }
            else if(warningMessage.equals("CS")){
                Log.i(TAG,"We got inside of CS read");
                if(!mp.isPlaying()){
                    mp.start();
                }
                ((ImageView)findViewById(R.id.overCurrentSignal)).setAlpha(ON_BRIGHT);
            }
            if(indexOfExcl + 3<blueInString.length()){
                blueInString = blueInString.substring(indexOfExcl + 3);
            }
            else{
                blueInString="";
            }
        }
	}

	private boolean isValidHeading(int heading) {
		if (heading < 0) {
			return false;
		}
		if (heading > 359) {
			return false;
		}
		return true;
	}

	public void sendNewHeading() {
		String message = "@";
//        if(((CheckBox)findViewById(R.id.useGPSCheckBox)).isChecked()){
//            message +=String.format("%03d",myCompass.getGPSCorrectedHeading());
//        }
//        else{
		    message += String.format("%03d", myCompass.getDesiredHeading());
//        }
        mChatService.sendMessage(message, this);
	}

	public void tackNewHeading(int direction) {
		if (AutoHelm.TACK_PORT == direction) {
            mChatService.sendMessage("-", this);
		} else {// AutoHelm.TACK_STARBOARD
            mChatService.sendMessage("+", this);
		}
		// message += String.format("%03d", myCompass.getDesiredHeading());
		// this.sendMessage(message);
	}

	public void confirmTack(String direction) {
		if (mChatService != null) {
			if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
				AlertDialog.Builder alert_box = new AlertDialog.Builder(this);
				// alert_box.setIcon(R.drawable.icon);
				alert_box.setMessage("Are you sure you want to tack to "
						+ direction + ".");
				alert_box.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								((ProgressBar) findViewById(R.id.progressBar1))
										.setVisibility(View.VISIBLE);
								if (tackStarboard) {
									tackNewHeading(AutoHelm.TACK_STARBOARD);
									Toast.makeText(getApplicationContext(),
											"Tacking STARBOARD",
											Toast.LENGTH_LONG).show();
								} else if (tackPort) {
									tackNewHeading(AutoHelm.TACK_PORT);
									Toast.makeText(getApplicationContext(),
											"Tacking PORT", Toast.LENGTH_LONG)
											.show();
								}

							}
						});
				alert_box.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),
										"Tack Cancelled", Toast.LENGTH_LONG)
										.show();
							}
						});
				alert_box.show();
			} else {
				Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void onAuthelmPause() {
		((ProgressBar) findViewById(R.id.progressBar1))
				.setVisibility(View.INVISIBLE);
		this.autohelmOn = false;
		myCompass.setAutoOn(false);
		//((Button) findViewById(R.id.pausebutton)).setText("auto");
	}

	public void onHelmAuto() {
		((ProgressBar) findViewById(R.id.progressBar1))
				.setVisibility(View.INVISIBLE);
		this.autohelmOn = true;
		myCompass.setAutoOn(true);
		//((Button) findViewById(R.id.pausebutton)).setText("pause");
	}

	public void sendStartupMessage() {
        mChatService.sendMessage("!", this);
	}
    public BluetoothChatService getBlueChat(){
        return mChatService;
    }

	public void setupGPScomms() {
		/**
		 * Code required to receive gps location. Activates GPS provider, and is
		 * set to update only after at least 20 seconds and a position change of
		 * at least 5 metres
		 */

		// setting up the location manager
		LocationManager locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
				(float) 10, locationListener);

	}
    //tells activity to run on ui thread
    class tempTask extends TimerTask {
        @Override
        public void run() {
            AutoHelm.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ((ImageView)findViewById(R.id.tempSignal)).setAlpha(TEMP_SIGNAL_OFF_SETTING);
                }
            });
        }
    };
    //tells activity to run on ui thread
    class batHighTask extends TimerTask {
        @Override
        public void run() {
            AutoHelm.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ((ImageView)findViewById(R.id.batHighSignal)).setAlpha(BAT_HIGH_OFF_SETTING);
                }
            });
        }
    };
    //tells activity to run on ui thread
    class batLowTask extends TimerTask {
        @Override
        public void run() {
            AutoHelm.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ((ImageView)findViewById(R.id.batLowSignal)).setAlpha(BAT_LOW_OFF_SETTING);
                }
            });
        }
    };
    //tells activity to run on ui thread
    class currentLimitTask extends TimerTask {
        @Override
        public void run() {
            AutoHelm.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ((ImageView)findViewById(R.id.currentLimitSignal)).setAlpha(CURRENT_LIMIT_OFF_SETTING);
                }
            });
        }
    };

 }
