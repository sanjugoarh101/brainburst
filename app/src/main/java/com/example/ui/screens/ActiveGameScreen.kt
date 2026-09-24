package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.generator.QuestionGenerator
import com.example.data.model.GameMode
import com.example.data.model.GameResult
import com.example.data.model.MathQuestion
import com.example.data.model.MemoryMatrixQuestion
import com.example.data.model.PowerUpType
import com.example.data.model.WordQuestion
import com.example.ui.components.ComboFeverBanner
import com.example.ui.components.ConfettiOverlay
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GemCyan
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.HapticsManager
import kotlinx.coroutines.delay

@Composable
fun ActiveGameScreen(
    mode: GameMode,
    freezeCount: Int,
    hintCount: Int,
    shieldCount: Int,
    haptics: HapticsManager,
    onUsePowerUp: (PowerUpType) -> Boolean,
    onGameFinished: (GameResult) -> Unit,
    onQuitGame: () -> Unit
) {
    // Game Session States
    var secondsLeft by remember { mutableIntStateOf(mode.baseDurationSeconds) }
    var isTimerFrozen by remember { mutableStateOf(false) }
    var freezeDurationRemaining by remember { mutableIntStateOf(0) }

    var score by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var maxCombo by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var wrongCount by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var hasShieldActive by remember { mutableStateOf(false) }

    var isGameOver by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Dynamic Multiplier & Fever
    val multiplier = when {
        combo >= 5 -> 3
        combo >= 3 -> 2
        else -> 1
    }
    val isFever = combo >= 5

    // Active Discipline & Questions
    var currentSubMode by remember {
        mutableStateOf(if (mode == GameMode.DAILY_WORKOUT) GameMode.SPEED_MATH else mode)
    }

    var mathQuestion by remember { mutableStateOf(QuestionGenerator.generateMathQuestion(combo)) }
    var wordQuestion by remember { mutableStateOf(QuestionGenerator.generateWordQuestion()) }
    var memoryQuestion by remember { mutableStateOf(QuestionGenerator.generateMemoryMatrix(combo)) }

    // Memory Matrix dynamic state
    var isMemoryShowingTarget by remember { mutableStateOf(true) }
    val userSelectedTiles = remember { mutableStateListOf<Int>() }

    // 50/50 Hint disabled indices
    val disabledOptions = remember { mutableStateListOf<Int>() }

    // Floating Feedback Popup
    var popupText by remember { mutableStateOf<String?>(null) }
    var feedbackColor by remember { mutableStateOf(NeonGreen) }

    // Countdown Timer Loop
    LaunchedEffect(isGameOver, isTimerFrozen) {
        while (!isGameOver && secondsLeft > 0) {
            delay(1000)
            if (!isTimerFrozen) {
                secondsLeft--
            } else {
                freezeDurationRemaining--
                if (freezeDurationRemaining <= 0) {
                    isTimerFrozen = false
                }
            }
        }
        if (secondsLeft <= 0 && !isGameOver) {
            isGameOver = true
            haptics.fever()
            if (score > 1000) showConfetti = true
        }
    }

    // Memory Matrix flash timer
    LaunchedEffect(currentSubMode, memoryQuestion) {
        if (currentSubMode == GameMode.MEMORY_MATRIX) {
            isMemoryShowingTarget = true
            userSelectedTiles.clear()
            delay(1300)
            isMemoryShowingTarget = false
        }
    }

    // Function to handle answer evaluation
    fun evaluateAnswer(isCorrect: Boolean, bonusScore: Int = 100) {
        disabledOptions.clear()
        if (isCorrect) {
            haptics.success()
            correctCount++
            combo++
            if (combo > maxCombo) maxCombo = combo

            val gainedScore = bonusScore * multiplier
            score += gainedScore

            if (combo == 5) {
                haptics.fever()
                popupText = "FEVER MODE! +3s"
                feedbackColor = NeonPink
                secondsLeft = (secondsLeft + 3).coerceAtMost(mode.baseDurationSeconds)
            } else {
                popupText = "+$gainedScore"
                feedbackColor = if (isFever) NeonPink else NeonGreen
            }

            // Next question setup
            if (mode == GameMode.DAILY_WORKOUT) {
                // Cycle between disciplines
                currentSubMode = when (currentSubMode) {
                    GameMode.SPEED_MATH -> GameMode.WORD_BLITZ
                    GameMode.WORD_BLITZ -> GameMode.MEMORY_MATRIX
                    else -> GameMode.SPEED_MATH
                }
            }

            when (currentSubMode) {
                GameMode.SPEED_MATH -> mathQuestion = QuestionGenerator.generateMathQuestion(combo)
                GameMode.WORD_BLITZ -> wordQuestion = QuestionGenerator.generateWordQuestion()
                GameMode.MEMORY_MATRIX -> memoryQuestion = QuestionGenerator.generateMemoryMatrix(combo)
                else -> {}
            }
        } else {
            // Wrong answer
            if (hasShieldActive) {
                hasShieldActive = false
                haptics.tap()
                popupText = "SHIELD SAVED YOU!"
                feedbackColor = NeonCyan
            } else {
                haptics.error()
                wrongCount++
                combo = 0
                lives--
                popupText = "MISTAKE!"
                feedbackColor = Color(0xFFFF3366)

                if (lives <= 0) {
                    isGameOver = true
                }
            }
        }
    }

    // Main Game Container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCanvas)
            .padding(16.dp)
            .testTag("active_game_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Quit, Timer Ring, Score, Lives
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onQuitGame,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .testTag("quit_game_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Quit",
                            tint = TextSecondary
                        )
                    }

                    // Countdown Ring & Timer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isTimerFrozen) NeonCyan.copy(alpha = 0.2f) else DarkSurfaceElevated)
                            .border(
                                1.dp,
                                if (isTimerFrozen) NeonCyan else DarkBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (isTimerFrozen) Icons.Default.AcUnit else Icons.Default.Timer,
                            contentDescription = "Time",
                            tint = if (isTimerFrozen) NeonCyan else if (secondsLeft <= 10) NeonPink else NeonAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTimerFrozen) "FROZEN ${freezeDurationRemaining}s" else "${secondsLeft}s",
                            color = if (isTimerFrozen) NeonCyan else if (secondsLeft <= 10) NeonPink else TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }

                    // Lives
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        repeat(3) { index ->
                            Icon(
                                imageVector = if (index < lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Heart",
                                tint = if (index < lives) NeonPink else TextSecondary.copy(alpha = 0.4f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar of remaining time
                LinearProgressIndicator(
                    progress = { secondsLeft.toFloat() / mode.baseDurationSeconds },
                    color = if (isTimerFrozen) NeonCyan else if (secondsLeft <= 10) NeonPink else NeonGreen,
                    trackColor = DarkSurfaceElevated,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Score and Combo Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SCORE",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "%,d".format(score),
                            color = TextPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    if (popupText != null) {
                        Surface(
                            color = feedbackColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = popupText!!,
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        LaunchedEffect(popupText) {
                            delay(900)
                            popupText = null
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Combo & Fever Banner
                ComboFeverBanner(combo = combo, multiplier = multiplier, isFever = isFever)
            }

            // Central Game Arena (Varies by discipline)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                when (currentSubMode) {
                    GameMode.SPEED_MATH -> {
                        SpeedMathArena(
                            question = mathQuestion,
                            disabledIndices = disabledOptions,
                            onOptionSelected = { index ->
                                val isCorrect = index == mathQuestion.correctIndex
                                evaluateAnswer(isCorrect, bonusScore = 120)
                            }
                        )
                    }
                    GameMode.WORD_BLITZ -> {
                        WordBlitzArena(
                            question = wordQuestion,
                            disabledIndices = disabledOptions,
                            onOptionSelected = { index ->
                                val isCorrect = index == wordQuestion.correctIndex
                                evaluateAnswer(isCorrect, bonusScore = 150)
                            }
                        )
                    }
                    GameMode.MEMORY_MATRIX -> {
                        MemoryMatrixArena(
                            question = memoryQuestion,
                            isShowingTarget = isMemoryShowingTarget,
                            selectedTiles = userSelectedTiles,
                            onTileTap = { index ->
                                if (!isMemoryShowingTarget && !userSelectedTiles.contains(index)) {
                                    userSelectedTiles.add(index)
                                    haptics.tap()
                                    if (!memoryQuestion.activeIndices.contains(index)) {
                                        // Wrong tile tapped!
                                        evaluateAnswer(false)
                                    } else if (userSelectedTiles.containsAll(memoryQuestion.activeIndices)) {
                                        // All target tiles found!
                                        evaluateAnswer(true, bonusScore = 200)
                                    }
                                }
                            }
                        )
                    }
                    else -> {}
                }
            }

            // Bottom In-Game Power-Ups Toolbar
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Freeze Time
                    PowerUpGameButton(
                        icon = Icons.Default.AcUnit,
                        name = "Freeze",
                        count = freezeCount,
                        tint = NeonCyan,
                        isActive = isTimerFrozen,
                        onClick = {
                            if (!isTimerFrozen && onUsePowerUp(PowerUpType.FREEZE_TIME)) {
                                isTimerFrozen = true
                                freezeDurationRemaining = 6
                                haptics.tap()
                            }
                        }
                    )

                    // 50/50 Hint
                    PowerUpGameButton(
                        icon = Icons.Default.HelpOutline,
                        name = "50/50",
                        count = hintCount,
                        tint = GoldCoin,
                        onClick = {
                            if (disabledOptions.isEmpty() && onUsePowerUp(PowerUpType.HINT_5050)) {
                                haptics.tap()
                                val correct = when (currentSubMode) {
                                    GameMode.SPEED_MATH -> mathQuestion.correctIndex
                                    GameMode.WORD_BLITZ -> wordQuestion.correctIndex
                                    else -> -1
                                }
                                if (correct != -1) {
                                    val wrongOnes = (0..3).filter { it != correct }.shuffled().take(2)
                                    disabledOptions.addAll(wrongOnes)
                                }
                            }
                        }
                    )

                    // Shield
                    PowerUpGameButton(
                        icon = Icons.Default.Shield,
                        name = "Shield",
                        count = shieldCount,
                        tint = NeonGreen,
                        isActive = hasShieldActive,
                        onClick = {
                            if (!hasShieldActive && onUsePowerUp(PowerUpType.SHIELD)) {
                                hasShieldActive = true
                                haptics.tap()
                            }
                        }
                    )
                }
            }
        }

        // Confetti Celebration
        ConfettiOverlay(active = showConfetti)

        // Game Over Dialog Modal
        if (isGameOver) {
            val coinsEarned = (score / 15).coerceAtLeast(20)
            val gemsEarned = if (score > 1200) 3 else 1
            val xpGained = (score / 10).coerceAtLeast(30)

            GameOverSummaryDialog(
                result = GameResult(
                    mode = mode,
                    finalScore = score,
                    correctCount = correctCount,
                    wrongCount = wrongCount,
                    maxCombo = maxCombo,
                    coinsEarned = coinsEarned,
                    gemsEarned = gemsEarned,
                    xpGained = xpGained,
                    isNewHighScore = score > 1500
                ),
                onPlayAgain = {
                    // Reset game
                    secondsLeft = mode.baseDurationSeconds
                    score = 0
                    combo = 0
                    maxCombo = 0
                    correctCount = 0
                    wrongCount = 0
                    lives = 3
                    isGameOver = false
                    showConfetti = false
                    mathQuestion = QuestionGenerator.generateMathQuestion(0)
                    wordQuestion = QuestionGenerator.generateWordQuestion()
                    memoryQuestion = QuestionGenerator.generateMemoryMatrix(0)
                },
                onComplete = { result ->
                    onGameFinished(result)
                }
            )
        }
    }
}

@Composable
fun SpeedMathArena(
    question: MathQuestion,
    disabledIndices: List<Int>,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Equation Display
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            shape = RoundedCornerShape(22.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, NeonPink),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("math_equation_display")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = question.expression,
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2x2 Grid of Answer Choices
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            items(question.options.size) { index ->
                val isDisabled = disabledIndices.contains(index)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDisabled) DarkSurface.copy(alpha = 0.4f) else DarkSurfaceElevated
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDisabled) DarkBorder.copy(alpha = 0.3f) else NeonPink.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .height(68.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !isDisabled) { onOptionSelected(index) }
                        .testTag("math_choice_$index")
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDisabled) "-" else question.options[index],
                            color = if (isDisabled) TextSecondary.copy(alpha = 0.3f) else TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WordBlitzArena(
    question: WordQuestion,
    disabledIndices: List<Int>,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Scrambled Letters Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            shape = RoundedCornerShape(22.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, NeonCyan),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("word_scramble_display")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "UNSCRAMBLE THE WORD",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = question.scrambled,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Hint: ${question.hint}",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Options 2x2
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            items(question.options.size) { index ->
                val isDisabled = disabledIndices.contains(index)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDisabled) DarkSurface.copy(alpha = 0.4f) else DarkSurfaceElevated
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDisabled) DarkBorder.copy(alpha = 0.3f) else NeonCyan.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !isDisabled) { onOptionSelected(index) }
                        .testTag("word_choice_$index")
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDisabled) "—" else question.options[index],
                            color = if (isDisabled) TextSecondary.copy(alpha = 0.3f) else TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryMatrixArena(
    question: MemoryMatrixQuestion,
    isShowingTarget: Boolean,
    selectedTiles: List<Int>,
    onTileTap: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = if (isShowingTarget) NeonAmber.copy(alpha = 0.2f) else NeonGreen.copy(alpha = 0.2f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = if (isShowingTarget) "MEMORIZE PATTERN..." else "TAP TO RECALL!",
                color = if (isShowingTarget) NeonAmber else NeonGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        // 3x3 Grid
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurfaceElevated)
                .border(2.dp, if (isShowingTarget) NeonAmber else NeonCyan, RoundedCornerShape(20.dp))
                .padding(14.dp)
                .testTag("memory_grid_box")
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(9) { index ->
                    val isTarget = question.activeIndices.contains(index)
                    val isSelected = selectedTiles.contains(index)

                    val tileBgColor by animateColorAsState(
                        targetValue = when {
                            isShowingTarget && isTarget -> NeonAmber
                            !isShowingTarget && isSelected && isTarget -> NeonGreen
                            !isShowingTarget && isSelected && !isTarget -> Color.Red
                            else -> DarkSurface
                        },
                        animationSpec = tween(250),
                        label = "tile_color"
                    )

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(tileBgColor)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isShowingTarget) {
                                onTileTap(index)
                            }
                            .testTag("memory_tile_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected && isTarget && !isShowingTarget) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Correct",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PowerUpGameButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    count: Int,
    tint: Color,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = count > 0 && !isActive) { onClick() }
            .padding(6.dp)
            .testTag("powerup_btn_${name.lowercase()}")
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isActive) tint else DarkSurfaceElevated)
                .border(
                    1.dp,
                    if (isActive) Color.White else if (count > 0) tint else DarkBorder,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = if (isActive) Color.Black else if (count > 0) tint else TextSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = name,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(3.dp))
            Surface(
                color = if (count > 0) tint else DarkSurfaceElevated,
                shape = CircleShape
            ) {
                Text(
                    text = "$count",
                    color = if (count > 0) Color.Black else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
fun GameOverSummaryDialog(
    result: GameResult,
    onPlayAgain: () -> Unit,
    onComplete: (GameResult) -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = { onComplete(result) }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(2.dp, NeonViolet),
            modifier = Modifier.fillMaxWidth().testTag("game_over_summary_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = NeonPink,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "WORKOUT COMPLETE!",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "%,d".format(result.finalScore),
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "TOTAL POINTS",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Stats breakdown
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Max Combo", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "${result.maxCombo}x",
                                color = NeonCyan,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Correct", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "${result.correctCount}",
                                color = NeonGreen,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Accuracy", color = TextSecondary, fontSize = 11.sp)
                            val total = result.correctCount + result.wrongCount
                            val acc = if (total > 0) (result.correctCount * 100 / total) else 100
                            Text(
                                text = "$acc%",
                                color = GoldCoin,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rewards Earned
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = GoldCoin,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${result.coinsEarned} Coins",
                        color = GoldCoin,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = "Gems",
                        tint = GemCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${result.gemsEarned} Gems",
                        color = GemCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onPlayAgain,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp).testTag("play_again_button")
                    ) {
                        Text(text = "Play Again", color = NeonCyan, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onComplete(result) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp).testTag("complete_game_button")
                    ) {
                        Text(text = "Done", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
