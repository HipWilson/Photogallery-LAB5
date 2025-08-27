package com.wil.photogallery

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Data class para representar una foto
data class Photo(
    val uri: Uri,
    val name: String
)

// ViewModel para gestionar el estado de las fotos
class PhotoGalleryViewModel : ViewModel() {
    // StateFlow privado para las fotos
    private val _photos = MutableStateFlow<List<Photo>>(emptyList())

    // StateFlow público de solo lectura
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()

    // Función para añadir una foto desde el photo picker
    fun addPhoto(uri: Uri, context: Context) {
        // Obtener el nombre de la imagen desde la URI
        val name = getImageName(uri, context)
        val newPhoto = Photo(uri, name)

        // Añadir la nueva foto a la lista existente
        _photos.value = _photos.value + newPhoto
    }

    // Función para añadir una foto desde la cámara
    fun addCameraPhoto(uri: Uri) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val name = "Foto_$timestamp.jpg"
        val newPhoto = Photo(uri, name)

        _photos.value = _photos.value + newPhoto
    }

    // Función auxiliar para obtener el nombre de la imagen
    private fun getImageName(uri: Uri, context: Context): String {
        // Intentar obtener el nombre real del archivo
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex != -1) {
                it.getString(nameIndex) ?: "Imagen_${System.currentTimeMillis()}"
            } else {
                "Imagen_${System.currentTimeMillis()}"
            }
        } ?: "Imagen_${System.currentTimeMillis()}"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Usamos MaterialTheme directamente
            MaterialTheme(
                colorScheme = lightColorScheme(),
                typography = Typography(),
                content = {
                    PhotoGalleryApp()
                }
            )
        }
    }
}

// Definición básica de Typography
val Typography = androidx.compose.material3.Typography()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryApp(viewModel: PhotoGalleryViewModel = viewModel()) {
    // Obtener el contexto actual
    val context = LocalContext.current

    // Observar la lista de fotos usando collectAsStateWithLifecycle
    val photos by viewModel.photos.collectAsStateWithLifecycle()

    // State para controlar si mostrar el menú de opciones
    var showOptionsDialog by remember { mutableStateOf(false) }

    // Crear archivo temporal para la cámara
    val photoFile = remember {
        File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
    }

    val photoUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }

    // Launcher para el photo picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        // Si se seleccionó una imagen, añadirla al ViewModel
        uri?.let { selectedUri ->
            viewModel.addPhoto(selectedUri, context)
        }
    }

    // Launcher para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.addCameraPhoto(photoUri)
        }
    }

    // Scaffold con barra superior y botón flotante
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fotos",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showOptionsDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Añadir foto"
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Mostrar la cuadrícula de fotos o mensaje si está vacía
            if (photos.isEmpty()) {
                // Mensaje cuando no hay fotos
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay fotos.\nPresiona + para añadir una imagen",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Cuadrícula de fotos usando LazyVerticalGrid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(photos) { photo ->
                        PhotoItem(photo = photo)
                    }
                }
            }
        }
    }

    // Dialog para seleccionar entre galería y cámara
    if (showOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showOptionsDialog = false },
            title = {
                Text(
                    text = "Añadir foto",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Selecciona de dónde quieres obtener la foto",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Row {
                    TextButton(
                        onClick = {
                            showOptionsDialog = false
                            // Usar PickVisualMediaRequest con ImageOnly
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Text(
                            text = "Galería",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón de cámara SIN ICONO para evitar errores
                    TextButton(
                        onClick = {
                            showOptionsDialog = false
                            cameraLauncher.launch(photoUri)
                        }
                    ) {
                        Text(
                            text = "Cámara",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showOptionsDialog = false }
                ) {
                    Text(
                        text = "Cancelar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }
}

@Composable
fun PhotoItem(photo: Photo) {
    // Card que contiene la imagen y el nombre
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f), // Mantener aspecto cuadrado
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen usando AsyncImage de Coil
            AsyncImage(
                model = photo.uri,
                contentDescription = photo.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            // Nombre de la foto
            Text(
                text = photo.name,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}