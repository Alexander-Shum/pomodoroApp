package alex.shum.pomdoro.ui.pomodoro

import alex.shum.pomdoro.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color.RED
import android.hardware.camera2.params.RggbChannelVector
import android.hardware.camera2.params.RggbChannelVector.RED
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val tasks: List<String>, val context: Context) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {

    private val listTasks: MutableList<String> = tasks as MutableList<String>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        @SuppressLint("ResourceAsColor")
        fun bind(index: Int) {
            val buttonDelete: ImageView = itemView.findViewById(R.id.buttonDelete)
            val task: TextView = itemView.findViewById(R.id.task)
            val countChoice: FrameLayout = itemView.findViewById(R.id.frameLayout)

            task.text = tasks[index]

            task.setOnClickListener {
                Toast.makeText(context, index.toString(), Toast.LENGTH_SHORT).show()
            }
            buttonDelete.setOnClickListener { deleteItem(index) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listTasks.size
    }

    override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun deleteItem(index: Int) {
        listTasks.removeAt(index)
        notifyDataSetChanged()
    }

}