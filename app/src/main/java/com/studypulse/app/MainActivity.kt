package com.studypulse.app

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.studypulse.app.data.datastore.SessionStateDataStore
import com.studypulse.app.domain.model.SessionState
import com.studypulse.app.navigation.StudyPulseNavGraph
import com.studypulse.app.presentation.MainViewModel
import com.studypulse.app.service.StudyTrackingService
import com.studypulse.app.ui.theme.StudyPulseTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject lateinit var sessionStateDataStore: SessionStateDataStore

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        recoverSessionIfNeeded()

        setContent {
            StudyPulseTheme {
                val navController = rememberNavController()
                StudyPulseNavGraph(
                    navController  = navController,
                    onTapTriggered = mainViewModel::handleTap
                )
            }
        }

        handleNfcIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(
            this,
            android.app.PendingIntent.getActivity(
                this, 0,
                Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            ),
            arrayOf(
                android.content.IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
                    try { addDataType("text/plain") } catch (e: Exception) { /* ignore */ }
                }
            ),
            null
        )
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNfcIntent(intent)
    }

    private fun handleNfcIntent(intent: Intent) {
        val nfcActions = listOf(
            NfcAdapter.ACTION_NDEF_DISCOVERED,
            NfcAdapter.ACTION_TECH_DISCOVERED,
            NfcAdapter.ACTION_TAG_DISCOVERED
        )
        if (intent.action !in nfcActions) return

        @Suppress("DEPRECATION")
        val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        rawMessages
            ?.filterIsInstance<NdefMessage>()
            ?.firstOrNull()
            ?.records
            ?.firstOrNull()
            ?.let { record ->
                parseNdefRecord(record)?.let { locationId ->
                    mainViewModel.handleTap(locationId)
                }
            }
    }

    private fun parseNdefRecord(record: NdefRecord): String? {
        if (record.tnf != NdefRecord.TNF_WELL_KNOWN) return null
        val payload    = record.payload
        val langLength = payload[0].and(0x3F).toInt()
        if (payload.size <= 1 + langLength) return null
        val text = String(payload, 1 + langLength, payload.size - 1 - langLength, Charsets.UTF_8)
        return try {
            JSONObject(text).getString("location_id")
        } catch (e: JSONException) {
            text.trim().takeIf { it.isNotBlank() }
        }
    }

    private fun recoverSessionIfNeeded() {
        lifecycleScope.launch {
            val state = sessionStateDataStore.getSessionState().first()
            if (state is SessionState.Studying) {
                startForegroundService(
                    StudyTrackingService.startIntent(this@MainActivity, state.locationId, state.startTime)
                )
            }
        }
    }
}
