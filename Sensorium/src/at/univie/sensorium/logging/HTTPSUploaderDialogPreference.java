package at.univie.sensorium.logging;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.Toast;
import at.univie.sensorium.R;

public class HTTPSUploaderDialogPreference extends DialogPreference {

	public HTTPSUploaderDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.upload_dialogpreference);
		setTitle("Upload Data");
		
	}
	
	@Override
	protected void onClick() {
		Toast x = Toast.makeText(getContext(), "test", Toast.LENGTH_SHORT);
		x.show();
	}

}
