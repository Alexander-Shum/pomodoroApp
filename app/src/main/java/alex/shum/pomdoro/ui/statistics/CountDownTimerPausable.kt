package alex.shum.pomdoro.ui.statistics

import android.os.CountDownTimer


abstract class CountDownTimerPausable(
    millisInFuture: Long,
    countDownInterval: Long
) {
    var millisInFuture: Long = 0
    var countDownInterval: Long = 0
    var millisRemaining: Long = 0
    var countDownTimer: CountDownTimer? = null
    var isPaused = true

    private fun createCountDownTimer() {
        countDownTimer = object : CountDownTimer(millisRemaining, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                millisRemaining = millisUntilFinished
                this@CountDownTimerPausable.onTick(millisUntilFinished)
            }

            override fun onFinish() {
                this@CountDownTimerPausable.onFinish()
            }
        }
    }

    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    abstract fun onTick(millisUntilFinished: Long)

    /**
     * Callback fired when the time is up.
     */
    abstract fun onFinish()

    /**
     * Cancel the countdown.
     */
    fun cancel() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        millisRemaining = 0
    }

    /**
     * Start or Resume the countdown.
     * @return CountDownTimerPausable current instance
     */
    @Synchronized
    fun start(): CountDownTimerPausable {
        if (isPaused) {
            createCountDownTimer()
            countDownTimer!!.start()
            isPaused = false
        }
        return this
    }

    /**
     * Pauses the CountDownTimerPausable, so it could be resumed(start)
     * later from the same point where it was paused.
     */
    @Throws(IllegalStateException::class)
    fun pause() {
        if (isPaused == false) {
            countDownTimer!!.cancel()
        } else {
            throw IllegalStateException("CountDownTimerPausable is already in pause state, start counter before pausing it.")
        }
        isPaused = true
    }

    init {
        this.millisInFuture = millisInFuture
        this.countDownInterval = countDownInterval
        millisRemaining = this.millisInFuture
    }
}