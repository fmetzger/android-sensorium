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

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import at.univie.sensorium.R;
import at.univie.sensorium.SensorRegistry;

public class HTTPSUploaderDialogPreference extends DialogPreference {

	EditText url;

	public HTTPSUploaderDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.upload_dialogpreference);
		setTitle("HTTPS Upload");
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		url = (EditText) view.findViewById(R.id.uploadurl_text);
		url.setText("http://homepage.univie.ac.at/lukas.puehringer/multipart/multipart.php");

		Spinner spinner = (Spinner) view.findViewById(R.id.upload_interval_selection);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.upload_intervals, android.R.layout.simple_spinner_item);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		Button button = (Button) view.findViewById(R.id.uploadpref_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SensorRegistry.getInstance().getJSONLogger().upload(url.getText().toString());
			}
		});
	}
}
