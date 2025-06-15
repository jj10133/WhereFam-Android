package to.holepunch.bare.android.core.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import to.holepunch.bare.android.data.local.DataStoreRepository

class OnboardingViewModel(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    fun saveOnboardingState(completed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveOnboardingState(completed)
        }
    }
}