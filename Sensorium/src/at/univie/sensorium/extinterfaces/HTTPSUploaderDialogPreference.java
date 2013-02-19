/*
 *  This file is part of Sensorium.
 *
 *   Sensorium is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Sensorium is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Sensorium. If not, see
 *   <http://www.gnu.org/licenses/>.
 * 
 * 
 */

package at.univie.sensorium.extinterfaces;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import at.univie.sensorium.R;
import at.univie.sensorium.SensorRegistry;

public class HTTPSUploaderDialogPreference extends DialogPreference {

	EditText url;
	Spinner interval;
	CheckBox automatic;
	CheckBox wifi;

	public HTTPSUploaderDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.upload_dialogpreference);
		setTitle("HTTPS Upload");
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		url = (EditText) view.findViewById(R.id.uploadurl_text);
//		url.setText("http://homepage.univie.ac.at/lukas.puehringer/multipart/multipart.php");

		interval = (Spinner) view.findViewById(R.id.upload_interval_selection);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.upload_intervals, android.R.layout.simple_spinner_item);

		interval.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				String selected = parent.getItemAtPosition(pos).toString();
				Log.d("SEATTLESPINNER", selected);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		interval.setAdapter(adapter);

		automatic = (CheckBox) view.findViewById(R.id.upload_automatic_toggle);
		wifi = (CheckBox) view.findViewById(R.id.upload_automatic_require_wifi);

		Button button = (Button) view.findViewById(R.id.uploadpref_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SensorRegistry.getInstance().getJSONLogger().upload(url.getText().toString());
			}
		});
		
		populateDialog();
		
	}

	public static final String UPLOAD_URL_PREF = "upload_url";
	public static final String UPLOAD_AUTOMATIC_PREF = "upload_automatic";
	public static final String UPLOAD_WIFI_PREF = "upload_wifi";
	public static final String UPLOAD_INTERVAL_PREF = "upload_interval";

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			Editor editor = getEditor();

			editor.putString(UPLOAD_URL_PREF, url.getText().toString());
			editor.putBoolean(UPLOAD_AUTOMATIC_PREF, automatic.isChecked());
			editor.putBoolean(UPLOAD_WIFI_PREF, wifi.isChecked());
			editor.putInt(UPLOAD_INTERVAL_PREF, interval.getSelectedItemPosition()); // Item
																						// or
																						// ItemID
																						// better?
			editor.commit();
		}
	}
	
	protected void populateDialog(){
		SharedPreferences sPref = getSharedPreferences();
		String sUrl = sPref.getString(UPLOAD_URL_PREF, "http://homepage.univie.ac.at/lukas.puehringer/multipart/multipart.php");
		Boolean bAuto = sPref.getBoolean(UPLOAD_AUTOMATIC_PREF, true);
		Boolean bWifi = sPref.getBoolean(UPLOAD_WIFI_PREF, false);
		Integer iIntervalPos = sPref.getInt(UPLOAD_INTERVAL_PREF, 0);
		
		url.setText(sUrl);
		automatic.setChecked(bAuto);
		wifi.setChecked(bWifi);
		interval.setSelection(iIntervalPos);
	}
	

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		populateDialog();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick();
		if ( which == Dialog.BUTTON_POSITIVE){
			
		} else if (which == Dialog.BUTTON_NEGATIVE){
			
		}
	}

}
