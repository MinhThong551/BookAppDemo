package com.example.bookappdemo.ui.listBook

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookappdemo.MyApp // Import class MyApp của bạn vào đây
import com.example.bookappdemo.ui.components.BottomNavItem
import com.example.bookappdemo.ui.components.MyBottomNavigationBar
import com.example.bookappdemo.utils.clearFocusOnTap
import com.google.android.recaptcha.RecaptchaAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

private const val SITE_KEY = "6Lde_OMsAAAAAOcli-sblz7plzHmDb18t9vii1L-"
private const val PROJECT_ID = "my-project-1101-424203"
private const val API_KEY = "AIzaSyCjeMmpveyechR4mqWXa6C9nSs9s55OPkg"
private const val TAG = "ReCaptchaFlow"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToFirestore: () -> Unit,
    onSaveClick: (String, String) -> Unit,
) {
    var title by rememberSaveable { mutableStateOf("") }
    var author by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 🌟 ĐÃ XOÁ: Biến trạng thái recaptchaClient nội bộ
    // 🌟 ĐÃ XOÁ: Toàn bộ block LaunchedEffect(Unit) khởi tạo trùng lặp gây lỗi dòng đời UI

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ADD NEW BOOK (TEST SPAM MODE)", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            MyBottomNavigationBar(selectedTab = BottomNavItem.Add, onTabSelected = {
                if (it == BottomNavItem.Home) onNavigateToHome() else onNavigateToFirestore()
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().clearFocusOnTap(focusManager).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Nhập text 1 lần rồi bật Auto Clicker vào nút SAVE", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Book Title") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), leadingIcon = { Icon(Icons.Default.Create, contentDescription = null) }, singleLine = true
            )

            OutlinedTextField(
                value = author, onValueChange = { author = it },
                label = { Text("Author Name") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }, singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            // =========================================================================
            // NÚT SAVE BOOK: LẤY TRỰC TIẾP CLIENT TỪ MYAPP
            // =========================================================================
            Button(
                onClick = {
                    focusManager.clearFocus()

                    // 🌟 LẤY CLIENT TOÀN CỤC TỪ CẤP APPLICATION (ĐÃ SỬA Ở ĐÂY)
                    val app = context.applicationContext as? MyApp
                    val activeRecaptchaClient = app?.recaptchaClient

                    if (activeRecaptchaClient == null) {
                        Toast.makeText(context, "reCAPTCHA trên Application chưa sẵn sàng, hãy đợi vài giây...", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        try {
                            // Gọi SDK lấy Token bằng client lấy từ MyApp
                            val tokenResult = activeRecaptchaClient.execute(RecaptchaAction.SIGNUP)
                            val token = tokenResult.getOrNull()

                            if (token != null) {
                                // Gửi lên API chấm điểm
                                val score = withContext(Dispatchers.IO) {
                                    getRecaptchaScoreFromGoogle(PROJECT_ID, API_KEY, SITE_KEY, token)
                                }

                                if (score != null) {
                                    Log.d(TAG, "=============================================")
                                    Log.d(TAG, "👉 👉 👉 [KET QUA SPAM] RECAPTCHA SCORE: $score")
                                    Log.d(TAG, "=============================================")

                                    if (score >= 0.5) {
                                        onSaveClick(title.trim(), author.trim())
                                    } else {
                                        Log.w(TAG, "❌ [BOT DETECTED] Phát hiện Spam với Score thấp: $score")
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Lỗi luồng click: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = title.isNotBlank() && author.isNotBlank(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Done, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SAVE BOOK (SPAM HERE)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ------------------------------------------------------------------
// HÀM GỌI API GOOGLE (GIỮ NGUYÊN PAYLOAD GIẢ LẬP BOT ĐỂ HẠ ĐIỂM NHANH)
// ------------------------------------------------------------------
private fun getRecaptchaScoreFromGoogle(projectId: String, apiKey: String, siteKey: String, token: String): Double? {
    try {
        val url = URL("https://recaptchaenterprise.googleapis.com/v1/projects/$projectId/assessments?key=$apiKey")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        connection.doOutput = true

        val jsonBody = JSONObject().apply {
            put("event", JSONObject().apply {
                put("token", token)
                put("siteKey", siteKey)
                put("expectedAction", "signup")
                put("userIpAddress", "52.24.12.57")
                put("userAgent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/114.0.0.0 Safari/537.36")
            })
        }

        OutputStreamWriter(connection.outputStream).use { it.write(jsonBody.toString()); it.flush() }

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val responseText = connection.inputStream.bufferedReader().readText()
            val jsonResponse = JSONObject(responseText)
            if (jsonResponse.has("riskAnalysis")) {
                return jsonResponse.getJSONObject("riskAnalysis").getDouble("score")
            }
        }
    } catch (e: Exception) { e.printStackTrace() }
    return null
}