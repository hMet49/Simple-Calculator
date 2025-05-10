package com.dmrsoft.simplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmrsoft.simplecalculator.ui.theme.SimpleCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkTheme = isSystemInDarkTheme() // Sistem teması neyse onu kullan
            SimpleCalculatorTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CalculatorUI()
                }
            }
        }
    }
}

@Composable
fun CalculatorUI() {
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
    ) {
        Text(
            text = input,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.End
        )

        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf(".", "0", "=", "+"),
            listOf("C", "DEL")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    Button(
                        onClick = {
                            when (label) {
                                "C" -> input = ""
                                "=" -> input = calculateResult(input)
                                "DEL" -> if (input.isNotEmpty()) input = input.dropLast(1)
                                else -> input += label
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Text(label, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

fun calculateResult(expression: String): String {
    return try {
        val tokens = Regex("([-+]?[0-9]*\\.?[0-9]+|[+\\-*/])").findAll(expression).map { it.value }.toList()

        if (tokens.isEmpty()) return ""

        val stack = mutableListOf<Double>()
        var currentOp = "+"

        for (token in tokens) {
            when (token) {
                "+", "-", "*", "/" -> currentOp = token
                else -> {
                    val number = token.toDoubleOrNull() ?: return "Hata"
                    when (currentOp) {
                        "+" -> stack.add(number)
                        "-" -> stack.add(-number)
                        "*" -> {
                            val last = stack.removeLastOrNull() ?: return "Hata"
                            stack.add(last * number)
                        }
                        "/" -> {
                            val last = stack.removeLastOrNull() ?: return "Hata"
                            if (number == 0.0) return "0'a bölünemez"
                            stack.add(last / number)
                        }
                    }
                }
            }
        }

        stack.sum().toString()
    } catch (e: Exception) {
        "Hata"
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    SimpleCalculatorTheme(darkTheme = true) {
        CalculatorUI()
    }
}

