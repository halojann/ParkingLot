package temple.edu.operator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class login extends Activity {
    EditText username_input,password_input;
    Button button;
    HttpProcess httpProcess = null;
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_login);
        username_input = (EditText)findViewById(R.id.editText);
        password_input = (EditText)findViewById(R.id.editText2);
        button  = (Button)findViewById(R.id.button2);

        String unm =settings.getString("name", "");
        String psd =settings.getString("password", "");
        if(!unm.isEmpty()){
            username_input.setText(unm);
            password_input.setText(psd);
        }
        httpProcess= new HttpProcess();

        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try {
                    httpProcess.setcontent("",new JSONObject("{'username':'"+username_input.getText().toString()+"','password':'"+password_input.getText().toString()+"'}"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private Intent processBeam(Intent intent){
        JSONObject jsontoken=null;
        this.setIntent(intent);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        String messageString = new String(msg.getRecords()[0].getPayload());
        try {
            jsontoken = new JSONObject(messageString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsontoken!=null){
            Intent starter = new Intent(this,temple.edu.operator.token.class);
            starter.putExtra("token",jsontoken.toString());
            return starter;
        }
        return null;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(settings.getBoolean("isLogin",false)){
            if(getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)){
                startActivity(processBeam(getIntent()));
            }
            else{
                Intent starter = new Intent(this,temple.edu.operator.token.class);
                startActivity(starter);
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        startActivity(processBeam(getIntent()));
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
                account.put("username",content.getString("username"));
                account.put("password",content.getString("password"));
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
                    Toast.makeText(temple.edu.operator.login.this,"login failed, please check the input or sign up",Toast.LENGTH_LONG).show();
                }
                if(out.getString("status").equals("valid")){
//                    Intent gotomap = new Intent(temple.edu.operator.login.this,com.example.user.parkinglot.MapsActivity.class);
//                    gotomap.putExtra("username",username_input.getText().toString());
//                    gotomap.putExtra("password",password_input.getText().toString());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("username",username_input.getText().toString());
                    editor.putString("password",password_input.getText().toString());
                    editor.putBoolean("isLogin",true);
                    editor.commit();

                    //startActivity(gotomap);
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
