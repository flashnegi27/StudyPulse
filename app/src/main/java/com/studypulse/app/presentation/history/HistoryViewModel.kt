package com.studypulse.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.domain.usecase.DeleteSessionUseCase
import com.studypulse.app.domain.usecase.GetSessionHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getSessionHistory: GetSessionHistoryUseCase,
    private val deleteSession: DeleteSessionUseCase
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = getSessionHistory()
        .map { HistoryUiState(sessions = it) }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryUiState(isLoading = true)
        )

    fun deleteSession(id: Long) {
        viewModelScope.launch { deleteSession.invoke(id) }
    }
}
