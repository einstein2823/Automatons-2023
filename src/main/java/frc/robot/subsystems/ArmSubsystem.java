// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;

public class ArmSubsystem extends SubsystemBase {
  private final TalonFX m_shoulderMotor;
  private final TalonFX m_elbowMotor;
  private final Joystick m_joystick;
  private final DutyCycleEncoder m_shoulderEncoder;
  private final DutyCycleEncoder m_elbowEncoder;
  private int m_targetState;
  private int m_currentState;
  private boolean m_isChanging;

  /** Creates a new ArmSubsystem. */
  public ArmSubsystem(Joystick joystick) {
    m_shoulderMotor = new TalonFX(ArmConstants.shoulderID);
    m_shoulderMotor.configFactoryDefault();
    m_shoulderMotor.setNeutralMode(NeutralMode.Coast); //TODO: reset to brake mode, testing
    m_shoulderMotor.config_kP(0, ArmConstants.shoulderkP);
    m_shoulderMotor.config_kI(0, ArmConstants.shoulderkI);
    m_shoulderMotor.config_kD(0, ArmConstants.shoulderkD);
    m_shoulderMotor.configPeakOutputForward(0.3);
    m_shoulderMotor.configPeakOutputReverse(-0.3); //TODO: remove these eventually, once tuning is finished
    m_shoulderMotor.configMotionAcceleration(ArmConstants.shoulderAcceleration);
    m_shoulderMotor.configMotionCruiseVelocity(ArmConstants.shoulderMaxVelocity);
    m_shoulderMotor.configMotionSCurveStrength(ArmConstants.shoulderSCurve);

    m_shoulderMotor.set(ControlMode.MotionMagic, 0);
    m_shoulderEncoder = new DutyCycleEncoder(1);

    m_elbowMotor = new TalonFX(ArmConstants.elbowID);
    m_elbowMotor.configFactoryDefault();
    m_elbowMotor.setNeutralMode(NeutralMode.Coast); //TODO: reset to brake mode, testing
    m_elbowMotor.config_kP(0, ArmConstants.shoulderkP);
    m_elbowMotor.config_kI(0, ArmConstants.shoulderkI);
    m_elbowMotor.config_kD(0, ArmConstants.shoulderkD);
    m_elbowMotor.configPeakOutputForward(0.3);
    m_elbowMotor.configPeakOutputReverse(-0.3); //TODO: remove these eventually, once tuning is finished
    m_elbowMotor.configMotionAcceleration(ArmConstants.elbowAcceleration);
    m_elbowMotor.configMotionCruiseVelocity(ArmConstants.elbowMaxVelocity);
    m_elbowMotor.configMotionSCurveStrength(ArmConstants.elbowSCurve);

    m_elbowEncoder = new DutyCycleEncoder(0);

    m_joystick = joystick;

    m_targetState = 0;
    m_currentState = 0;
    m_isChanging = false;

    m_shoulderMotor.set(ControlMode.Position, ArmConstants.shoulderPositions[m_currentState]);
    m_elbowMotor.set(ControlMode.Position, ArmConstants.elbowPositions[m_currentState]);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    //moveShoulder(m_joystick.getRawAxis(1) * -0.3);
    //moveElbow(m_joystick.getRawAxis(5) * 0.3);
    SmartDashboard.putNumber("Shoulder Absolute Encoder", m_shoulderEncoder.getAbsolutePosition());
    SmartDashboard.putNumber("Elbow Absolute Encoder", m_elbowEncoder.getAbsolutePosition());
    SmartDashboard.putNumber("Shoulder Falcon Encoder", m_shoulderMotor.getSelectedSensorPosition());
    SmartDashboard.putNumber("Elbow Falcon Encoder", m_elbowMotor.getSelectedSensorPosition());

    SmartDashboard.putNumber("Target State", m_targetState);
    SmartDashboard.putNumber("Current State", m_currentState);
    SmartDashboard.putBoolean("Is Changing?", m_isChanging);
    SmartDashboard.putNumber("Shoulder Error",
        m_shoulderMotor.getSelectedSensorPosition() - ArmConstants.shoulderPositions[m_currentState]);
    SmartDashboard.putNumber("Elbow Error",
        m_elbowMotor.getSelectedSensorPosition() - ArmConstants.elbowPositions[m_currentState]);

    m_isChanging = Math.abs(m_shoulderMotor.getSelectedSensorPosition()
        - ArmConstants.shoulderPositions[m_currentState]) > ArmConstants.errorThreshold
        || Math.abs(m_elbowMotor.getSelectedSensorPosition()
            - ArmConstants.elbowPositions[m_currentState]) > ArmConstants.errorThreshold;
  }

  public void chickenMove() {
    m_shoulderMotor.set(ControlMode.Position, 1000);

  }

  public void backToNormal() {
    m_shoulderMotor.set(ControlMode.Position, 0);

  }

  public void moveShoulder(double speed) {
    m_shoulderMotor.set(ControlMode.PercentOutput, speed);
    System.out.println("shoulder running at " + speed);
  }

  public void moveElbow(double speed) {
    m_elbowMotor.set(ControlMode.PercentOutput, speed);
    System.out.println("elbow running at " + speed);
  }

  public double absoluteToAngle(double absValue) {
    return 0;
  }

  public void changeState(int state) {
    if (state < ArmConstants.intakeState) {
      state = ArmConstants.intakeState;
    } else if (state > ArmConstants.highState) {
      state = ArmConstants.highState;
    }
    m_targetState = state;

  }

  public int getCurrentState() {
    return m_currentState;
  }

  public int getTargetState() {
    return m_targetState;
  }

  public boolean getIsChanging() {
    return m_isChanging;
  }

  public void moveDownState() {
    if (m_isChanging) {
      return;
    }
    if (m_currentState == ArmConstants.intakeState) {
      return;
    }
    m_currentState--;
    m_isChanging = true;
    m_shoulderMotor.set(ControlMode.MotionMagic, ArmConstants.shoulderPositions[m_currentState]);
    m_elbowMotor.set(ControlMode.MotionMagic, ArmConstants.elbowPositions[m_currentState]);
  }

  public void moveUpState() {
    if (m_isChanging) {
      return;
    }
    if (m_currentState == ArmConstants.highState) {
      return;
    }
    m_currentState++;
    m_isChanging = true;
    m_shoulderMotor.set(ControlMode.MotionMagic, ArmConstants.shoulderPositions[m_currentState]);
    m_elbowMotor.set(ControlMode.MotionMagic, ArmConstants.elbowPositions[m_currentState]);

  }

  public void setTargetState(int state) {
    if (state < ArmConstants.intakeState) {
      state = ArmConstants.intakeState;
    } else if (state > ArmConstants.highState) {
      state = ArmConstants.highState;
    }
    m_targetState = state;
  }
}
