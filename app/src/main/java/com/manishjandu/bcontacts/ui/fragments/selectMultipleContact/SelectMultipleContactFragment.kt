package com.manishjandu.bcontacts.ui.fragments.selectMultipleContact

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.FragmentSelectMultipleContactBinding
import com.manishjandu.bcontacts.utils.Constants.CONTACT_SELECTED
import com.manishjandu.bcontacts.utils.Constants.FRAGMENT_SELECT_MULTIPLE_CONTACT_REQUEST_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectMultipleContactFragment : Fragment(R.layout.fragment_select_multiple_contact) {
    private val viewModel: SelectMultipleContactViewModel by viewModels()
    private var _binding: FragmentSelectMultipleContactBinding?=null
    private val binding get()=_binding!!
    private var tracker: SelectionTracker<Long>?=null
    private var selectedContactIds: List<Long>?=null
    private var contacts: List<Contact> =mutableListOf()
    private val multipleUserAdapter=MultipleUserAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding=FragmentSelectMultipleContactBinding.bind(view)

        val multipleUserAdapter=MultipleUserAdapter()
        val recyclerView=binding.recyclerViewSelectMultipleContact

        viewModel.getBContacts()

        recyclerView.apply {
            adapter=multipleUserAdapter
            binding.recyclerViewSelectMultipleContact.layoutManager=LinearLayoutManager(context)
        }

        viewModel.contacts.observe(viewLifecycleOwner) {
            it?.let {
                multipleUserAdapter.submitList(it)
                contacts=it
            }
        }

        tracker=SelectionTracker.Builder<Long>(
            "selection-1",
            recyclerView,
            StableIdKeyProvider(recyclerView),
            PositionLookup(recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        multipleUserAdapter.setTracker(tracker)

        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    val nItems: Int?=tracker?.selection?.size()

                    tracker?.let {
                        selectedContactIds=it.selection.toMutableList()
                    }

                }
            })
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_item_multiple_contact_selected, menu)

        val itemSelectedButton=menu.findItem(R.id.menu_item_contact_selected)
        itemSelectedButton.setOnMenuItemClickListener {

            val selectedContactItems=arrayListOf<Contact>()
            selectedContactIds?.forEach {
                selectedContactItems.add(contacts[it.toInt()])
            }

            //set result
            setFragmentResult(
                FRAGMENT_SELECT_MULTIPLE_CONTACT_REQUEST_KEY, bundleOf(
                    CONTACT_SELECTED to selectedContactItems
                )
            )
            findNavController().navigateUp()

            true
        }

    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        multipleUserAdapter.clearSelection()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}


