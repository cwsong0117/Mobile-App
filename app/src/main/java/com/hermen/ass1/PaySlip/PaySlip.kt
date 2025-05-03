package com.hermen.ass1.PaySlip

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hermen.ass1.BackButton
import java.time.Year
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.FileProvider
import com.hermen.ass1.R
import com.hermen.ass1.User.SessionManager
import com.hermen.ass1.User.UserProfileViewModel
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun PaySlip(navController: NavController, isDarkTheme: Boolean) {
    val user = SessionManager.currentUser!!
    val userId = user.id

    if (userId.startsWith("A")) {
        val backgroundColor = if (isDarkTheme) Color.Transparent else Color(0xFFE5FFFF)
        val viewButton = if (isDarkTheme) Color.Transparent else Color(0xFF89CFF0)
        val manageButton = if (isDarkTheme) Color.Transparent else Color(0xFF89CFF0)
        Column {
            BackButton(navController, "Pay Slip Panel", isDarkTheme)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Admin Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { navController.navigate("viewPayslip") },
                    modifier = Modifier.fillMaxWidth(0.6f),
                    colors = ButtonDefaults.buttonColors(viewButton)
                ) {
                    Text("View Payslips")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("managePayslip") },
                    modifier = Modifier.fillMaxWidth(0.6f),
                    colors = ButtonDefaults.buttonColors(Color.White),
                    border = BorderStroke(2.dp, manageButton)
                ) {
                    Text("Manage Payslips", color = manageButton)
                }
            }
        }
    } else {
        PaySlipHomeScreen(navController, isDarkTheme)
    }
}

@Composable
fun PaySlipHomeScreen(navController: NavController, isDarkTheme: Boolean, viewModel: PaySlipViewModel = viewModel()) {
    var selectedMonth by remember { mutableStateOf<String>("All") }
    var selectedYear by remember { mutableStateOf<String?>(Year.now().value.toString()) }

    val requestList by viewModel.requestList.collectAsState()
    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)

    val user = SessionManager.currentUser!!
    val userId = user.id

    val filteredList = requestList
        .filter { it.year == selectedYear }
        .filter { it.month == selectedMonth || selectedMonth == "All" }

    val visibleList = filteredList.filter { it.userId == userId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Divider(
            color = Color.LightGray,
            thickness = 1.dp
        )
        Row {
            BackButton(navController = navController, title = "Pay Slips", isDarkTheme = false)
        }
        Row {
            FilterBar(
                selectedMonth = selectedMonth,
                onMonthSelected = { selectedMonth = it },
                selectedYear = selectedYear ?: Year.now().value.toString(),
                onYearSelected = { selectedYear = it },
                isDarkTheme = isDarkTheme
            )
        }

        if (visibleList.isEmpty()) {
            Text(
                "No payslips available.",
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        } else {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)) {
                items(visibleList) { paySlip ->
                    PaySlipCard(navController = navController, paySlip = paySlip)
                }
            }
        }
    }
}

@Composable
fun PaySlipCard(navController: NavController, paySlip: PaySlipResource) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { navController.navigate("paySlipDetails/${paySlip.month}/${paySlip.year}") },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Payslip for ${paySlip.month} ${paySlip.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = "Net Salary: RM %.2f".format(paySlip.netSalary),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp
            )
        }
    }
}


@Composable
fun FilterBar(selectedMonth: String, onMonthSelected: (String) -> Unit,
              selectedYear: String, onYearSelected: (String) -> Unit,
              isDarkTheme: Boolean) {
    val months = listOf("All","January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    val currentYear = Year.now().value.toInt()
    val years = (2020..currentYear).map { it.toString() }
    var expandedMonth by remember { mutableStateOf(false) }
    var expandedYear by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        //Month Drop Down Button
        Box{
            TextButton(onClick = {expandedMonth = true}) {
                Text(text = selectedMonth, fontSize = 24.sp)
            }
            DropdownMenu(expanded = expandedMonth, onDismissRequest = {expandedMonth = false}) {
                months.forEach { month ->
                    DropdownMenuItem(
                        text = { Text(text = month) },
                        onClick = {
                            onMonthSelected(month)
                            expandedMonth = false
                        }
                    )
                }
            }
        }

        //Year Drop Down Button
        Box{
            TextButton(onClick = {expandedYear = true}) {
                Text(text = selectedYear, fontSize = 24.sp)
            }
            DropdownMenu(expanded = expandedYear, onDismissRequest = {expandedYear = false}) {
                years.forEach { year ->
                    DropdownMenuItem(
                        text = { Text(text = year) },
                        onClick = {
                            onYearSelected(year)
                            expandedYear = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PaySlipDetailsScreen(navController: NavController, month: String, year: String,
                         viewModel: PaySlipViewModel = viewModel(),
                         isDarkTheme: Boolean) {
    val paySlips by viewModel.requestList.collectAsState()
    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)
    val selectedSlip = paySlips.find { it.month == month && it.year == year }
    val context = LocalContext.current

    if (selectedSlip != null) {

        Column(modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()) {
            Row{
                val backgroundColor = if (isDarkTheme) Color.Transparent else Color.White
                val textColor = if (isDarkTheme) Color.White else Color.Black
                val iconColor = if (isDarkTheme) Color.White else Color.Black

                Column(modifier = Modifier.fillMaxWidth()) {
                    Surface(modifier = Modifier.fillMaxWidth(), color = backgroundColor) {
                        Row(
                            modifier = Modifier
                                .height(52.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                                    contentDescription = "Back",
                                    modifier = Modifier.size(24.dp),
                                    tint = iconColor
                                )
                            }
                            Text(
                                text = "Payslip Details",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f),
                                color = textColor
                            )
                            IconButton(onClick = {
                                selectedSlip?.let { slip ->
                                    val pdfFile = generatePdf(context, slip)
                                    sharePdf(context, pdfFile)
                                }
                                Toast.makeText(context, "Export or share clicked", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_share_24), // use appropriate icon
                                    contentDescription = "Export or Share",
                                    tint = iconColor
                                )
                            }

                        }
                    }
                    androidx.compose.material.Divider(
                        color = Color.LightGray,
                        thickness = 2.dp
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Payslip Details for $month $year", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.padding(8.dp))

                SectionHeader("Income")
                DetailRow("Basic Salary", selectedSlip.basicSalary)
                DetailRow("Allowance", selectedSlip.allowance)
                DetailRow("Bonus", selectedSlip.bonus)
                DetailRow("Overtime Pay", selectedSlip.overtimePay)

                Spacer(modifier = Modifier.padding(8.dp))

                SectionHeader("Deductions")
                DetailRow("Income Tax", selectedSlip.incomeTax)
                DetailRow("Unpaid Leave", selectedSlip.unpaidLeave)

                Spacer(modifier = Modifier.padding(8.dp))

                SectionHeader("Summary")
                DetailRow("Gross Salary", selectedSlip.grossSalary)
                DetailRow("Total Deduction", selectedSlip.deduction)
                DetailRow("Net Salary", selectedSlip.netSalary)
            }

        }
    } else {
        Text("Payslip not found.", modifier = Modifier.padding(16.dp), color = Color.Red)
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun DetailRow(label: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text("RM %.2f".format(amount), fontWeight = FontWeight.Bold)
    }
}

fun generatePdf(context: Context, slip: PaySlipResource): File {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()
    paint.textSize = 14f
    paint.color = Color.Black.toArgb()
    var yPos = 40

    fun drawLine(text: String) {
        canvas.drawText(text, 40f, yPos.toFloat(), paint)
        yPos += 25
    }

    drawLine("Payslip Details - ${slip.month} ${slip.year}")
    drawLine("User ID: ${slip.userId}")
    drawLine("")
    drawLine("Income")
    drawLine("Basic Salary: RM ${slip.basicSalary}")
    drawLine("Allowance: RM ${slip.allowance}")
    drawLine("Bonus: RM ${slip.bonus}")
    drawLine("Overtime Pay: RM ${slip.overtimePay}")
    drawLine("")
    drawLine("Deductions")
    drawLine("Income Tax: RM ${slip.incomeTax}")
    drawLine("Unpaid Leave: RM ${slip.unpaidLeave}")
    drawLine("")
    drawLine("Summary")
    drawLine("Gross Salary: RM ${slip.grossSalary}")
    drawLine("Total Deductions: RM ${slip.deduction}")
    drawLine("Net Salary: RM ${slip.netSalary}")

    pdfDocument.finishPage(page)

    val file = File(context.cacheDir, "Payslip_${slip.userId}_${slip.month}_${slip.year}.pdf")
    pdfDocument.writeTo(FileOutputStream(file))
    pdfDocument.close()

    return file
}

fun sharePdf(context: Context, file: File) {
    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider", // match with your Manifest
        file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share Payslip PDF"))
}

@Composable
fun PaySlipHomeScreenForAdmin(navController: NavController, isDarkTheme: Boolean) {
    //fetch the department from viewModel
    val viewModel: UserProfileViewModel = viewModel()
    val psviewMdel: PaySlipViewModel = viewModel()

    val departmentList = viewModel.departmentList
    val employeeList = viewModel.employeesInSelectedDepartment

    var expandedDpt by remember { mutableStateOf(false) }
    var expandedEmp by remember { mutableStateOf(false) }

    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        BackButton(navController = navController, title = "Pay Slip Panel", isDarkTheme = false)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                value = viewModel.department,
                onValueChange = { viewModel.department = it },
                label = { Text("Department") },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "dpt",
                        modifier = Modifier
                            .clickable { expandedDpt = true }
                    )
                },
                modifier = Modifier.fillMaxWidth().clickable { expandedDpt = true }
            )

            DropdownMenu(
                expanded = expandedDpt,
                onDismissRequest = { expandedDpt = false },
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
            ) {
                departmentList.forEach { dp ->
                    DropdownMenuItem(
                        text = { Text(dp) },
                        onClick = {
                            viewModel.department = dp
                            viewModel.name = ""
                            viewModel.fetchEmployeesByDepartment(dp)
                            expandedDpt = false
                        },
                    )
                }
            }
        }

        //Second drop down for employee
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (viewModel.department.isNotEmpty()) {
                TextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.name = it },
                    label = { Text("Employee Name") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "emp",
                            modifier = Modifier.clickable { expandedEmp = true })
                    },
                    modifier = Modifier.fillMaxWidth().clickable { expandedEmp = true }
                )

                DropdownMenu(
                    expanded = expandedEmp,
                    onDismissRequest = { expandedEmp = false },
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
                ) {
                    employeeList.forEach { (name, userId) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                viewModel.name = name
                                viewModel.userId = userId
                                psviewMdel.month = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                                psviewMdel.year = Year.now().value.toString()
                                expandedEmp = false
                            }
                        )
                    }
                }
            }
        }
        Column {
            if (viewModel.department.isNotEmpty() && viewModel.name.isNotEmpty()) {
                PaySlipEditorScreen(viewModel.userId, {}, isDarkTheme, navController, onSuccess = {},
                    onError = {})
            }
        }
    }
}


@Composable
fun PaySlipEditorScreen(
    employeeId: String, // selected from previous screen
    onSubmit: (paySlip: PaySlipResource) -> Unit,
    isDarkTheme: Boolean, navController: NavController,
    onSuccess: () -> Unit, onError: (Exception) -> Unit
) {
    val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    val currentYear = Year.now().value.toInt()
    val years = (2020..currentYear).map { it.toString() }
    var expandedMonth by remember { mutableStateOf(false) }
    var expandedYear by remember { mutableStateOf(false) }

    val viewModel : PaySlipViewModel = viewModel()

    val backgroundColor = if (isDarkTheme) Color.Black else Color(0xFFE5FFFF)

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val calculation = PaySlipResource(
        userId = employeeId,
        year = viewModel.year,
        month = viewModel.month,
        basicSalary = viewModel.basicSalary.toDoubleOrNull() ?: 0.0,
        allowance = viewModel.allowance.toDoubleOrNull() ?: 0.0,
        bonus = viewModel.bonus.toDoubleOrNull() ?: 0.0,
        overtimePay = viewModel.overtimePay.toDoubleOrNull() ?: 0.0,
        otherIncome = viewModel.otherIncome.toDoubleOrNull() ?: 0.0,
        incomeTax = viewModel.incomeTax.toDoubleOrNull() ?: 0.0,
        unpaidLeave = viewModel.unpaidLeave.toDoubleOrNull() ?: 0.0,
        otherDeduction = viewModel.otherDeduction.toDoubleOrNull() ?: 0.0
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxWidth()
                .padding(start = 0.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Year Selector (Start-aligned)
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(text = "Year", style = MaterialTheme.typography.labelMedium)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                ) {
                    TextButton(
                        onClick = { expandedYear = true },
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(0.dp)
                    ){
                        Text(text = viewModel.year, fontSize = 16.sp, color = Color.Black)
                    }
                    DropdownMenu(expanded = expandedYear, onDismissRequest = { expandedYear = false }) {
                        years.forEach { year ->
                            DropdownMenuItem(
                                text = { Text(text = year) },
                                onClick = {
                                    viewModel.year = year
                                    expandedYear = false
                                }
                            )
                        }
                    }
                }
            }

            // Month Selector (End-aligned)
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = "Month",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.Start)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                ) {
                    TextButton(
                        onClick = { expandedMonth = true },
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(text = viewModel.month, fontSize = 16.sp, color = Color.Black)
                    }
                    DropdownMenu(expanded = expandedMonth, onDismissRequest = { expandedMonth = false }) {
                        months.forEach { month ->
                            DropdownMenuItem(
                                text = { Text(text = month) },
                                onClick = {
                                    viewModel.month = month
                                    viewModel.fetchRecordByMonth(userId = employeeId)
                                    expandedMonth = false
                                }
                            )
                        }
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            if(viewModel.year.isNotEmpty() && viewModel.month.isNotEmpty()){
                viewModel.fetchRecordByMonth(userId = employeeId)
            }
            viewModel.loadBasicSalary(userId = employeeId)
        }

        OutlinedTextField(
            value = viewModel.basicSalary,
            onValueChange = { viewModel.basicSalary = it },
            label = { Text("Basic Salary (RM)") },
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = viewModel.allowance,
            onValueChange = { viewModel.allowance = it },
            label = { Text("Allowance (RM)") },
            modifier = Modifier.fillMaxWidth()
            .padding(bottom = 12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = viewModel.bonus,
            onValueChange = { viewModel.bonus = it },
            label = { Text("Bonus (RM)") },
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = viewModel.overtimePay,
            onValueChange = {viewModel.overtimePay = it},
            label = { Text("Overtime Pay (RM)") },
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = viewModel.otherIncome,
            onValueChange = {viewModel.otherIncome = it},
            label = { Text("Other Incomes (RM)") },
            modifier = Modifier.fillMaxWidth()
            .padding(bottom = 12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        //display the gross income using details row
        Spacer(modifier = Modifier.height(8.dp))
        DetailRow("Gross Salary", calculation.grossSalary)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.incomeTax,
            onValueChange = { viewModel.incomeTax = it },
            label = { Text("Income Tax (RM)") },
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = viewModel.unpaidLeave,
            onValueChange = { viewModel.unpaidLeave = it },
            label = { Text("Unpaid Leave (RM)") },
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = viewModel.otherDeduction,
            onValueChange = { viewModel.otherDeduction = it },
            label = { Text("Other Deductions (RM)") },
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        //display the gross income using details row
        Spacer(modifier = Modifier.height(8.dp))
        DetailRow("Total Deduction", calculation.deduction)

        Spacer(modifier = Modifier.height(24.dp))

        //display the gross income using details row
        Spacer(modifier = Modifier.height(8.dp))
        DetailRow("Net Salary", calculation.netSalary)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                //submit to firebase
                viewModel.submitPaySlip(
                    year = viewModel.year,
                    month = viewModel.month,
                    allowance = viewModel.allowance.toDoubleOrNull() ?: 0.0,
                    bonus = viewModel.bonus.toDoubleOrNull() ?: 0.0,
                    overtimePay = viewModel.overtimePay.toDoubleOrNull() ?: 0.0,
                    incomeTax = viewModel.incomeTax.toDoubleOrNull() ?: 0.0,
                    unpaidLeave = viewModel.unpaidLeave.toDoubleOrNull() ?: 0.0,
                    employeeId = employeeId,
                    onSuccess = {
                        Toast.makeText(context, "Pay Slip submitted successfully!", Toast.LENGTH_SHORT).show()
                        // Pop back stack after successful submission
                        navController.popBackStack()
                        onSuccess()

                    },
                    onError = { e ->
                        Log.e("Submit", "Error submitting application", e)
                    },
                )
                onSubmit(calculation)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Pay Slip")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaySlipHomeScreenPreview() {
    PaySlipHomeScreen(navController = NavController(LocalContext.current), isDarkTheme = false, viewModel())
}