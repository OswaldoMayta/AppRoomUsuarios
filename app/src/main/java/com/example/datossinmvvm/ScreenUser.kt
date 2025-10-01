package com.example.datossinmvvm

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUser() {
    val context = LocalContext.current
    val db = UserDatabase.getDatabase(context)
    val dao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    val dataUser = remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión Usuarios") },
                actions = {
                    // Agregar
                    TextButton(onClick = {
                        // Ejecutar la inserción
                        val user = User(firstName = firstName.ifBlank { null }, lastName = lastName.ifBlank { null })
                        coroutineScope.launch {
                            try {
                                val rowId = dao.insert(user)
                                id = rowId.toString()
                                firstName = ""
                                lastName = ""
                                snackbarHostState.showSnackbar("Usuario agregado (id=$rowId)")
                            } catch (e: Exception) {
                                Log.e("ScreenUser", "Error insert: ${e.message}")
                                snackbarHostState.showSnackbar("Error al agregar usuario")
                            }
                        }
                    }) {
                        Text("Agregar")
                    }

                    // Listar
                    TextButton(onClick = {
                        coroutineScope.launch {
                            try {
                                val users = dao.getAll()
                                val sb = StringBuilder()
                                users.forEach { u ->
                                    sb.append("${u.uid} - ${u.firstName ?: ""} ${u.lastName ?: ""}\n")
                                }
                                dataUser.value = sb.toString()
                                snackbarHostState.showSnackbar("Se listaron ${users.size} usuarios")
                            } catch (e: Exception) {
                                Log.e("ScreenUser", "Error getAll: ${e.message}")
                                snackbarHostState.showSnackbar("Error al listar usuarios")
                            }
                        }
                    }) {
                        Text("Listar")
                    }

                    // Eliminar último
                    TextButton(onClick = {
                        coroutineScope.launch {
                            try {
                                val last = dao.getLastUser()
                                if (last == null) {
                                    snackbarHostState.showSnackbar("No hay usuarios para eliminar")
                                } else {
                                    val deletedRows = dao.deleteById(last.uid)
                                    // actualizar la lista visible
                                    val users = dao.getAll()
                                    val sb = StringBuilder()
                                    users.forEach { u ->
                                        sb.append("${u.uid} - ${u.firstName ?: ""} ${u.lastName ?: ""}\n")
                                    }
                                    dataUser.value = sb.toString()
                                    id = "" // limpiamos id si era el último eliminado
                                    snackbarHostState.showSnackbar("Eliminado uid=${last.uid} (filas: $deletedRows)")
                                }
                            } catch (e: Exception) {
                                Log.e("ScreenUser", "Error delete: ${e.message}")
                                snackbarHostState.showSnackbar("Error al eliminar usuario")
                            }
                        }
                    }) {
                        Text("Eliminar último")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ID (solo lectura)
            OutlinedTextField(
                value = id,
                onValueChange = { /* readOnly */ },
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

            Spacer(Modifier.height(16.dp))

            Text(text = "Usuarios guardados:", fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text(text = dataUser.value, fontSize = 14.sp)
        }
    }
}
