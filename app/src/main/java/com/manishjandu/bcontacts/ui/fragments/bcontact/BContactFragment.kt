package com.manishjandu.bcontacts.ui.fragments.bcontact

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.assent.*
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.data.local.entities.SavedContact
import com.manishjandu.bcontacts.databinding.FragmentBContactBinding
import com.manishjandu.bcontacts.ui.viewModels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

private const val TAG="BContactFragment"

@AndroidEntryPoint
class BContactFragment : Fragment(R.layout.fragment_b_contact) {
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentBContactBinding
    private lateinit var bContactAdapter: BContactAdapter

    @Inject
    @Named("futureMessageIntent")
      lateinit var intent: Intent
    @Inject
      lateinit var alarmManager: AlarmManager

    override fun onStart() {
        super.onStart()
        if (!checkPermission()) {
            setPermissions()
        } else {
            viewModel.getContactLocally()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentBContactBinding.bind(view)
        val recyclerViewBContact=binding.recyclerViewBContact

        bContactAdapter=BContactAdapter(OnClick())

        recyclerViewBContact.adapter=bContactAdapter
        recyclerViewBContact.layoutManager=LinearLayoutManager(requireContext())


        viewModel.bContacts.observe(viewLifecycleOwner) {
            bContactAdapter.submitList(it)
        }

        viewModel.futureMessage.observe(viewLifecycleOwner){futureMessages->
            for (message in futureMessages){
                val pendingIntent=PendingIntent.getBroadcast(requireContext(), message.messageId, intent, 0)
                alarmManager.cancel(pendingIntent)
            }
        }

    }

    inner class OnClick : BContactAdapter.OnClick {
        override fun onCallClicked(contactNumber: String) {
            val callIntent=Intent(Intent.ACTION_DIAL, Uri.parse("tel:$contactNumber"))
            startActivity(callIntent)
        }

        override fun onMessageClicked(contactNumber: String) {
            val messageIntent=Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$contactNumber"))
            startActivity(messageIntent)
        }

        override fun onMoreOptionClicked(
            savedContact: SavedContact,
            buttonMoreOption: ImageButton
        ) {
            val popupMenu=PopupMenu(requireContext(), buttonMoreOption)
            popupMenu.menuInflater.inflate(R.menu.more_options_menu_b_contact, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.button_remove_from_b_contact -> {
                        alertDialog(savedContact)
                    }
                    R.id.button_notes -> {
                        val action=BContactFragmentDirections.actionBContactFragmentToNotesFragment(
                            savedContact.contactId
                        )
                        findNavController().navigate(action)
                    }
                    R.id.button_future_messages -> {
                        val action = BContactFragmentDirections.actionBContactFragmentToMessagesFragment(
                            savedContact.contactId,savedContact.phone
                        )
                        findNavController().navigate(action)
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun alertDialog(savedContact: SavedContact) {
        AlertDialog.Builder(requireContext())
            .setTitle("Alert!")
            .setMessage("Do you really want to delete this contact, as it will also delete notes and messages related to it?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.removeContactLocally(savedContact)
            }
            .setNegativeButton("No"){_,_ ->

            }
            .create()
            .show()
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
                viewModel.getContactLocally()
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