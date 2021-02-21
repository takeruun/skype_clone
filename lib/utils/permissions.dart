import 'package:permission_handler/permission_handler.dart';
import 'package:flutter/services.dart';

class Permissions {
  static Future<bool> cameraAndMicrophonePermissionsGranted() async {
    PermissionStatus cameraPermissionStatus = await _getCameraPermission();
    PermissionStatus microphonePermissionStatus =
        await _getMicrophonePermission();

    if (cameraPermissionStatus == PermissionStatus.granted &&
        microphonePermissionStatus == PermissionStatus.granted) {
      return true;
    } else {
      _handleInvalidPermissions(
          cameraPermissionStatus: cameraPermissionStatus,
          microphonePermissionStatus: microphonePermissionStatus);
      return false;
    }
  }

  static Future<bool> microphonePermissonsGranted() async {
    PermissionStatus microphonePermissionStatus =
        await _getMicrophonePermission();

    if (microphonePermissionStatus == PermissionStatus.granted) {
      return true;
    } else {
      _handleInvalidPermissions(
          microphonePermissionStatus: microphonePermissionStatus);
      return false;
    }
  }

  static Future<PermissionStatus> _getCameraPermission() async {
    PermissionStatus permission = await Permission.camera.request();
    if (!permission.isGranted) {
      Map<Permission, PermissionStatus> permissionStatus =
          await [Permission.camera].request();
      return permissionStatus[Permission.camera] ?? Permission.unknown;
    } else {
      return permission;
    }
  }

  static Future<PermissionStatus> _getMicrophonePermission() async {
    PermissionStatus permission = await Permission.microphone.request();
    if (!permission.isGranted) {
      Map<Permission, PermissionStatus> permissionStatus =
          await ([Permission.microphone]).request();
      return permissionStatus[Permission.microphone] ?? Permission.unknown;
    } else {
      return permission;
    }
  }

  static void _handleInvalidPermissions({
    PermissionStatus cameraPermissionStatus,
    PermissionStatus microphonePermissionStatus,
  }) async {
    if (cameraPermissionStatus == PermissionStatus.denied &&
        microphonePermissionStatus == PermissionStatus.denied) {
      throw new PlatformException(
          code: "PERMISSION_DENIED",
          message: "Access to camera and microphone denied",
          details: null);
    } else if (await Permission.location.serviceStatus.isEnabled) {
      throw new PlatformException(
          code: "PERMISSION_DISABLED",
          message: "Location data is not available on device",
          details: null);
    }
  }
}
