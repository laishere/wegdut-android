package com.wegdut.wegdut.utils

import android.Manifest
import android.app.Activity
import android.os.Build
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

object PermissionUtils {
    fun nextOrWarn(
        activity: Activity, permissions: Array<String>,
        next: () -> Unit, warn: String?
    ) {
        val denyListener =
            DialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(activity)
                .withButtonText("好的")
                .withMessage(warn)
                .build()
        val nextListener = object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) next()
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
            }

        }
        Dexter.withActivity(activity)
            .withPermissions(*permissions)
            .withListener(CompositeMultiplePermissionsListener(denyListener, nextListener))
            .check()
    }

    fun pickImage(activity: Activity, warning: String, next: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            nextOrWarn(
                activity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), next, warning
            )
        } else nextOrWarn(
            activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), next, warning
        )
    }
}