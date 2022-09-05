package alex.shum.pomdoro.ui.pomodoro

import alex.shum.pomdoro.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class MyListAdapter(context: Context, var resource: Int, var items: List<String>, var textState: TextView) :
    ArrayAdapter<String>(context, resource, items) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(context)

        val view: View = layoutInflater.inflate( resource, null)
        val imageView: ImageView = view.findViewById(R.id.buttonDelete)
        var textView: TextView = view.findViewById(R.id.task)

        imageView.setOnClickListener {
            remove(items[position])
            textState.text = "Time to work!"
        }


        var task: String = items[position]

        textView.text = task

        return view
    }

}