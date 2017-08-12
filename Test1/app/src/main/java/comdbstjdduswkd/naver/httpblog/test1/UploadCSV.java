package comdbstjdduswkd.naver.httpblog.test1;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by DONGHEE on 2017-08-11.
 */

public class UploadCSV extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {

        String result = null;
        String filePath = null;
        String fileName = null;
        String defaultUrl = null;
        String key = null;
        String twoHyphens = null;

        if(params[0].equals("air")) {
            filePath = Environment.getExternalStorageDirectory() + File.separator +
                    "data/data/teamb/AQI_history.csv";
            defaultUrl = "http://teamb-iot.calit2.net/slim-api/saveupload";
            key = "file";
            fileName = "AQI_history.csv";
            twoHyphens = "--";
        }else if(params[0].equals("heart")){
            filePath = Environment.getExternalStorageDirectory() + File.separator +
                    "data/data/teamb/heart_history.csv";
            defaultUrl = "http://teamb-iot.calit2.net/slim-api/saveupload";
            key = "file";
            fileName = "heart_history.csv";
            twoHyphens = "--";
        }

        String lineEnd = "\r\n";
        String boundary = "androidupload";
        File targetFile = null;
        if (filePath != null) {
            targetFile = new File(filePath);
        }

        byte[] buffer;
        int maxBufferSize = 5 * 1024 * 1024;
        HttpURLConnection conn = null;
        String urlStr = defaultUrl;
        try {
            conn = (HttpURLConnection) new URL(urlStr).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(10000);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        String delimiter = twoHyphens + boundary + lineEnd; // --androidupload\r\n
        StringBuffer postDataBuilder = new StringBuffer();

        if (fileName != null) {
            postDataBuilder.append(delimiter);
            postDataBuilder.append("Content-Disposition: form-data; name=\"" + key + "\";filename=\"" + fileName + "\"" + lineEnd);
        }
        try {
            DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
            ds.write(postDataBuilder.toString().getBytes());

            if (filePath != null) {
                ds.writeBytes(lineEnd);
                FileInputStream fStream = new FileInputStream(targetFile);
                buffer = new byte[maxBufferSize];
                int length = -1;
                while ((length = fStream.read(buffer)) != -1) {
                    ds.write(buffer, 0, length);
                }
                ds.writeBytes(lineEnd);
                ds.writeBytes(lineEnd);
                ds.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd); // requestbody end
                fStream.close();
            } else {
                ds.writeBytes(lineEnd);
                ds.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd); // requestbody end
            }
            ds.flush();
            ds.close();
            int responseCode = conn.getResponseCode();
            StringBuilder sb = new StringBuilder();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line = null;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
            }
            Log.i("Upload", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
