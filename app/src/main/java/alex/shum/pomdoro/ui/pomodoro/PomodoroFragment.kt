package alex.shum.pomdoro.ui.pomodoro

import alex.shum.pomdoro.R
import alex.shum.pomdoro.util.PrefUtil
import alex.shum.pomdoro.util.Util
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class PomodoroFragment : Fragment() {

    enum class TimerState {
        Stopped, Paused, Running
    }

    lateinit var adapter: ArrayAdapter<String>
    private var timerBreak: CountDownTimer? = null
    private var timerPomodoro: CountDownTimer? = null
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped
    private var secondRemaining = 0L


    lateinit var addTask: TextView
    lateinit var textTimer: TextView
    lateinit var buttonPomodoro: TextView
    lateinit var buttonBreak: TextView
    lateinit var fab_start: FloatingActionButton
    lateinit var fab_pause: FloatingActionButton
    lateinit var fab_stop: FloatingActionButton

    var isBreak = false
    var arrayList: ArrayList<String> = ArrayList()

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_pomodoro, container, false)

        val listTask: ListView = root.findViewById(R.id.tasks)
        var textState: TextView = root.findViewById(R.id.timeToWork)
        addTask = root.findViewById(R.id.addTask)
        buttonPomodoro = root.findViewById(R.id.buttonPomodoro)
        buttonBreak = root.findViewById(R.id.buttonBreak)
        textTimer = root.findViewById(R.id.timer)
        fab_start = root.findViewById(R.id.fab_start)
        fab_pause = root.findViewById(R.id.fab_pause)
        fab_stop = root.findViewById(R.id.fab_stop)

        addTask.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Task")

            val input = EditText(requireContext())
            input.hint = "What are you working on?"
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            input.setPadding(20, 10, 20, 10)
            input.layoutParams = lp
            alertDialog.setView(input)

            alertDialog.setPositiveButton("SAVE") { dialog, which ->
                arrayList.add(input.text.toString())

                listTask.adapter =
                    MyListAdapter(requireContext(), R.layout.task_item, arrayList, textState)

                listTask.setOnItemClickListener { parent, view, position, id ->
                    val item: String = adapter.getItemId(position).toString()
                    Toast.makeText(requireContext(), "Selected : " + item, Toast.LENGTH_SHORT)
                        .show()

                    for (i in 0 until listTask.childCount) {
                        if (position === i) {
                            listTask.getChildAt(i).setBackgroundColor(R.color.black)
                            textState.text = ("\"${arrayList[i]}\"")
                        } else {
                            listTask.getChildAt(i).setBackgroundColor(Color.TRANSPARENT)
                        }
                    }

                }
            }



            alertDialog.setNegativeButton("CANCEL") { dialog, which ->
                dialog.dismiss()
            }

            alertDialog.show()


        }

        adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            arrayList
        )


        //init start conf
        initButtons(0)

        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setMessage("Are you want stopped timer?")

        buttonPomodoro.setOnClickListener {

            alertDialog.setPositiveButton("Yes") { dialog, which ->
                isBreak = false

                timerBreak?.cancel()

                onTimerFinished(0)
                initButtons(0)
            }

            alertDialog.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            alertDialog.show()
        }

        buttonBreak.setOnClickListener {
            alertDialog.setPositiveButton("Yes") { dialog, which ->
                isBreak = true

                timerPomodoro?.cancel()

                onTimerFinished(0)
                initButtons(1)
            }

            alertDialog.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            alertDialog.show()
        }

        fab_start.setOnClickListener {
            if (isBreak)
                onTimerBreak()
            else
                startTimer()
            timerState = TimerState.Running
            updateButtons()
        }

        fab_pause.setOnClickListener {
            if (isBreak)
                timerBreak?.cancel()
            else
                timerPomodoro?.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }

        fab_stop.setOnClickListener {
            if (isBreak)
                timerBreak?.cancel()
            else
                timerPomodoro?.cancel()
            onTimerFinished(0)
        }

        return root
    }


    override fun onResume() {
        super.onResume()
        initTimer()

    }

    override fun onPause() {
        super.onPause()
        if (timerState == TimerState.Running) {
            if (isBreak)
                timerBreak?.cancel()
            else
                timerPomodoro?.cancel()
        } else if (timerState == TimerState.Paused) {

        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, requireContext())
        PrefUtil.setSecondsRemaining(secondRemaining, requireContext())
        PrefUtil.setTimerState(timerState, requireContext())
    }

    private fun initTimer() {
        timerState = PrefUtil.getTimerState(requireContext())
        if (timerState == TimerState.Stopped)
            if (isBreak)
                onTimerBreak()
            else
                setNewTimerLenght()
        else
            setPreviousTimerLenght()

        secondRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused)
            PrefUtil.getSecondsRemaining(requireContext())
        else
            timerLengthSeconds

        if (secondRemaining <= 0)
            onTimerFinished(0)
        else if (timerState == TimerState.Running)
            if (isBreak)
                onTimerBreak()
            else
                startTimer()
        updateButtons()
        updateCountDawnUI()
    }

    private fun onTimerFinished(count: Int) {
        if (count == 0)
            timerState = TimerState.Stopped
        else if (count == 1)
            timerState = TimerState.Running

        if (isBreak)
            setTimeBreak()
        else
            setNewTimerLenght()

        PrefUtil.setSecondsRemaining(timerLengthSeconds, requireContext())
        secondRemaining = timerLengthSeconds

        updateButtons()
        updateCountDawnUI()
    }

    private fun onTimerBreak() {
        timerState = TimerState.Running

        timerBreak = object : CountDownTimer(secondRemaining * 1000, 1000) {
            @SuppressLint("ResourceAsColor")
            override fun onFinish() {
                initButtons(0)
                isBreak = false

                onTimerFinished(1)
                startTimer()
            }

            override fun onTick(millisUntilFinished: Long) {
                secondRemaining = millisUntilFinished / 1000
                updateCountDawnUI()
            }

        }.start()
    }

    private fun startTimer() {
        timerState = TimerState.Running

        timerPomodoro = object : CountDownTimer(secondRemaining * 1000, 1000) {
            @SuppressLint("ResourceAsColor")
            override fun onFinish() {
                timerPomodoro?.cancel()
                initButtons(1)
                isBreak = true
                setTimeBreak()

                PrefUtil.setSecondsRemaining(timerLengthSeconds, requireContext())
                secondRemaining = timerLengthSeconds
                onTimerBreak()
            }

            override fun onTick(millisUntilFinished: Long) {
                secondRemaining = millisUntilFinished / 1000
                updateCountDawnUI()
            }
        }.start()
    }

    private fun setNewTimerLenght() {
        val lenghtInMinutes = PrefUtil.getTimerLength25(requireContext())
        timerLengthSeconds = (lenghtInMinutes * 60L)
    }

    private fun setTimeBreak() {
        val lenghtInMinutes = PrefUtil.getTimerLength5(requireContext())
        timerLengthSeconds = (lenghtInMinutes * 60L)
    }

    private fun setPreviousTimerLenght() {
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(requireContext())
    }

    //update UI
    private fun updateCountDawnUI() {
        val minutesUntilFinished = secondRemaining / 60
        val secondsInMinuteUntilFinished = secondRemaining - minutesUntilFinished * 60
        val secondStr = secondsInMinuteUntilFinished.toString()
        textTimer.text = "$minutesUntilFinished:${
        if (secondStr.length == 2) secondStr
        else "0" + secondStr}"
    }

    private fun updateButtons() {
        when (timerState) {
            TimerState.Running -> {
                fab_start.isEnabled = false
                fab_pause.isEnabled = true
                fab_stop.isEnabled = true
            }
            TimerState.Stopped -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = false
            }
            TimerState.Paused -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = true
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun initButtons(count: Int) {
        if (count == 0) {
            Util.selectedButton(buttonPomodoro, requireContext())
            Util.unselectedButton(buttonBreak, requireContext())
        } else if (count == 1) {
            Util.selectedButton(buttonBreak, requireContext())
            Util.unselectedButton(buttonPomodoro, requireContext())
        }
    }
}
