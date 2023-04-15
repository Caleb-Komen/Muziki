package com.techdroidcentre.musicplayer.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun PermissionDialog(
    context: Context,
    permissionAction: (PermissionAction) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(true) }
    val readPermission = Manifest.permission.READ_EXTERNAL_STORAGE
    val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

    val isPermissionGranted = (ContextCompat.checkSelfPermission(context, readPermission) == PackageManager.PERMISSION_GRANTED)
            && (ContextCompat.checkSelfPermission(context, writePermission) == PackageManager.PERMISSION_GRANTED)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
        if (isGranted.containsValue(false))
            permissionAction(PermissionAction.PermissionDenied)
        else
            permissionAction(PermissionAction.PermissionGranted)
    }

    if (isPermissionGranted) {
        permissionAction(PermissionAction.PermissionGranted)
        return
    }

    val activity = context as Activity?
    if (activity == null) {
        Log.d("PermissionUtil", "Activity is null")
    }
    val showPermissionRationale = (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, readPermission))
            || (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, writePermission))
    
    if (showPermissionRationale) {
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                text = {
                    Text(text = "This app requires access to your media files in order to continue")
                },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        launcher.launch(arrayOf(readPermission, writePermission))
                    }) {
                        Text(text = "Grant Access")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                        permissionAction(PermissionAction.PermissionDenied)
                    }) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    } else {
        SideEffect {
            launcher.launch(arrayOf(readPermission, writePermission))
        }
    }
}

sealed class PermissionAction {
    object PermissionGranted : PermissionAction()
    object PermissionDenied : PermissionAction()
}