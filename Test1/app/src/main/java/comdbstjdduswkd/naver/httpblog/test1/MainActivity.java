package comdbstjdduswkd.naver.httpblog.test1;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.StringTokenizer;

import comdbstjdduswkd.naver.httpblog.test1.UDOO.BluetoothChatService;
import comdbstjdduswkd.naver.httpblog.test1.UDOO.Constants;
import comdbstjdduswkd.naver.httpblog.test1.UDOO.DeviceListActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager manager = getFragmentManager();
    RealTimeActivity real;
    HistoryActivity history;
    Bundle bundle = new Bundle();

    int i=0;
    private final String TAG = "YourActivity";
    PolarBleService mPolarBleService;
    String mpolarBleDeviceAddress = "00:22:D0:9C:F9:8E";	// your need to pass the address (주소를 전달해야한다.)
    int batteryLevel=0;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private BluetoothAdapter mBluetoothAdapter = null;
    //Member object for the chat services
    private BluetoothChatService mChatService = null;
    //Name of the connected device
    private String mConnectedDeviceName = null;
     //Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        real = new RealTimeActivity();
        history = new HistoryActivity();

        //manager.beginTransaction().replace(R.id.content_main, real).commit(); //if push the button, change the frame
        changeFragment(0);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = MainActivity.this;
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        mChatService = new BluetoothChatService(this, mHandler);
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            //이상이 없으면 채팅세션을 설정한다.
            //setupChat(); 보류
            mChatService = new BluetoothChatService(this, mHandler); //setupChat에서 일부 발췌
            Toast.makeText(this, "Ready for chat ! :)", Toast.LENGTH_SHORT).show();
        }

        Log.w(this.getClass().getName(), "onCreate()");
        activatePolar();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    mChatService = new BluetoothChatService(this, mHandler);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d("Main", "BT not enabled");
                    Toast.makeText(this, "Bluetooth was not enabled. Leaving this APP",
                            Toast.LENGTH_SHORT).show();
                    this.finish();
                }
        }
    }
    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.Bt_connect && i==0) {
            i = 1;
            Toast.makeText(this, "Bluetooth is connected!!", Toast.LENGTH_SHORT).show();
            item.setIcon(R.drawable.bt_able);
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                // Otherwise, setup the chat session
            } else if (mChatService == null) {
                //이상이 없으면 채팅세션을 설정한다.
                //setupChat(); 보류
            }
            return true;
        }else if(id == R.id.Bt_connect && i==1){
            i=0;
            if(mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
                Toast.makeText(this, "Disconnect the Bluetooth connection..", Toast.LENGTH_SHORT).show();
            }
            item.setIcon(R.drawable.bt_disable);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) { // Handle the map action
            //manager.beginTransaction().replace(R.id.content_main, new RealTimeActivity()).commit(); //if push the button, change the frame
            changeFragment(0);
        } else if (id == R.id.nav_chart) {
            //manager.beginTransaction().replace(R.id.content_main, new HistoryActivity()).commit();
            changeFragment(1);
        } else if (id == R.id.nav_logout) {

        } else if (id == R.id.nav_dereg) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeFragment(int fNum) {
        switch (fNum) {
            case 0:
                if (manager.findFragmentByTag("a") != null) {
                    //if the fragment exists, show it.
                    manager.beginTransaction().show(manager.findFragmentByTag("a")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    manager.beginTransaction().add(R.id.content_main, real, "a").commit();
                }
                if (manager.findFragmentByTag("b") != null) {
                    //if the other fragment is visible, hide it.
                    manager.beginTransaction().hide(manager.findFragmentByTag("b")).commit();
                }
                break;
            case 1:
                if (manager.findFragmentByTag("b") != null) {
                    //if the fragment exists, show it.
                    manager.beginTransaction().show(manager.findFragmentByTag("b")).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    manager.beginTransaction().add(R.id.content_main, history, "b").commit();
                }
                if (manager.findFragmentByTag("a") != null) {
                    //if the other fragment is visible, hide it.
                    manager.beginTransaction().hide(manager.findFragmentByTag("a")).commit();
                }
                break;
        }
    }
    /**
     * The Handler that gets information back from the BluetoothChatService
     */

    //블루투스 채팅 핸들러 메인 부분
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = MainActivity.this;
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;
                //메시지를 쓰는 부분
                case Constants.MESSAGE_WRITE:
                    break;
                //메시지를 읽는 부분
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    real.setAQI(readMessage);
                    real.addEntry(readMessage);
                    //print the sensor data
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    protected void activatePolar() {
        Log.w(this.getClass().getName(), "** activatePolar()");
        Intent gattactivateClickerServiceIntent = new Intent(this, PolarBleService.class);
        Log.e("ee",""+gattactivateClickerServiceIntent.toString());
        bindService(gattactivateClickerServiceIntent, mPolarBleServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mPolarBleUpdateReceiver, makePolarGattUpdateIntentFilter());
    }

    protected void deactivatePolar() {
        Log.w(this.getClass().getName(), "deactivatePolar()");
        if(mPolarBleService!=null){
            unbindService(mPolarBleServiceConnection);
        }
        unregisterReceiver(mPolarBleUpdateReceiver);
        mPolarBleService.disconnect();
    }
    private final BroadcastReceiver mPolarBleUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            final String action = intent.getAction();
            Log.e("000","시작하는부ㅜㄴ");
            if (PolarBleService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.e("1111111","mPolarBleService.initialize");
            } else if (PolarBleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //dataFragPolar.stopAnimation();
                Log.e("222","ACTION_GATT_DISCONNECTED");
            } else if (PolarBleService.ACTION_HR_DATA_AVAILABLE.equals(action)) {
                Log.e("333","ACTION_HR_DATA_AVAILABLE");
                //heartRate+";"+pnnPercentage+";"+pnnCount+";"+rrThreshold+";"+bioHarnessSessionData.totalNN
                //String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);  //-> is it miss type? (i don't need that class)
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                Log.e("data",""+data);
                StringTokenizer tokens = new StringTokenizer(data, ";");
                int hr = Integer.parseInt(tokens.nextToken());
                Toast.makeText(MainActivity.this, ""+hr,Toast.LENGTH_LONG);
                Log.e("hr detect",""+hr);
                int prrPercenteage = Integer.parseInt(tokens.nextToken());
                int prrCount = Integer.parseInt(tokens.nextToken());
                int rrThreshold = Integer.parseInt(tokens.nextToken());	//50%, 30%, etc.
                int rrTotal = Integer.parseInt(tokens.nextToken());
                int rrValue = Integer.parseInt(tokens.nextToken());
                long sid = Long.parseLong(tokens.nextToken());

                //dataFragPolar.settvHR(Integer.toString(hr));
            }else if (PolarBleService.ACTION_BATTERY_DATA_AVAILABLE.equals(action)) {
                Log.e("444","ACTION_BATTERY_DATA_AVAILABLE");
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                batteryLevel = Integer.parseInt(data);
            }else if (PolarBleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.e("555","ACTION_GATT_SERVICES_DISCOVERED");
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                StringTokenizer tokens = new StringTokenizer(data, ";");
                int totalNN = Integer.parseInt(tokens.nextToken());
                long lSessionId = Long.parseLong(tokens.nextToken());

                //Enable your UI
            }
        }
    };
    private static IntentFilter makePolarGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PolarBleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(PolarBleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(PolarBleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(PolarBleService.ACTION_HR_DATA_AVAILABLE);
        intentFilter.addAction(PolarBleService.ACTION_BATTERY_DATA_AVAILABLE);
        return intentFilter;
    }
    private final ServiceConnection mPolarBleServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mPolarBleService = ((PolarBleService.LocalBinder) service).getService();
            if (!mPolarBleService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            mPolarBleService.connect("00:22:D0:9C:F9:8E", false);
            Log.e("mPolarBleService.init",": Sucscc");
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            /////////if(app.runtimeLogging)
                //Log.w("onServiceDisconnected","onServiceDisconnected() ");

            mPolarBleService = null;
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(this.getClass().getName(), "onDestroy");
        deactivatePolar();
    }
}
