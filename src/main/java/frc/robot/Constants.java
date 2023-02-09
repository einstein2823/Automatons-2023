// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean
 * constants. This class should not be used for any other purpose. All constants
 * should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the
 * constants are needed, to reduce verbosity.
 */

public final class Constants {

  public static class VisionConstants {
    public static String kcameraName = "Arducam_Global_Shutter";
    public static boolean kHasVision = true;
  }

  public static String autonomousMode = "NotVision";

  public static class OperatorConstants { // For operator button bindings, and other stuff directly related to the
                                          // operator
    public static final int kDriverControllerPort = 0;
  }

  public static class SubsystemConstants {
    public static final boolean useDrive = true;
    public static final boolean useArm = false;
    public static final boolean usePneumatics = true;
    public static final boolean useTurnTables = false;
    public static final boolean useIntake = false;
    public static final boolean useLimelight = true;
  }

  public static class DriveConstants {
    public static final double kMaxSpeed = 4.0; // 3 meters per second
    public static final double kMaxAngularSpeed = Math.PI; // 1/2 rotation per second

    public static final double kWheelRadius = 0.0508;
    public static final int kEncoderResolution = 2048;

    public static final double driveGearRatio = 6.75;
    public static final double rotationGearRatio = 15.429;

    public final static Translation2d kFrontLeftLocation = new Translation2d(0.244, 0.244);
    public final static Translation2d kFrontRightLocation = new Translation2d(0.244, -0.244);
    public final static Translation2d kBackLeftLocation = new Translation2d(-0.244, 0.244);
    public final static Translation2d kBackRightLocation = new Translation2d(-0.244, -0.244);

    public final static SwerveDriveKinematics kDriveKinematics = new SwerveDriveKinematics(
        kFrontLeftLocation, kFrontRightLocation, kBackRightLocation, kBackLeftLocation);

    public static final double drivekP = 0.0005; //old value: 0.0005 OR 0.07
    public static final double drivekI = 0.00; //old value: 0.000175
    public static final double drivekD = 0.0;
    public static final double drivekF = 0.048;
    public static final double drivekAllowableError = 50;
    public static final double drivekMaxIntegralAccumulation = 20000; // TODO: a guess, finetune later
    public static final double drivekIntegralZone = 10000; //TODO: a guess, finetune later

    public static final double turningkP = .1;
    public static final double turningkI = .0002;
    public static final double turningkD = 1; //TODO: please tune these later
    public static final double turningkAllowableError = 10;

    public static final double balancekP = 0.05;
    public static final double balancekI = 0.0001;
    public static final double balancekD = 0.0075;
    public static final int balanceThreshold = 2;

    public static final double encoderTolerance = 0.01;
  }

  public static class RobotConstants { // For robot values that remain the same, such as max speed
    public static final double swerveOffsetFL = 0.617738640443466;
    public static final double swerveOffsetFR = 0.59958238998956;
    public static final double swerveOffsetBL = 0.890661022266526;
    public static final double swerveOffsetBR = 0.125644328141108; // Fill in later
  }

  public static class ArmConstants {
    public static final int shoulderID = 22;
    public static final double shoulderkP = .1;
    public static final double shoulderkI = .0001;
    public static final double shoulderkD = 0;

  }

  public static final class AutoConstants {
    public static final double kMaxSpeedMetersPerSecond = 3;
    public static final double kMaxAccelerationMetersPerSecondSquared = 0.5; //TODO measure this
    public static final double kMaxAngularSpeedRadiansPerSecond = Math.PI;
    public static final double kMaxAngularSpeedRadiansPerSecondSquared = Math.PI;

    public static final double kPXController = 1;
    public static final double kPYController = 1;
    public static final double kPThetaController = 1;

    // Constraint for the motion profiled robot angle controller
    public static final TrapezoidProfile.Constraints kThetaControllerConstraints = new TrapezoidProfile.Constraints(
        kMaxAngularSpeedRadiansPerSecond, kMaxAngularSpeedRadiansPerSecondSquared);
  }

  public static final class PneumaticsConstants {
    public static final int hubID = 49;
    public static final int minPressure = 110;
    public static final int maxPressure = 118;

  }

  public static final class TurntableConstants {
    public static final int motorID = 31;
    public static final double motorkP = .1;
    public static final double motorkI = .0001;
    public static final double motorkD = 0;
    public static final double clockwiseSpeed = .15;
    public static final double counterClockwiseSpeed = -.15;
    public static final I2C.Port i2cPort = I2C.Port.kOnboard;
    //TODO: Add a current limit
  }

  public static final class IntakeConstants {
    public static final int motorID = 40;
    //TODO: Find motor ID for intake
    public static final double motorkP = .1;
    public static final double motorkI = .0001;
    public static final double motorkD = 0;
    public static final double intakeSpeed = .15;
    public static final double outakeSpeed = -.15;
    public static final I2C.Port i2cPort = I2C.Port.kOnboard;
  }

  public static final class LimelightConstants {
    public static final double turnValue = -0.08; //TODO: tune this number
  }
}
