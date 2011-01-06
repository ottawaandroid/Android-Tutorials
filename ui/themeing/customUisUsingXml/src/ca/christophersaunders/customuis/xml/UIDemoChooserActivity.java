package ca.christophersaunders.customuis.xml;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

public class UIDemoChooserActivity extends Activity implements OnClickListener
{
    private Button defaultUIButton, customUIButton;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

	defaultUIButton = (Button) findViewById(R.id.defaultUIButton);
	defaultUIButton.setOnClickListener(this);
	
	customUIButton = (Button) findViewById(R.id.customUIButton);
	customUIButton.setOnClickListener(this);
    }

    public void onClick(View v){
	if(v == defaultUIButton){
	    startActivity( new Intent(this, DefaultUIElementsActivity.class) );
	} else if( v == customUIButton ){
	    startActivity( new Intent(this, CustomUIElementsActivity.class));
	}
    }
}
