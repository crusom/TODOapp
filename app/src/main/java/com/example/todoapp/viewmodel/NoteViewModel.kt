package com.example.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.NoteDatabase
import com.example.todoapp.model.Note
import com.example.todoapp.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel (application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Note>>
    private val repository: NoteRepository

    init{
        val noteDao = NoteDatabase.getDatabase(application).NoteDao()
        repository = NoteRepository(noteDao)
        readAllData = repository.readAllData.asLiveData()
    }

    fun addNote(note: Note){
        viewModelScope.launch(Dispatchers.IO){
            repository.addNote(note)
        }
    }

    fun updateNote(note: Note){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: Note){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteNote(note)
        }
    }

    fun deleteAllNotes(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAllNotes()
        }
    }

    fun update(noteList: List<Note>){
        viewModelScope.launch(Dispatchers.IO){
            repository.update(noteList)
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<Note>> {
        return repository.searchDatabase(searchQuery).asLiveData()
    }

    fun getNoteById(id: Int): Note{
        return repository.getNoteById(id)
    }

}