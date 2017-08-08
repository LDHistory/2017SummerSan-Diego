package comdbstjdduswkd.naver.httpblog.test1;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import comdbstjdduswkd.naver.httpblog.test1.SeosorFragment.CO;
import comdbstjdduswkd.naver.httpblog.test1.SeosorFragment.NO2;
import comdbstjdduswkd.naver.httpblog.test1.SeosorFragment.O3;
import comdbstjdduswkd.naver.httpblog.test1.SeosorFragment.PM25;
import comdbstjdduswkd.naver.httpblog.test1.SeosorFragment.SO2;
import comdbstjdduswkd.naver.httpblog.test1.SeosorFragment.TEMP;
import comdbstjdduswkd.naver.httpblog.test1.UDOO.BluetoothChatService;
import comdbstjdduswkd.naver.httpblog.test1.UDOO.Constants;
import comdbstjdduswkd.naver.httpblog.test1.UDOO.DeviceListActivity;
import comdbstjdduswkd.naver.httpblog.test1.UserManagement.LoginActivity;
import comdbstjdduswkd.naver.httpblog.test1.UserManagement.RegActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager manager = getFragmentManager();
    RealTimeActivity real;
    HistoryActivity history;
    LoginActivity login;

    CO cofragment;
    NO2 no2fragment;
    O3 o3fragment;
    PM25 pm25fragment;
    SO2 so2fragment;
    TEMP tempfragemnt;
    String id, PWcheckResult;
    public String jsonreadmessage;

    int i = 0;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String saveaddr;
    boolean checkPW;

    public static int onlinestatus = 0;
    public static String readlocation;

    Date mDate;
    long mNow;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private final String TAG = "YourActivity";
    PolarBleService mPolarBleService;
    String mpolarBleDeviceAddress = "00:22:D0:9C:F9:8E";    // your need to pass the address (주소를 전달해야한다.)
    int batteryLevel = 0;
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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        Toast.makeText(this, "" + id, Toast.LENGTH_SHORT).show();

        //데이터 저장하기 위해 sharedpreferences 객체 구현
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        real = new RealTimeActivity();
        history = new HistoryActivity();
        login = new LoginActivity();

        cofragment = new CO();
        no2fragment = new NO2();
        o3fragment = new O3();
        pm25fragment = new PM25();
        so2fragment = new SO2();
        tempfragemnt = new TEMP();

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

        //sharedpreferences
        if (mBluetoothAdapter.isEnabled()) {
            saveaddr = sharedpreferences.getString(MyPREFERENCES, "null");
            if (!saveaddr.equals("null")) {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(saveaddr);
                // Attempt to connect to the device
                mChatService.connect(device, false);
            }
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
                //sharedpreferences
                saveaddr = sharedpreferences.getString(MyPREFERENCES, "null");
                if (!saveaddr.equals("null")) {
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(saveaddr);
                    // Attempt to connect to the device
                    mChatService.connect(device, false);
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
        editor.putString(MyPREFERENCES, address);
        editor.commit();
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
        if (id == R.id.Bt_connect && i == 0) {
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
                saveaddr = sharedpreferences.getString(MyPREFERENCES, "null");
                if (!saveaddr.equals("null")) {
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(saveaddr);
                    // Attempt to connect to the device
                    mChatService.connect(device, false);
                }
                //이상이 없으면 채팅세션을 설정한다.
                //setupChat(); 보류
            }
            return true;
        } else if (id == R.id.Bt_connect && i == 1) {
            i = 0;
            if (mBluetoothAdapter.isEnabled()) {
                //mBluetoothAdapter.disable();
                mChatService.connectionLost();
                try {
                    onlinestatus = 0;
                    JsonTransfer jsonTransfer = new JsonTransfer();
                    JSONObject wrapObject = new JSONObject(jsonreadmessage);

                    //Add realtime location value
                    wrapObject.put("latitude", real.latitude);
                    wrapObject.put("longitude", real.longitude);

                    //Add real time year month date
                    mNow = System.currentTimeMillis();
                    mDate = new Date(mNow);
                    wrapObject.put("apptime", mFormat.format(mDate));

                    //Add user number
                    wrapObject.put("user_num", login.usernum);
                    wrapObject.put("online_state", onlinestatus);
                    String jsonString = wrapObject.toString();
                    Log.i("onlinestatus2", jsonString);
                    jsonTransfer.execute("http://teamb-iot.calit2.net/slim-api/receive-air-data", "[" + jsonString + "]");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this); //get builder
            alertDialogBuilder.setTitle("Wanning!");
            //set the message
            alertDialogBuilder.setMessage("Are you sure you wanna sign out?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", //positive fuction write
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.this.finish();
                                    Toast.makeText(MainActivity.this,
                                            "Successfully sign out !", Toast.LENGTH_LONG).show();
                                }
                            })
                    .setNegativeButton("No", //Negative button function write
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else if (id == R.id.nav_dereg) {
            AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(this); //get builder
            alertDialogBuilder2.setTitle("Identification");
            //set the message
            final EditText editpw = new EditText(MainActivity.this);
            alertDialogBuilder2.setView(editpw);
            alertDialogBuilder2.setMessage("Enter your password.")
                    .setCancelable(false)
                    .setPositiveButton("Confirm", //positive fuction write
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (checkPWtoSever(editpw.getText().toString())) {
                                        finish();
                                        Toast.makeText(MainActivity.this,
                                                "Successfully canceled your ID, goodbye", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MainActivity.this,
                                                "Check your password!!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                    .setNegativeButton("Cancel", //Negative button function write
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog2 = alertDialogBuilder2.create();
            alertDialog2.show();
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
                    jsonreadmessage = readMessage;
                    try {
                        onlinestatus = 1;
                        JsonTransfer jsonTransfer = new JsonTransfer();
                        JSONObject wrapObject = new JSONObject(readMessage);
                        real.setAQI(wrapObject);
                        cofragment.addEntryCO(wrapObject);
                        no2fragment.addEntryNO2(wrapObject);
                        o3fragment.addEntryO3(wrapObject);
                        pm25fragment.addEntryPM25(wrapObject);
                        so2fragment.addEntrySO2(wrapObject);
                        tempfragemnt.addEntryTEMP(wrapObject);
                        //Add realtime location value
                        wrapObject.put("latitude", real.latitude);
                        wrapObject.put("longitude", real.longitude);
                        //send AQI data(jsonObject) to server

                        //Add real time year month date
                        mNow = System.currentTimeMillis();
                        mDate = new Date(mNow);
                        wrapObject.put("apptime", mFormat.format(mDate));

                        //Add user number
                        wrapObject.put("user_num", login.usernum);
                        wrapObject.put("online_state", onlinestatus);

                        String jsonString = wrapObject.toString();
                        Log.i("json String print", "" + jsonString);
                        jsonTransfer.execute("http://teamb-iot.calit2.net/slim-api/receive-air-data", "[" + jsonString + "]");
                        readlocation = jsonTransfer.strJson;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        Log.e("ee", "" + gattactivateClickerServiceIntent.toString());
        bindService(gattactivateClickerServiceIntent, mPolarBleServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mPolarBleUpdateReceiver, makePolarGattUpdateIntentFilter());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void deactivatePolar() {
        Log.w(this.getClass().getName(), "deactivatePolar()");
        if (mPolarBleService != null) {
            unbindService(mPolarBleServiceConnection);
        }
        unregisterReceiver(mPolarBleUpdateReceiver);
        mPolarBleService.disconnect();
    }

    private final BroadcastReceiver mPolarBleUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            final String action = intent.getAction();
            if (PolarBleService.ACTION_GATT_CONNECTED.equals(action)) {
            } else if (PolarBleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //dataFragPolar.stopAnimation();
            } else if (PolarBleService.ACTION_HR_DATA_AVAILABLE.equals(action)) {
                //heartRate+";"+pnnPercentage+";"+pnnCount+";"+rrThreshold+";"+bioHarnessSessionData.totalNN
                //String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);  //-> is it miss type? (i don't need that class)
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                StringTokenizer tokens = new StringTokenizer(data, ";");
                int hr = Integer.parseInt(tokens.nextToken());
                Toast.makeText(MainActivity.this, "" + hr, Toast.LENGTH_LONG);
                Log.e("hr detect", "" + hr);
                real.setHeart(hr);
                real.addHEntry(hr);

                //Send hr value to server
                JsonTransfer jsonTransfer = new JsonTransfer();
                JSONObject wrapObject = new JSONObject();
                try {
                    wrapObject.put("huser_num", login.usernum);
                    wrapObject.put("macaddress", "00:22:D0:9C:F9:8E");
                    wrapObject.put("heart_rate", hr);

                    //Add real time year month date
                    mNow = System.currentTimeMillis();
                    mDate = new Date(mNow);
                    wrapObject.put("appdate", mFormat.format(mDate));

                    String jsonString = wrapObject.toString();
                    Log.e("heart_test", jsonString);
                    jsonTransfer.execute("http://teamb-iot.calit2.net/slim-api/receive-heart-data", "[" + jsonString + "]");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //dataFragPolar.settvHR(Integer.toString(hr));
            } else if (PolarBleService.ACTION_BATTERY_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                batteryLevel = Integer.parseInt(data);
            } else if (PolarBleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
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

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mPolarBleService = ((PolarBleService.LocalBinder) service).getService();
            if (!mPolarBleService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            mPolarBleService.connect("00:22:D0:9C:F9:8E", false);
            // mPolarBleService.connect("00:22:D0:9C:F9:8E", false);
            Log.e("mPolarBleService.init", ": Sucscc");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //if(app.runtimeLogging)
            //    Log.w("onServiceDisconnected","onServiceDisconnected() ");

            mPolarBleService = null;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(this.getClass().getName(), "onDestroy");
        deactivatePolar();
        mChatService.connectionLost();
        if(jsonreadmessage != null) {
            try {
                onlinestatus = 0;
                JsonTransfer jsonTransfer = new JsonTransfer();
                JSONObject wrapObject = new JSONObject(jsonreadmessage);

                //Add realtime location value
                wrapObject.put("latitude", real.latitude);
                wrapObject.put("longitude", real.longitude);

                //Add real time year month date
                mNow = System.currentTimeMillis();
                mDate = new Date(mNow);
                wrapObject.put("apptime", mFormat.format(mDate));

                //Add user number
                wrapObject.put("user_num", login.usernum);
                wrapObject.put("online_state", onlinestatus);
                String jsonString = wrapObject.toString();
                Log.i("onlinestatus2", jsonString);
                jsonTransfer.execute("http://teamb-iot.calit2.net/slim-api/receive-air-data", "[" + jsonString + "]");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkPWtoSever(String pw) {
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL("http://teamb-iot.calit2.net/slim-api/appcancel-email");       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST

            // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //--------------------------
            //   서버로 값 전송 (URL Tag protocol)
            //--------------------------
            StringBuffer buffer = new StringBuffer();
            buffer.append("email").append("=").append(id).append("&");                 // php 변수에 값 대입
            buffer.append("password").append("=").append(pw);


            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();
            //--------------------------
            //   서버에서 전송받기
            //--------------------------
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
            }
            PWcheckResult = builder.toString();                       // 전송결과를 전역 변수에 저장
            try {
                JSONObject jsonObject = new JSONObject(PWcheckResult);
                if (jsonObject.getString("status").equals("true")) {
                    checkPW = true;
                } else if (jsonObject.getString("status").equals("false")) {
                    checkPW = false;
                }
                //((TextView)(findViewById(R.id.text_result))).setText(myResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            //
        } // try
        Log.e("pwcheck", "" + checkPW);
        return checkPW;
    }
}
