package com.example.todoapp.fragments.update

import MyDatePickerDialog
import MyTimePickerDialog
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.*
import com.example.todoapp.databinding.FragmentUpdateBinding
import com.example.todoapp.model.User
import com.example.todoapp.viewmodel.UserViewModel
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

    private lateinit var mUserViewModel: UserViewModel

    lateinit var alarmManager: AlarmManager
    lateinit var alarmIntent: PendingIntent

    private val calendar: Calendar = Calendar.getInstance()
    var year = calendar.get(Calendar.YEAR)
    var month = calendar.get(Calendar.MONTH)
    var day = calendar.get(Calendar.DAY_OF_MONTH)


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentUpdateBinding.inflate(layoutInflater, container, false)


        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.updateSubjectEt.setText(args.currentUser.subject)
        binding.updateDescriptionEt.setText(args.currentUser.description)

        binding.updateButton.setOnClickListener {
            updateItem()
        }


        setHasOptionsMenu(true)

        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menu_delete_note){
            deleteUser()
        }
        if(item.itemId==R.id.menu_notification){

            val timePicker = MyTimePickerDialog(this)
            val datePicker = MyDatePickerDialog(this)

//            val params: WindowManager.LayoutParams? = datePicker.dialog?.window?.attributes
//
//            params?.gravity = Gravity.TOP
//
//            params?.width = 2000
//
//            params?.height = 2000
//
//            datePicker.dialog?.window?.attributes = params

            timePicker.show(requireActivity().supportFragmentManager, null)
            datePicker.show(requireActivity().supportFragmentManager, null)
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


    private fun deleteUser() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){ _, _->
            mUserViewModel.deleteUser(args.currentUser)
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            Toast.makeText(requireContext(), "Successfully removed", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("No"){ _, _->}
            builder.setTitle("Delete ${args.currentUser.subject}?")
            builder.setMessage("Are you sure?")
            builder.create().show()
    }



    private fun updateItem(){
        val subject = updateSubject_et.text.toString()
        val description = updateDescription_et.text.toString()

        if(inputCheck(subject, description)){
            val currentTime = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedDate = currentTime.format(formatter)
            val updatedUser = User(args.currentUser.id, subject, description, formattedDate)

            // Update Current User
            mUserViewModel.updateUser(updatedUser)
            Toast.makeText(requireContext(), "Updated successfully", Toast.LENGTH_SHORT).show()
            // Navigate Back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        else{
            Toast.makeText(requireContext(), "Please fill all gaps", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(subject: String, description: String): Boolean{
        return !TextUtils.isEmpty(subject) && !TextUtils.isEmpty(description)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        Log.d("MINUTA", minute.toString())
        Log.d("GODZINA", hourOfDay.toString())
        Log.d("DZIEN", day.toString())

        alarmIntent = createAlarmIntent()

        val date = Calendar.Builder()
                .setDate(year, month, day)
                .setTimeOfDay(hourOfDay, minute, 0)
                .build()
        alarmManager.set(AlarmManager.RTC_WAKEUP, date.timeInMillis, alarmIntent)
        Toast.makeText(context, "Reminder set to: $day-${month + 1}-$year $hourOfDay:$minute", Toast.LENGTH_SHORT).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        this.year = year
        this.month = month
        this.day = dayOfMonth

    }

    private fun createAlarmIntent(): PendingIntent {
        alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent: Intent = Intent(requireContext(), AlarmReceiver::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("subject", args.currentUser.subject)
        intent.putExtra("description", args.currentUser.description)
        intent.putExtra("id", System.currentTimeMillis().toInt())

        val id = System.currentTimeMillis().toInt()
//        Log.d("ID", id.toString())

        alarmIntent = PendingIntent.getBroadcast(requireActivity().applicationContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return alarmIntent
    }


}