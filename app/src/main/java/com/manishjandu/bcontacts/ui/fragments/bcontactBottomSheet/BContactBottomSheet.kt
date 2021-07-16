package com.manishjandu.bcontacts.ui.fragments.bcontactBottomSheet

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
 import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.BottomSheetBContactBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class BContactBottomSheet() : BottomSheetDialogFragment() {
    private val viewModel: BContactBottomSheetViewModel by viewModels()
    private var _binding: BottomSheetBContactBinding?=null
    private val binding get()=_binding!!
    private val args: BContactBottomSheetArgs by navArgs()

    @Inject
    @Named("futureMessageIntent")
    lateinit var intent: Intent

    @Inject
    lateinit var alarmManager: AlarmManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=BottomSheetBContactBinding.inflate(inflater, container, false)

        val contact=args.contact

        binding.apply {
            textViewBContactName.text=contact.name
            textViewBContactNumber.text=contact.phone

            buttonCall.setOnClickListener {
                val callIntent=Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                startActivity(callIntent)
            }

            buttonMessage.setOnClickListener {
                val messageIntent=Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${contact.phone}"))
                startActivity(messageIntent)
            }

            buttonFutureMessages.setOnClickListener {
                val action=
                    BContactBottomSheetDirections.actionBContactBottomSheetToMessagesFragment(
                        contact.contactId,
                        contact.phone
                    )
                findNavController().navigate(action)
            }

            buttonNotes.setOnClickListener {
                val action=
                    BContactBottomSheetDirections.actionBContactBottomSheetToNotesFragment(contact.contactId)
                findNavController().navigate(action)
            }

            buttonBContactBDay.setOnClickListener {
                //Todo:birth day fragment
            }

            buttonRemoveFromBContact.setOnClickListener {
                alertDialog(contact)
            }

        }

        viewModel.futureMessage.observe(viewLifecycleOwner) { futureMessages ->
            for (message in futureMessages) {
                val pendingIntent=
                    PendingIntent.getBroadcast(requireContext(), message.messageId, intent, 0)
                alarmManager.cancel(pendingIntent)
            }
        }

        return binding.root
    }

    private fun alertDialog(contact: Contact) {
        AlertDialog.Builder(requireContext())
            .setTitle("Alert!")
            .setMessage("Do you really want to delete this contact, as it will also delete notes and messages related to it?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.removeContactLocally(contact)
            }
            .setNegativeButton("Cancel") { _, _ ->

            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}