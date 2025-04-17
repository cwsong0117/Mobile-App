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


@Composable
fun SignupScreen(navController: NavController, isDarkTheme: Boolean) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val fullname = remember { mutableStateOf("") }
    val birthday = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmpassword = remember { mutableStateOf("") }
    val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            val formattedMonth = String.format("%02d", selectedMonth + 1)
            val formattedDay = String.format("%02d", selectedDayOfMonth)
            birthday.value = "$formattedMonth/$formattedDay/$selectedYear"
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
                    text = "Full Name as per IC",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                TextField(
                    value = fullname.value,
                    onValueChange = { fullname.value = it },
                    placeholder = { Text("ex: LEE KEE ZHAN") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp)),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Date od Birth",
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
                    placeholder = { Text("MM/DD/YYYY") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp))
                        .clickable {
                            datePickerDialog.show()
                        },
                    enabled = false, // 让TextField不可编辑，必须通过选择器输入
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(30.dp))
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
                    placeholder = { Text("ex: Ah Lee") },
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
                    singleLine = true
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
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(30.dp))
            }
            Text(text = "Username: ${username.value}")
            Text(text = "Password: ${password.value}")

            // 在图标和按钮之间增加间距
            Spacer(modifier = Modifier.height(50.dp))

            Box(
                modifier = Modifier
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    SignupScreen(navController, isDarkTheme = false)
}