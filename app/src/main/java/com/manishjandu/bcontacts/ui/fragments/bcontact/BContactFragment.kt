package com.manishjandu.bcontacts.ui.fragments.bcontact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.FragmentBContactBinding
import com.manishjandu.bcontacts.ui.fragments.bcontactBottomSheet.BContactBottomSheet.Companion.BOTTOM_SHEET_REQUEST_KEY
import com.manishjandu.bcontacts.ui.fragments.bcontactBottomSheet.BContactBottomSheet.Companion.REFRESH_BCONTACTS
import com.manishjandu.bcontacts.ui.viewModels.SharedViewModel
import com.manishjandu.bcontacts.utils.checkReadWriteContactPermission
import com.manishjandu.bcontacts.utils.setReadWriteContactPermission
import dagger.hilt.android.AndroidEntryPoint

private const val TAG="BContactFragment"

@AndroidEntryPoint
class BContactFragment : Fragment(R.layout.fragment_b_contact) {
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentBContactBinding
    private lateinit var bContactAdapter: BContactAdapter

    override fun onStart() {
        super.onStart()
        if (!checkReadWriteContactPermission()) {
            setReadWriteContactPermission { viewModel.getContactLocally() }
        } else {
            viewModel.getContactLocally()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentBContactBinding.bind(view)

        bContactAdapter=BContactAdapter(OnClick())

        binding.recyclerViewBContact.apply {
            adapter=bContactAdapter
            layoutManager=LinearLayoutManager(requireContext())
        }

        viewModel.bContacts.observe(viewLifecycleOwner) {
            bContactAdapter.submitList(it)
        }
        setFragmentResultListener(BOTTOM_SHEET_REQUEST_KEY) { _, bundle ->
            val result=bundle.getBoolean(REFRESH_BCONTACTS)
            if (result) {
                viewModel.getContactLocally()
                Toast.makeText(requireContext(), "Contact Removed", Toast.LENGTH_SHORT).show()
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_item_multiple_message, menu)

        val buttonMultiUser=menu.findItem(R.id.action_navigate_to_multiple_message_fragment)
        buttonMultiUser.setOnMenuItemClickListener {
            val action=BContactFragmentDirections.actionBContactFragmentToMultipleUsersFragment()
            findNavController().navigate(action)
            true
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

        override fun bottomSheet(contact: Contact) {
            val action=
                BContactFragmentDirections.actionBContactFragmentToBContactBottomSheet(contact)
            findNavController().navigate(action)
        }
    }

}