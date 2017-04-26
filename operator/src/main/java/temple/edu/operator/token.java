package temple.edu.operator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;

public class token extends Activity {
    String str, server_url = "http://10.109.106.250:8000/accounts/arrive/";
    JSONObject token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

    }

    private void processintent(Intent intent) throws JSONException {
        intent.getStringExtra("token");
        token = new JSONObject(intent.getStringExtra("token"));
        if (token != null) {
            try {
                String data = token.getString("username") + token.getString("parkinglot") + token.getString("start") + token.getString("end") + token.getString("transaction");
                byte[] signature = token.getString("signature").getBytes();
                if (verifySignature(data.getBytes(), signature, readPublicKey())) {
                    Toast.makeText(this, "verification successful", Toast.LENGTH_LONG).show();

                    //notify server of user arrival
                    JSONObject json = new JSONObject();
                    json.put("transaction", token.getString("transaction"));
                    new SendtoServer().execute(server_url, json.toString());
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("token parse:  ", "token can't parse");
            }
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

    public PublicKey readPublicKey() {
        String res = "";
        try {
            FileInputStream fin = openFileInput("public_key");
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            fin.close();
            X509EncodedKeySpec spec =
                    new X509EncodedKeySpec(buffer);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


//    protected PublicKey getpublickey(Uri keypath){
//        PublicKey PublicKey = null;
//        BufferedReader br = null;
//        try {
//            br = new BufferedReader(new FileReader("my-prvkey.pem"));
//            String s = br.readLine();
//            str = "";
//            s = br.readLine();
//            while (s.charAt(0) != '-'){
//                str += s + "\r";
//                s = br.readLine();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        //BASE64 base64decoder = new BASE64Decoder();
//        byte[] b = Base64.decode(str,Base64.DEFAULT);
//
////生成私匙
//        KeyFactory kf = null;
//        try {
//            kf = KeyFactory.getInstance("RSA");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(b);
//
//        //PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(b);
////PublicKey privateKey = kf.generatePublic(keySpec);
//        try {
//            PublicKey =  kf.generatePublic(keySpec);
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
//        return PublicKey;
//    }

    protected Boolean verifySignature(byte[] data, byte[] token, PublicKey publicKey) {
        Signature s = null;
        boolean valid = false;
        try {
            s = Signature.getInstance("SHA256withRSA/PSS");
            s.initVerify(publicKey);
            s.update(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        //s.initVerify(((PrivateKeyEntry) entry).getCertificate());
        //s.update(data);
        try {
            valid = s.verify(token);
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return valid;
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
            Log.d("arrival response: ", result);
            Toast.makeText(getApplicationContext(), "Arrival Notified", Toast.LENGTH_LONG).show();
            String status = "";

            try {
                JSONObject json = new JSONObject(result);
                status = json.getString("status");

            } catch (JSONException j) {
                j.printStackTrace();
                Log.d("json status ", status);
            }

        }
    }
}
