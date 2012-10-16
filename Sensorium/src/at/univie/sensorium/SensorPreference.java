package at.univie.sensorium;

import java.io.PrintWriter;
import java.io.StringWriter;

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
import at.univie.sensorium.sensors.AbstractSensor;

/**
 * adapted from: http://robobunny.com/wp/2011/08/13/android-seekbar-preference/
 * 
 * 
 */
public class SensorPreference extends Preference implements OnSeekBarChangeListener {
	
	private int mMaxValue = 100;
	private int mMinValue = 0;
	private int mInterval = 1;
	private int mCurrentValue;
	
	private AbstractSensor sensor;
	
	private SeekBar mPrivacyLevel;
	private TextView mStatusText;

	public SensorPreference(Context context, AbstractSensor sensor) {
		super(context);

		mMaxValue = PrivacyHelper.PrivacyLevel.NO.value();
		mMinValue = PrivacyHelper.PrivacyLevel.FULL.value();

		mInterval = 1;

		mPrivacyLevel = new SeekBar(context);
		mPrivacyLevel.setMax(mMaxValue - mMinValue);
		mPrivacyLevel.setOnSeekBarChangeListener(this);
		
		this.sensor = sensor;
	}
	
	/**
	 * if we are called without a specific sensor, we override all the others
	 * TODO: needs a state in which the seekbar is undefined to display a
	 * 		non-overriding state where all the sensors have individual settings
	 * @param context
	 */
	public SensorPreference(Context context) {
		super(context);

		mMaxValue = PrivacyHelper.PrivacyLevel.NO.value();
		mMinValue = PrivacyHelper.PrivacyLevel.FULL.value();

		mInterval = 1;

		mPrivacyLevel = new SeekBar(context);
		mPrivacyLevel.setMax(mMaxValue - mMinValue);
		mPrivacyLevel.setOnSeekBarChangeListener(this);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {

		RelativeLayout layout = null;

		try {
			LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (RelativeLayout) mInflater.inflate(R.layout.sensor_preference_item, parent, false);
			
			TextView title = (TextView)layout.findViewById(R.id.seekBarTitle);
			title.setText(sensor.getName());
			TextView summary = (TextView)layout.findViewById(R.id.seekBarSummary);
			summary.setText(sensor.getDescription());
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d("SeattleSensors", sw.toString());
		}

		return layout;

	}

	@Override
	public void onBindView(View view) {
		super.onBindView(view);

		try {
			ViewParent oldContainer = mPrivacyLevel.getParent();
			ViewGroup newContainer = (ViewGroup) view.findViewById(R.id.seekBarPrefBarContainer);

			if (oldContainer != newContainer) {
				if (oldContainer != null) {
					((ViewGroup) oldContainer).removeView(mPrivacyLevel);
				}
				newContainer.removeAllViews();
				newContainer.addView(mPrivacyLevel, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d("SeattleSensors", sw.toString());
		}

		updateView(view);
	}

	/**
	 * Update a SeekBarPreference view with our current state
	 * 
	 * @param view
	 */
	protected void updateView(View view) {

		try {
			RelativeLayout layout = (RelativeLayout) view;

			mStatusText = (TextView) layout.findViewById(R.id.seekBarPrefValue);
			mStatusText.setText(PrivacyHelper.PrivacyLevel.fromInt(mCurrentValue).getName());
			mStatusText.setMinimumWidth(30);
			
			sensor.setPrivacylevel(PrivacyHelper.PrivacyLevel.fromInt(mCurrentValue));

			mPrivacyLevel.setProgress(mCurrentValue - mMinValue);

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Log.d("SeattleSensors", sw.toString());
		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int newValue = progress + mMinValue;

		if (newValue > mMaxValue)
			newValue = mMaxValue;
		else if (newValue < mMinValue)
			newValue = mMinValue;
		else if (mInterval != 1 && newValue % mInterval != 0)
			newValue = Math.round(((float) newValue) / mInterval) * mInterval;

		// change rejected, revert to the previous value
		if (!callChangeListener(newValue)) {
			seekBar.setProgress(mCurrentValue - mMinValue);
			return;
		}

		// change accepted, store it
		mCurrentValue = newValue;
		mStatusText.setText(PrivacyHelper.PrivacyLevel.fromInt(mCurrentValue).getName());
		persistInt(newValue);

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		notifyChanged();
	}

	@Override
	protected Object onGetDefaultValue(TypedArray ta, int index) {

		int defaultValue = ta.getInt(index, PrivacyHelper.PrivacyLevel.FULL.value());
		return defaultValue;

	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

		if (restoreValue) {
			mCurrentValue = getPersistedInt(mCurrentValue);
		} else {
			int temp = 0;
			try {
				temp = (Integer) defaultValue;
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.d("SeattleSensors", sw.toString());
			}

			persistInt(temp);
			mCurrentValue = temp;
		}

	}
	
	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
	}

}