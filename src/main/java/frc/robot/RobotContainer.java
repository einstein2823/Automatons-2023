// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.*;
import frc.robot.commands.BackToNormalCommand;
import frc.robot.commands.BalanceCommand;
import frc.robot.commands.ChickenCommand;
import frc.robot.commands.ChompForward;
import frc.robot.commands.ChompReverse;
import frc.robot.commands.IntakeCommand;
import frc.robot.commands.MoveSetDistanceCommand;
import frc.robot.commands.PositionUpdateCommand;
import frc.robot.commands.SpinClockwiseCommand;
import frc.robot.commands.SpinCounterClockwiseCommand;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.PneumaticsSubsystem;
import frc.robot.subsystems.TurntableSubsystem;

import java.util.List;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.subsystems.VisionSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  public final Command leftAutoButton;
  public final Command rightAutoButton;
  public final Command middleAutoButton;

  // The robot's subsystems and commands are defined here...
  private final DriveSubsystem m_robotDrive = SubsystemConstants.useDrive ? new DriveSubsystem() : null;
  private final VisionSubsystem m_visionSubsystem = SubsystemConstants.useVision ? new VisionSubsystem() : null;
  private final ArmSubsystem m_robotArm = SubsystemConstants.useArm ? new ArmSubsystem() : null;
  private final PneumaticsSubsystem m_pneumatics = SubsystemConstants.usePneumatics ? new PneumaticsSubsystem() : null;
  private final TurntableSubsystem m_turntables = SubsystemConstants.useTurnTables ? new TurntableSubsystem() : null;
  private final IntakeSubsystem m_intake = SubsystemConstants.useIntake ? new IntakeSubsystem() : null;
  // The driver's controller
  private final Joystick m_joystick = new Joystick(1);
  private final Joystick m_opJoystick = new Joystick(0);

  private final SendableChooser<String> m_startPosition;
  private final SendableChooser<Boolean> m_grabPiece1;
  private final SendableChooser<Boolean> m_grabPiece2;
  private final SendableChooser<Boolean> m_chargeBalance;

  NetworkTableInstance inst = NetworkTableInstance.getDefault();
  NetworkTableEntry team = inst.getTable("FMSInfo").getEntry("IsRedAlliance");

  // Replace with CommandPS4Controller or CommandJoystick if needed

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {

    if (SubsystemConstants.useVision) {
      leftAutoButton = new RunCommand(
          () -> m_visionSubsystem.getDestination(m_robotDrive.getPose(), "left"),
          m_visionSubsystem);
      rightAutoButton = new RunCommand(
          () -> m_visionSubsystem.getDestination(m_robotDrive.getPose(), "right"),
          m_visionSubsystem);
      middleAutoButton = new RunCommand(
          () -> m_visionSubsystem.getDestination(m_robotDrive.getPose(), "middle"),
          m_visionSubsystem);
    }

    m_startPosition = new SendableChooser<String>();
    m_startPosition.addOption("Station Side", "station");
    m_startPosition.addOption("Middle", "middle");
    m_startPosition.addOption("Drive Side", "drive");
    m_startPosition.setDefaultOption("Middle", "middle");
    SmartDashboard.putData("Starting Position?", m_startPosition);

    m_grabPiece1 = new SendableChooser<Boolean>();
    m_grabPiece1.addOption("Yes", true);
    m_grabPiece1.addOption("No", false);
    m_grabPiece1.setDefaultOption("No", false);
    SmartDashboard.putData("Grabbing First Piece?", m_grabPiece1);

    m_grabPiece2 = new SendableChooser<Boolean>();
    m_grabPiece2.addOption("Yes", true);
    m_grabPiece2.addOption("No", false);
    m_grabPiece2.setDefaultOption("No", false);
    SmartDashboard.putData("Grabbing Second Piece?", m_grabPiece2);

    m_chargeBalance = new SendableChooser<Boolean>();
    m_chargeBalance.addOption("Yes", true);
    m_chargeBalance.addOption("No", false);
    m_chargeBalance.setDefaultOption("No", false);
    SmartDashboard.putData("Balancing on Charge Station?", m_chargeBalance);

    // Configure default commands
    if (SubsystemConstants.useDrive) {
      m_robotDrive.setDefaultCommand(
          // The left stick controls translation of the robot.
          // Turning is controlled by the X axis of the right stick.

          new RunCommand(

              () -> m_robotDrive.drive(
                  Math.pow(MathUtil.applyDeadband(m_joystick.getRawAxis(1), 0.1), 1) * -1 * DriveConstants.kMaxSpeed,
                  Math.pow(MathUtil.applyDeadband(m_joystick.getRawAxis(0), 0.1), 1) * -1 * DriveConstants.kMaxSpeed,
                  MathUtil.applyDeadband(m_joystick.getRawAxis(2), 0.2) * -1
                      * DriveConstants.kMaxAngularSpeed,
                  //0.2 * DriveConstants.kMaxSpeed, 0, 0,
                  m_robotDrive.m_fieldRelative),
              m_robotDrive));
    }

    // Configure the trigger bindings
    configureBindings();
  }

  public Command getAutonomousCommand() {

    if (DriverStation.getAlliance() == Alliance.Blue) {
      System.out.println(m_startPosition.getSelected() + " " + m_grabPiece1.getSelected() + " "
          + m_grabPiece2.getSelected() + " " + m_chargeBalance.getSelected());
      if (m_startPosition.getSelected() == "middle") {
        m_robotDrive.resetOdometry(new Pose2d(1.36, 2.19, new Rotation2d(0)));
        if (m_chargeBalance.getSelected()) {
          return new SequentialCommandGroup(
              //Place cone
              new MoveSetDistanceCommand(m_robotDrive, 7.1196, 2.19, new Rotation2d(0),
                  AutoConstants.kMaxChargeStationVelocity, AutoConstants.kMaxChargeStationAcceleration, List.of()),
              new MoveSetDistanceCommand(m_robotDrive, 3.485, 2.7615, new Rotation2d(0),
                  AutoConstants.kMaxChargeStationVelocity, AutoConstants.kMaxChargeStationAcceleration, List.of()),
              new BalanceCommand(m_robotDrive));
        }
      }
    } else {
      if (m_startPosition.getSelected() == "middle") {
        m_robotDrive.resetOdometry(new Pose2d(16.54 - 1.36, 2.19, new Rotation2d(Math.PI)));
        if (m_chargeBalance.getSelected()) {
          return new SequentialCommandGroup(
              //Place cone
              new MoveSetDistanceCommand(m_robotDrive, 16.54 - 7.1196, 2.19, new Rotation2d(Math.PI),
                  AutoConstants.kMaxChargeStationVelocity, AutoConstants.kMaxChargeStationAcceleration, List.of()),
              new MoveSetDistanceCommand(m_robotDrive, 16.54 - 3.485, 2.7615, new Rotation2d(Math.PI),
                  AutoConstants.kMaxChargeStationVelocity, AutoConstants.kMaxChargeStationAcceleration, List.of()),
              new BalanceCommand(m_robotDrive));
        }
      }
    }

    if (DriverStation.getAlliance() == Alliance.Blue) {
      System.out.println(m_startPosition.getSelected() + " " + m_grabPiece1.getSelected() + " "
          + m_grabPiece2.getSelected() + " " + m_chargeBalance.getSelected());
      if (m_startPosition.getSelected() == "station") {
        m_robotDrive.resetOdometry(new Pose2d(1.36, 5.20, new Rotation2d(0)));
        if (m_chargeBalance.getSelected()) {
          return new SequentialCommandGroup(
              //Place cone
              new MoveSetDistanceCommand(m_robotDrive, 7.1196, 4.58, new Rotation2d(0),
                  AutoConstants.kMaxAutoVelocity, AutoConstants.kMaxAutoAcceleration, List.of()),
              new MoveSetDistanceCommand(m_robotDrive, 7.1196, 2.7615, new Rotation2d(0),
                  AutoConstants.kMaxAutoVelocity, AutoConstants.kMaxAutoAcceleration, List.of()),
              new MoveSetDistanceCommand(m_robotDrive, 3.485, 2.7615, new Rotation2d(0),
                  AutoConstants.kMaxAutoVelocity, AutoConstants.kMaxAutoAcceleration, List.of()),
              new BalanceCommand(m_robotDrive));
        }
      }
    } else {
      if (m_startPosition.getSelected() == "station") {
        m_robotDrive.resetOdometry(new Pose2d(16.54 - 1.36, 2.19, new Rotation2d(Math.PI)));
        if (m_chargeBalance.getSelected()) {
          return new SequentialCommandGroup(
              //Place cone
              new MoveSetDistanceCommand(m_robotDrive, 16.54 - 7.1196, 2.19, new Rotation2d(Math.PI),
                  AutoConstants.kMaxChargeStationVelocity, AutoConstants.kMaxChargeStationAcceleration, List.of()),
              new MoveSetDistanceCommand(m_robotDrive, 16.54 - 3.485, 2.7615, new Rotation2d(Math.PI),
                  AutoConstants.kMaxChargeStationVelocity, AutoConstants.kMaxChargeStationAcceleration, List.of()),
              new BalanceCommand(m_robotDrive));
        }
      }
    }

    // Reset odometry to the starting pose of the trajectory.
    //m_robotDrive.resetOdometry();

    // Run path following command, then stop at the end.

    return new InstantCommand();

  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be
   * created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with
   * an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link
   * CommandXboxController
   * Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or
   * {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    if (SubsystemConstants.useDrive) {
      new JoystickButton(m_joystick, 2).onTrue(new InstantCommand(m_robotDrive::forceRobotRelative, m_robotDrive));
      new JoystickButton(m_joystick, 2).onFalse(new InstantCommand(m_robotDrive::forceFieldRelative, m_robotDrive));
      new JoystickButton(m_joystick, 10).onTrue(new InstantCommand(m_robotDrive::resetYaw, m_robotDrive));
      new JoystickButton(m_joystick, 8).whileTrue(new BalanceCommand(m_robotDrive));

      // Create config for trajectory

      new JoystickButton(m_joystick, 12)
          .onTrue(new MoveSetDistanceCommand(m_robotDrive, 1.0, 0.5, new Rotation2d(0),
              AutoConstants.kMaxSpeedMetersPerSecond, AutoConstants.kMaxAccelerationMetersPerSecondSquared, List.of()));
      new JoystickButton(m_joystick, 13).onTrue(new MoveSetDistanceCommand(m_robotDrive, 0, 0, new Rotation2d(0),
          AutoConstants.kMaxSpeedMetersPerSecond, AutoConstants.kMaxAccelerationMetersPerSecondSquared, List.of()));

    }

    if (SubsystemConstants.useArm) {
      new JoystickButton(m_opJoystick, 4).onTrue(new ChickenCommand(m_robotArm));
      new JoystickButton(m_opJoystick, 6).onTrue(new BackToNormalCommand(m_robotArm));
    }
    if (SubsystemConstants.usePneumatics) {
      new JoystickButton(m_opJoystick, 2).onTrue(new ChompForward(m_pneumatics));
      new JoystickButton(m_opJoystick, 3).onTrue(new ChompReverse(m_pneumatics));
    }
    if (SubsystemConstants.useTurnTables) {
      new JoystickButton(m_opJoystick, 9).whileTrue(new SpinClockwiseCommand(m_turntables));
      new JoystickButton(m_opJoystick, 10).whileTrue(new SpinCounterClockwiseCommand(m_turntables));
    }
    if (SubsystemConstants.useIntake) {
      new JoystickButton(m_opJoystick, 7).whileTrue(new IntakeCommand(m_intake));

    }
    new JoystickButton(m_joystick, 16).whileTrue(new SequentialCommandGroup(
        new MoveSetDistanceCommand(m_robotDrive, m_visionSubsystem.getDestination(m_robotDrive.getPose(), "left")),
        new RunCommand(
            () -> m_robotDrive.drive(0, 0, 0, m_robotDrive.m_fieldRelative), m_robotDrive)));
    new JoystickButton(m_joystick, 15).whileTrue(new SequentialCommandGroup(
        new MoveSetDistanceCommand(m_robotDrive, m_visionSubsystem.getDestination(m_robotDrive.getPose(), "middle")),
        new RunCommand(
            () -> m_robotDrive.drive(0, 0, 0, m_robotDrive.m_fieldRelative), m_robotDrive)));
    new JoystickButton(m_joystick, 14).whileTrue(new SequentialCommandGroup(
        new MoveSetDistanceCommand(m_robotDrive, m_visionSubsystem.getDestination(m_robotDrive.getPose(), "right")),
        new RunCommand(
            () -> m_robotDrive.drive(0, 0, 0, m_robotDrive.m_fieldRelative), m_robotDrive)));
    new JoystickButton(m_joystick, 9).whileTrue(new PositionUpdateCommand(m_visionSubsystem, m_robotDrive));
  }

  public void resetDriveOffsets() {
    m_robotDrive.resetOffsets();
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */

}
// private void driveWithJoystick(boolean fieldRelative) {
// // Get the x speed. We are inverting this because Xbox controllers return
// // negative values when we push forward.
// final var xSpeed =
// -m_xspeedLimiter.calculate(MathUtil.applyDeadband(m_joystick.getRawAxis(1),
// 0.02))
// * DriveSubsystem.kMaxSpeed;

// // Get the y speed or sideways/strafe speed. We are inverting this because
// // we want a positive value when we pull to the left. Xbox controllers
// // return positive values when you pull to the right by default.
// final var ySpeed =
// -m_yspeedLimiter.calculate(MathUtil.applyDeadband(m_joystick.getRawAxis(0),
// 0.02))
// * DriveSubsystem.kMaxSpeed;
// // Get the rate of angular rotation. We are inverting this because we want a
// // positive value when we pull to the left (remember, CCW is positive in
// // mathematics). Xbox controllers return positive values when you pull to
// // the right by default.
// final var rot =
// -m_rotLimiter.calculate(MathUtil.applyDeadband(m_joystick.getRawAxis(2),
// 0.02))
// * DriveSubsystem.kMaxAngularSpeed;

// m_swerve.drive(xSpeed, ySpeed, rot, fieldRelative);
// }
