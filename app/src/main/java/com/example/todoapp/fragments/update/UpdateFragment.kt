package com.example.todoapp.fragments.update

import MyDatePickerDialog
import MyTimePickerDialog
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_INCLUDE_STOPPED_PACKAGES
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import android.view.*
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.AlarmReceiver
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentUpdateBinding
import com.example.todoapp.fragments.list.ListAdapter
import com.example.todoapp.model.Note
import com.example.todoapp.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


open class UpdateFragment : Fragment(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{

    private val args by navArgs<UpdateFragmentArgs>()

    private var _binding: FragmentUpdateBinding? = null
    private val binding
        get() = _binding!!

    lateinit var mNoteViewModel: NoteViewModel

    lateinit var alarmManager: AlarmManager
    lateinit var alarmIntent: PendingIntent

    private val calendar: Calendar = Calendar.getInstance()
    var year = calendar.get(Calendar.YEAR)
    var month = calendar.get(Calendar.MONTH)
    var day = calendar.get(Calendar.DAY_OF_MONTH)

    private val adapter = ListAdapter()


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentUpdateBinding.inflate(layoutInflater, container, false)


        mNoteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        binding.updateSubjectEt.setText(args.currentNote.subject)
        binding.updateDescriptionEt.setText(args.currentNote.description)

//        @Suppress("Deprecated")
//        activity?.window?.setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setHasOptionsMenu(true)

        mNoteViewModel.readAllData.observe(viewLifecycleOwner, { note ->
            adapter.setData(note)
        })


        return binding.root
    }

    override fun onDestroyView() {
        updateItem()
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
        if(args.currentNote.reminder!=null){
            menu.findItem(R.id.menu_notification).icon = context?.resources?.getDrawable(R.drawable.ic_notification_cancel, requireContext().theme)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menu_delete_note){
            deleteNoteDialog()
        }
        if(item.itemId==R.id.menu_notification){
            if(args.currentNote.reminder==null) {

                val timePicker = MyTimePickerDialog(this)
                val datePicker = MyDatePickerDialog(this)

                timePicker.show(requireActivity().supportFragmentManager, null)
                datePicker.show(requireActivity().supportFragmentManager, null)
            }
            else{
                deleteReminderDialog()
            }

        }

        if(item.itemId == R.id.menu_share){
            val intent= Intent()
            intent.action= Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, view?.updateSubject_et?.text.toString() + "\n" + view?.updateDescription_et?.text.toString())
            intent.type="text/plain"
            startActivity(Intent.createChooser(intent, "Share your note to:"))
        }
        return super.onOptionsItemSelected(item)
    }


    private fun deleteNoteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){ _, _->
            mNoteViewModel.deleteNote(args.currentNote)
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            Toast.makeText(requireContext(), "Successfully removed", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("No"){ _, _->}
            builder.setTitle("Delete ${args.currentNote.subject}?")
            builder.setMessage("Are you sure?")
            builder.create().show()
    }

    private fun deleteReminderDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){ _, _->
            try {
                alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(requireContext(), AlarmReceiver::class.java)

                alarmIntent = PendingIntent.getBroadcast(requireActivity().applicationContext, args.currentNote.reminder!!, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                alarmIntent.describeContents()

                alarmManager.cancel(alarmIntent)
                alarmIntent.cancel()

                args.currentNote.reminder = null
                args.currentNote.reminderDate = null

                val updatedNote = Note(args.currentNote.id, args.currentNote.subject, args.currentNote.description, args.currentNote.date, null, null)

                Toast.makeText(context, "Reminder canceled", Toast.LENGTH_SHORT).show()
                requireActivity().invalidateOptionsMenu()
                mNoteViewModel.updateNote(updatedNote)
            }
            catch (e: NullPointerException) {
                Toast.makeText(context, "NullPointerException", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("No"){ _, _->}
        builder.setTitle("Delete reminder on ${args.currentNote.reminderDate}?")
        builder.setMessage("Are you sure?")
        builder.create().show()
    }


    private fun updateItem(){
        val subject = updateSubject_et.text.toString()
        val description = updateDescription_et.text.toString()

        if(args.currentNote.subject != subject ||
                args.currentNote.description != description){

        val note: Note = mNoteViewModel.getNoteById(args.currentNote.id)

        if(subject != "" || description != "") {
            val currentTime = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedDate = currentTime.format(formatter)


            val updatedNote = Note(args.currentNote.id, subject, description, formattedDate, note.reminder, note.reminderDate)

            mNoteViewModel.updateNote(updatedNote)

            Toast.makeText(requireContext(), "Updated successfully", Toast.LENGTH_SHORT).show()
        }
            else{
            mNoteViewModel.deleteNote(note)
            Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT).show()
        }
//            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }

    }


    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
//        Log.d("minute", minute.toString())
//        Log.d("hour", hourOfDay.toString())
//        Log.d("day", day.toString())


        val date = Calendar.Builder()
                .setDate(year, month, day)
                .setTimeOfDay(hourOfDay, minute, 0)
                .build()
        val formattedDate = "${if (day < 10) "0$day" else day}-${if (month + 1 < 10) "0${month + 1}" else month}-$year ${if (hourOfDay < 10) "0$hourOfDay" else hourOfDay}:${if (minute < 10) "0$minute" else minute}"

        args.currentNote.reminderDate = formattedDate

        alarmIntent = createAlarmIntent()

        alarmManager.set(AlarmManager.RTC_WAKEUP, date.timeInMillis, alarmIntent)

        Toast.makeText(context, "Reminder set to: $formattedDate", Toast.LENGTH_SHORT).show()

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        this.year = year
        this.month = month
        this.day = dayOfMonth
    }

    @SuppressLint("WrongConstant", "BatteryLife")
    private fun createAlarmIntent(): PendingIntent {
        alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager


        val intent: Intent = Intent(requireContext(), AlarmReceiver::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val reminderId = System.currentTimeMillis().toInt()
        val subject = updateSubject_et.text.toString()
        val description = updateDescription_et.text.toString()

        intent.putExtra("note_id", args.currentNote.id)
        intent.putExtra("subject", subject)
        intent.putExtra("description", description)
        intent.putExtra("date", args.currentNote.date)
        intent.putExtra("id", reminderId)


        val updatedNote = Note(args.currentNote.id, subject, description, args.currentNote.date, reminderId, args.currentNote.reminderDate)

        args.currentNote.reminder = reminderId
        requireActivity().invalidateOptionsMenu()

        mNoteViewModel.updateNote(updatedNote)

        alarmIntent = PendingIntent.getBroadcast(requireActivity().applicationContext, reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        return alarmIntent
    }


}