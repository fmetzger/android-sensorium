package at.univie.sensorium.sensors;

import java.util.List;

/**
 * Created by fm on 17.02.14.
 */
public interface NestedSensorValue {

    public List<SensorValue> getInnerSensorValues();
}
