package alex.shum.pomdoro.util

import alex.shum.pomdoro.R
import android.content.Context
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat

class Util {

    companion object{

        fun selectedButton(textView: TextView, context: Context){
            textView?.isEnabled = false
         //   textView?.setTextColor(ContextCompat.getColor(context, R.color.white))
            textView?.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_500))
        }

        fun unselectedButton(textView: TextView, context: Context){
            textView?.isEnabled = true
         //   textView?.setTextColor(ContextCompat.getColor(context, R.color.black))
            textView?.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }

    }

}