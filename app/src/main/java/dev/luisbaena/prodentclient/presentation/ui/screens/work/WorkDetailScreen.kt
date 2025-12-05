package dev.luisbaena.prodentclient.presentation.ui.screens.work

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkChangeStatusDTO
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkImageDto
import dev.luisbaena.prodentclient.domain.model.WorkStatus
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.ImageUploadProgressDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.text.style.TextOverflow
import java.util.Locale
import dev.luisbaena.prodentclient.presentation.utils.ApiConfig
import dev.luisbaena.prodentclient.presentation.utils.uploadImageToWork
import dev.luisbaena.prodentclient.data.local.preferencias.UserPreferences
import dev.luisbaena.prodentclient.domain.model.WorkTypeCategory
import dev.luisbaena.prodentclient.presentation.ui.components.common.WorkDetailContent
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.AddImageDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.CancelWorkDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.DeleteImageConfirmDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.ImageViewerDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.WorkChangeStatusDialog
import dev.luisbaena.prodentclient.presentation.utils.formatDate
import dev.luisbaena.prodentclient.presentation.viewmodel.AuthViewModel
import dev.luisbaena.prodentclient.presentation.viewmodel.WorkViewModel
import kotlinx.coroutines.runBlocking

/**
 * Pantalla de detalle de trabajo
 *
 * Formado por los siguientes componentes
 * - Cabecera con título y botón de menú
 * - Contenido principal con detalles del trabajo
 * - Diálogos para cambiar estado, agregar imagen, ver imagen, eliminar imagen y cancelar trabajo
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkDetailScreen(
    workId: String,
    onOpenDrawer: () -> Unit,
    navController: NavHostController,
    viewModel: WorkViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsState()
    val statusChangeState by viewModel.statusChangeState.collectAsState()
    val imageUploadState by viewModel.imageUploadState.collectAsState()
    val imageDeleteState by viewModel.imageDeleteState.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()

    // Verificar si el usuario es administrador
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Estados de diálogos
    var showImageUploadProgressDialog by rememberSaveable { mutableStateOf(false) }
    var showAddImageDialog by rememberSaveable { mutableStateOf(false) }
    var showChangeStatusDialog by rememberSaveable { mutableStateOf(false) }
    var showImageViewer by remember { mutableStateOf<WorkImageDto?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<WorkImageDto?>(null) }
    var showCancelWorkDialog by rememberSaveable { mutableStateOf(false) }

    // Cargar trabajo al entrar
    LaunchedEffect(workId) {
        viewModel.loadWorkById(workId)
    }

    // Manejar éxito de cambio de estado
    LaunchedEffect(statusChangeState.isSuccess) {
        if (statusChangeState.isSuccess) {
            viewModel.resetStatusChangeState()
        }
    }

    // Manejar éxito de subida de imagen
    LaunchedEffect(imageUploadState.isSuccess) {
        if (imageUploadState.isSuccess) {
            showAddImageDialog = false
            showImageUploadProgressDialog = false
        }
    }

    // Manejar cuando empieza la carga de imagen
    LaunchedEffect(imageUploadState.isLoading) {
        showImageUploadProgressDialog = imageUploadState.isLoading
    }

    // Mostrar error de subida de imagen
    LaunchedEffect(imageUploadState.error) {
        imageUploadState.error?.let { error ->
            showImageUploadProgressDialog = false
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
        }
    }

    // Mostrar error de eliminación de imagen
    LaunchedEffect(imageDeleteState.error) {
        imageDeleteState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
        }
    }

    // Manejar éxito de eliminación de imagen
    LaunchedEffect(imageDeleteState.isSuccess) {
        if (imageDeleteState.isSuccess) {
            showDeleteConfirmDialog = null
            viewModel.resetImageDeleteState()
        }
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Detalle del Trabajo",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                // LOADING
                detailState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // ERROR
                detailState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = detailState.error!!,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(onClick = { viewModel.loadWorkById(workId) }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                // CONTENIDO
                detailState.work != null -> {
                    WorkDetailContent(
                        work = detailState.work!!,
                        onChangeStatus = { showChangeStatusDialog = true },
                        onAddImage = { showAddImageDialog = true },
                        onImageClick = { showImageViewer = it },
                        onDeleteImage = { showDeleteConfirmDialog = it },
                        onCallClinic = { phone ->
                            val intent = Intent(Intent.ACTION_DIAL, "tel:$phone".toUri())
                            context.startActivity(intent)
                        },
                        navController = navController,
                    )
                }
            }
        }

        // DIÁLOGOS
        if (showChangeStatusDialog && detailState.work != null) {
            WorkChangeStatusDialog(
                currentStatus = detailState.work!!.estado,
                onDismiss = { showChangeStatusDialog = false },
                onConfirm = { newStatus, observaciones ->
                    viewModel.changeStatus(
                        id = workId,
                        statusChange = WorkChangeStatusDTO(
                            nuevoEstado = newStatus,
                            observaciones = observaciones
                        ),
                        onSuccess = {
                            showChangeStatusDialog = false
                        }
                    )
                }
            )
        }

        if (showAddImageDialog) {
            AddImageDialog(
                onDismiss = { showAddImageDialog = false },
                onConfirm = { uri, tipo, desc ->
                    // Usar uploadImageToWork que maneja FileUtils, compresión y la subida
                    showImageUploadProgressDialog = true
                    showAddImageDialog = false
                    uploadImageToWork(
                        context = context,
                        workId = workId,
                        imageUri = uri,
                        imageType = tipo,
                        description = desc,
                        viewModel = viewModel
                    )
                }
            )
        }

        if (showImageViewer != null) {
            ImageViewerDialog(
                image = showImageViewer!!,
                onDismiss = { showImageViewer = null }
            )
        }

        if (showDeleteConfirmDialog != null) {
            DeleteImageConfirmDialog(
                image = showDeleteConfirmDialog!!,
                onDismiss = { showDeleteConfirmDialog = null },
                onConfirm = {
                    viewModel.deleteImage(
                        workId = workId,
                        imageId = showDeleteConfirmDialog!!.id,
                        onSuccess = {
                            showDeleteConfirmDialog = null
                        }
                    )
                }
            )
        }

        if (showCancelWorkDialog) {
            CancelWorkDialog(
                onDismiss = { showCancelWorkDialog = false },
                onConfirm = { observaciones ->
                    viewModel.changeStatus(
                        id = workId,
                        statusChange = WorkChangeStatusDTO(
                            nuevoEstado = WorkStatus.CANCELLED.value,
                            observaciones = observaciones
                        ),
                        onSuccess = {
                            showCancelWorkDialog = false
                            navController.navigateUp()
                        }
                    )
                }
            )
        }

        // Diálogo de progreso de subida de imagen
        if (showImageUploadProgressDialog) {
            ImageUploadProgressDialog(
                uploadedCount = if (imageUploadState.isSuccess) 1 else 0,
                totalCount = 1,
                error = imageUploadState.error,
                onDismiss = {
                    showImageUploadProgressDialog = false
                }
            )
        }
    }
}

/**
 * Sección de galería de imágenes del trabajo
 * con botón para agregar nuevas imágenes
 * y miniaturas de imágenes existentes
 */

@Composable
fun ImageGallerySection(
    images: List<WorkImageDto>,
    onAddImage: () -> Unit,
    onImageClick: (WorkImageDto) -> Unit,
    onDeleteImage: (WorkImageDto) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Imágenes (${images.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onAddImage) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Agregar imagen",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (images.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No hay imágenes asociadas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(images) { image ->
                        ImageThumbnail(
                            image = image,
                            onClick = { onImageClick(image) },
                            onDelete = { onDeleteImage(image) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Componente miniatura de imagen con botón eliminar y badge de tipo
 * para mostrar en la galería de imágenes
 */
@Composable
fun ImageThumbnail(
    image: WorkImageDto,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Box {
        Card(
            modifier = Modifier
                .size(120.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            // Construir URL absoluta y añadir Authorization para Coil
            val context = LocalContext.current
            val token = remember {
                runBlocking {
                    try {
                        UserPreferences(context).getUserToken()
                    } catch (_: Exception) {
                        null
                    }
                }
            }
            val fullUrl = remember(image.urlDescarga) {
                ApiConfig.absoluteUrl(image.urlDescarga)
            }
            val request = ImageRequest.Builder(context)
                .data(fullUrl)
                .apply {
                    if (!token.isNullOrBlank()) {
                        addHeader("Authorization", "Bearer $token")
                    }
                }
                .crossfade(true)
                .build()

            AsyncImage(
                model = request,
                contentDescription = image.descripcion,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Botón eliminar
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(32.dp)
                .background(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(bottomStart = 8.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }

        // Badge de tipo
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = image.tipoImagen,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Componente fila de detalle con icono, etiqueta y valor
 * para mostrar en secciones de detalle
 */
@Composable
fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}