package com.example.todoapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "note_table")
data class Note(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        var subject: String,
        val description: String,
        val date: String,
        var reminder: Int?,
        var reminderDate: String?
        ): Parcelable