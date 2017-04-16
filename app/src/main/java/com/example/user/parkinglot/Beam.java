package com.example.user.parkinglot;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;

public class Beam extends Activity implements NfcAdapter.CreateNdefMessageCallback {
    byte[] token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);
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
