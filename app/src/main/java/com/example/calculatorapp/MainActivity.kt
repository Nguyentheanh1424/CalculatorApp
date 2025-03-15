package com.example.calculatorapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var displayText: TextView
    private var currentInput = ""
    private var lastNumeric = false
    private var lastDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        displayText = findViewById(R.id.displayText)
    }

    fun numberAction(view: View) {
        currentInput += (view as Button).text
        displayText.text = currentInput
        lastNumeric = true
    }

    fun clear(view: View) {
        currentInput = ""
        displayText.text = "0"
        lastNumeric = false
        lastDot = false
    }

    fun clearEntry(view: View) {
        val regex = "[-+*/]?\\d+(\\.\\d+)?$".toRegex()
        currentInput = regex.replace(currentInput, "")
        displayText.text = if (currentInput.isEmpty()) "0" else currentInput
        lastNumeric = currentInput.lastOrNull()?.isDigit() == true
    }

    fun backSpace(view: View) {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            displayText.text = if (currentInput.isEmpty()) "0" else currentInput
            lastNumeric = currentInput.lastOrNull()?.isDigit() == true
        }
    }

    fun dotAction(view: View) {
        if (lastNumeric && !lastDot) {
            currentInput += "."
            displayText.text = currentInput
            lastNumeric = false
            lastDot = true
        }
    }

    fun operationAction(view: View) {
        if (lastNumeric) {
            currentInput += (view as Button).text
            displayText.text = currentInput
            lastNumeric = false
            lastDot = false
        }
    }

    fun signChange(view: View) {
        val regex = "[-+*/]?\\d+(\\.\\d+)?$".toRegex()
        val match = regex.find(currentInput)
        match?.let {
            val number = it.value
            val newNumber = if (number.startsWith("-")) number.substring(1) else "-$number"
            currentInput = currentInput.replace(regex, newNumber)
            displayText.text = currentInput
        }
    }

    fun displayResult(view: View) {
        if (lastNumeric) {
            try {
                val sanitizedExpression = currentInput.replace("x", "*").replace("÷", "/")
                val result = ExpressionBuilder(sanitizedExpression).build().evaluate()
                displayText.text = if (result % 1.0 == 0.0) {
                    result.toInt().toString()
                } else {
                    String.format("%.6f", result).trimEnd('0').trimEnd('.')
                }
                currentInput = displayText.text.toString()
                lastNumeric = true
                lastDot = currentInput.contains(".")
            } catch (e: Exception) {
                displayText.text = "Error"
                currentInput = ""
                lastNumeric = false
            }
        }
    }
}