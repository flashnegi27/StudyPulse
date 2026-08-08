package com.studypulse.app.presentation.scanner

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor() : ViewModel() {

    sealed class ScanState {
        object Scanning : ScanState()
        data class Valid(val locationId: String) : ScanState()
    }

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Scanning)
    val scanState = _scanState.asStateFlow()

    fun onBarcodeDetected(rawValue: String) {
        if (_scanState.value is ScanState.Valid) return
        val locationId = parseLocationId(rawValue) ?: return
        _scanState.value = ScanState.Valid(locationId)
    }

    fun resetState() {
        _scanState.value = ScanState.Scanning
    }

    private fun parseLocationId(raw: String): String? {
        return try {
            JSONObject(raw).getString("location_id")
        } catch (e: JSONException) {
            raw.trim().takeIf { it.isNotBlank() }
        }
    }
}
