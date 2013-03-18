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

package at.univie.sensorium.preferences;

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
import at.univie.sensorium.SensorRegistry;
import at.univie.sensorium.R;

public class HTTPSUploaderDialogPreference extends DialogPreference {

	private EditText url;
	private Spinner intervalSel;
	private CheckBox automatic;
	private CheckBox wifi;

	public HTTPSUploaderDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.upload_dialogpreference);
		setTitle("HTTPS Upload");
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		url = (EditText) view.findViewById(R.id.uploadurl_text);

		intervalSel = (Spinner) view.findViewById(R.id.upload_interval_selection);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.upload_intervals, android.R.layout.simple_spinner_item);

		intervalSel.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				String selected = parent.getItemAtPosition(pos).toString();
				Log.d(SensorRegistry.TAG, "new http upload interval selected: " + selected);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// this shouldn't happen
			}

		});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		intervalSel.setAdapter(adapter);

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
	

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		Log.d(SensorRegistry.TAG, "dialog closed");
		super.onDialogClosed(positiveResult);

//		if (positiveResult) { // save it even on cancel, to be on the safe side
			Editor editor = getEditor();

			editor.putString(Preferences.UPLOAD_URL_PREF, url.getText().toString());
			editor.putBoolean(Preferences.UPLOAD_AUTOMATIC_PREF, automatic.isChecked());
			editor.putBoolean(Preferences.UPLOAD_WIFI_PREF, wifi.isChecked());
			editor.putInt(Preferences.UPLOAD_INTERVAL_PREF, intervalSel.getSelectedItemPosition());
			editor.commit();
			
			// alternative var storing
			Preferences prefs = SensorRegistry.getInstance().getPreferences();
			prefs.putString(Preferences.UPLOAD_URL_PREF, url.getText().toString());
			prefs.putBoolean(Preferences.UPLOAD_AUTOMATIC_PREF, automatic.isChecked());
			prefs.putBoolean(Preferences.UPLOAD_WIFI_PREF, wifi.isChecked());
			prefs.putInt(Preferences.UPLOAD_INTERVAL_PREF, intervalSel.getSelectedItemPosition());
//		}
	}

	protected void populateDialog() {
		SharedPreferences sPref = getSharedPreferences();
		String sUrl = sPref.getString(Preferences.UPLOAD_URL_PREF, "");
		Boolean bAuto = sPref.getBoolean(Preferences.UPLOAD_AUTOMATIC_PREF, true);
		Boolean bWifi = sPref.getBoolean(Preferences.UPLOAD_WIFI_PREF, false);
		Integer lInterval = sPref.getInt(Preferences.UPLOAD_INTERVAL_PREF, 3600);

		url.setText(sUrl);
		automatic.setChecked(bAuto);
		wifi.setChecked(bWifi);
		intervalSel.setSelection(getSpinnerPosForInterval(lInterval));
	}

	protected int retrieveInterval() {
		int interval;
		if (intervalSel.getSelectedItem().equals("1h")) {
			interval = 3600;
		} else if (intervalSel.getSelectedItem().equals("1d")) {
			interval = 86400; // 24*3600s
		} else { // default value
			interval = 3600;
		}
		return interval;
	}

	protected int getSpinnerPosForInterval(int interval) {
		int pos;
		if (interval == 3600) {
			pos = ((ArrayAdapter<String>) intervalSel.getAdapter()).getPosition("1h");
		} else if (interval == 86400) {
			pos = ((ArrayAdapter<String>) intervalSel.getAdapter()).getPosition("1d");
		} else {
			pos = 0;
		}
		return pos;
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		populateDialog();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick();

		// TODO: update persisted values only when OK button pressed

		if (which == Dialog.BUTTON_POSITIVE) {
			Log.d(SensorRegistry.TAG, "OK pressed");
			if (automatic.isChecked()) {
				// update the prefs first
				SensorRegistry.getInstance().getJSONLogger().autoupload(url.getText().toString(), retrieveInterval(), wifi.isChecked());
			} else {
				SensorRegistry.getInstance().getJSONLogger().cancelautoupload();
			}
		} else if (which == Dialog.BUTTON_NEGATIVE) {
			// don't change anything
		}
		getDialog().dismiss();
	}

}
