package com.wherefam.android.core.home.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wherefam.android.data.PeerRepository
import com.wherefam.android.data.UserRepository
import com.wherefam.android.data.local.Peer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PeopleViewModel(private val userRepository: UserRepository, private val peerRepository: PeerRepository) :
    ViewModel() {
    private val _peopleList = MutableStateFlow<List<Peer>>(emptyList())
    val peopleList: StateFlow<List<Peer>> = _peopleList

    init {
        viewModelScope.launch {
            peerRepository.getAllPeers().collectLatest { peers ->
                _peopleList.value = peers
            }
        }
    }

    fun addPerson(peer: Peer) {
        viewModelScope.launch {
            peerRepository.upsert(peer)
        }
    }

    fun removePerson(id: String) {
        viewModelScope.launch {
            val peer = _peopleList.value.find { it.id == id }
            peer?.let { peerRepository.delete(it) }
        }
    }

    suspend fun joinPeer(key: String) {
        userRepository.joinPeer(key)
    }

}