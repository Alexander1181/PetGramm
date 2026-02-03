package com.example.foroapp.ui.screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.foroapp.navigation.Route
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CameraWrapperScreen(navController: NavController) {
    val context = LocalContext.current

    // Lanzador para la actividad de recorte
    val cropLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract(),
    ) { result ->
        if (result.isSuccessful) {
            // La imagen recortada está disponible en result.uriContent
            val croppedUriString = result.uriContent.toString()
            navController.currentBackStackEntry?.savedStateHandle?.set("imageUris", listOf(croppedUriString))
            navController.navigate(Route.CreatePost.path)
        } else {
            // Manejar error de recorte
            val exception = result.error
            Toast.makeText(context, "Error al recortar: ${exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // --- OPCIONES DE RECORTE --- 
    val cropOptions = CropImageOptions(
        fixAspectRatio = true,
        aspectRatioX = 1,
        aspectRatioY = 1
    )

    // Crear un archivo temporal para la imagen de la cámara
    val photoFile = remember {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        File.createTempFile("JPEG_${timeStamp}_", ".jpg", context.cacheDir)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val uri = Uri.fromFile(photoFile)
            val contractOptions = CropImageContractOptions(uri, cropOptions)
            cropLauncher.launch(contractOptions)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            // Función auxiliar para copiar URI a un archivo temporal
            fun copyToTempFile(uri: Uri): Uri? {
                return try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val outputFile = File.createTempFile("TEMP_GALLERY_${System.currentTimeMillis()}_", ".jpg", context.cacheDir)
                    inputStream?.use { input ->
                        outputFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    Uri.fromFile(outputFile)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            if (uris.size == 1) {
                // Imagen única: Copiar a archivo temporal -> Recortar
                val tempUri = copyToTempFile(uris.first())
                if (tempUri != null) {
                    val contractOptions = CropImageContractOptions(tempUri, cropOptions)
                    cropLauncher.launch(contractOptions)
                } else {
                    Toast.makeText(context, "Error al procesar la imagen de la galería", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Múltiples imágenes: Copiar todas a archivos temporales -> Navegar
                val tempUris = uris.mapNotNull { copyToTempFile(it) }
                if (tempUris.isNotEmpty()) {
                    val uriStrings = tempUris.map { it.toString() }
                    navController.currentBackStackEntry?.savedStateHandle?.set("imageUris", uriStrings)
                    navController.navigate(Route.CreatePost.path)
                } else {
                     Toast.makeText(context, "Error al procesar las imágenes", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Lanzador de permisos para la cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val authority = "${context.packageName}.fileprovider"
                val uri = FileProvider.getUriForFile(context, authority, photoFile)
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(context, "Error cámara: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Permiso de cámara requerido", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("📸 Tomar Foto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { galleryLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("🖼️ Elegir de Galería")
        }
    }
}
