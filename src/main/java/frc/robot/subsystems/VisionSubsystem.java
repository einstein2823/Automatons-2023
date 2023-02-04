// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class VisionSubsystem extends SubsystemBase {

  GenericEntry robotPose;
  NetworkTableEntry yEntry;
  NetworkTableEntry targetPose;
  Transform2d apriltagPose;
  Pose2d absolutePose;
  Pose2d cameraPose;
  Map<Integer, Transform2d> apriltags;
  int apriltageid;
  Translation2d cameraTrans;
  Pose2d rotationCamPose;

  private Field2d m_field = new Field2d();
  PhotonCamera camera = new PhotonCamera(Constants.VisionConstants.kcameraName);

  ShuffleboardTab Tab = Shuffleboard.getTab("Field");
  // Tab.add("CameraPose", null).withwidget;

  /** Creates a new ExampleSubsystem. */
  public VisionSubsystem() {

    SmartDashboard.putData("Field", m_field);
    apriltags = new HashMap<Integer, Transform2d>();
    apriltags.put(1, new Transform2d(new Translation2d(15.514, 1.072), new Rotation2d(Math.PI)));
    apriltags.put(2, new Transform2d(new Translation2d(15.514, 2.748), new Rotation2d(Math.PI)));
    apriltags.put(3, new Transform2d(new Translation2d(15.514, 4.424), new Rotation2d(Math.PI)));
    apriltags.put(4, new Transform2d(new Translation2d(16.179, 6.75), new Rotation2d(Math.PI)));
    apriltags.put(5, new Transform2d(new Translation2d(0.362, 6.75), new Rotation2d(0)));
    apriltags.put(6, new Transform2d(new Translation2d(1.027, 4.424), new Rotation2d(0)));
    apriltags.put(7, new Transform2d(new Translation2d(1.027, 2.748), new Rotation2d(0)));
    apriltags.put(8, new Transform2d(new Translation2d(1.027, 1.072), new Rotation2d(0)));

    // robotPose = Shuffleboard.getTab("Field")
    // .add("CameraPose", new Pose2d())
    // .withWidget(BuiltInWidgets.kField)
    // .getEntry();

    // Get the default instance of NetworkTables that was created automatically
    // when your program starts
    // NetworkTableInstance inst = NetworkTableInstance.getDefault();

    // Get the table within that instance that contains the data. There can
    // be as many tables as you like and exist to make it easier to organize
    // your data. In this case, it's a table called datatable.
    // NetworkTable table = inst.getTable("photonvision/Microsoft_LifeCam_HD-3000");

    // Get the entries within that table that correspond to the X and Y values
    // for some operation in your program.
    // xEntry = table.getEntry("X");
    // yEntry = table.getEntry("Y");
    // targetPose = table.getEntry("targetPose");
  }

  // double x = 0;
  // double y = 0;

  public Pose2d getCameraAbsolute() {
    PhotonPipelineResult result = camera.getLatestResult();
    if (result.hasTargets()) {
      List<PhotonTrackedTarget> targets = result.getTargets();
      for (PhotonTrackedTarget target : targets) {
        //System.out.println(target.getBestCameraToTarget().getX());
        //System.out.println(target.getBestCameraToTarget().getY());
        //System.out.println(target.getBestCameraToTarget().getZ());
        int apriltagid = target.getFiducialId();

        apriltagPose = apriltags.get(apriltagid);
        if (apriltagPose == null) {
          return null;
        }
        cameraPose = new Pose2d(target.getBestCameraToTarget().getX(), target.getBestCameraToTarget().getY(),
            new Rotation2d());
        cameraTrans = cameraPose.getTranslation().rotateBy(apriltagPose.getRotation());
        rotationCamPose = new Pose2d(cameraTrans, new Rotation2d());
        // robotPose.setValue(absolutePose);
        absolutePose = rotationCamPose.plus(apriltagPose);
        m_field.setRobotPose(absolutePose);

        return absolutePose;
      }

    }
    return null;

  }

  @Override
  public void periodic() {

  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
