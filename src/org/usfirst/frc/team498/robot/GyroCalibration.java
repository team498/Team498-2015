package org.usfirst.frc.team498.robot;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Timer;

public class GyroCalibration {
	Timer GyroClock = new Timer();
	boolean finishedCalibration = false;
	boolean init = false;
	double gyroOffset;
	Gyro m_gyro;
	
	GyroCalibration(Gyro gyro) {
		m_gyro = gyro;
		GyroClock.start();

	}
	public void calibrateGyro() {
		if(!init) {
			GyroClock.reset();
			m_gyro.reset();
			init = true;
		}
		if(GyroClock.get() > 6) {
			gyroOffset = m_gyro.getAngle() / 6; //Angle change per 1 seconds
			finishedCalibration = true;
		}
	}
	public void reset() {
		m_gyro.reset();
		GyroClock.reset();
	}
	public double getCalibratedAngle() {
		return m_gyro.getAngle()  - gyroOffset * GyroClock.get();
	}
}
