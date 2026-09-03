package com.example.crudpractica1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crudpractica1.Data.Equipo
import com.example.crudpractica1.Data.RetrofitClient
import com.example.crudpractica1.ui.theme.CrudPractica1Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrudPractica1Theme {
                // Estados para los campos y la lista
                var listaEquipos by remember { mutableStateOf(listOf<Equipo>()) }
                var nombre by remember { mutableStateOf("") }
                var pais by remember { mutableStateOf("") }
                var idEditando by remember { mutableStateOf<String?>(null) }

                val scope = rememberCoroutineScope()

                // Instancia limpia usando el objeto RetrofitClient
                val api = remember { RetrofitClient.apiService }

                // Función para cargar los equipos desde MockAPI
                fun cargarDatos() {
                    scope.launch {
                        try {
                            listaEquipos = api.obtenerEquipos()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                // Cargar datos al iniciar la app
                LaunchedEffect(Unit) { cargarDatos() }

                Scaffold(
                    topBar = {
                        @OptIn(ExperimentalMaterial3Api::class)
                        TopAppBar(title = { Text("CRUD Equipos - MockAPI") })
                    },
                    // Botón flotante para limpiar y preparar un nuevo registro
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                nombre = ""
                                pais = ""
                                idEditando = null
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Nuevo")
                        }
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Campos de texto
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre del Equipo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = pais,
                            onValueChange = { pais = it },
                            label = { Text("País") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Botón Guardar / Actualizar
                        Button(
                            onClick = {
                                if (nombre.isBlank() || pais.isBlank()) return@Button
                                scope.launch {
                                    try {
                                        val equipo = Equipo(nombre = nombre, pais = pais)
                                        if (idEditando == null) {
                                            api.crearEquipo(equipo) // CREATE (POST)
                                        } else {
                                            api.actualizarEquipo(idEditando!!, equipo) // UPDATE (PUT)
                                        }
                                        // Limpiar formulario y recargar lista
                                        nombre = ""
                                        pais = ""
                                        idEditando = null
                                        cargarDatos()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (idEditando == null) "Guardar" else "Actualizar Cambios")
                        }

                        // Botón cancelar si está editando
                        if (idEditando != null) {
                            TextButton(
                                onClick = {
                                    nombre = ""
                                    pais = ""
                                    idEditando = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cancelar")
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Lista de Registros", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Lista visual (READ & DELETE)
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(listaEquipos) { equipo ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = equipo.nombre, style = MaterialTheme.typography.titleMedium)
                                            Text(text = equipo.pais, style = MaterialTheme.typography.bodyMedium)
                                        }
                                        Row {
                                            // Botón Editar (Carga los datos arriba)
                                            IconButton(onClick = {
                                                nombre = equipo.nombre
                                                pais = equipo.pais
                                                idEditando = equipo.id
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                                            }
                                            // Botón Eliminar
                                            IconButton(onClick = {
                                                scope.launch {
                                                    try {
                                                        equipo.id?.let { api.eliminarEquipo(it) }
                                                        cargarDatos()
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }
                                            }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}