package com.example.datossinmvvm

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun ScreenUser() {
    val context = LocalContext.current
    val db = UserDatabase.getDatabase(context)          // singleton
    val dao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    val dataUser = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val user = User(firstName = firstName.ifBlank { null }, lastName = lastName.ifBlank { null })
                coroutineScope.launch {
                    try {
                        val rowId = dao.insert(user)     // Long
                        id = rowId.toString()            // mostramos el id insertado
                    } catch (e: Exception) {
                        Log.e("ScreenUser", "Error insert: ${e.message}")
                    }
                }
                firstName = ""
                lastName = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Usuario", fontSize = 16.sp)
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val users = dao.getAll()
                        val sb = StringBuilder()
                        users.forEach { u ->
                            sb.append("${u.uid} - ${u.firstName ?: ""} ${u.lastName ?: ""}\n")
                        }
                        dataUser.value = sb.toString()
                    } catch (e: Exception) {
                        Log.e("ScreenUser", "Error getAll: ${e.message}")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Listar Usuarios", fontSize = 16.sp)
        }

        Spacer(Modifier.height(16.dp))

        Text(text = dataUser.value, fontSize = 16.sp)
    }
}
