import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.fragment.app.DialogFragment


class MyDatePickerDialog : DialogFragment {
    //Fragments need to empty constructor
    internal constructor()
    internal constructor(onDateSetListener: DatePickerDialog.OnDateSetListener?) {
        this.onDateSetListener = onDateSetListener
    }

    var onDateSetListener: DatePickerDialog.OnDateSetListener? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val dayOfMonth: Int = c.get(Calendar.DAY_OF_MONTH)
//        val datePicker = DatePickerDialog(requireActivity(), onDateSetListener, year, month, dayOfMonth)

//        datePicker.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT);
//        datePicker.window?.setGravity(Gravity.CENTER);
////        val buttonPositive = datePicker.getButton(DialogInterface.BUTTON_POSITIVE)
////        buttonPositive.width = 123
////        buttonPositive.height = 100
////        buttonPositive.textSize = 30F
        return DatePickerDialog(requireActivity(), onDateSetListener, year, month, dayOfMonth)

    }
}