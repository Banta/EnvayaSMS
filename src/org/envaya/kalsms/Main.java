package org.envaya.kalsms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.Date;

public class Main extends Activity {   
	
    private BroadcastReceiver logReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {  
            showLogMessage(intent.getExtras().getString("message"));
        }
    };
    
    private long lastLogTime = 0;

    public void showLogMessage(String message)
    {
        TextView info = (TextView) Main.this.findViewById(R.id.info);        
        if (message != null)
        {                                                
            int length = info.length();
            int maxLength = 20000;
            if (length > maxLength)
            {
                CharSequence text = info.getText();
                
                int startPos = length - maxLength / 2;
                
                for (int cur = startPos; cur < startPos + 100 && cur < length; cur++)
                {
                    if (text.charAt(cur) == '\n')
                    {
                        startPos = cur;
                        break;
                    }
                }
                
                CharSequence endSequence = text.subSequence(startPos, length);
                
                info.setText("[Older log messages not shown]");
                info.append(endSequence);
            }
            
            long logTime = System.currentTimeMillis();
            if (logTime - lastLogTime > 60000)
            {
                Date date = new Date(logTime);                
                info.append("[" + DateFormat.getTimeInstance().format(date) + "]\n");                
                lastLogTime = logTime;
            }            
            
            info.append(message + "\n");
            
            final ScrollView scrollView = (ScrollView) this.findViewById(R.id.info_scroll);
            scrollView.post(new Runnable() { public void run() { 
                scrollView.fullScroll(View.FOCUS_DOWN);
            } });
        }        
    }
        
    
    public void onResume() {
        App.debug("RESUME");
        super.onResume();                                		
    }	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        App.debug("STARTED");
        
        App app = App.getInstance(this.getApplication());
        
        setContentView(R.layout.main);
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);               
        
        TextView info = (TextView) this.findViewById(R.id.info);
        
        info.setText(Html.fromHtml("<b>SMS Gateway running.</b><br />"));                        
        info.append(Html.fromHtml("<b>Press Menu to edit settings.</b><br />"));                
        
        showLogMessage("Server URL is: " + app.getDisplayString(app.getServerUrl()));
        showLogMessage("Your phone number is: " + app.getDisplayString(app.getPhoneNumber()));
        
        info.setMovementMethod(new ScrollingMovementMethod());        
        
        IntentFilter logReceiverFilter = new IntentFilter();        
        logReceiverFilter.addAction(App.LOG_INTENT);
        registerReceiver(logReceiver, logReceiverFilter);        
        
        app.setOutgoingMessageAlarm();     
        
        for (int i = 0; i < 30; i++)
        {
            showLogMessage(" " + i);   
        }
    }
    
    // first time the Menu key is pressed
    public boolean onCreateOptionsMenu(Menu menu) {
        startActivity(new Intent(this, Prefs.class));
        return(true);
    }

    // any other time the Menu key is pressed
    public boolean onPrepareOptionsMenu(Menu menu) {
        startActivity(new Intent(this, Prefs.class));
        return(true);
    }
	    
    @Override
    protected void onStop(){
    	// dont do much with this, atm..
    	super.onStop();
    }
    
}