package com.example.innomobil

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.example.innomobil.ui.theme.INNOMOBILTheme
import kotlinx.coroutines.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.app.Activity

// ==================== НОВЫЕ КОМПОНЕНТЫ ДЛЯ ONBOARDING ====================

@Composable
fun PrivacyPolicyDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { /* Нельзя закрыть по клику вне диалога */ },
        title = {
            Text("INNOCONTROL", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            Column {
                Text(
                    "Использование приложения INNOCONTROL требует чтобы вы согласились с условиями использования и политикой конфиденциальности.",
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Условия использования",
                    color = Color.Blue,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://www.info.ru")
                                )
                            )
                        }
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Политика конфиденциальности",
                    color = Color.Blue,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://www.info2.ru")
                                )
                            )
                        }
                        .fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Text("Принимаю", fontSize = 16.sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDecline,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Не принимаю", color = Color.Gray, fontSize = 16.sp)
            }
        }
    )
}

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    var currentPage by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Добро пожаловать$currentPage",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Нажмите далее или пропустить$currentPage",
            color = Color.Gray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Место для фото
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray.copy(alpha = 0.3f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("ФОТО $currentPage", fontSize = 18.sp, color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Индикатор точек
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(5) { index ->
                val isActive = index + 1 == currentPage
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (isActive) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) Color.Green else Color.Gray
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray
                ),
                modifier = Modifier.weight(0.45f)
            ) {
                Text("Пропустить", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    if (currentPage < 5) {
                        currentPage++
                    } else {
                        onFinish()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                ),
                modifier = Modifier.weight(0.45f)
            ) {
                Text(
                    if (currentPage == 5) "Завершить" else "Далее",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun AuthScreen(
    onGuestClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Промышленность",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Место для картинки
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.LightGray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text("КАРТИНКА", fontSize = 16.sp, color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://www.info3.ru")
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32)
            )
        ) {
            Text("Вход в систему / Регистрация", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onGuestClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Продолжить в качестве гостя",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

// ==================== СУЩЕСТВУЮЩИЙ КОД БЕЗ ИЗМЕНЕНИЙ ====================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            INNOMOBILTheme {
                MainNavigationContainer()
            }
        }
    }
}

enum class AppDestinations(val label: String, val icon: ImageVector) {
    CONNECTION("Связь", Icons.Default.Bluetooth),
    CONTROL("Команды", Icons.Default.SettingsRemote),
    MONITOR("Данные", Icons.Default.Assessment),
    GRAPHICS("Графики", Icons.Default.ShowChart),
    TERMINAL("Терминал", Icons.Default.Code)
}

data class ParameterGroup(
    val prefix: String,
    val description: String,
    val parameters: List<String>
)

data class CustomCommand(val name: String, val hexCode: String)

data class TerminalLogEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String,
    val command: String,
    val direction: String,
    val isValid: Boolean = true,
    val isError: Boolean = false
)

// Словарь ошибок ПЧ
val errorCodes = mapOf(
    "0001" to "E.OC (Перегрузка по току)",
    "0002" to "E.OV (Перенапряжение)",
    "0003" to "E.LU (Пониженное напряжение)",
    "0004" to "E.OL (Перегрузка двигателя)",
    "0005" to "E.OH (Перегрев преобразователя)",
    "0006" to "E.OT (Перегрев двигателя)",
    "0007" to "E.IP (Неисправность входной фазы)",
    "0008" to "E.OP (Неисправность выходной фазы)",
    "0009" to "E.EF (Внешняя ошибка)",
    "0010" to "E.CE (Ошибка связи)",
    "0011" to "E.PE (Ошибка параметров)",
    "0012" to "E.SD (Ошибка сохранения данных)",
    "0013" to "E.GF (Земляная ошибка)",
    "0014" to "E.PUE (Питание двигателя отключено)",
    "0015" to "E.OL1 (Предупреждение перегрузки)")

@Composable
fun AdaptiveColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(
                    if (screenWidth > 600) 0.7f else 1f
                )
                .padding(
                    horizontal = if (screenWidth > 600) 24.dp else 16.dp,
                    vertical = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

@Composable
fun RealTimeGraph(
    points: List<Float>,
    color: Color,
    modifier: Modifier = Modifier,
    label: String = "",
    unit: String = ""
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A), MaterialTheme.shapes.medium)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            if (points.isNotEmpty()) {
                Text(
                    "${String.format("%.1f", points.last())} $unit",
                    color = color,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            if (points.size < 2) return@Canvas

            val path = Path()
            val xStep = size.width / (points.size - 1)
            val maxVal = (points.maxOrNull() ?: 1f).coerceAtLeast(1f)
            val minVal = (points.minOrNull() ?: 0f)
            val range = (maxVal - minVal).coerceAtLeast(1f)

            // Horizontal grid lines
            for (i in 0..4) {
                val y = size.height * (1 - i * 0.2f)
                drawLine(
                    color = Color(0xFF333333),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
            }

            // Draw graph line
            points.forEachIndexed { i, value ->
                val x = i * xStep
                val normalizedValue = (value - minVal) / range
                val y = size.height * (1 - normalizedValue * 0.8f)

                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 3f)
            )

            // Draw points
            points.forEachIndexed { i, value ->
                val x = i * xStep
                val normalizedValue = (value - minVal) / range
                val y = size.height * (1 - normalizedValue * 0.8f)

                drawCircle(
                    color = color,
                    radius = 3f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

fun checkResponseCRC(responseHex: String): Boolean {
    if (responseHex.length < 4) return false
    val cleanHex = responseHex.filter { it.isDigit() || it in 'A'..'F' }
    if (cleanHex.isEmpty()) return false

    val data = cleanHex.substring(0, cleanHex.length - 4)
    val receivedCRC = cleanHex.substring(cleanHex.length - 4).uppercase()
    val calculated = appendCRC(data).replace(" ", "").takeLast(4).uppercase()

    return calculated == receivedCRC
}

// Функция для экспорта в CSV
fun saveParamsToCsv(context: Context, uri: Uri, vfdType: String, allGroups: List<ParameterGroup>) {
    context.contentResolver.openOutputStream(uri)?.use { output ->
        val writer = OutputStreamWriter(output, Charsets.UTF_8)
        writer.write("\uFEFF") // BOM для правильного отображения в Excel
        writer.write("Адрес регистра;Значение;Описание\n")
        allGroups.forEach { group ->
            group.parameters.forEach { param ->
                writer.write("${param};0;${group.description}\n")
            }
        }
        writer.flush()
    }
}

// Функция для импорта из CSV
fun readParamsFromCsv(context: Context, uri: Uri) {
    val scope = CoroutineScope(Dispatchers.Main)
    scope.launch {
        context.contentResolver.openInputStream(uri)?.bufferedReader()?.useLines { lines ->
            lines.drop(1).forEach { line ->
                val parts = line.split(";")
                if (parts.size >= 2) {
                    val regCode = parts[0]
                    val value = parts[1]

                    // Симуляция отправки в ПЧ
                    sendModbusCommand(regCode, value)
                    delay(150) // Задержка для стабилизации RS485
                }
            }
        }
    }
}

// Заглушка для отправки Modbus команды
fun sendModbusCommand(regCode: String, value: String) {
    // Реальная реализация будет отправлять команду через Bluetooth
    println("Отправка команды: $regCode = $value")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainNavigationContainer() {
    // ==================== ИЗМЕНЕННАЯ ЛОГИКА ХРАНЕНИЯ СОСТОЯНИЙ ====================
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }

    // Состояние: принято ли соглашение (сохраняется навсегда)
    var isAccepted by remember { mutableStateOf(prefs.getBoolean("accepted", false)) }

    // Состояние: просмотрено ли обучение (сохраняется навсегда)
    var isOnboardingDone by remember { mutableStateOf(prefs.getBoolean("onboarding_done", false)) }

    // ==================== КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: ГОСТЕВОЙ РЕЖИМ ТОЛЬКО ДЛЯ СЕССИИ ====================
    // Состояние: авторизован ли пользователь (или гость) - ТОЛЬКО для текущей сессии
    // rememberSaveable сохраняется при повороте экрана и сворачивании, но сбрасывается при убийстве процесса
    var isGuestMode by rememberSaveable { mutableStateOf(false) }

    // ==================== ОСНОВНАЯ ЛОГИКА ПОКАЗА ЭКРАНОВ ====================
    when {
        // 1. Диалог политики конфиденциальности
        !isAccepted -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                PrivacyPolicyDialog(
                    onAccept = {
                        isAccepted = true
                        prefs.edit().putBoolean("accepted", true).apply()
                    },
                    onDecline = {
                        (context as? Activity)?.finish()
                    }
                )
            }
        }

        // 2. Онбординг (5 экранов обучения)
        !isOnboardingDone -> {
            OnboardingScreen(
                onFinish = {
                    isOnboardingDone = true
                    prefs.edit().putBoolean("onboarding_done", true).apply()
                }
            )
        }

        // 3. Экран авторизации - теперь показывается ПРИ КАЖДОМ НОВОМ ЗАПУСКЕ приложения
        !isGuestMode -> {
            AuthScreen(
                onGuestClick = {
                    // ВАЖНО: НЕ сохраняем в SharedPreferences, только в оперативной памяти
                    // Это обеспечит показ экрана авторизации при следующем холодном старте
                    isGuestMode = true
                }
            )
        }

        // 4. Основное приложение (существующий код) - только для текущей сессии
        else -> {
            mainAppContent()
        }
    }
}

@Composable
fun DisplayMetric(label: String, value: String, unit: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Text("$value $unit", color = color, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun StatusButton(text: String, isActive: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (text == "ПУСК") Color(0xFF2E7D32) else Color(0xFFC62828)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isActive) 8.dp else 2.dp
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isActive) Color.Green else Color.Red)
            )
        }
    }
}

// ==================== СУЩЕСТВУЮЩИЙ КОД ВЫНЕСЕН В ОТДЕЛЬНУЮ ФУНКЦИЮ ====================

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun mainAppContent() {
    var selectedVfdType by rememberSaveable { mutableStateOf("ISD mini") }
    var installedVfdType by rememberSaveable { mutableStateOf("ISD mini") }
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AppDestinations.CONNECTION.name

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showRightMenu by remember { mutableStateOf(false) }

    // Состояния для новых функций
    var showProgress by remember { mutableStateOf(false) }
    var progressText by remember { mutableStateOf("") }
    var scanningId by remember { mutableStateOf(0) }
    val foundIds = remember { mutableStateListOf<Int>() }
    var showScanDialog by remember { mutableStateOf(false) }
    var showErrorDiagnosis by remember { mutableStateOf(false) }
    val errorLog = remember { mutableStateListOf<String>() }

    // Лаунчеры для импорта/экспорта
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        uri?.let {
            val allGroups = getParameterGroupsForVfdType(installedVfdType)
            saveParamsToCsv(context, it, installedVfdType, allGroups)
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            showProgress = true
            progressText = "Импорт конфигурации..."
            scope.launch {
                readParamsFromCsv(context, it)
                delay(2000) // Симуляция длительной операции
                showProgress = false
            }
        }
    }

    // Функция для сканирования сети
    fun startScan() {
        scope.launch {
            showScanDialog = true
            foundIds.clear()
            for (id in 1..10) { // Сканируем только 10 адресов для демонстрации
                scanningId = id
                // Симуляция отправки запроса и получения ответа
                val cmd = appendCRC(id.toString(16).padStart(2, '0') + "0300000001")
                val success = sendAndAwaitResponse(cmd)
                if (success) foundIds.add(id)
                delay(200) // Задержка между запросами
            }
        }
    }

    // Функция для чтения ошибок
    fun readErrorLog() {
        scope.launch {
            showErrorDiagnosis = true
            errorLog.clear()
            // Симуляция чтения ошибок из ПЧ
            errorLog.add("E.OC (Перегрузка по току) [${SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())}]")
            errorLog.add("E.OV (Перенапряжение) [${SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date(System.currentTimeMillis() - 3600000))}]")
            // Добавление расшифровки
            errorCodes.forEach { (code, description) ->
                errorLog.add("$code: $description")
            }
        }
    }

    val openLeftDrawer = {
        scope.launch {
            drawerState.open()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "INNOMOBIL",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                NavigationDrawerItem(
                    label = {
                        Text("Документация", fontSize = 16.sp)
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Description,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = {
                        Text("Журнал событий", fontSize = 16.sp)
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    icon = {
                        Icon(
                            Icons.Default.History,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = {
                        Text("Техподдержка", fontSize = 16.sp)
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Support,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Версия: 2026.1 PRO",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "ПЧ: $installedVfdType",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) {
        // Диалог прогресса
        if (showProgress) {
            AlertDialog(
                onDismissRequest = { showProgress = false },
                title = { Text("Выполнение операции") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(progressText)
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showProgress = false }) {
                        Text("Отмена")
                    }
                }
            )
        }

        // Диалог сканирования сети
        if (showScanDialog) {
            AlertDialog(
                onDismissRequest = { showScanDialog = false },
                title = { Text("Сканирование сети Modbus") },
                text = {
                    Column {
                        Text("Сканирование адресов: $scanningId/10")
                        if (foundIds.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Найдены адреса: ${foundIds.joinToString()}")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showScanDialog = false }) {
                        Text("ОК")
                    }
                }
            )
        }

        // Диалог диагностики ошибок
        if (showErrorDiagnosis) {
            AlertDialog(
                onDismissRequest = { showErrorDiagnosis = false },
                title = { Text("Диагностика ошибок ПЧ") },
                text = {
                    Column(
                        modifier = Modifier
                            .height(300.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (errorLog.isEmpty()) {
                            Text("Ошибок не обнаружено")
                        } else {
                            errorLog.forEachIndexed { index, error ->
                                Text(
                                    text = error,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = if (error.startsWith("E.")) Color.Red else Color.Gray
                                )
                                if (index < errorLog.size - 1) {
                                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showErrorDiagnosis = false }) {
                        Text("Закрыть")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        // Сброс ошибок
                        errorLog.clear()
                    }) {
                        Text("Сбросить ошибки")
                    }
                }
            )
        }

        if (showRightMenu) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showRightMenu = false }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(300.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = 24.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "НАСТРОЙКИ ПЧ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            IconButton(
                                onClick = { showRightMenu = false },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Закрыть настройки",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        HorizontalDivider()

                        val rightActions = listOf(
                            "Прочитать из ПЧ",
                            "Записать в ПЧ",
                            "Экспорт конфигурации",
                            "Импорт конфигурации",
                            "Сброс к заводским",
                            "Сканировать сеть",
                            "Диагностика ошибок"
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            rightActions.forEach { action ->
                                ListItem(
                                    headlineContent = {
                                        Text(action, fontSize = 16.sp)
                                    },
                                    modifier = Modifier
                                        .clickable {
                                            when (action) {
                                                "Прочитать из ПЧ" -> {
                                                    scope.launch {
                                                        showProgress = true
                                                        progressText = "Чтение параметров из ПЧ..."
                                                        // Симуляция чтения всех параметров
                                                        delay(3000)
                                                        showProgress = false
                                                    }
                                                }
                                                "Записать в ПЧ" -> {
                                                    scope.launch {
                                                        showProgress = true
                                                        progressText = "Запись параметров в ПЧ..."
                                                        delay(3000)
                                                        showProgress = false
                                                    }
                                                }
                                                "Экспорт конфигурации" -> {
                                                    exportLauncher.launch("InnoVFD_${installedVfdType}_${System.currentTimeMillis()}.csv")
                                                }
                                                "Импорт конфигурации" -> {
                                                    importLauncher.launch(arrayOf("text/csv", "text/comma-separated-values"))
                                                }
                                                "Сброс к заводским" -> {
                                                    scope.launch {
                                                        showProgress = true
                                                        progressText = "Сброс к заводским настройкам..."
                                                        delay(2000)
                                                        showProgress = false
                                                    }
                                                }
                                                "Сканировать сеть" -> {
                                                    startScan()
                                                }
                                                "Диагностика ошибок" -> {
                                                    readErrorLog()
                                                }
                                            }
                                            showRightMenu = false
                                        }
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = { showRightMenu = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Закрыть")
                        }
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("INNOMOBIL", fontWeight = FontWeight.Bold)
                    },
                    navigationIcon = {
                        IconButton(onClick = { openLeftDrawer() }) {
                            Icon(Icons.Default.Menu, "Меню")
                        }
                    },
                    actions = {
                        if (currentRoute == AppDestinations.MONITOR.name) {
                            IconButton(onClick = { showRightMenu = true }) {
                                Icon(Icons.Default.Tune, "Настройки ПЧ")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    AppDestinations.entries.forEach { destination ->
                        NavigationBarItem(
                            icon = { Icon(destination.icon, destination.label) },
                            label = { Text(destination.label) },
                            selected = currentRoute == destination.name,
                            onClick = {
                                if (currentRoute != destination.name) {
                                    navController.navigate(destination.name) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = AppDestinations.CONNECTION.name
                ) {
                    composable(AppDestinations.CONNECTION.name) {
                        ConnectionScreen(
                            selectedType = selectedVfdType,
                            onTypeChange = { selectedVfdType = it },
                            onInstall = { installedVfdType = selectedVfdType }
                        )
                    }
                    composable(AppDestinations.CONTROL.name) {
                        ControlScreen(installedVfdType)
                    }
                    composable(AppDestinations.MONITOR.name) {
                        MonitorScreen(installedVfdType)
                    }
                    composable(AppDestinations.GRAPHICS.name) {
                        GraphicsScreen()
                    }
                    composable(AppDestinations.TERMINAL.name) {
                        TerminalScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MarqueeText(text: String, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        while (true) {
            scrollState.animateScrollTo(
                value = scrollState.maxValue,
                animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
            )
            delay(1000)
            scrollState.scrollTo(0)
            delay(500)
        }
    }

    Row(modifier = modifier.horizontalScroll(scrollState, enabled = false)) {
        Text(
            text = text,
            maxLines = 1,
            softWrap = false,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(100.dp))
    }
}

// Функция для получения групп параметров по типу ПЧ - ОБНОВЛЕННАЯ ВЕРСИЯ ИЗ ВТОРОГО КОДА
fun getParameterGroupsForVfdType(vfdType: String): List<ParameterGroup> {
    return if (vfdType in listOf("IHD_T", "ITD B2", "ITD B3", "ITD B3 EE05(24)")) {
        listOf(
            ParameterGroup("F0", "Группа базовых параметров", (0..13).map { "F0.%02d".format(it) }),
            ParameterGroup("F2", "Управление запуском", (0..10).map { "F2.%02d".format(it) }),
            ParameterGroup("F3", "Вспомогательные рабочие команды", (0..8).map { "F3.%02d".format(it) }),
            ParameterGroup("F5", "Параметры двигателя", (0..12).map { "F5.%02d".format(it) }),
            ParameterGroup("F6", "Входные клеммы", (0..9).map { "F6.%02d".format(it) }),
            ParameterGroup("F7", "Выходные клеммы", (0..7).map { "F7.%02d".format(it) }),
            ParameterGroup("F8", "Параметры ПИД-регулятора", (0..15).map { "F8.%02d".format(it) }),
            ParameterGroup("Fd", "Параметры коммуникационного интерфейса", (0..9).map { "Fd.%02d".format(it) })
        )
    } else {
        listOf(
            ParameterGroup("PA", "Параметры для чтения", (0..12).map { "PA.%02d".format(it) }),
            ParameterGroup("Pb", "Параметры управления", (0..10).map { "Pb.%02d".format(it) }),
            ParameterGroup("PC", "Параметры двигателя", (0..8).map { "PC.%02d".format(it) }),
            ParameterGroup("Pd", "Клеммы управления", (0..9).map { "Pd.%02d".format(it) }),
            ParameterGroup("PG", "Параметры ПИД", (0..11).map { "PG.%02d".format(it) })
        )
    }
}

@Composable
fun ExpandableParameterGroup(group: ParameterGroup, isInitiallyExpanded: Boolean = false) {
    var expanded by remember(isInitiallyExpanded) { mutableStateOf(isInitiallyExpanded) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = group.prefix,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                MarqueeText(
                    text = group.description,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    group.parameters.forEach { parameter ->
                        ParameterItem(parameterCode = parameter)
                    }
                }
            }
        }
    }
}

@Composable
fun ParameterItem(parameterCode: String) {
    var showConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = parameterCode,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            )

            Row {
                IconButton(
                    onClick = {
                        // Чтение параметра
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        "Прочитать",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { showConfirm = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        "Записать",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Запись параметра") },
            text = { Text("Изменить значение регистра $parameterCode? Это может повлиять на работу двигателя.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Логика записи
                        showConfirm = false
                    }
                ) {
                    Text("ЗАПИСАТЬ", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("ОТМЕНА")
                }
            }
        )
    }
}

@Composable
fun MonitorScreen(vfdType: String) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    // ВАЖНО: Используем ту же логику, что и во втором коде для определения групп параметров
    val allGroups = remember(vfdType) {
        if (vfdType in listOf("IHD_T", "ITD B2", "ITD B3", "ITD B3 EE05(24)")) {
            listOf(
                ParameterGroup("F0", "Группа базовых параметров", (0..13).map { "F0.%02d".format(it) }),
                ParameterGroup("F2", "Управление запуском", (0..10).map { "F2.%02d".format(it) }),
                ParameterGroup("F3", "Вспомогательные рабочие команды", (0..8).map { "F3.%02d".format(it) }),
                ParameterGroup("F5", "Параметры двигателя", (0..12).map { "F5.%02d".format(it) }),
                ParameterGroup("F6", "Входные клеммы", (0..9).map { "F6.%02d".format(it) }),
                ParameterGroup("F7", "Выходные клеммы", (0..7).map { "F7.%02d".format(it) }),
                ParameterGroup("F8", "Параметры ПИД-регулятора", (0..15).map { "F8.%02d".format(it) }),
                ParameterGroup("Fd", "Параметры коммуникационного интерфейса", (0..9).map { "Fd.%02d".format(it) })
            )
        } else {
            listOf(
                ParameterGroup("PA", "Параметры для чтения", (0..12).map { "PA.%02d".format(it) }),
                ParameterGroup("Pb", "Параметры управления", (0..10).map { "Pb.%02d".format(it) }),
                ParameterGroup("PC", "Параметры двигателя", (0..8).map { "PC.%02d".format(it) }),
                ParameterGroup("Pd", "Клеммы управления", (0..9).map { "Pd.%02d".format(it) }),
                ParameterGroup("PG", "Параметры ПИД", (0..11).map { "PG.%02d".format(it) })
            )
        }
    }

    val filteredGroups = allGroups.map { group ->
        val filteredParams = group.parameters.filter { param ->
            param.contains(searchQuery, ignoreCase = true) ||
                    group.description.contains(searchQuery, ignoreCase = true) ||
                    group.prefix.contains(searchQuery, ignoreCase = true)
        }
        group.copy(parameters = filteredParams)
    }.filter { it.parameters.isNotEmpty() }

    AdaptiveColumn {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!isSearchExpanded) {
                    Text(
                        "Параметры: $vfdType",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                AnimatedVisibility(
                    visible = isSearchExpanded,
                    enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                    exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End),
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("F5 или 'двигатель'...") },
                        modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                        singleLine = true,
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, null)
                                }
                            }
                        }
                    )
                }

                IconButton(onClick = {
                    isSearchExpanded = !isSearchExpanded
                    if (!isSearchExpanded) searchQuery = ""
                }) {
                    Icon(
                        imageVector = if (isSearchExpanded) Icons.Default.ArrowBack else Icons.Default.Search,
                        contentDescription = "Поиск",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                "ПАРАМЕТРЫ ПЧ",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                if (filteredGroups.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.SearchOff,
                                null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Параметры не найдены", color = Color.Gray)
                            if (searchQuery.isNotEmpty()) {
                                Text("Запрос: '$searchQuery'", color = Color.LightGray, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    filteredGroups.forEach { group ->
                        ExpandableParameterGroup(
                            group = group,
                            isInitiallyExpanded = searchQuery.isNotEmpty()
                        )
                    }
                }
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun GraphicsScreen() {
    // Данные для графиков
    val currentPoints = remember { mutableStateListOf<Float>() }
    val voltagePoints = remember { mutableStateListOf<Float>() }
    val powerPoints = remember { mutableStateListOf<Float>() }

    // Симуляция данных для графиков
    LaunchedEffect(Unit) {
        var time = 0f
        while (true) {
            delay(500)
            time += 0.5f

            // Симуляция тока (2-4А с шумом)
            val current = 2.5f + 1.5f * kotlin.math.sin(time.toDouble()).toFloat() + (0..100).random() * 0.01f
            currentPoints.add(current)
            if (currentPoints.size > 50) currentPoints.removeFirst()

            // Симуляция напряжения (220-240В)
            val voltage = 230f + 10f * kotlin.math.sin(time * 0.7f.toDouble()).toFloat() + (0..100).random() * 0.05f
            voltagePoints.add(voltage)
            if (voltagePoints.size > 50) voltagePoints.removeFirst()

            // Симуляция мощности (0.8-1.5кВт)
            val power = 1.1f + 0.4f * kotlin.math.sin(time * 0.5f.toDouble()).toFloat() + (0..100).random() * 0.01f
            powerPoints.add(power)
            if (powerPoints.size > 50) powerPoints.removeFirst()
        }
    }

    AdaptiveColumn {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Графики в реальном времени",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            RealTimeGraph(
                points = currentPoints,
                color = Color(0xFF00BCD4),
                label = "ТОК ДВИГАТЕЛЯ",
                unit = "A"
            )

            RealTimeGraph(
                points = voltagePoints,
                color = Color(0xFFFF9800),
                label = "НАПРЯЖЕНИЕ",
                unit = "V"
            )

            RealTimeGraph(
                points = powerPoints,
                color = Color(0xFF4CAF50),
                label = "МОЩНОСТЬ",
                unit = "kW"
            )
        }
    }
}

@SuppressLint("InlinedApi")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreen(selectedType: String, onTypeChange: (String) -> Unit, onInstall: () -> Unit) {
    val vfdList = listOf("IBD", "IBD_E", "IBD_B", "IDD", "IDD mini PLUS", "IHD_T", "IMD_E", "IPD",
        "ISD", "ISD mini", "ISD mini PLUS", "ITD B2", "ITD B3", "ITD B3 EE05(24)", "IVD", "IVD_E")
    var expanded by remember { mutableStateOf(false) }

    // Упрощенная версия для всех Android
    val permsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    AdaptiveColumn {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Bluetooth,
                null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Конфигурация подключения",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Выберите тип преобразователя частоты",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Тип ПЧ") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    vfdList.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = { onTypeChange(type); expanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Простая информационная панель
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = if (permsState.allPermissionsGranted)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (permsState.allPermissionsGranted)
                                Icons.Default.CheckCircle
                            else
                                Icons.Default.Bluetooth,
                            contentDescription = null,
                            tint = if (permsState.allPermissionsGranted)
                                Color.Green
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (permsState.allPermissionsGranted)
                                "✓ Все разрешения предоставлены"
                            else
                                "Необходимы разрешения Bluetooth",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Основная кнопка для подключения
            Button(
                onClick = {
                    if (!permsState.allPermissionsGranted) {
                        permsState.launchMultiplePermissionRequest()
                    } else {
                        onInstall()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedType.isNotEmpty()
            ) {
                Text("ПОДКЛЮЧИТЬСЯ", fontWeight = FontWeight.Bold)
            }

            // Кнопка для запроса разрешений (отдельная)
            if (!permsState.allPermissionsGranted) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { permsState.launchMultiplePermissionRequest() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text("ПРЕДОСТАВИТЬ РАЗРЕШЕНИЯ BLUETOOTH", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ControlScreen(vfdType: String) {
    var freqTarget by remember { mutableStateOf("0") }
    var freqSlider by remember { mutableStateOf(0f) }
    var isRunning by remember { mutableStateOf(false) }
    var showExtra by remember { mutableStateOf(false) }
    var autoInc by remember { mutableStateOf(false) }
    var autoDec by remember { mutableStateOf(false) }
    var connectionAlive by remember { mutableStateOf(true) }

    // Watchdog для поддержания связи
    LaunchedEffect(isRunning) {
        while (true) {
            delay(2000)
            if (isRunning) {
                // Симуляция проверки связи
                connectionAlive = (0..10).random() > 1 // 90% шанс успеха

                // Отправка пинга (закомментировано для симуляции)
                // launch(Dispatchers.IO) {
                //     sendHex("010321030001")
                // }
            }
        }
    }

    LaunchedEffect(autoInc) {
        if (autoInc) {
            delay(500)
            while (autoInc) {
                if (freqSlider < 50f) {
                    freqSlider += 1f
                    freqTarget = freqSlider.toInt().toString()
                }
                delay(100)
            }
        }
    }

    LaunchedEffect(autoDec) {
        if (autoDec) {
            delay(500)
            while (autoDec) {
                if (freqSlider > 0f) {
                    freqSlider -= 1f
                    freqTarget = freqSlider.toInt().toString()
                }
                delay(100)
            }
        }
    }

    AdaptiveColumn {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (freqSlider >= 49.9f && isRunning) Color(0x33FF0000)
                    else Color(0xFF1B1B1B)
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (freqSlider >= 49.9f && isRunning) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                null,
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "ПРЕДЕЛ ЧАСТОТЫ",
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text(
                        "ВЫХОДНАЯ ЧАСТОТЫ",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        "${String.format("%.1f", freqSlider)} Hz",
                        color = if (isRunning) Color.Cyan else Color.Gray,
                        fontSize = 48.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DisplayMetric(
                            label = "ТОК",
                            value = if (isRunning) "2.4" else "0.0",
                            unit = "A",
                            color = Color.Green
                        )
                        DisplayMetric(
                            label = "МОЩНОСТЬ",
                            value = if (isRunning) "1.2" else "0.0",
                            unit = "kW",
                            color = Color.Yellow
                        )
                    }
                }
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Задание частоты:", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = freqTarget,
                        onValueChange = {
                            val newValue = it.filter { char -> char.isDigit() || char == '.' }
                            freqTarget = newValue
                            freqSlider = newValue.toFloatOrNull() ?: 0f
                            if (freqSlider > 50f) {
                                freqSlider = 50f
                                freqTarget = "50"
                            }
                        },
                        modifier = Modifier.width(100.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        suffix = { Text("Hz") }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("${((freqSlider / 50f) * 100).toInt()}%", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = freqSlider,
                    onValueChange = {
                        freqSlider = it
                        freqTarget = it.toInt().toString()
                    },
                    valueRange = 0f..50f,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (freqSlider > 0f) {
                                freqSlider -= 1f
                                freqTarget = freqSlider.toInt().toString()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        autoDec = true
                                        tryAwaitRelease()
                                        autoDec = false
                                    }
                                )
                            }
                            .height(56.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    StatusButton("ПУСК", isRunning, Modifier.weight(2f)) {
                        isRunning = true
                    }
                    StatusButton("СТОП", !isRunning, Modifier.weight(2f)) {
                        isRunning = false
                    }

                    IconButton(
                        onClick = {
                            if (freqSlider < 50f) {
                                freqSlider += 1f
                                freqTarget = freqSlider.toInt().toString()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        autoInc = true
                                        tryAwaitRelease()
                                        autoInc = false
                                    }
                                )
                            }
                            .height(56.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { showExtra = !showExtra },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (showExtra) "Скрыть дополнительные команды" else "Показать дополнительные команды")
                    Icon(
                        if (showExtra) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        null
                    )
                }

                AnimatedVisibility(visible = showExtra) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text("Вращение вперед")
                        }
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text("Вращение назад")
                        }
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text("Сброс ошибки")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = MaterialTheme.shapes.small,
                color = if (connectionAlive) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.errorContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (connectionAlive) Icons.Default.CheckCircle else Icons.Default.Warning,
                        null,
                        tint = if (connectionAlive) Color.Green else Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (connectionAlive) "Статус: Подключено к $vfdType"
                        else "ВНИМАНИЕ: Проблемы со связью",
                        fontSize = 12.sp,
                        fontWeight = if (!connectionAlive) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TerminalScreen() {
    var inputCommand by remember { mutableStateOf("") }
    val commandLog = remember { mutableStateListOf<TerminalLogEntry>() }
    val savedCommands = remember {
        mutableStateListOf(
            CustomCommand("ПУСК", "010600010001"),
            CustomCommand("СТОП", "010600010002"),
            CustomCommand("ЧТЕНИЕ", "010300010001"),
            CustomCommand("СБРОС", "010600010003")
        )
    }
    var showDialog by remember { mutableStateOf(false) }
    var newCommandName by remember { mutableStateOf("") }
    var newCommandHex by remember { mutableStateOf("") }

    AdaptiveColumn {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text("HEX Терминал", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A))
            ) {
                Column(Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1A1A1A))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ЖУРНАЛ КОМАНД", color = Color.Gray, fontSize = 12.sp)
                        IconButton(
                            onClick = { commandLog.clear() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ClearAll, null, tint = Color.Gray)
                        }
                    }

                    if (commandLog.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Terminal, null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("История команд пуста", color = Color.Gray)
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(8.dp)
                        ) {
                            commandLog.forEach { entry ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        entry.timestamp,
                                        color = Color.Gray,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.width(60.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .height(12.dp)
                                            .background(
                                                when {
                                                    entry.isError -> Color.Red
                                                    entry.direction == "SENT" -> Color(0xFF2196F3)
                                                    else -> Color(0xFF4CAF50)
                                                }
                                            )
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        entry.command,
                                        color = when {
                                            entry.isError -> Color.Red
                                            !entry.isValid -> Color.Yellow
                                            else -> Color(0xFF00FF00)
                                        },
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = inputCommand,
                onValueChange = { inputCommand = it.uppercase().filter { c ->
                    c.isDigit() || c in 'A'..'F' || c == ' '
                }},
                label = { Text("HEX команда (без CRC)") },
                placeholder = { Text("Например: 01 06 00 01 00 01") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Row {
                        IconButton(
                            onClick = {
                                inputCommand = appendCRC(inputCommand.replace(" ", ""))
                            }
                        ) {
                            Icon(Icons.Default.Calculate, "Добавить CRC", tint = Color(0xFF2196F3))
                        }
                        IconButton(
                            onClick = {
                                if (inputCommand.isNotBlank()) {
                                    val commandToSend = if (!inputCommand.contains(" ")) {
                                        inputCommand.chunked(2).joinToString(" ")
                                    } else {
                                        inputCommand
                                    }
                                    val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                                    // Проверка на ошибку Modbus (старший бит в функции)
                                    val cleanCommand = inputCommand.replace(" ", "")
                                    val isModbusError = if (cleanCommand.length >= 2) {
                                        val functionCode = cleanCommand.substring(0, 2).toIntOrNull(16) ?: 0
                                        functionCode >= 0x80
                                    } else false

                                    commandLog.add(
                                        TerminalLogEntry(
                                            timestamp = timestamp,
                                            command = commandToSend,
                                            direction = "SENT",
                                            isValid = validateHexCommand(cleanCommand),
                                            isError = isModbusError
                                        )
                                    )
                                    inputCommand = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, "Отправить", tint = Color(0xFF4CAF50))
                        }
                    }
                },
                isError = inputCommand.isNotBlank() && !validateHexCommand(inputCommand.replace(" ", ""))
            )

            if (inputCommand.isNotBlank() && !validateHexCommand(inputCommand.replace(" ", ""))) {
                Text(
                    "Ошибка: Некорректный HEX формат",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Быстрые команды:", style = MaterialTheme.typography.labelLarge)
                Button(
                    onClick = { showDialog = true },
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Добавить")
                }
            }

            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                savedCommands.forEach { cmd ->
                    AssistChip(
                        onClick = {
                            inputCommand = cmd.hexCode.chunked(2).joinToString(" ")
                        },
                        label = { Text(cmd.name) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                "Удалить",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { savedCommands.remove(cmd) }
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        val response = "01 03 04 00 00 00 00 FB 2A"
                        val isValidCRC = checkResponseCRC(response.replace(" ", ""))

                        commandLog.add(
                            TerminalLogEntry(
                                timestamp = timestamp,
                                command = "$response (СИМУЛЯЦИЯ${if (!isValidCRC) " - ОШИБКА CRC" else ""})",
                                direction = "RECEIVED",
                                isValid = isValidCRC,
                                isError = !isValidCRC
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("Симуляция ответа")
                }

                Button(
                    onClick = {
                        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        // Моделируем ответ с ошибкой Modbus (код функции + 0x80)
                        commandLog.add(
                            TerminalLogEntry(
                                timestamp = timestamp,
                                command = "01 83 02 C0 F1 (СИМУЛЯЦИЯ ОШИБКИ MODBUS)",
                                direction = "RECEIVED",
                                isValid = false,
                                isError = true
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text("Симуляция ошибки")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Новая команда") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newCommandName,
                        onValueChange = { newCommandName = it },
                        label = { Text("Название команды") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCommandHex,
                        onValueChange = { newCommandHex = it.uppercase().filter { c ->
                            c.isDigit() || c in 'A'..'F' || c == ' '
                        }},
                        label = { Text("HEX код (без CRC)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("010600010001") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCommandName.isNotBlank() && newCommandHex.isNotBlank()) {
                            savedCommands.add(
                                CustomCommand(
                                    newCommandName,
                                    newCommandHex.replace(" ", "")
                                )
                            )
                            showDialog = false
                            newCommandName = ""
                            newCommandHex = ""
                        }
                    },
                    enabled = newCommandName.isNotBlank() && newCommandHex.isNotBlank()
                ) {
                    Text("Создать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

// Вспомогательные функции (вынесены в конец файла)
fun appendCRC(hexString: String): String {
    if (hexString.isEmpty()) return ""

    val cleanHex = hexString.filter { it.isDigit() || it in 'A'..'F' }
    if (cleanHex.length % 2 != 0) return hexString

    val bytes = cleanHex.chunked(2).map { it.toInt(16) }.toIntArray()
    var crc = 0xFFFF

    for (pos in bytes.indices) {
        crc = crc xor bytes[pos]
        for (i in 8 downTo 1) {
            if ((crc and 0x0001) != 0) {
                crc = (crc shr 1) xor 0xA001
            } else {
                crc = crc shr 1
            }
        }
    }

    val crcLow = (crc and 0xFF).toString(16).padStart(2, '0').uppercase()
    val crcHigh = ((crc shr 8) and 0xFF).toString(16).padStart(2, '0').uppercase()

    val formattedHex = cleanHex.chunked(2).joinToString(" ")
    return "$formattedHex $crcLow $crcHigh"
}

fun validateHexCommand(hexString: String): Boolean {
    if (hexString.isEmpty()) return true
    val cleanHex = hexString.filter { it.isDigit() || it in 'A'..'F' }
    if (cleanHex.length % 2 != 0) return false
    return try {
        cleanHex.chunked(2).all { it.toInt(16) in 0..255 }
    } catch (e: Exception) {
        false
    }
}

// Заглушка для отправки и ожидания ответа
fun sendAndAwaitResponse(cmd: String): Boolean {
    // Реальная реализация будет отправлять команду через Bluetooth и ждать ответа
    return cmd.startsWith("01") // Для демонстрации считаем, что ПЧ с адресом 1 отвечает
}

// Функция для синуса (исправлена)
fun sin(x: Float): Float = kotlin.math.sin(x.toDouble()).toFloat()