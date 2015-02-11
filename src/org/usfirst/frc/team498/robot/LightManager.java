package org.usfirst.frc.team498.robot;

import edu.wpi.first.wpilibj.Relay;

public class LightManager {
	Robot robot;
	LightParser currentParser;
	int currentPhase;
	public LightManager(Robot robot) {
		this.robot = robot;
	}
	public void setLightParser(LightParser parser) {
		currentParser = parser;
	}
	public void Tick() {
		boolean shouldTurnLight1on = currentParser.Pieces[currentPhase].Light1;
		boolean shouldTurnLight2on = currentParser.Pieces[currentPhase].Light2;
		boolean shouldTurnLight3on = currentParser.Pieces[currentPhase].Light3;
		boolean shouldTurnLight4on = currentParser.Pieces[currentPhase].Light4;
		int phaseTime = currentParser.Pieces[currentPhase].Duration;
		if (robot.mainClock.get() * 1000 > phaseTime) {
			robot.mainClock.reset();
			if (currentPhase >= currentParser.Pieces.length - 1) {
				currentPhase = 0;
			} else {
				currentPhase++;
			}
		}
		/*
		 * light0.set(Relay.Value.kReverse); 1 
		 * light0.set(Relay.Value.kForward);2
		 *  light1.set(Relay.Value.kForward); 3
		 * light1.set(Relay.Value.kReverse); 4
		 */
		if (shouldTurnLight1on && shouldTurnLight2on) {
			robot.light0.set(Relay.Value.kOn);
		} else if (shouldTurnLight1on) {
			robot.light0.set(Relay.Value.kReverse);
		} else if (shouldTurnLight2on) {
			robot.light0.set(Relay.Value.kForward);
		} else {
			robot.light0.set(Relay.Value.kOff);
		}
		if (shouldTurnLight3on && shouldTurnLight4on) {
			robot.light1.set(Relay.Value.kOn);
		} else if (shouldTurnLight3on) {
			robot.light1.set(Relay.Value.kForward);
		} else if (shouldTurnLight4on) {
			robot.light1.set(Relay.Value.kReverse);
		} else {
			robot.light1.set(Relay.Value.kOff);
		}
	}
	public void switchPattern() {
		currentPhase = 0;
		// other switch things
	}
}