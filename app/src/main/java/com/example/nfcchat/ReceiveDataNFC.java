package com.example.nfcchat;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;


public class ReceiveDataNFC extends FragmentActivity{
	public static String data = "Hola";
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_message);
        Toast.makeText(getApplicationContext(), "The message was received", Toast.LENGTH_LONG).show();
          
    }

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		setIntent(intent);
	}
	
	
	/**
	* Parses the NDEF Message from the intent and prints to the TextView
	*/
	void processIntent(Intent intent) {
	
		//boolean updateList;
		//UtilsListView UtilsLiVim = new UtilsListView();
		
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
		        NfcAdapter.EXTRA_NDEF_MESSAGES);
		
		
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		if (msg != null)
		{
			// record 0 contains the MIME type, record 1 is the AAR, if present
			String str = new String(msg.getRecords()[0].getPayload());
			Toast.makeText(getApplicationContext(), "The message was received", Toast.LENGTH_LONG).show();
			Intent i = new Intent(this, MainActivity.class);
			i.putExtra("str", str);
			//data = str;
	        startActivity(i);
			
		}else
		{
			Toast.makeText(getApplicationContext(), "Not message received", Toast.LENGTH_LONG).show();
		}
	}
	
	
    @Override
    protected void onResume() {
        super.onResume();
       	// Check to see that the Activity started due to an Android Beam
    	Toast.makeText(getApplicationContext(), "The message was received", Toast.LENGTH_LONG).show();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
    	    processIntent(getIntent());
    	}
        
        

    }


}