package com.wherefam.android.core.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wherefam.android.data.local.DataStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    fun saveOnboardingState(completed: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            dataStoreRepository.saveOnboardingState(completed)
        }
    }
}