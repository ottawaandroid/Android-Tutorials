package ca.christophersaunders.demos.crest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.view.View;

import org.codegist.crest.CRestBuilder;
import org.codegist.crest.CRest;

public class CRestDemoActivity extends Activity implements OnClickListener
{
	Button get, post, put, delete;
	EditText parameters, body;
	TextView response;

	CRest crest;
	PlantsService plantService;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        crest = new CRestBuilder()
        					.expectsJson()
        					.build();
        plantService = crest.build(PlantsService.class);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        get = fabricateButton("GET");
        post = fabricateButton("POST");
        put = fabricateButton("PUT");
        delete = fabricateButton("DELETE");

        parameters = fabricateEditText("Parameters (i.e 2)");
        body = fabricateEditText("Body (i.e JSON data)");

        response = new TextView(this);

        addTo(root,
        	new View[] {
	        	get, post, put, delete,
	        	parameters, body, response,
	        	}
        	);

        setContentView(root);
    }

    private Button fabricateButton(String title){
    	Button button = new Button(this);
    	button.setOnClickListener(this);
    	button.setText(title);
    	return button;
    }

    private EditText fabricateEditText(String hint){
    	EditText edit = new EditText(this);
    	edit.setHint(hint);
    	return edit;
    }

    private void addTo(LinearLayout parent, View[] children){
    	for(View child : children){
    		parent.addView(child);
    	}
    }

    private String getParams(){
        return parameters.getText().toString();
    }

    private String getBody(){
        return body.getText().toString();
    }

    public void onClick(View v){
        String params = getParams();
        String body = getBody();
    	if(v == get){
    		if(params.equals("count")){
              response.setText(String.valueOf(plantService.getCount()));
            } else if(params.length() == 0){
                response.setText(plantService.getPlants().toString());
            } else {
                response.setText(plantService.getPlant(Integer.parseInt(getParams())).toString());
            }
    	} else if(v == post){
    		// TODO: Perform the POST request
    	} else if(v == put){
    		// TODO: Perform the PUT request
    	} else if(v == delete){
    		// TODO: Perform the DELETE request
    	}
    }
}
