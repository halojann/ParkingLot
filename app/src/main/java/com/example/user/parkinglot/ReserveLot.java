package com.example.user.parkinglot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReserveLot extends Activity {

    SharedPreferences settings;
    String username, lotname;
    String duration;
    final String server_url="http://ssh.missingrain.live:8001/userservice/reserve/";
    EditText time_duration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_lot);

        TextView lot_name, rem_slots, rate_hr, open_from, close_at, e_mail, ph;

        lot_name = (TextView) findViewById(R.id.textView3);
        rem_slots = (TextView) findViewById(R.id.textView5);
        rate_hr = (TextView) findViewById(R.id.textView9);
        open_from = (TextView) findViewById(R.id.textView7);
        close_at = (TextView) findViewById(R.id.textView21);
        e_mail = (TextView) findViewById(R.id.textView19);
        ph = (TextView) findViewById(R.id.textView25);

        lotname = MapsActivity.lot_name;

        settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        username = settings.getString("username", "");
        String vacancies = "vacancies", rate = "rate", openfrom = "opentime", closesat = "closetime", email = "email", phone = "phone", status = "";
        Intent intent = getIntent();

        String response = intent.getStringExtra("response");
        JSONObject json;
        try {
            json = new JSONObject(response);

            status = json.getString("status");
            vacancies = json.getString("remaining_number");
            rate = json.getString("price_info");
            openfrom = json.getString("start_time");
            closesat = json.getString("close_time");
            email = json.getString("email");
            phone = json.getString("phone");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Invalid json", status);
        }

        lot_name.setText(lotname);
        rem_slots.setText(vacancies);
        rate_hr.setText(rate);
        open_from.setText(openfrom);
        close_at.setText(closesat);
        e_mail.setText(email);
        ph.setText(phone);

        time_duration = (EditText) findViewById(R.id.editText7);
        time_duration.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);


        Button button = (Button) findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duration = time_duration.getText().toString();
                JSONObject json = new JSONObject();
                try {
                    json.put("parkinglot_name", lotname);
                    json.put("duration", duration);
                    json.put("username", username);
                } catch (JSONException j) {
                    j.printStackTrace();
                    Log.d("json exception: ", "send reservation details to server");
                }
                Log.d("datatosend",json.toString());
                new SendtoServer().execute(server_url, json.toString());

            }
        });
    }

    void sendIntent(String username,String parkinglogname, String start, String end, String token, int transaction){
        Intent intent = new Intent(com.example.user.parkinglot.ReserveLot.this, com.example.user.parkinglot.Beam.class);
        intent.putExtra("username", username);
        intent.putExtra("parkinglotname", parkinglogname);
        intent.putExtra("start", start);
        intent.putExtra("end", end);
        intent.putExtra("transaction_no", transaction);
        intent.putExtra("token", token);
        startActivity(intent);
    }

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


                httpURLConnection.setConnectTimeout(30000);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setDoInput(true);

                //connection.setChunkedStreamingMode(0);
//                account.put("username","Chenglong Fu");
//                account.put("password","fcl199303");
                httpURLConnection.getOutputStream().write(params[1].getBytes());//把数据以流的方式写给服务器。
                httpURLConnection.getOutputStream().close();
                //POST JSON.tostring()
//                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
//                wr.writeBytes(params[1]);
//                wr.flush();
//                wr.close();

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
            Log.d("reservation response: ", result);
            JSONObject jsonObject=null;
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if((jsonObject!=null)){
                try {
                    if(jsonObject.getString("status").equals("11")){
                        Toast.makeText(getApplicationContext(), "Reservation completed", Toast.LENGTH_LONG).show();
                        try {
                            OutputStream outputStream = openFileOutput("token", Context.MODE_PRIVATE);
                            outputStream.write(jsonObject.getString("token").getBytes());
                            outputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sendIntent(username,lotname, jsonObject.getString("start"),jsonObject.getString("end"),jsonObject.getString("token"),jsonObject.getInt("transaction_no"));
                        //Log.d("transaction number",String.valueOf(jsonObject.getInt("transaction_no")));
                    }
                    else if(jsonObject.getString("status").equals("00")){
                        Toast.makeText(getApplicationContext(), "No room", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



            String token = "", start = "", end = "", transaction = "", status = "";

            try {
                JSONObject json = new JSONObject(result);
                status = json.getString("status");
                token = json.getString("token");
                start = json.getString("start");
                end = json.getString("end");
                transaction = json.getString("transaction");

            } catch (JSONException j) {
                j.printStackTrace();
                Log.d("json status ", status);
            }





        }
    }
}
