import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.DialogFragment


class MyTimePickerDialog : DialogFragment {
    //Fragments need to empty constructor
    internal constructor()
    internal constructor(onTimeSetListener: OnTimeSetListener?) {
        this.onTimeSetListener = onTimeSetListener
    }

    var onTimeSetListener: OnTimeSetListener? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val hour: Int = c.get(Calendar.HOUR_OF_DAY)
        val minute: Int = c.get(Calendar.MINUTE)
        return TimePickerDialog(activity, onTimeSetListener, hour, minute, true)
    }
}