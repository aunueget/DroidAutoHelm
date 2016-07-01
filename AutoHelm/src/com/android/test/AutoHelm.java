package com.android.test;



import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class AutoHelm extends Activity {
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 31;
    private static final int REQUEST_ENABLE_BT = 33;
	public HandsOnImageView myCompass;

    // Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter;
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    /** A handle to the thread that's actually running the animation. */
   // private CompassThread compassThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autohelm);
        myCompass = (HandsOnImageView) findViewById(R.id.imageView1);
        System.out.println("Setting the view");
        myCompass.invalidate();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
            Toast.makeText( this,"Bluetooth is not available", Toast.LENGTH_LONG).show();
		}

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_autohelm, menu);
        return true;
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       Intent serverIntent = null;
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.connect:
	    		if (!mBluetoothAdapter.isEnabled()) {
	    			serverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    		    startActivityForResult(serverIntent, REQUEST_ENABLE_BT);
	    		}
	    		else{
		            // Launch the DeviceListActivity to see devices and do scan
		            serverIntent = new Intent(this, DeviceListActivity.class);
		            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
		            
	    		}
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

   @Override
   protected void onActivityResult (int requestCode, int resultCode, Intent data){
       Intent serverIntent = null;
       switch (requestCode) {
       case REQUEST_CONNECT_DEVICE_SECURE:
           // When DeviceListActivity returns with a device to connect
           if (resultCode == Activity.RESULT_OK) {
               connectDevice(data, true);
           }
           break;
       case REQUEST_ENABLE_BT:
           // When the request to enable Bluetooth returns
           if (resultCode == Activity.RESULT_OK) {
	            // Launch the DeviceListActivity to see devices and do scan
	            serverIntent = new Intent(this, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
           } else {
               // User did not enable Bluetooth or an error occurred

               Toast.makeText(this, "BT not available", Toast.LENGTH_SHORT).show();
               finish();
           }
           break;
       }
   }
    public void tackPort(View myView){
    	myCompass.setTriangleLocation(2,handleLoop(myCompass.getTriangleLocation(2)+90));
    	myCompass.setTriangleLocation(3,handleLoop(myCompass.getTriangleLocation(3)+90));
    	myCompass.invalidate();
    	System.out.println("tackPort");
    }

    public void tackStarboard(View myView){
    	myCompass.setTriangleLocation(2,handleLoop(myCompass.getTriangleLocation(2)-90));
    	myCompass.setTriangleLocation(3,handleLoop(myCompass.getTriangleLocation(3)-90));
    	myCompass.invalidate();
    	System.out.println("tackStarboard");
    }

    public void pauseAutohelm(View myView){
    	System.out.println("pauseAutohelm");
    }

    public void incrementHeading(View myView){
    	myCompass.setTriangleLocation(2,handleLoop(myCompass.getTriangleLocation(2)+1));
    	myCompass.setTriangleLocation(3,handleLoop(myCompass.getTriangleLocation(3)+1));
    	myCompass.invalidate();
    	System.out.println("incrementHeading");
    }
    public void decrementHeading(View myView){
    	myCompass.setTriangleLocation(2,handleLoop(myCompass.getTriangleLocation(2)-1));
    	myCompass.setTriangleLocation(3,handleLoop(myCompass.getTriangleLocation(3)-1));
    	myCompass.invalidate();
    	System.out.println("decrementHeading");
    }
    private int handleLoop(int degrees){
    	if (degrees>359){
    		degrees-=360;
    	}
    	else if(degrees<0){
    		degrees+=360;
    	}
    	return degrees;
    }
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
       mChatService.connect(device, secure);
    }
    private void setupChat() {

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    setStatus(getString(R.string.title_connected_to , mConnectedDeviceName));
                    mConversationArrayAdapter.clear();
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
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
}
