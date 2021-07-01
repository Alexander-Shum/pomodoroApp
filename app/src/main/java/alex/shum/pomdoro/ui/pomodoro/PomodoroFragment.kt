package alex.shum.pomdoro.ui.pomodoro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import alex.shum.pomdoro.R
import alex.shum.pomdoro.util.PrefUtil
import android.annotation.SuppressLint
import android.os.CountDownTimer
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PomodoroFragment : Fragment() {

    enum class TimerState {
        Stopped, Paused, Running
    }

    private lateinit var timerPomodoro: CountDownTimer
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped
    private var secondRemaining = 0L

    lateinit var textTimer: TextView
    lateinit var buttonPomodoro: TextView
    lateinit var buttonBreak: TextView
    lateinit var fab_start: FloatingActionButton
    lateinit var fab_pause: FloatingActionButton
    lateinit var fab_stop: FloatingActionButton


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
        buttonPomodoro.setBackgroundColor(R.color.purple_500)
        buttonBreak.setBackgroundColor(R.color.black)

        fab_start.setOnClickListener {
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        }

        fab_pause.setOnClickListener {
            timerPomodoro.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }

        fab_stop.setOnClickListener {
            timerPomodoro.cancel()
            onTimerFinished()
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
            timerPomodoro.cancel()
        } else if (timerState == TimerState.Paused) {

        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, requireContext())
        PrefUtil.setSecondsRemaining(secondRemaining, requireContext())
        PrefUtil.setTimerState(timerState, requireContext())
    }

    private fun initTimer() {
        timerState = PrefUtil.getTimerState(requireContext())
        if (timerState == TimerState.Stopped)
            setNewTimerLenght()
        else
            setPreviousTimerLenght()

        secondRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused)
            PrefUtil.getSecondsRemaining(requireContext())
        else
            timerLengthSeconds

        if (secondRemaining <= 0)
            onTimerFinished()
        else if (timerState == TimerState.Running)
            startTimer()
        updateButtons()
        updateCountDawnUI()
    }

    private fun onTimerFinished() {
        timerState = TimerState.Stopped

        setNewTimerLenght()

        PrefUtil.setSecondsRemaining(timerLengthSeconds, requireContext())
        secondRemaining = timerLengthSeconds

        updateButtons()
        updateCountDawnUI()
    }

    private fun onTimerBreak(){
        timerState = TimerState.Running
    }

    private fun startTimer() {
        timerState = TimerState.Running

        timerPomodoro = object : CountDownTimer(secondRemaining*100, 1000) {
            override fun onFinish() {
                timerPomodoro.cancel()
                onTimerFinished()
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

    private fun updateButtons(){
        when (timerState) {
            TimerState.Running ->{
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
}
