package alex.shum.pomdoro.ui.pomodoro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import alex.shum.pomdoro.R
import alex.shum.pomdoro.util.PrefUtil
import alex.shum.pomdoro.util.Util
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.CountDownTimer
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PomodoroFragment : Fragment() {

    enum class TimerState {
        Stopped, Paused, Running
    }

    private var timerBreak: CountDownTimer? = null
    private var timerPomodoro: CountDownTimer? = null
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped
    private var secondRemaining = 0L

    lateinit var textTimer: TextView
    lateinit var buttonPomodoro: TextView
    lateinit var buttonBreak: TextView
    lateinit var fab_start: FloatingActionButton
    lateinit var fab_pause: FloatingActionButton
    lateinit var fab_stop: FloatingActionButton
    var isBreak = false

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_pomodoro, container, false)

        buttonPomodoro = root.findViewById(R.id.buttonPomodoro)
        buttonBreak = root.findViewById(R.id.buttonBreak)
        textTimer = root.findViewById(R.id.timer)
        fab_start = root.findViewById(R.id.fab_start)
        fab_pause = root.findViewById(R.id.fab_pause)
        fab_stop = root.findViewById(R.id.fab_stop)

        //init start conf
        initButtons(0)
//        buttonPomodoro.isEnabled = false
//        buttonBreak.isEnabled = true

        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setMessage("Are you want stopped timer?")

        buttonPomodoro.setOnClickListener {
            alertDialog.setPositiveButton("Yes") { dialog, which ->
                isBreak = false

                timerBreak?.cancel()

                onTimerFinished(0)
                initButtons(0)
//                buttonPomodoro.isEnabled = false
//                buttonBreak.isEnabled = true
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
//                buttonPomodoro.isEnabled = true
//                buttonBreak.isEnabled = false
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
