package com.manishjandu.bcontacts.ui.fragments.allcontact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.FragmentAllContactBinding
import com.manishjandu.bcontacts.ui.viewModels.SharedViewModel
import com.manishjandu.bcontacts.utils.checkReadWriteContactPermission
import com.manishjandu.bcontacts.utils.setReadWriteContactPermission
import dagger.hilt.android.AndroidEntryPoint

private const val TAG="AllContactFragment"

@AndroidEntryPoint
class AllContactFragment : Fragment(R.layout.fragment_all_contact) {
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentAllContactBinding
    private lateinit var allContactAdapter: AllContactAdapter
    private lateinit var searchView: SearchView

    override fun onStart() {
        super.onStart()
        if (!checkReadWriteContactPermission()) {
            setReadWriteContactPermission { viewModel.getContactsList() }
        } else {
            viewModel.getContactsList()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentAllContactBinding.bind(view)
        allContactAdapter=AllContactAdapter(OnClick())

        binding.floatingButtonAddEditContact.setOnClickListener {
            val intent=Intent(Intent.ACTION_INSERT_OR_EDIT).apply {
                type=ContactsContract.Contacts.CONTENT_ITEM_TYPE
            }
            startActivity(intent)
        }

        binding.recyclerViewAllContact.apply {
            adapter=allContactAdapter
            layoutManager=LinearLayoutManager(requireContext())
        }

        viewModel.contacts.observe(viewLifecycleOwner) {
            it?.let {
                allContactAdapter.submitList(it)
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_item_search, menu)

        val searchItem=menu.findItem(R.id.action_search)
        searchView=searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrEmpty()) {
                    viewModel.getContactsList()
                } else {
                    viewModel.getContactsList(query)
                }
                binding.recyclerViewAllContact.scrollToPosition(0)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.getContactsList()
                } else {
                    viewModel.getContactsList(newText)
                }
                binding.recyclerViewAllContact.scrollToPosition(0)
                return false
            }
        })

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

        override fun onAddToBContactClicked(contact: Contact) {
            viewModel.addContactLocally(contact)
            Toast.makeText(requireContext(), "Added to B contact", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}