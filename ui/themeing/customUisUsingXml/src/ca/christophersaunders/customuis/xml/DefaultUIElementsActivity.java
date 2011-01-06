package ca.christophersaunders.customuis.xml;

import android.app.Activity;
import android.os.Bundle;

public class DefaultUIElementsActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_layout);
    }
}
