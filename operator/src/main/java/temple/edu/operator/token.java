package temple.edu.operator;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class token extends Activity {
    String  server_url_arrive = "http://ssh.missingrain.live:8001/lotservice/arrive/";
    String server_url_leave="http://ssh.missingrain.live:8001/lotservice/leave/";
    JSONObject token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

    }

    private void processintent(Intent intent) throws JSONException {
        if(intent.hasExtra("token")){
            token = new JSONObject(intent.getStringExtra("token"));
            if (token != null) {
                try {
                    String data = token.getString("username") + token.getString("parkinglotname") + token.getString("start") + token.getString("end") + String.valueOf(token.getInt("transaction_no"));
                    InputStream fin = openFileInput("publickey.pem");
                    TokenVerify tokenVerify = new TokenVerify();
                    if (tokenVerify.verifytoken(fin,token.getString("token"),data)) {
                        JSONObject json = new JSONObject();
                        json.put("transaction_no", token.getString("transaction_no"));
                        new SendtoServer().execute(server_url_arrive, json.toString());
                        Toast.makeText(this, "verification successful", Toast.LENGTH_LONG).show();

                        //notify server of user arrival

                    }
                    else{
                        Toast.makeText(this, "Token Invalid", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(intent.hasExtra("transaction_no")){
            JSONObject json = new JSONObject();
            json.put("transaction_no",intent.getStringExtra("transaction_no"));
            new SendtoServer().execute(server_url_leave, json.toString());
           // Toast.makeText(this,"transaction_no : "+ intent.getStringExtra("transaction_no"),Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        try {
            processintent(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            processintent(getIntent());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//

    private class SendtoServer extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String data = "";
            HttpURLConnection httpURLConnection = null;
            try {

                //connect to url
                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);


                //POST JSON.tostring()
                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(params[1]);
                wr.flush();
                wr.close();

                //get response
                Log.d("Response code: ", (String.valueOf(httpURLConnection.getResponseCode())));
                InputStream in = httpURLConnection.getInputStream();

                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            Log.d("arrival response: ", result);

            //Toast.makeText(getApplicationContext(), "Notified", Toast.LENGTH_LONG).show();
            String status = "";

            try {
                JSONObject json = new JSONObject(result);
                status = json.getString("status");
                Log.d("leavingstatus",status);
                if(json.has("duration")){
                    Toast.makeText(getApplicationContext(), "total time:"+json.getString("duration"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException j) {
                j.printStackTrace();
                Log.d("json status ", status);
            }
        }
    }
}
