package com.hermen.ass1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoomDetail(navController: NavController, meetingRoomId : String?) {
    //show the room details of the meeting room based on the meetingRoomId
    Scaffold(
        modifier = Modifier
            .background(Color(0xFFe5ffff)),
        topBar = {
            BackButton(navController = navController, title = "Huddle Room")
        },
        bottomBar = {
            BottomNavigationBar()
        }
    ) {
        innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            ApplyDetails(name = " ", onNameChange = {},
                date = " ", onDateChange = {},
                startTime = " ", onStartTimeChange = {},
                endTime = " ", onEndTimeChange = {},
                totalOfPerson = " ", onPersonChange = {})
        }
    }
}

@Composable
fun ApplyDetails(name:String, onNameChange: (String) -> Unit,
                 date:String, onDateChange: (String) -> Unit,
                 startTime:String, onStartTimeChange: (String) -> Unit,
                 endTime:String, onEndTimeChange: (String) -> Unit,
                 totalOfPerson:String, onPersonChange: (String) -> Unit) {

    val cyanInTitle = Color(0xFF00cccc)
    val cyanInBackground = Color(0xFF00ffff)
    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "Name",
                modifier = Modifier
                    .padding(start = 40.dp, top = 20.dp),
                color = cyanInTitle
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center

        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier
                    .padding(top = 10.dp),
                placeholder = { Text("Enter Name") }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "Date Of Apply",
                modifier = Modifier
                    .padding(start = 40.dp, top = 8.dp),
                color = cyanInTitle
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center

        ) {
            OutlinedTextField(
                value = date,
                onValueChange = onDateChange,
                modifier = Modifier
                    .padding(10.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "Start Time",
                modifier = Modifier
                    .padding(start = 40.dp, top = 8.dp),
                color = cyanInTitle
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center

        ) {
            OutlinedTextField(
                value = startTime,
                onValueChange = onStartTimeChange,
                modifier = Modifier
                    .padding(10.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "End Time",
                modifier = Modifier
                    .padding(start = 40.dp, top = 8.dp),
                color = cyanInTitle
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center

        ) {
            OutlinedTextField(
                value = endTime,
                onValueChange = onEndTimeChange,
                modifier = Modifier
                    .padding(10.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "Total Of Person",
                modifier = Modifier
                    .padding(start = 40.dp, top = 8.dp),
                color = cyanInTitle
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center

        ) {
            OutlinedTextField(
                value = totalOfPerson,
                onValueChange = onPersonChange,
                modifier = Modifier
                    .padding(10.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    modifier = Modifier
                        .padding(bottom = 20.dp, end = 40.dp),
                    onClick = { /*TODO*/ }
                ) {
                    Text("Submit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp)
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun RoomDetailPreview() {
    RoomDetail(navController = rememberNavController(), meetingRoomId = " ")
}