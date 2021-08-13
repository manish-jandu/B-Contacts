package com.manishjandu.bcontacts.utils

import android.graphics.Color
import android.view.View
import androidx.fragment.app.Fragment
import com.afollestad.assent.GrantResult
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.afollestad.assent.showSystemAppDetailsPage
import com.google.android.material.snackbar.Snackbar

fun Fragment.checkReadWriteContactPermission(): Boolean {
    return isAllGranted(Permission.READ_CONTACTS, Permission.WRITE_CONTACTS)
}

fun Fragment.setReadWriteContactPermission(ifGrantedExecute: () -> Unit) {
    askForPermissions(
        Permission.READ_CONTACTS,
        Permission.WRITE_CONTACTS,
    ) { result ->

        val isAllGranted=result.isAllGranted(
            Permission.READ_CONTACTS,
            Permission.WRITE_CONTACTS,
        )

        if (isAllGranted) {
            ifGrantedExecute()
        }

        if (result[Permission.READ_CONTACTS] == GrantResult.DENIED ||
            result[Permission.WRITE_CONTACTS] == GrantResult.DENIED
        ) {
            showSnackBarOnPermissionError(requireView(), "Permission is required", "Ok") {
                setReadWriteContactPermission(ifGrantedExecute)
            }
        }

        if (result[Permission.READ_CONTACTS] == GrantResult.PERMANENTLY_DENIED ||
            result[Permission.WRITE_CONTACTS] == GrantResult.PERMANENTLY_DENIED
        ) {
            showSnackBarOnPermissionError(requireView(), "Accept Contacts Permission", "Settings") {
                showSystemAppDetailsPage()
            }
        }

    }
}

private fun showSnackBarOnPermissionError(
    view: View,
    message: String,
    textAction: String,
    action: () -> Unit
) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        .setAction(textAction) {
            action()
        }.setActionTextColor(Color.RED)
        .show()
}
