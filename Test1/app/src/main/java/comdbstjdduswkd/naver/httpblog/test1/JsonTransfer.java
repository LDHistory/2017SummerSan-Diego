package comdbstjdduswkd.naver.httpblog.test1;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by USER on 2017-08-02.
 */

public class JsonTransfer extends AsyncTask<String, Void, String> {

    public static String strJson;

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlConnection;
        String data = params[1];
        String result = null;
        try{
            //Connect
            urlConnection = (HttpURLConnection) ((new URL(params[0]).openConnection()));
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(10000); //10000ms
            urlConnection.setConnectTimeout(15000); //15000ms
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            urlConnection.setRequestProperty("X-Requested-With","XMLHttpRequest");
            urlConnection.setRequestMethod("POST");

            //Write
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            writer.write(data);
            Log.v("Send data", ""+data);
            writer.close();
            outputStream.close();

            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();

            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }
            //Toast.makeText(MainActivity.this, "전송 후 결과 받음", Toast.LENGTH_LONG).show();
            bufferedReader.close();
            result = sb.toString();
            strJson = result;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){e.printStackTrace();}
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        strJson = result;
            /*
            mainAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(mainAct, "Received!", Toast.LENGTH_LONG).show();
                    try {
                        JSONArray json = new JSONArray(strJson);
                        mainAct.textResult.setText(json.toString(1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            */
    }
}
