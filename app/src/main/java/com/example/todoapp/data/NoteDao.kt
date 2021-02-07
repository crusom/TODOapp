package com.example.todoapp.data

import androidx.room.*
import com.example.todoapp.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM note_table ORDER BY reminder DESC")
    fun readAllData(): Flow<List<Note>>

    @Query("DELETE FROM note_table")
    suspend fun deleteAllNotes()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(noteList: List<Note>)

    @Query("SELECT * FROM note_table WHERE subject LIKE :searchQuery OR description LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): Flow<List<Note>>

    @Query("select * from note_table where id= :id")
    fun getNoteById(id: Int) : Note


}