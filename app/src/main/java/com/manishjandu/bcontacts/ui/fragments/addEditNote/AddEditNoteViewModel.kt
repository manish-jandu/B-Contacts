package com.manishjandu.bcontacts.ui.fragments.addEditNote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.entities.Notes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(private val repo: ContactsRepository) : ViewModel(){
    private val _note=MutableLiveData<Notes>()
    val note: LiveData<Notes> = _note

    private val addEditNoteEventChannel=Channel<AddEditNoteEvent>()
    val addEditNoteEvent=addEditNoteEventChannel.receiveAsFlow()

    fun addNote(contactId: Long, title: String?, description: String?, notesId: Int)=
        viewModelScope.launch {
            if (!title.isNullOrEmpty() && !description.isNullOrEmpty()) {
                if (notesId == 0) {
                    val note=Notes(contactId, title, description)
                    repo.addNote(note)
                } else {
                    val note=Notes(contactId, title, description, notesId)
                    repo.addNote(note)
                }
                addEditNoteEventChannel.send(AddEditNoteEvent.NoteAddingSuccessful)
            } else {
                addEditNoteEventChannel.send(AddEditNoteEvent.NoteAddingNotSuccessful)
            }
        }

    fun getNote(notesId: Int)=viewModelScope.launch {
        if (notesId != 0) {
            val result=repo.getNote(notesId)
            _note.postValue(result)
        }
    }

    sealed class AddEditNoteEvent {
        object NoteAddingSuccessful : AddEditNoteEvent()
        object NoteAddingNotSuccessful : AddEditNoteEvent()
    }
}