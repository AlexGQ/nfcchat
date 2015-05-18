package com.example.nfcchat;

import java.nio.charset.Charset;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends FragmentActivity implements CreateNdefMessageCallback,OnNdefPushCompleteCallback{
	
	
	private NfcAdapter mNfcAdapter;
	private static final int MESSAGE_SENT = 1;
	private TextView receiveText;
	private TextView sendText;
	//private SendDataNFC mSendDataNFC;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		 
		
		if (mNfcAdapter == null) {
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
		      return;
		}
		else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			 // If Android Beam isn't available, don't continue. 
			Toast.makeText(this, "Android Beam is not available", Toast.LENGTH_LONG).show();
		      
		} else
		{
			// Register callback to set NDEF message
			mNfcAdapter.setNdefPushMessageCallback(this, this);
			// Register callback to listen for message-sent success
			mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
			
		}
		
		 receiveText = (TextView) findViewById(R.id.ReceiveText);
		 sendText = (TextView)findViewById(R.id.SendText);
		 
		 sendText.setEnabled(true);
		 sendText.setFocusableInTouchMode(true);
		 sendText.setClickable(true);
		 
		// You can be pretty confident that the intent will not be null here.
		 Intent intent = getIntent();
		 //receiveText.setText(ReceiveDataNFC.data);

		 // Get the extras (if there are any)
		 Bundle extras = intent.getExtras();
		 if (extras != null) {
		     if (extras.containsKey("str")) {
		    	 String str = getIntent().getStringExtra("str");
		    	 receiveText.setText(str);
		         // TODO: Do something with the value of isNew.
		     }
		 }
		 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
//-----------------------------------------------------------------------------------------------------------------------------------
// Send Data by NFC    
//-----------------------------------------------------------------------------------------------------------------------------------    
    
    /**
	* Implementation for the CreateNdefMessageCallback interface
	*/
				
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		
		//List<Contact> contactListToShare;
		NdefMessage msg = null;
		
		//String cardData = "Hola como estas?";
		String cardData = sendText.getText().toString();
		
		msg = new NdefMessage(
			        new NdefRecord[] { createMimeRecord(
			                "application/com.example.nfcchat", cardData.getBytes()) /**
			           	  * The Android Application Record (AAR) is commented out. When a device
			           	  * receives a push with an AAR in it, the application specified in the AAR
			           	  * is guaranteed to run. The AAR overrides the tag dispatch system.
			           	  * You can add it back in to guarantee that this
			           	  * activity starts when receiving a beamed message. For now, this code
			           	  * uses the tag dispatch system.
			           	  */
			           	  ,NdefRecord.createApplicationRecord("com.example.nfcchat")
			           	});
				
	
		return msg;
	}
	
	/**
	* Implementation for the OnNdefPushCompleteCallback interface
	*/
	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		// A handler is needed to send messages to the activity when this
		// callback occurs, because it happens from a binder thread
		mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}
	
	/** This handler receives a message from onNdefPushComplete */
	private final Handler mHandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    switch (msg.what) {
	    
	    case MESSAGE_SENT:
	        //Toast.makeText(getApplicationContext(), "Card(s) sent!", Toast.LENGTH_LONG).show();
	        Toast.makeText(getApplicationContext(), "Message was sent", Toast.LENGTH_LONG).show();
	        break;
	    }
	}
	};
	
	/**
	* Creates a custom MIME type encapsulated in an NDEF record
	*
	* @param mimeType
	*/
	
	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
	byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
	NdefRecord mimeRecord = new NdefRecord(
	        NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
	return mimeRecord;
	}
		
//-----------------------------------------------------------------------------------------------------------------------------------
// Receive Data by NFC    
//-----------------------------------------------------------------------------------------------------------------------------------    
	/*
	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		setIntent(intent);
	}
	
	
	/**
	* Parses the NDEF Message from the intent and prints to the TextView
	*/
	/*void processIntent(Intent intent) {
	
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
	    	receiveText.setText(str);
			
		}else
		{
			Toast.makeText(getApplicationContext(), "Not message received", Toast.LENGTH_LONG).show();
		}
	}
	
	
    @Override
    protected void onResume() {
        super.onResume();
       	// Check to see that the Activity started due to an Android Beam
    	//Toast.makeText(getApplicationContext(), "The message was received", Toast.LENGTH_LONG).show();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
    	    processIntent(getIntent());
    	}
        
        

    }
*/
}
