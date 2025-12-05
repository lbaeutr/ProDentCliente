package dev.luisbaena.prodentclient.presentation.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkImageDto
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.worksections.DatesSection
import dev.luisbaena.prodentclient.presentation.ui.components.common.worksections.ObservationsSection
import dev.luisbaena.prodentclient.presentation.ui.components.common.worksections.PatientClinicSection
import dev.luisbaena.prodentclient.presentation.ui.components.common.worksections.PriceSection
import dev.luisbaena.prodentclient.presentation.ui.components.common.worksections.TechnicalDetailsSection
import dev.luisbaena.prodentclient.presentation.ui.components.common.worksections.WorkDetailHeaderCard
import dev.luisbaena.prodentclient.presentation.ui.screens.work.ImageGallerySection


   /**
     * Componente principal para mostrar el detalle completo de un trabajo
     * con los diferentes apartados: header, paciente, clínica, detalles técnicos,
     * fechas, precio, observaciones e imágenes.
     */
@Composable
fun WorkDetailContent(
    work: WorkDetailDto,
    onChangeStatus: () -> Unit,
    onAddImage: () -> Unit,
    onImageClick: (WorkImageDto) -> Unit,
    onDeleteImage: (WorkImageDto) -> Unit,
    onCallClinic: (String) -> Unit,
    navController: NavHostController,
    isAdmin: Boolean = false
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER CARD
        item {
            WorkDetailHeaderCard(work = work, onChangeStatus = onChangeStatus)
        }

        // INFORMACIÓN DEL PACIENTE Y CLÍNICA
        item {
            PatientClinicSection(work = work, onCallClinic = onCallClinic)
        }

        // DETALLES TÉCNICOS
        item {
            TechnicalDetailsSection(work = work)
        }

        // FECHAS
        item {
            DatesSection(work = work)
        }

        // PRECIO (Solo para administradores)
        if (isAdmin) {
            item {
                PriceSection(work = work)
            }
        }

        // OBSERVACIONES
        if (!work.observaciones.isNullOrBlank()) {
            item {
                ObservationsSection(observations = work.observaciones)
            }
        }

        // GALERÍA DE IMÁGENES
        item {
            ImageGallerySection(
                images = work.imagenes,
                onAddImage = onAddImage,
                onImageClick = onImageClick,
                onDeleteImage = onDeleteImage
            )
        }

        item {
            // Boton actualizar trabajo
            PrimaryLoadingButton(
                text = "Actualizar Trabajo",
                onClick = {
                    navController.navigate("work_update/${work.id}")
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Espaciado final
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}