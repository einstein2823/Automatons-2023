// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.VisionSubsystem;

import java.util.List;

import org.opencv.core.Point;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;

/** An example command that uses an example subsystem. */
public class MoveWithVisionCommand extends CommandBase {
  @SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.SingularField" })
  private final DriveSubsystem m_subsystem;
  private final VisionSubsystem m_vision;
  private MoveSetDistanceCommand m_moveCommand;
  private String m_targetLocation;

  /*
   * Creates a new ResetFalconCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public MoveWithVisionCommand(DriveSubsystem subsystem, VisionSubsystem vSubsystem, String targetLocation) {
    m_subsystem = subsystem;
    m_targetLocation = targetLocation;
    m_vision = vSubsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_moveCommand = new MoveSetDistanceCommand(m_subsystem,
        m_vision.getDestination(m_subsystem.getPose(), m_targetLocation));
    m_moveCommand.initialize();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_moveCommand.execute();
  }
  // if (m_vision != null) {
  //   m_subsystem.resetOdometry(m_vision.getRobotAbsolute());
  // }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_moveCommand.end(interrupted);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_moveCommand.isFinished();
  }
}