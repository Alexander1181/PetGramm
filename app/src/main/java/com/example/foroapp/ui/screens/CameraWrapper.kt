package com.example.foroapp.ui.screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.foroapp.ui.navigation.Screen
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CameraWrapperScreen(navController: NavController) {
    val context = LocalContext.current
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Create a temporary file for the camera image
    val photoFile = remember {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        File.createTempFile("JPEG_${timeStamp}_", ".jpg", context.cacheDir)
    }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val uriString = Uri.fromFile(photoFile).toString()
            navController.currentBackStackEntry?.savedStateHandle?.set("imageUri", uriString)
            navController.navigate(Screen.CreatePost.route)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            navController.currentBackStackEntry?.savedStateHandle?.set("imageUri", it.toString())
            navController.navigate(Screen.CreatePost.route)
        }
    }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
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
            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
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
