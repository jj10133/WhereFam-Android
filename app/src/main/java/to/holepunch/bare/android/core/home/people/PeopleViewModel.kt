package to.holepunch.bare.android.core.home.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import to.holepunch.bare.android.data.UserRepository

class PeopleViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _peopleList = MutableStateFlow<List<Person>>(emptyList())
    val peopleList: StateFlow<List<Person>> = _peopleList

    init {
        viewModelScope.launch {
            userRepository.locationUpdates.collectLatest { locations ->
                _peopleList.update { currentPeople ->
                    val updatedPeople = currentPeople.toMutableList()
                    locations.forEach { locationData ->
                        val existingPersonIndex = updatedPeople.indexOfFirst { it.id == locationData.id }
                        if (existingPersonIndex != -1) {
                            // Update existing person's name (and maybe other properties from locationData)
                            val existingPerson = updatedPeople[existingPersonIndex]
                            if (existingPerson.name != locationData.name) {
                                updatedPeople[existingPersonIndex] = existingPerson.copy(name = locationData.name)
                            }
                        } else {
                            updatedPeople.add(
                                Person(
                                    id = locationData.id,
                                    name = locationData.name,
                                    isOptionRevealed = false
                                )
                            )
                        }
                    }
                    updatedPeople.toList()
                }
            }
        }
    }

    fun addPerson(person: Person) {
        _peopleList.update { it + person }
    }

    fun removePerson(id: String) {
        _peopleList.update { currentList -> currentList.filterNot { it.id == id } }
    }

    suspend fun joinPeer(key: String) {
        userRepository.joinPeer(key)
    }

}