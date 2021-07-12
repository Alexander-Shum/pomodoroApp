package alex.shum.pomdoro.ui.pomodoro

import alex.shum.pomdoro.R
import alex.shum.pomdoro.R.string.app_name
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView

class MyListAdapter(context: Context, var resource: Int, var items: List<String>) :
    ArrayAdapter<String>(context, resource, items) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(context)

        val view: View = layoutInflater.inflate( resource, null)
        val imageView: ImageView = view.findViewById(R.id.buttonDelete)
        var textView: TextView = view.findViewById(R.id.task)



        var task: String = items[position]

        textView.text = task

        return view
    }

}