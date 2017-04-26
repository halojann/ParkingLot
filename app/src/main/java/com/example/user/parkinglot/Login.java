package com.example.user.parkinglot;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends Activity {

    TextView acc,psw;
    Button button3,button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final HttpProcess httpProcess= new HttpProcess();

        acc = (TextView) findViewById(R.id.editText);
        psw = (TextView) findViewById(R.id.editText2);
        button3 = (Button) findViewById(R.id.button3);
        button2 = (Button) findViewById(R.id.button2);
        button3.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                JSONObject send = null;
                try {
                    send.put("account",acc.getText().toString()) ;
                    send.put("password",psw.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                httpProcess.setcontent("http://10.109.106.250:8000/accounts/hello/",send);
                httpProcess.execute();
            }
        });
        button2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });

    }


    private class HttpProcess extends AsyncTask<String, Void, String> {
        String destination =null;
        JSONObject content = null;
        @Override
        protected String doInBackground(String... params) {
            JSONObject account = new JSONObject(),send=new JSONObject();

            String result="";

            try {

                URL url = new URL(destination);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(30000);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                //connection.setChunkedStreamingMode(0);
                connection.setRequestMethod("POST");
                account.put("username","Chenglong Fu");
                account.put("password","fcl199303");
                connection.getOutputStream().write(String.valueOf(account).getBytes());//把数据以流的方式写给服务器。
                connection.getOutputStream().close();
                int code = connection.getResponseCode();
                Log.i("code==" ,String.valueOf(code));
                Log.i("status code: ",String.valueOf(connection.getResponseCode()));
                InputStream in = connection.getInputStream();
                //Read Result
                int val;
                while ((val=in.read()) > 0) {
                    result += (char) val;
                }
                in.close();

            } catch(Exception e) {
                e.printStackTrace();
            }
            return result;
        }



        @Override
        protected void onPostExecute(String result)
        {
            JSONObject out = null;
            try {
                out = new JSONObject(result);
                if(out.getString("status").equals("invalid")){
                    Toast.makeText(com.example.user.parkinglot.Login.this,"login failed, please check the input or sign up",Toast.LENGTH_LONG).show();
                }
                if(out.getString("status").equals("valid")){
                    Intent gotomap = new Intent(com.example.user.parkinglot.Login.this,com.example.user.parkinglot.MapsActivity.class);
                    gotomap.putExtra("username",acc.getText().toString());
                    gotomap.putExtra("password",psw.getText().toString());
                    startActivity(gotomap);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        protected void setcontent(String dest, JSONObject cont){
            destination = dest;
            content = cont;
        }

    }


}