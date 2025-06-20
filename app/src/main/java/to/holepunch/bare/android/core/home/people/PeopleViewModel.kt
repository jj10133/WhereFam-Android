package to.holepunch.bare.android.core.home.people

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PeopleViewModel : ViewModel() {
    private val _peopleList = MutableStateFlow<List<Person>>(emptyList())
    val peopleList: StateFlow<List<Person>> = _peopleList

    fun addPerson(person: Person) {
        _peopleList.value = _peopleList.value + person
    }

    fun removePerson(id: Int) {
        _peopleList.update { currentList -> currentList.filterNot { it.id == id } }
    }

    fun updatePerson(updatedPerson: Person) {
        _peopleList.update { currentList ->
            currentList.map { person ->
                if (person.id == updatedPerson.id) updatedPerson else person
            }
        }
    }
}