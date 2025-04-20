package com.hermen.ass1

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Calendar
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.google.firebase.firestore.FirebaseFirestore
import com.hermen.ass1.ui.theme.Screen
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.text.input.VisualTransformation
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun SignupScreen(navController: NavController, isDarkTheme: Boolean) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val email = remember { mutableStateOf("") }
    val birthday = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmpassword = remember { mutableStateOf("") }
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
    val role = remember { mutableStateOf("") } // "admin" 或 "staff"
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    val isEmailValid = email.value.matches(emailPattern.toRegex())
    val isPasswordStrong = password.value.length >= 6
    val passwordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }
    val position = remember { mutableStateOf("") }
    val department = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            val formattedMonth = String.format("%02d", selectedMonth + 1)
            val formattedDay = String.format("%02d", selectedDayOfMonth)
            birthday.value = "$formattedDay/$formattedMonth/$selectedYear"
        }, year, month, day
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 文字部分
            Spacer(modifier = Modifier.height(50.dp)) // 调整高度，推开顶部

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 230.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Signup",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // 在文字和图标之间增加间隔
            Spacer(modifier = Modifier.height(50.dp))

            Column(
                modifier = Modifier
                    .padding(start = 30.dp, end = 30.dp)
            ) {
                Text(
                    text = "Username",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    placeholder = { Text("ex: LEE KEE ZHAN") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp)),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Email",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    placeholder = { Text("ex: Ah Lee") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp)),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Date of Birth",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                TextField(
                    value = birthday.value,
                    onValueChange = { birthday.value = it },
                    placeholder = { Text("DD/MM/YYYY") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                contentDescription = "Calendar Icon"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp))
                        .clickable {
                            datePickerDialog.show()
                        },
                    enabled = false, // So users can't manually edit, only pick from calendar
                    readOnly = true,
                )

                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Position",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                TextField(
                    value = position.value,
                    onValueChange = { position.value = it },
                    placeholder = { Text("ex: Manager") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp)),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 👉 加入 Department
                Text(
                    text = "Department",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                TextField(
                    value = department.value,
                    onValueChange = { department.value = it },
                    placeholder = { Text("ex: Human Resource") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp)),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Password",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    placeholder = { Text("ex: ******") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp)),
                    singleLine = true,
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordVisible.value = !passwordVisible.value
                        }) {
                            val iconRes = if (passwordVisible.value) R.drawable.img else R.drawable.img_1
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = if (passwordVisible.value) "Hide Password" else "Show Password",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Confirm Password",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                TextField(
                    value = confirmpassword.value,
                    onValueChange = { confirmpassword.value = it },
                    placeholder = { Text("ex: ******") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp)),
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            confirmPasswordVisible.value = !confirmPasswordVisible.value
                        }) {
                            val iconRes = if (confirmPasswordVisible.value) R.drawable.img else R.drawable.img_1
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = if (confirmPasswordVisible.value) "Hide Password" else "Show Password",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))

                // 角色选择按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (role.value == "admin") Color.Gray else Color.LightGray)
                            .clickable { role.value = "admin" }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Admin", fontSize = 16.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (role.value == "staff") Color.Gray else Color.LightGray)
                            .clickable  { role.value = "staff" }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(" Staff ", fontSize = 16.sp)
                    }
                }

                // Confirm 按钮与 Firestore 写入逻辑
                Button(
                    onClick = {
                        val db = FirebaseFirestore.getInstance()
                        val prefix = if (role.value == "admin") "A" else "S"
                        val ref = db.collection("User")

                        if (email.value.isBlank() || username.value.isBlank() || birthday.value.isBlank() ||
                            password.value.isBlank() || confirmpassword.value.isBlank() || role.value.isBlank()||
                            position.value.isBlank() || department.value.isBlank()
                        ) {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (!isEmailValid) {
                            Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (!isPasswordStrong) {
                            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (password.value != confirmpassword.value) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // 查询所有同前缀 doc IDs
                        ref.get()
                            .addOnSuccessListener { snap ->
                                // 收集已用数字
                                val used = snap.documents.mapNotNull { doc ->
                                    val id = doc.id
                                    if (id.startsWith(prefix) && id.length == 4) {
                                        id.substring(1).toIntOrNull()
                                    } else null
                                }.toSet()

                                // 找出第一个空缺
                                val nextNum = (1..999).firstOrNull { it !in used }
                                if (nextNum == null) {
                                    Toast.makeText(context, "ID 已用完", Toast.LENGTH_SHORT).show()
                                    return@addOnSuccessListener
                                }
                                val newId = prefix + String.format("%03d", nextNum)

                                // 构建用户数据
                                val userMap = hashMapOf(
                                    "name" to username.value,
                                    "email" to email.value,
                                    "birthday" to birthday.value,
                                    "password" to password.value,
                                    "position" to position.value,
                                    "department" to department.value
                                )

                                // 校验后写入
                                if (username.value.isNotBlank() &&
                                    email.value.isNotBlank() &&
                                    birthday.value.isNotBlank() &&
                                    password.value == confirmpassword.value &&
                                    role.value.isNotBlank() &&
                                    position.value.isNotBlank() &&
                                    department.value.isNotBlank()
                                ) {
                                    FirebaseAuth.getInstance()
                                        .createUserWithEmailAndPassword(email.value, password.value)
                                        .addOnCompleteListener { authTask ->
                                            if (authTask.isSuccessful) {
                                                // If auth creation successful, then write user info to Firestore
                                                val userMap = hashMapOf(
                                                    "name" to username.value,
                                                    "email" to email.value,
                                                    "birthday" to birthday.value,
                                                    "password" to password.value,
                                                    "role" to role.value,
                                                    "position" to position.value,
                                                    "department" to department.value,
                                                )

                                                ref.document(newId)
                                                    .set(userMap)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(context, "Signup successful: ID=$newId", Toast.LENGTH_SHORT).show()
                                                        navController.navigate(Screen.InitialPage.route) {
                                                            popUpTo(Screen.Signup.route) { inclusive = true }
                                                        }
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(context, "Failed to save user info", Toast.LENGTH_SHORT).show()
                                                    }
                                            } else {
                                                // Auth failed - show reason
                                                Toast.makeText(
                                                    context,
                                                    "Authentication failed: ${authTask.exception?.localizedMessage}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to connect to database", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(text = "Confirm", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    SignupScreen(navController, isDarkTheme = false)
}