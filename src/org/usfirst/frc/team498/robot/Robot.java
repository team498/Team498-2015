package org.usfirst.frc.team498.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class. The
 * SampleRobot class is the base of a robot application that will automatically
 * call your Autonomous and OperatorControl methods at the right time as
 * controlled by the switches on the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
public class Robot extends SampleRobot {
	LightManager lightManager = new LightManager(this);
	LightPatterns patterns = new LightPatterns();
	RobotDrive drive = new RobotDrive(3, 4, 1, 2);
	Joystick thisStick = new Joystick(0);
	Joystick thatStick = new Joystick(1);
	Timer mainClock = new Timer();
	Timer ultrasonicClock = new Timer();
	Victor pulley = new Victor(5);
	Relay light0 = new Relay(0);
	Relay light1 = new Relay(1);
	Relay extenderLight = new Relay(2);
	Relay light3 = new Relay(3);
	DigitalInput switch1 = new DigitalInput(4); // whoever decided Limit Switch
												// 1 goes at the top of the
												// robot is cancer
	DigitalInput switch2 = new DigitalInput(5);
	DigitalInput switch3 = new DigitalInput(6);
	DigitalInput switch4 = new DigitalInput(7);
	DigitalInput switch5 = new DigitalInput(8); // the bottom
	DoubleSolenoid armExtender = new DoubleSolenoid(0, 1);
	Ultrasonic eye0 = new Ultrasonic(0, 1);// (Input, Output) Slots on the
											// sensor
	Ultrasonic eye1 = new Ultrasonic(2, 3);
	Gyro gyro = new Gyro(1);
	GyroCalibration calibratedGyro = new GyroCalibration(gyro);
	Timer rampClock = new Timer();
	boolean lightsFirstTime = false;
	boolean usingLights = false;
	boolean Initialized = false;
	double gyroOffset;
	int phase = 0; //Current Autonomous Phase
	double u = .1; //Debug value used to find drift constant in teleop
	double i = .1;//Debug value used to find drift constant in teleop	
	
	
	/*
	 * Constants
	 */
	double driftConstant = -0.17; //The number that needs to be added to RotateValue to drive straight
	double armLength = 21; //Length in inches of the robot's arms
	double scalingValue = (1 / 2); //For Ultrasonic Drive to Tote
	double rampTime = .3; //Time in seconds before drive motors reach 100%
	double robotToToteWhenSetDistance = 6; //Distance in inches from ultrasonic to the tote for optimal lifeting
	
	/*
	 * Autonomous Variables
	 */
	boolean haveArrived = false;
	double moveValue;
	double rotateValue;
	
	public void initilize() {
		if (!Initialized) {
			eye1.setAutomaticMode(true);
			LightPatterns lightPatterns = new LightPatterns();
			lightManager.setLightParser(lightPatterns.lightPattern1);
			mainClock.start();
			CameraServer server = CameraServer.getInstance();
			server.setQuality(50);
			server.startAutomaticCapture("cam0");
			rampClock.start();
			calibratedGyro.reset();
			Initialized = true;
		}
	}
	private void driveToTote() {
		drive.arcadeDrive(moveValue, rotateValue + driftConstant);
		if (eye1.getRangeInches() < armLength
				&& eye1.getRangeInches() > robotToToteWhenSetDistance) {
			rotateValue = 0;
			moveValue = .75; // Maybe needs to be slower
			haveArrived = false;
		} else if (eye1.getRangeInches() < robotToToteWhenSetDistance) {
			rotateValue = 0;
			moveValue = 0;
			haveArrived = true;
		} else {
			moveValue = .75;
			rotateValue = ((eye1.getRangeInches() - eye0.getRangeInches()) * scalingValue);
			haveArrived = false;
		}
	}
	private void driveStraight() {
		drive.arcadeDrive(moveValue, rotateValue); 
		rotateValue = (calibratedGyro.getCalibratedAngle() * -1 * .03) + driftConstant;
		if (eye1.getRangeInches() < armLength
				&& eye1.getRangeInches() > robotToToteWhenSetDistance) {
			moveValue = .45;
			haveArrived = false;
		} else if (eye1.getRangeInches() > armLength) {
			moveValue = .55;
			haveArrived = false;
		} else {
			moveValue = 0;
			haveArrived = true;
		}
	}
	private void driveAlongWall() {
		drive.arcadeDrive(moveValue, rotateValue + driftConstant);
		// drive.tankDrive(.6, .585);
		if (eye0.getRangeInches() < 27 && mainClock.get() > 1.25
				&& eye0.getRangeInches() < 40 && eye0.getRangeInches() > 20) {
			rotateValue = -.4; // Turn Left
		} else if (eye0.getRangeInches() > 29 && mainClock.get() > 1.25
				&& eye0.getRangeInches() < 40 && eye0.getRangeInches() > 20) {
			rotateValue = .4; // Turn Right
		} else {
			rotateValue = 0;
		}
		if (eye1.getRangeInches() < armLength
				&& eye1.getRangeInches() > robotToToteWhenSetDistance) {
			moveValue = .45;
			haveArrived = false;
		} else if (eye1.getRangeInches() > armLength) {
			moveValue = .6;
			haveArrived = false;
		} else {
			moveValue = 0;
			haveArrived = true;
		}
	}
	private void extender() {
		if (thisStick.getRawButton(6) && !thisStick.getRawButton(5)) {
			armExtender.set(DoubleSolenoid.Value.kReverse);
			extenderLight.set(Relay.Value.kOff);
		} else if (thisStick.getRawButton(5) && !thisStick.getRawButton(6)) {
			armExtender.set(DoubleSolenoid.Value.kForward);
			extenderLight.set(Relay.Value.kForward);
		}
	}
	private void lights() {
		/*
		 * light0.set(Relay.Value.kReverse); 1 light0.set(Relay.Value.kForward);
		 * 2 light1.set(Relay.Value.kForward); 3
		 * light1.set(Relay.Value.kReverse); 4
		 */
		if(!usingLights){
		if (!switch5.get()) {
			light0.set(Relay.Value.kOff);
			light1.set(Relay.Value.kOff);
		}
		if (!switch4.get()) {
			light0.set(Relay.Value.kReverse);
			light1.set(Relay.Value.kOff);
		}
		if (!switch3.get()) {
			light0.set(Relay.Value.kOn);
			light1.set(Relay.Value.kOff);
		}
		if (!switch2.get()) {
			light0.set(Relay.Value.kOn);
			light1.set(Relay.Value.kForward);
		}
		if (!switch1.get()) {
			light0.set(Relay.Value.kOn);
			light1.set(Relay.Value.kOn);
		}
		}
	}
	private void pulley() {
		if (!switch5.get()) {
			if (thisStick.getRawAxis(5) < -.1) {
				pulley.set(thisStick.getRawAxis(5) * -1);
			} else {
				pulley.set(0);
			}
		}
		if (!switch1.get()) {
			if (thisStick.getRawAxis(5) > .1) {
				pulley.set(thisStick.getRawAxis(5) * -1);
			} else {
				pulley.set(0);
			}
		}
		if (switch1.get() & switch5.get()) {
			if (thisStick.getRawAxis(5) > 0.1 || thisStick.getRawAxis(5) < -0.1) {
				pulley.set(thisStick.getRawAxis(5) * -1);
			} else {
				pulley.set(0);
			}
		}
	}
	private void dashboard() {
		SmartDashboard.putNumber("Eye0", eye0.getRangeInches());
		SmartDashboard.putNumber("Eye1", eye1.getRangeInches());
		SmartDashboard.putNumber("Clock Value", mainClock.get());
		SmartDashboard.putNumber("rampClock", rampClock.get());
		SmartDashboard.putNumber("_U", u);
		SmartDashboard.putNumber("_i", i);
		SmartDashboard.putNumber("Move Value", moveValue);
		SmartDashboard.putNumber("Rotate Value", rotateValue);
		SmartDashboard.putNumber("Trigger Values", thisStick.getRawAxis(3));
		SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
		SmartDashboard.putNumber("Gyro Calibrated Angle",
				calibratedGyro.getCalibratedAngle());
		SmartDashboard.putNumber("Gyro Offset", calibratedGyro.gyroOffset);
		SmartDashboard.putNumber("Gyro Clock", calibratedGyro.GyroClock.get());
		SmartDashboard.putBoolean("Condition 1",
				eye1.getRangeInches() > armLength
						&& eye1.getRangeInches() < robotToToteWhenSetDistance);
		SmartDashboard.putBoolean("Condition 2",
				eye1.getRangeInches() < robotToToteWhenSetDistance);
		SmartDashboard.putBoolean("Arrived?", haveArrived);
		SmartDashboard.putBoolean("switch0", switch1.get());
		SmartDashboard.putBoolean("switch1", switch2.get());
		SmartDashboard.putBoolean("switch2", switch3.get());
		SmartDashboard.putBoolean("switch3", switch4.get());
		SmartDashboard.putBoolean("switch4", switch5.get());
		SmartDashboard.putBoolean("Initilized", Initialized);
		SmartDashboard.putNumber("Auto Phase", phase);
		SmartDashboard.putNumber("Current Phase", lightManager.currentPhase);
	}
	private void driveCode() {
		if (thisStick.getRawButton(7)) {
			drive.arcadeDrive(i, u);
			// driveStraight();
		} else {
			if (thisStick.getRawButton(9)) {
				if (thisStick.getRawAxis(3) > 0) {
					drive.arcadeDrive(ramp(thisStick, 3), thisStick.getX() * -1
							* .50);
				} else {
					drive.arcadeDrive(ramp(thisStick, 2) * -1 * .8,
							thisStick.getX() * -1 * .50);
				}
			} else {
				if (thisStick.getRawAxis(3) > 0) {
					drive.arcadeDrive(ramp(thisStick, 3), thisStick.getX() * -1
							* .75);
				} else {
					drive.arcadeDrive(ramp(thisStick, 2) * -1 * .8,
							thisStick.getX() * -1 * .75);
				}
			}
		}
	}
	private double ramp(Joystick stick, int axis) {
		if (rampClock.get() < rampTime) {
			return stick.getRawAxis(axis) / rampTime * rampClock.get();
		} else {
			return stick.getRawAxis(axis);
		}
	}
	private void rampManager() {
		if (thisStick.getRawAxis(2) < .1 && thisStick.getRawAxis(3) < .1) {
			rampClock.reset();
		}
	}
	private void operatorOperations() {
		if(usingLights){
			lightManager.Tick();
		}
		if (thatStick.getRawButton(1)) {
			mainClock.reset();
			lightManager.setLightParser(patterns.strobe);
			usingLights = true;
		}
		if (thatStick.getRawButton(2)) {
			mainClock.reset();
			usingLights = true;
			lightManager.setLightParser(patterns.lightPattern1);
		}
		if (thatStick.getRawButton(3)) {
			mainClock.reset();
			lightManager.setLightParser(patterns.lightPattern2);
			usingLights = true;
		}
		if (thatStick.getRawButton(4)) {
			mainClock.reset();
			lightManager.setLightParser(patterns.lightPattern3);
			usingLights = true;
		}
		if (thatStick.getRawButton(5)) {
			mainClock.reset();
			lightManager.setLightParser(patterns.lightPattern4);
			usingLights = true;
		}
		if (thatStick.getRawButton(6)) {
			mainClock.reset();
			
		}
		if (thatStick.getRawButton(8)) {
			mainClock.reset();
			usingLights = false;
			lightManager.setLightParser(patterns.off);
		}
	}
	public void autonomous() {
		initilize();
		calibratedGyro.reset();
		mainClock.reset();
		phase = 0;
		while (isEnabled() && isAutonomous()) {
			dashboard();
			switch (phase) {
			case 40:
				break;
			case 0:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(.5);
				phase++;
				break;
			case 1:
				pulley.set(.75);
				if (!switch4.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 2:
				driveStraight();
				if (haveArrived) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 3:
				pulley.set(-.75);
				if (mainClock.get() > .4) {
					pulley.set(0);
					phase++;
					break;
				}
				phase++;
				break;
			case 4:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(.75);
				phase++;
				mainClock.reset();
				break;
			case 5:
				drive.arcadeDrive(0, 0.4);
				if (mainClock.get() > .4) {
					pulley.set(0);
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 6:
				pulley.set(-.75);
				if (!switch5.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 7:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(.75);
				phase++;
				break;
			case 8:
				pulley.set(.75);
				if (!switch4.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 9:
				driveStraight();
				if (haveArrived) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 10:
				pulley.set(-.75);
				if (mainClock.get() > .4) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 11:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(.75);
				phase++;
				mainClock.reset();
				break;
			case 12:
				drive.arcadeDrive(0, 0.4);
				if (mainClock.get() > .4) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 13:
				pulley.set(-.75);
				if (!switch5.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 14:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(.5);
				phase++;
				break;
			case 15:
				pulley.set(.75);
				if (!switch4.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 16:
				driveStraight();
				if (haveArrived) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 17:
				pulley.set(-.75);
				if (mainClock.get() > .1) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 18:
				armExtender.set(DoubleSolenoid.Value.kForward);
				Timer.delay(.5);
				mainClock.reset();
				phase++;
				break;
			case 19:
				pulley.set(-.75);
				if (mainClock.get() > .1) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 20:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(.5);
				phase++;
				break;
			case 21:
				pulley.set(.75);
				if (!switch4.get()) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 22:
				drive.arcadeDrive(0, -.7);
				if (gyro.getAngle() > 85) {
					drive.arcadeDrive(0, 0);
					phase++;
					break;
				}
				break;
			case 23:
				// Drive to center
				if (haveArrived) {
					haveArrived = false;
					phase++;
					mainClock.reset();
					break;
				}
				break;
			case 24:
				pulley.set(-.75);
				if (mainClock.get() > .35) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			case 25:
				armExtender.set(DoubleSolenoid.Value.kReverse);
				Timer.delay(.75);
				phase++;
				mainClock.reset();
				break;
			case 26:
				pulley.set(-.75);
				if (mainClock.get() > .1) {
					pulley.set(0);
					phase++;
					break;
				}
				break;
			// drive back
			}
		}
	}
	public void operatorControl() {
		// int shoukldTurnLifht1on=parser.Pieces[currentPhase].Duration;
		initilize();

		calibratedGyro.reset();
		light0.set(Relay.Value.kOn);
		light1.set(Relay.Value.kOn);
		while (isOperatorControl() && isEnabled()) {
			if (eye1.getRangeInches() < robotToToteWhenSetDistance) {
				light3.set(Relay.Value.kForward);
			} else {
				light3.set(Relay.Value.kOff);
			}
			if (thisStick.getRawButton(4)) {
				u += .001;
			}
			if (thisStick.getRawButton(3)) {
				u -= .001;
			}
			if (thisStick.getRawButton(1)) {
				i += .001;
			}
			if (thisStick.getRawButton(2)) {
				i -= .001;
			}
			if (thisStick.getRawButton(8)) {
				calibratedGyro.reset();
			}
			pulley();
			driveCode();
			extender();
			operatorOperations();
			dashboard();
			rampManager();
			lights();
		}
	}
	public void disabled() {
		initilize();
		while (isDisabled()) {
			dashboard();
			if (!calibratedGyro.finishedCalibration) {
				calibratedGyro.calibrateGyro();
			}
		}
	}

}
