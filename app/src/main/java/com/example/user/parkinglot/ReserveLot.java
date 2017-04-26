package com.example.user.parkinglot;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReserveLot extends Activity {

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

        final String lotname = MapsActivity.lot_name;
        final String username = " ";//todo get username from sharedpreferences
        final String server_url = "http://10.109.106.250:8000/accounts/reserve/";

        String vacancies = "vacancies", rate = "rate", openfrom = "opentime", closesat = "closetime", email = "email", phone = "phone";
        Intent intent = getIntent();

        String response = intent.getStringExtra("response");
        JSONObject json;
        try {
            json = new JSONObject(response);

            vacancies = json.getString("remaining_number");
            rate = json.getString("price_info");
            openfrom = json.getString("start_time");
            closesat = json.getString("close_time");
            email = json.getString("email");
            phone = json.getString("phone");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Invalid json", "crash");
        }

        lot_name.setText(lotname);
        rem_slots.setText(vacancies);
        rate_hr.setText(rate);
        open_from.setText(openfrom);
        close_at.setText(closesat);
        e_mail.setText(email);
        ph.setText(phone);

        EditText startuser = (EditText) findViewById(R.id.editText6);
        EditText enduser = (EditText) findViewById(R.id.editText7);

        final String starttime = startuser.getText().toString();
        final String endtime = enduser.getText().toString();

        Button button = (Button) findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("parkinglot_name", lotname);
                    json.put("start", starttime);
                    json.put("end", endtime);
                    json.put("username", username);
                } catch (JSONException j) {
                    j.printStackTrace();
                    Log.d("json exception: ", "send reservation details to server");
                }

                new SendtoServer().execute(server_url, json.toString());

            }
        });
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


                //POST JSON.tostring()
                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes("PostData=" + params[1]);
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
            Log.d("reservation response: ", result);

            Toast.makeText(getApplicationContext(), "Reservation completed", Toast.LENGTH_LONG).show();

            //todo store voucher from response in sharedpreferences
            //todo launch pending arrival activity
        }
    }
}
