package com.manishjandu.bcontacts.ui.bcontact

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.assent.*
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.bcontacts.R

private const val TAG="BContactFragment"

class BContactFragment : Fragment(R.layout.fragment_b_contact) {

    override fun onStart() {
        super.onStart()
        if (!checkPermission()) {
            setPermissions()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Todo:show contacts
    }


    private fun checkPermission(): Boolean {
        return isAllGranted(Permission.READ_CONTACTS, Permission.WRITE_CONTACTS)
    }

    private fun setPermissions() {

        askForPermissions(
            Permission.READ_CONTACTS,
            Permission.WRITE_CONTACTS,
        ) { result ->

            if (result.isAllGranted()) {
                //Todo:
            }

            if (result[Permission.READ_CONTACTS] == GrantResult.DENIED ||
                result[Permission.WRITE_CONTACTS] == GrantResult.DENIED
            ) {
                showSnackBarOnPermissionError("Permission is required", "Ok") {
                    setPermissions()
                }
            }

            if (result[Permission.READ_CONTACTS] == GrantResult.PERMANENTLY_DENIED ||
                result[Permission.WRITE_CONTACTS] == GrantResult.PERMANENTLY_DENIED
            ) {
                showSnackBarOnPermissionError("Goto Settings Page", "Settings") {
                    showSystemAppDetailsPage()
                }
            }

        }
    }

    private fun showSnackBarOnPermissionError(
        message: String,
        textAction: String,
        action: () -> Unit
    ) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setAction(textAction) {
                action()
            }.setActionTextColor(Color.RED)
            .show()
    }
}