package alex.shum.pomdoro.ui.statistics

import android.os.CountDownTimer

class Timer(
    _millisInFuture: Long,
    _countDownInterval: Long
) {
    var  millisInFuture: Long
    var countDownInterval: Long
    var millisRemaining: Long = 0
    lateinit var countDownTimer: CountDownTimer

    init {
        millisInFuture = _millisInFuture
        countDownInterval = _countDownInterval
    }

    fun createTimer(){
        countDownTimer = object : CountDownTimer(millisInFuture, countDownInterval){

            override fun onTick(millisUntilFinished: Long) {
                TODO("Not yet implemented")
            }

            override fun onFinish() {
                TODO("Not yet implemented")
            }
        }
    }

}