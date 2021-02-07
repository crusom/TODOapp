package com.example.todoapp.repository

import com.example.todoapp.data.NoteDao
import com.example.todoapp.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    val readAllData: Flow<List<Note>> = noteDao.readAllData()

    suspend fun addNote(note: Note){
        noteDao.addNote(note)
    }

    suspend fun updateNote(note: Note){
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: Note){
        noteDao.deleteNote(note)
    }

    suspend fun deleteAllNotes(){
        noteDao.deleteAllNotes()
    }

    suspend fun update(noteList: List<Note>){
        noteDao.update(noteList)
    }

    fun searchDatabase(searchQuery: String): Flow<List<Note>>{
        return noteDao.searchDatabase(searchQuery)
    }

    fun getNoteById(id: Int): Note{
        return noteDao.getNoteById(id)
    }
}