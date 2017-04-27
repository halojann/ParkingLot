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

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReserveLot extends Activity {

    SharedPreferences settings;
    String username, lotname;

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
        final String server_url = "http://ssh.missingrain.live:8000/accounts/reserve/";

        settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        username = settings.getString("name", "");

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

        EditText time_duration = (EditText) findViewById(R.id.editText7);
        time_duration.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final String duration = time_duration.getText().toString();

        Button button = (Button) findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("parkinglot_name", lotname);
                    json.put("duration", duration);
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

            SharedPreferences.Editor editor = getSharedPreferences("account", Context.MODE_PRIVATE).edit();
            editor.putString("token", token);
            editor.apply();


            Intent intent = new Intent(com.example.user.parkinglot.ReserveLot.this, com.example.user.parkinglot.Beam.class);
            intent.putExtra("username", username);
            intent.putExtra("parkinglot", lotname);
            intent.putExtra("start", start);
            intent.putExtra("end", end);
            intent.putExtra("transaction", transaction);
            intent.putExtra("signature", token);
            startActivity(intent);
        }
    }
}
