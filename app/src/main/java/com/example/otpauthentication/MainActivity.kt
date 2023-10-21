package com.example.otpauthentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import com.example.otpauthentication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var otpText : TextView
    private lateinit var regenText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        regenText = binding.regenerateText

        binding.regenerate.setOnClickListener {
            otpText = binding.otpText
            otpText.text = getTime()
            binding.regenerateText.visibility = VISIBLE
            binding.regenerate.isEnabled = false
            startCountdown()
        }
    }

    private fun startCountdown() {
        // Create a CountDownTimer for 60 seconds (60,000 milliseconds)
        val countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt().toString()
                val countdown = getString(R.string.to_regenerate_wait_60_seconds, secondsRemaining)
                regenText.text = countdown
            }

            override fun onFinish() {
                regenText.visibility = GONE
                binding.regenerate.isEnabled = true
            }
        }

        countDownTimer.start()
    }

    private fun getTime(): String {
        val unixTime = System.currentTimeMillis()
        val lastSixDigits = unixTime % 1000000
        val subtract = subtractAndLoopBack(lastSixDigits.toString())
        val rearrangedString = rearrangeDigits(subtract)
        return mappedDigits(rearrangedString)
    }

    private fun subtractAndLoopBack(number: String): String {
        val numbered: String = if (number.length != 6){
            "0$number"
        } else {
            number
        }
        val numStr = numbered.toCharArray()
        val lastDigit = Character.getNumericValue(numStr[numStr.size - 1])

        val result = numStr.map { digitChar ->
            val digit = Character.getNumericValue(digitChar)
            if (digit < lastDigit) {
                9 - (lastDigit - digit)
            } else {
                digit - lastDigit
            }
        }.joinToString("").toInt()
        val resultString = (result + lastDigit).toString()
        return if (resultString.length == 6){
            resultString
        } else {
            "0$resultString"
        }
    }

    private fun rearrangeDigits(number: String): String {
        if (number.length != 6) {
            throw IllegalArgumentException("Input must be a six-digit number")
        }
        var numbered = ""
        numbered += number[4]
        numbered += number[0]
        numbered += number[1]
        numbered += number[5]
        numbered += number[3]
        numbered += number[2]

        return numbered
    }

    private fun mappedDigits(number: String): String{
        var mapped = ""
        val numberMap = mapOf(
            0 to 4,
            1 to 3,
            2 to 9,
            3 to 6,
            4 to 2,
            5 to 8,
            6 to 1,
            7 to 5,
            8 to 7,
            9 to 0
        )

        for (i in 0..5){
            val checker = numberMap[number[i].digitToInt()]
            mapped += checker.toString()
        }
        return mapped
    }
}