package com.studypulse.app.presentation.scanner

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.studypulse.app.R
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel,
    onScanSuccess: (String) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)
    val scanState        by viewModel.scanState.collectAsStateWithLifecycle()

    LaunchedEffect(scanState) {
        if (scanState is ScannerViewModel.ScanState.Valid) {
            onScanSuccess((scanState as ScannerViewModel.ScanState.Valid).locationId)
            viewModel.resetState()
        }
    }

    if (cameraPermission.status.isGranted) {
        CameraPreview(
            onBarcodeDetected = viewModel::onBarcodeDetected,
            modifier          = modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    } else {
        Column(
            modifier            = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text  = stringResource(R.string.camera_permission_required),
                style = MaterialTheme.typography.bodyLarge
            )
            Button(
                onClick  = { cameraPermission.launchPermissionRequest() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.grant_permission))
            }
        }
    }
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
private fun CameraPreview(
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context          = LocalContext.current
    val lifecycleOwner   = LocalLifecycleOwner.current
    val cameraExecutor   = remember { Executors.newSingleThreadExecutor() }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory  = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraFuture = ProcessCameraProvider.getInstance(ctx)
                cameraFuture.addListener({
                    val cameraProvider = cameraFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(cameraExecutor, QrCodeAnalyzer(onBarcodeDetected))
                        }
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            }
        )

        Box(
            modifier         = Modifier
                .size(220.dp)
                .align(Alignment.Center)
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
        )

        Text(
            text     = stringResource(R.string.scan_qr),
            style    = MaterialTheme.typography.titleMedium,
            color    = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
        )
    }
}
