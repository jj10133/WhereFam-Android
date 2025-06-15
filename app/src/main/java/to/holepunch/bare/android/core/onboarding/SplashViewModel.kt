package to.holepunch.bare.android.core.onboarding

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.launch
import to.holepunch.bare.android.data.local.DataStoreRepository

class SplashViewModel(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _startDestination: MutableState<String> = mutableStateOf("Onboarding")
    val startDestination: State<String> = _startDestination

    init {
        viewModelScope.launch {
            dataStoreRepository.readOnBoardingState().collect { completed ->
                if (completed) {
                    _startDestination.value = "Home"
                } else {
                    _startDestination.value = "Onboarding"
                }
            }
            _isLoading.value = false
        }
    }
}