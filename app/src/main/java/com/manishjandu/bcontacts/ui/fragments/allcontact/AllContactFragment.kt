package com.manishjandu.bcontacts.ui.fragments.allcontact

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.assent.*
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.FragmentAllContactBinding
import com.manishjandu.bcontacts.ui.viewModels.SharedViewModel

private const val TAG="AllContactFragment"

class AllContactFragment : Fragment(R.layout.fragment_all_contact) {
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentAllContactBinding
    private lateinit var allContactAdapter: AllContactAdapter

    override fun onStart() {
        super.onStart()
        if (!checkPermission()) {
            setPermissions()
        } else {
            viewModel.getContactsList(requireContext())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentAllContactBinding.bind(view)
        allContactAdapter=AllContactAdapter(OnClick())

        binding.floatingButton.setOnClickListener {
            val intent=Intent(Intent.ACTION_INSERT_OR_EDIT).apply {
                type=ContactsContract.Contacts.CONTENT_ITEM_TYPE
            }
            startActivity(intent)
        }

        binding.recyclerViewAllContact.adapter=allContactAdapter
        binding.recyclerViewAllContact.layoutManager=LinearLayoutManager(requireContext())

        viewModel.contacts.observe(viewLifecycleOwner) {
            it?.let {
                allContactAdapter.submitList(it)
            }
        }

    }


    inner class OnClick : AllContactAdapter.OnClick {
        override fun onCallClicked(contactNumber: String) {
            val callIntent=Intent(Intent.ACTION_DIAL, Uri.parse("tel:$contactNumber"))
            startActivity(callIntent)
        }

        override fun onMessageClicked(contactNumber: String) {
            val messageIntent=Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$contactNumber"))
            startActivity(messageIntent)
        }

        override fun onMoreOption(contact: Contact, buttonMoreOption: ImageButton) {
            val popupMenu=PopupMenu(requireContext(), buttonMoreOption)
            popupMenu.menuInflater.inflate(R.menu.more_options_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.button_add_to_b_contact -> {
                        //Todo:Add to favourite

                        viewModel.addContactLocally(contact)

                        //Todo:if already exist then remove it from favourite
                    }
                }
                true
            }
            popupMenu.show()
        }
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
                viewModel.getContactsList(requireContext())
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