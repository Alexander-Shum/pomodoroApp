package alex.shum.pomdoro.util

import alex.shum.pomdoro.ui.pomodoro.PomodoroFragment
import android.content.Context
import android.preference.PreferenceManager

class PrefUtil {
    companion object{

        fun getTimerLength25(context: Context): Int{
            return 25
        }

        fun getTimerLength5(context: Context): Int{
            return 5
        }


        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "alex.shum.timer.previous_timer_length_seconds"

        fun getPreviousTimerLengthSeconds(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        private const val  TIMER_STATE_ID = "alex.shum.timer.timer_state"

        fun getTimerState(context: Context): PomodoroFragment.TimerState{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return PomodoroFragment.TimerState.values()[ordinal]
        }

        fun setTimerState(state: PomodoroFragment.TimerState, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private const val SECONDS_REMAINING_ID = "alex.shum.timer.seconds_remaining"

        fun getSecondsRemaining(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }
    }
}