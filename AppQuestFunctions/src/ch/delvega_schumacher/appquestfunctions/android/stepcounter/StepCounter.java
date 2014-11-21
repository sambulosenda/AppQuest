package ch.delvega_schumacher.appquestfunctions.android.stepcounter;

import ch.delavega_schumacher.appquestfunctions.Mathematics.Formulas.CalculatingHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class StepCounter implements SensorEventListener {

	private static final int LONG = 1000;
	private static final int SHORT = 50;

	private boolean accelerating = false;
	private StepListener listener;

	private RingBuffer shortBuffer = new RingBuffer(SHORT);
	private RingBuffer longBuffer = new RingBuffer(LONG);
	private CalculatingHelper calcHelper = CalculatingHelper.getInstance();

	public StepCounter(StepListener listener) {
		this.listener = listener;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {
		float magnitude = (float) calcHelper.getVector(event.values);

		shortBuffer.put(magnitude);
		longBuffer.put(magnitude);

		float shortAverage = shortBuffer.getAverage();
		float longAverage = longBuffer.getAverage();

		if (!accelerating && (shortAverage > longAverage * 1.1)) {
			accelerating = true;
			listener.onStep();
		}

		if ((accelerating && shortAverage < longAverage * 0.9)) {
			accelerating = false;
		}
	}
}