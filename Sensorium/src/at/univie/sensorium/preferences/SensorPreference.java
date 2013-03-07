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

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import at.univie.sensorium.R;
import at.univie.sensorium.privacy.Privacy;
import at.univie.sensorium.privacy.Privacy.PrivacyLevel;
import at.univie.sensorium.sensors.AbstractSensor;

public class SensorPreference extends Preference implements OnSeekBarChangeListener {

	private final int mMaxValue = Privacy.PrivacyLevel.FULL.value();

	/**
	 * represents the current privacy level value, NOT the slider pos value!
	 */
	private int mCurrentValue;

	private AbstractSensor sensor;

	private SeekBar mPrivacyLevel;
	private TextView mStatusText;

	public SensorPreference(Context context, AbstractSensor sensor) {
		super(context);
		init(context);
		this.sensor = sensor;
	}

	public SensorPreference(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mPrivacyLevel = new SeekBar(context);
		mPrivacyLevel.setMax(mMaxValue);
		mPrivacyLevel.setOnSeekBarChangeListener(this);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) mInflater.inflate(R.layout.sensor_preference_item, parent, false);

		TextView title = (TextView) layout.findViewById(R.id.seekBarTitle);
		title.setText(sensor.getName());
		TextView summary = (TextView) layout.findViewById(R.id.seekBarSummary);
		summary.setText(sensor.getDescription());

		return layout;
	}

	@Override
	public void onBindView(View view) {
		super.onBindView(view);

		ViewParent oldContainer = mPrivacyLevel.getParent();
		ViewGroup newContainer = (ViewGroup) view.findViewById(R.id.seekBarPrefBarContainer);

		if (oldContainer != newContainer) {
			if (oldContainer != null) {
				((ViewGroup) oldContainer).removeView(mPrivacyLevel);
			}
			newContainer.removeAllViews();
			newContainer.addView(mPrivacyLevel, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		updateView(view);
	}

	protected void updateView(View view) {
		RelativeLayout layout = (RelativeLayout) view;
		mStatusText = (TextView) layout.findViewById(R.id.seekBarPrefValue);
		mStatusText.setText(sensor.getSensorStateDescription());
		mStatusText.setMinimumWidth(30);
		mPrivacyLevel.setProgress(mMaxValue - mCurrentValue);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		// slider is reverse to the actual values, so subtract maxval
		int newValue = mMaxValue - progress;

		sensor.setPrivacylevel(Privacy.PrivacyLevel.fromInt(newValue));
		if (newValue == Privacy.PrivacyLevel.FULL.value() && sensor.isEnabled()) {
			sensor.disable();
			Log.d("SeattleSensors", "trying to disable " + sensor.getName());
		} else if (mCurrentValue == Privacy.PrivacyLevel.FULL.value() && mCurrentValue != newValue && !sensor.isEnabled()) {
			Log.d("SeattleSensors", "trying to enable " + sensor.getName());
			sensor.enable();
		}
		mCurrentValue = newValue;

		mStatusText.setText(sensor.getSensorStateDescription());
		persistInt(newValue);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@Override
	protected Object onGetDefaultValue(TypedArray ta, int index) {
		return ta.getInt(index, PrivacyLevel.FULL.value());
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		if (restoreValue) {
			mCurrentValue = getPersistedInt(mCurrentValue);
		} else {
			mCurrentValue = (Integer) defaultValue;
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}
}