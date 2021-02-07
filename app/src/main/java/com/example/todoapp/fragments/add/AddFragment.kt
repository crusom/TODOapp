package com.example.todoapp.fragments.add

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.databinding.FragmentAddBinding
import com.example.todoapp.model.Note
import com.example.todoapp.viewmodel.NoteViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AddFragment : Fragment() {

    private lateinit var mNoteViewModel: NoteViewModel
    private var _binding: FragmentAddBinding? = null
    private val binding
        get() = _binding!!

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentAddBinding.inflate(layoutInflater, container, false)

        mNoteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)


        activity?.window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onDestroyView() {
        insertDataToDatabase()
        super.onDestroyView()
        _binding = null
    }




    private fun insertDataToDatabase() {
        val subject = binding.addSubjectEt.text.toString()
        val description = binding.addDescriptionEt.text.toString()

        if(subject != "" || description != ""){
        val currentTime = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDate = currentTime.format(formatter)
        val note = Note(0, subject, description, formattedDate, null, null)

        mNoteViewModel.addNote(note)
        Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()
        }

    }

}