package temple.edu.operator;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Window;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class login extends Activity {
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences("account", Context.MODE_PRIVATE);
        String unm =settings.getString("name", "");
        String psd =settings.getString("password", "");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        tx.add(R.id.id_content, new LoginFragment(),"ONE");
        tx.commit();


//        httpProcess= new HttpProcess();
//
//        button.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                try {
//                    httpProcess.setcontent("",new JSONObject("{'username':'"+username_input.getText().toString()+"','password':'"+password_input.getText().toString()+"'}"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });


    }

    private Intent processBeam(Intent intent){
        //JSONObject jsontoken=null;
        this.setIntent(intent);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        String messageString = new String(msg.getRecords()[0].getPayload());
//        try {
//            jsontoken = new JSONObject(messageString);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        if(extract_type(intent).equals("com")) {
            Log.d("receiving","a coming");
            Intent starter = new Intent(this, temple.edu.operator.token.class);
            starter.putExtra("token", messageString);
            Log.d("received string: ",messageString);
            return starter;

        }
        else if(extract_type(intent).equals("lev")){
            Log.d("receiving","a leaving");
            Intent starter = new Intent(this, temple.edu.operator.token.class);
            starter.putExtra("transaction_no", messageString);
            return starter;
        }
        return null;
    }

    public void savePublic(String pubkey){
        OutputStream outputStream = null;
        try {
            outputStream = this.openFileOutput("publickey.pem", Context.MODE_PRIVATE);
            outputStream.write(pubkey.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String extract_type(Intent intent){
        String tag = intent.getDataString();
        String type = tag.substring(tag.length()-3);
        return type;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(settings.getBoolean("isLogin",false)){
            if(getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)){
                startActivity(processBeam(getIntent()));
            }
            else{
//                Intent starter = new Intent(this,temple.edu.operator.token.class);
//                startActivity(starter);
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        if(settings.getBoolean("isLogin",false)) {
            startActivity(processBeam(getIntent()));
        }
    }


//    private class HttpProcess extends AsyncTask<String, Void, String> {
//        String destination =null;
//        JSONObject content = null;
//        @Override
//        protected String doInBackground(String... params) {
//            JSONObject account = new JSONObject(),send=new JSONObject();
//
//            String result="";
//
//            try {
//
//                URL url = new URL(destination);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setConnectTimeout(30000);
//                connection.setRequestProperty("Content-Type", "application/json");
//                connection.setDoOutput(true);
//                connection.setDoInput(true);
//                connection.setUseCaches(false);
//                //connection.setChunkedStreamingMode(0);
//                connection.setRequestMethod("POST");
//                account.put("username",content.getString("username"));
//                account.put("password",content.getString("password"));
//                connection.getOutputStream().write(String.valueOf(account).getBytes());//把数据以流的方式写给服务器。
//                connection.getOutputStream().close();
//                int code = connection.getResponseCode();
//                Log.i("code==" ,String.valueOf(code));
//                Log.i("status code: ",String.valueOf(connection.getResponseCode()));
//                InputStream in = connection.getInputStream();
//                //Read Result
//                int val;
//                while ((val=in.read()) > 0) {
//                    result += (char) val;
//                }
//                in.close();
//
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//            return result;
//        }
//
//
//
//        @Override
//        protected void onPostExecute(String result)
//        {
//            JSONObject out = null;
//            try {
//                out = new JSONObject(result);
//                if(out.getString("status").equals("invalid")){
//                    Toast.makeText(temple.edu.operator.login.this,"login failed, please check the input or sign up",Toast.LENGTH_LONG).show();
//                }
//                if(out.getString("status").equals("valid")){
////                    Intent gotomap = new Intent(temple.edu.operator.login.this,com.example.user.parkinglot.MapsActivity.class);
////                    gotomap.putExtra("username",username_input.getText().toString());
////                    gotomap.putExtra("password",password_input.getText().toString());
//                    SharedPreferences.Editor editor = settings.edit();
//                    editor.putString("username",username_input.getText().toString());
//                    editor.putString("password",password_input.getText().toString());
//                    editor.putBoolean("isLogin",true);
//                    editor.commit();
//
//                    //startActivity(gotomap);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//        }
//
//        protected void setcontent(String dest, JSONObject cont){
//            destination = dest;
//            content = cont;
//        }
//
//    }

}
