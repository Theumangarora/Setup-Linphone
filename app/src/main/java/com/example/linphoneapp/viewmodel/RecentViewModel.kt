package com.example.linphoneapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linphoneapp.LinphoneManager
import com.example.linphoneapp.data.RecentCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.linphone.core.CallLog
import javax.inject.Inject


@HiltViewModel
class RecentViewModel @Inject constructor(
    private val linphoneManager: LinphoneManager
) : ViewModel() {

    private val _groupedCalls = MutableStateFlow<List<RecentCall>>(emptyList())
    val groupedCalls: StateFlow<List<RecentCall>> = _groupedCalls

    init {
        observeAndGroupCalls()
    }
    fun makecall(number:String) = linphoneManager.makeCall(number)

    private fun observeAndGroupCalls() {
        // collect raw calls and compute grouped
        viewModelScope.launch {
            linphoneManager.recentCalls.collect { logs ->
                val grouped = logs
                    .groupBy { it.remoteAddress.username ?: "Unknown" }
                    .map { (number, calls) ->
                        RecentCall(
                            number = number,
                            count = calls.size,
                            lastTime = calls.maxOfOrNull { it.startDate } ?: 0L
                        )
                    }
                    .sortedByDescending { it.lastTime }

                _groupedCalls.value = grouped
            }
        }
    }
}
