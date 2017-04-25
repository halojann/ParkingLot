package com.example.user.parkinglot;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Beam extends Activity implements NfcAdapter.CreateNdefMessageCallback {
    byte[] token;
    NfcAdapter mNfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter[] intentFiltersArray;
    NdefMessage msg=null;
    TextView textView1,textView2,textView3;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);
        textView1=(TextView)findViewById(R.id.textView) ;
        textView2=(TextView)findViewById(R.id.textView2) ;
        textView3=(TextView)findViewById(R.id.textView3) ;
        button = (Button)findViewById(R.id.button);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefFilter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[] {ndefFilter};

    }

    protected void send(View view){
        if (msg==null){

        }
        else {
            mNfcAdapter.setNdefPushMessage(msg, this, this);
        }
    }

    private void sendToken(Intent intent){
        JSONObject info = null;
        try {

            info = new JSONObject();
            info.put("username",intent.getStringExtra("username"));
            info.put("parkinglot",intent.getStringExtra("parkinglot"));
            info.put("start",intent.getLongExtra("start",0));
            info.put("end",intent.getLongExtra("end",0));
            info.put("signature",intent.getStringExtra("signature"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Date start=null,end=null;
        start.setTime(intent.getLongExtra("start",0));
        end.setTime(intent.getLongExtra("end",0));
        textView1.setText("username:  "+intent.getStringExtra("username"));
        textView2.setText("parkinglot:  "+intent.getStringExtra("parkinglot"));
        textView3.setText("duration:   "+ start.toString()+"    --    "+end.toString());
        //SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        msg = new NdefMessage(new NdefRecord[]{
                NdefRecord.createExternal("com.example.user.parkinglot","token",String.valueOf(info).getBytes())
        });



    }



    private void processBeam(Intent intent){
        setIntent(intent);



    }

    @Override
    public void onResume(){
        super.onResume();
        if (NfcAdapter.getDefaultAdapter(this) != null) {
            NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null);
            // Check to see that the Activity started due to an Android Beam
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                //processBeam(getIntent());
                //textView.setText(getIntent().toString());
                processBeam(getIntent());
            }
            else{
                sendToken(getIntent());
            }
        }
    }
    @Override
    public  void onPause(){
        super.onPause();
        if (mNfcAdapter != null) {
            try {
                // Disable foreground dispatch:
                mNfcAdapter.disableForegroundDispatch(this);
            } catch (NullPointerException e) {
                // Drop NullPointerException that is sometimes thrown
                // when NFC service crashed
            }
        }
    }
    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)){
            processBeam(intent);
        }
        else{
            sendToken(intent);
        }
    }


    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] {
                        NdefRecord.createExternal("com.example.user.parkinglot", "tkn", token)
//                new NdefRecord[] { NdefRecord.createMime(
//                        "application/acer.example.com.nfcbeam", keyfortransfer)
                });
        return msg;
    }

}
