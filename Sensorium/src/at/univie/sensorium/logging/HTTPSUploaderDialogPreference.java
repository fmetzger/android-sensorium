package at.univie.sensorium.logging;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import at.univie.sensorium.R;
import at.univie.sensorium.SensorRegistry;

public class HTTPSUploaderDialogPreference extends DialogPreference {

	public HTTPSUploaderDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.upload_dialogpreference);
		setTitle("Upload Data");
	}
	
	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		
		Button button = (Button) view.findViewById(R.id.uploadpref_button);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast x = Toast.makeText(getContext(), "Starting upload...", Toast.LENGTH_SHORT);
				x.show();
				SensorRegistry.getInstance().getJSONLogger().upload();
			}
		});
	}
	
//	@Override
//	protected void onClick() {
//		Toast x = Toast.makeText(getContext(), "Starting upload...", Toast.LENGTH_SHORT);
//		x.show();
//		SensorRegistry.getInstance().getJSONLogger().upload();
//	}

}
