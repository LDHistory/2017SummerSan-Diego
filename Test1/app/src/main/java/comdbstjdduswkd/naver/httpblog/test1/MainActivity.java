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
import android.os.Environment;
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

import com.opencsv.CSVWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Setting the location of Extenal storage to save the csv file.
    String filePath = Environment.getExternalStorageDirectory() + File.separator +
            "data/data/teamb/";

    //To save the bluetooth location, Declare the arraylist
    ArrayList<String> historyArrList;

    ///////////////////////////////////////////////////////////////
    //To use another class method and value, Declare the instance//
    //////////////////////////////////////////////////////////////
    FragmentManager manager = getFragmentManager();
    RealTimeActivity real;
    HistoryActivity history;
    LoginActivity login;
    UploadCSV upload;

    CO cofragment;
    NO2 no2fragment;
    O3 o3fragment;
    PM25 pm25fragment;
    SO2 so2fragment;
    TEMP tempfragemnt;
    String id, PWcheckResult;
    //////////////////////////////////////////////////////////////
    public String jsonreadmessage;

    //Check the option button on the navigation bar
    //if option button is not activated, the i is 0
    int i = 0;

    //To use SharedPreferences class, Declare the setting
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    //Save the address
    String saveaddr;
    //Save the check pw
    boolean checkPW;

    //To give the user onlinestatus to the server
    public static int onlinestatus = 0;
    //To read the udoo board location
    public static String readlocation;

    //To use the current time, Declare the setting
    Date mDate;
    long mNow;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    //To save the history data from coming the udoo board, Declare the Map and ArrayList
    Map<String, Object> hmap = null;
    ArrayList<Map<String, Object>> list;
    Map<String, Object> heartmap = null;
    ArrayList<Map<String, Object>> hlist;

    private final String TAG = "YourActivity";
    PolarBleService mPolarBleService;
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
    private StringBuffer mOutStringBuffer;

    DecimalFormat df;

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

        //To save the data, Implement the shardpreferences objects.
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        //Implement the ArrayList to make a csv file
        list = new ArrayList<Map<String, Object>>();
        hlist = new ArrayList<Map<String, Object>>();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        historyArrList = new ArrayList<String>();

        real = new RealTimeActivity();
        history = new HistoryActivity();
        login = new LoginActivity();
        upload = new UploadCSV();

        cofragment = new CO();
        no2fragment = new NO2();
        o3fragment = new O3();
        pm25fragment = new PM25();
        so2fragment = new SO2();
        tempfragemnt = new TEMP();
        mOutStringBuffer = new StringBuffer("");

        df = new DecimalFormat(".0");

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
            mChatService = new BluetoothChatService(this, mHandler);
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
            }
            return true;
        } else if (id == R.id.Bt_connect && i == 1) {
            i = 0;
            if (mBluetoothAdapter.isEnabled()) {
                //mBluetoothAdapter.disable();
                //If app is disconnected with the sensor, this message will be sended to the sensor
                sendMessage("stop\n");
                mChatService.connectionLost();
                if (jsonreadmessage != null) {
                    try {
                        //If app is disconnected with the sensor, online status sets the 0 value
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
                Toast.makeText(this, "Disconnect the Bluetooth connection..", Toast.LENGTH_SHORT).show();
                //}
                item.setIcon(R.drawable.bt_disable);
                return true;
            }
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

    //To save the life cycle of fragment...
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

    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(MainActivity.this, "You are not connected.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
            Log.i("send", "Successful send!");

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = MainActivity.this;
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //If app is connected with the sensor, This message will be sended.
                            MainActivity.this.sendMessage("start\n");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;
                //Write message by using bluetooth
                case Constants.MESSAGE_WRITE:
                    break;
                //Read message by using bluetooth
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    String checkfirst = readMessage.substring(0, 1);
                    String receive = readMessage.substring(1, readMessage.length() - 1);
                    //if app receives the real-time data from udoo board, then under the code will operate.
                    //"r" means real-time data
                    if (checkfirst.equals("r")) {
                        jsonreadmessage = receive;
                        try {
                            onlinestatus = 1;
                            JsonTransfer jsonTransfer = new JsonTransfer();
                            JSONObject wrapObject = new JSONObject(jsonreadmessage);
                            real.setAQI(wrapObject);
                            cofragment.addEntryCO();
                            no2fragment.addEntryNO2();
                            o3fragment.addEntryO3();
                            pm25fragment.addEntryPM25();
                            so2fragment.addEntrySO2();
                            tempfragemnt.addEntryTEMP(wrapObject);

                            JSONObject sendObject = new JSONObject();
                            sendObject.put("CO",df.format(real.indexco));
                            sendObject.put("SO2",df.format(real.indexso2));
                            sendObject.put("NO2",df.format(real.indexno2));
                            sendObject.put("O3",df.format(real.indexo3));
                            sendObject.put("PM25",df.format(real.indexpm25));
                            sendObject.put("temp",wrapObject.getString("temp"));
                            sendObject.put("time",wrapObject.getString("time"));
                            sendObject.put("MAC",wrapObject.get("MAC"));
                            //Add realtime location value
                            sendObject.put("latitude", real.latitude);
                            sendObject.put("longitude", real.longitude);
                            //send AQI data(jsonObject) to server
                            //Add real time year month date
                            mNow = System.currentTimeMillis();
                            mDate = new Date(mNow);
                            sendObject.put("apptime", mFormat.format(mDate));

                            //Add user number
                            sendObject.put("user_num", login.usernum);
                            sendObject.put("online_state", onlinestatus);

                            String jsonString = sendObject.toString();
                            Log.i("json String print", "" + jsonString);
                            jsonTransfer.execute("http://teamb-iot.calit2.net/slim-api/receive-air-data", "[" + jsonString + "]");
                            readlocation = jsonTransfer.strJson;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //if app receives the history data from udoo board, then under the code will operate.
                        //"h" means history data
                    } else if (checkfirst.equals("h")) {
                        //"en" means end of file
                        if (!receive.equals("en")) {
                            Log.i("read2", receive);
                            StringTokenizer tokens = new StringTokenizer(receive, ",");
                            String tokenArray[] = receive.split(",");
                            String name[] = {"MAC", "time", "temp", "NO2", "O3", "CO", "SO2", "PM25"};

                            //Save the value of history data on the hashMap
                            try {
                                hmap = new HashMap<String, Object>();
                                for (int i = 0; i < 8; i++) {
                                    hmap.put(name[i], tokenArray[i]);
                                }
                                list.add(hmap);
                            } catch (Exception e){
                                Toast.makeText(activity, "Making CSV Error! ", Toast.LENGTH_SHORT).show();
                            }
                        } else if (receive.equals("en")) {
                            //Make a CSV file part
                            try {
                                CSVWriter cw = new CSVWriter(new OutputStreamWriter
                                        (new FileOutputStream(filePath + "AQI_history.csv"), "EUC-KR"), ',', '"');
                                try {
                                    for (Map<String, Object> m : list) {
                                        cw.writeNext(new String[]{String.valueOf(m.get("MAC")), String.valueOf(m.get("time")),
                                                String.valueOf(m.get("temp")), String.valueOf(m.get("NO2")), String.valueOf(m.get("O3")),
                                                String.valueOf(m.get("CO")), String.valueOf(m.get("SO2")), String.valueOf(m.get("PM25"))});
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    list.clear();
                                    Log.i("Make CSV", "Successful");
                                    Toast.makeText(activity, "Successfuly make csv file! ", Toast.LENGTH_SHORT).show();
                                    upload.execute("air");
                                    cw.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
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
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                StringTokenizer tokens = new StringTokenizer(data, ";");
                int hr = Integer.parseInt(tokens.nextToken());
                int rr = Integer.parseInt(tokens.nextToken());
                //To set the hr value on the text and chart...
                real.setHeart(hr);
                real.addHEntry(hr, rr);

                if (rr != 0 && rr != 100)
                    //To set the rr value on the text and chart...
                    real.setHeartrr(rr);

                //Send hr value to server
                JsonTransfer jsonTransfer = new JsonTransfer();
                JSONObject wrapObject = new JSONObject();
                try {
                    wrapObject.put("huser_num", login.usernum);
                    wrapObject.put("macaddress", "00:22:D0:3D:2E:81");
                    wrapObject.put("heart_rate", hr);
                    wrapObject.put("rest_heart_rate", rr);

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

                //Save the value of history data on the hashMap
                try {
                    heartmap = new HashMap<String, Object>();
                    heartmap.put("hr", hr);
                    heartmap.put("rr", rr);
                    hlist.add(heartmap);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Making CSV Error! ", Toast.LENGTH_SHORT).show();
                }

                if(hlist.size() == 300){
                    //Make a CSV file part
                    try {
                        CSVWriter cw = new CSVWriter(new OutputStreamWriter
                                (new FileOutputStream(filePath + "heart_history.csv"), "EUC-KR"), ',', '"');
                         try {
                            for (Map<String, Object> m : hlist) {
                                cw.writeNext(new String[]{String.valueOf(m.get("hr")), String.valueOf(m.get("rr"))});
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            hlist.clear();
                            Log.i("Make CSV", "Successful");
                            Toast.makeText(MainActivity.this, "Successfuly make heart csv file! ", Toast.LENGTH_SHORT).show();
                            upload.execute("heart");
                            cw.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            //mPolarBleService.connect("00:22:D0:9C:F9:8E", false);
            mPolarBleService.connect("00:22:D0:3D:2E:81", false);
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
        //If activity is destoryed, then send the stop message to udoo board.
        sendMessage("stop\n");
        Log.e(this.getClass().getName(), "onDestroy");
        deactivatePolar();
        mChatService.connectionLost();
        //If activity is destoyed, then send send the current user data to the web server.
        if (jsonreadmessage != null) {
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
            //   Check the URL and Connect
            //--------------------------
            URL url = new URL("http://teamb-iot.calit2.net/slim-api/appcancel-email");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            //--------------------------
            //   Transmission option
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");

            // Tell the server to process it the same way that the value passed from the Web to <Form>.
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //--------------------------
            //   Send the data to the server (URL Tag protocol)
            //--------------------------
            StringBuffer buffer = new StringBuffer();
            buffer.append("email").append("=").append(id).append("&");
            buffer.append("password").append("=").append(pw);


            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();
            //--------------------------
            //   Receive the data from server
            //--------------------------
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {       //It will be sent line by line from the server, so read it line by line.
                builder.append(str + "\n");                     //Add line separator for display in View.
            }
            PWcheckResult = builder.toString();                       // Store transmission results in global variables.
            try {
                JSONObject jsonObject = new JSONObject(PWcheckResult);
                if (jsonObject.getString("status").equals("true")) {
                    checkPW = true;
                } else if (jsonObject.getString("status").equals("false")) {
                    checkPW = false;
                }
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