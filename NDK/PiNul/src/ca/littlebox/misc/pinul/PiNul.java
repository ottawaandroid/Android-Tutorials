package ca.littlebox.misc.pinul;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PiNul extends Activity {
	Button pi_button, nul_button;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        pi_button = (Button) this.findViewById(R.id.pi_button);        
        pi_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	long start_time = System.currentTimeMillis();
            	// Calculate 5000 digits of Pi
            	pi(5000);
            	long stop_time = System.currentTimeMillis();
            	Toast.makeText(PiNul.this, "Pi took " + (stop_time - start_time) + " ms.", Toast.LENGTH_LONG).show();            
            }
        });

        nul_button = (Button) this.findViewById(R.id.nul_button);
        nul_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	nul();
            	Toast.makeText(PiNul.this, "Huh?  I didn't crash?", Toast.LENGTH_LONG).show();        
            }
        });
    }
    
    // Load our native library
	static {
    	System.loadLibrary("pinul");
    }
	
	// Function stubs for native code
	private native int pi(int digits);
	private native void nul();
}