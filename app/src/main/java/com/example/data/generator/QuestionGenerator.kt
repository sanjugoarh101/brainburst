package com.example.data.generator

import com.example.data.model.MathQuestion
import com.example.data.model.MemoryMatrixQuestion
import com.example.data.model.WordQuestion
import kotlin.random.Random

object QuestionGenerator {

    private val wordCatalog = listOf(
        Pair("BRAIN", "Center of human thought & cognition"),
        Pair("FOCUS", "State of intense mental concentration"),
        Pair("LOGIC", "System of valid reasoning and deduction"),
        Pair("GENIUS", "Exceptional cognitive ability & creativity"),
        Pair("PUZZLE", "Enigma testing lateral problem solving"),
        Pair("WISDOM", "Deep knowledge combined with good judgment"),
        Pair("STREAK", "Continuous chain of consecutive wins"),
        Pair("MEMORY", "The neural vault of past knowledge"),
        Pair("ENERGY", "Vibrant capacity for quick mental work"),
        Pair("MATRIX", "Lattice structure of interconnected nodes"),
        Pair("REFLEX", "Rapid, instinctive mental or physical reaction"),
        Pair("QUANTUM", "Smallest indivisible packet of light & energy"),
        Pair("SYNAPSE", "Microscopic bridge between communicating neurons"),
        Pair("NEURON", "Core electrical cell of the nervous system"),
        Pair("CLARITY", "Crystalline transparency of understanding"),
        Pair("AGILITY", "Ability to think and react swiftly"),
        Pair("SPARK", "Sudden ignition of brilliant inspiration"),
        Pair("INSIGHT", "Penetrating intuitive understanding"),
        Pair("ACUITY", "Sharpness and keenness of thought"),
        Pair("DISCOVERY", "Act of finding new truth and revelation")
    )

    fun generateMathQuestion(comboLevel: Int = 0): MathQuestion {
        val rand = Random.Default
        val operator = when {
            comboLevel > 4 -> rand.nextInt(4) // +, -, *, /
            comboLevel > 2 -> rand.nextInt(3) // +, -, *
            else -> rand.nextInt(2) // +, -
        }

        var expression: String
        var answer: Int

        when (operator) {
            0 -> { // Addition
                val a = rand.nextInt(12, 50 + comboLevel * 10)
                val b = rand.nextInt(8, 45 + comboLevel * 8)
                answer = a + b
                expression = "$a + $b = ?"
            }
            1 -> { // Subtraction
                val a = rand.nextInt(25, 80 + comboLevel * 10)
                val b = rand.nextInt(10, a - 2)
                answer = a - b
                expression = "$a - $b = ?"
            }
            2 -> { // Multiplication
                val a = rand.nextInt(3, 12)
                val b = rand.nextInt(4, 15)
                answer = a * b
                expression = "$a × $b = ?"
            }
            else -> { // Division
                val b = rand.nextInt(3, 12)
                val quotient = rand.nextInt(4, 16)
                val a = b * quotient
                answer = quotient
                expression = "$a ÷ $b = ?"
            }
        }

        // Generate 3 distractors
        val optionsSet = mutableSetOf(answer)
        while (optionsSet.size < 4) {
            val offset = rand.nextInt(-12, 13)
            if (offset != 0) {
                val candidate = answer + offset
                if (candidate > 0) optionsSet.add(candidate)
            }
        }

        val optionsList = optionsSet.toList().shuffled()
        val correctIndex = optionsList.indexOf(answer)

        return MathQuestion(
            expression = expression,
            options = optionsList.map { it.toString() },
            correctIndex = correctIndex
        )
    }

    fun generateWordQuestion(): WordQuestion {
        val rand = Random.Default
        val chosen = wordCatalog[rand.nextInt(wordCatalog.size)]
        val word = chosen.first
        val hint = chosen.second

        // Scramble letters (ensure it's not identical to word)
        var scrambled: String
        var attempts = 0
        do {
            val charList = word.toList().shuffled()
            scrambled = charList.joinToString(" ")
            attempts++
        } while (scrambled.replace(" ", "") == word && attempts < 10)

        // Generate 3 plausible distractor words of similar length
        val pool = wordCatalog.map { it.first }.filter { it != word }
        val distractors = pool.shuffled().take(3)
        val allOptions = (distractors + word).shuffled()
        val correctIndex = allOptions.indexOf(word)

        return WordQuestion(
            scrambled = scrambled,
            hint = hint,
            options = allOptions,
            correctIndex = correctIndex
        )
    }

    fun generateMemoryMatrix(level: Int = 0): MemoryMatrixQuestion {
        val rand = Random.Default
        val totalTiles = 9 // 3x3
        val activeCount = when {
            level >= 6 -> 5
            level >= 3 -> 4
            else -> 3
        }

        val indices = (0 until totalTiles).shuffled().take(activeCount).toSet()
        return MemoryMatrixQuestion(
            gridSize = 3,
            activeIndices = indices
        )
    }
}
