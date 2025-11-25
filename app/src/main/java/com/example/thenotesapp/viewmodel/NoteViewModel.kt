package com.example.thenotesapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.model.User
import com.example.thenotesapp.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(
    app: Application,
    private val noteRepository: NoteRepository
) : AndroidViewModel(app) {

    // --- FITUR CATATAN (CRUD) ---

    fun addNote(note: Note) = viewModelScope.launch {
        noteRepository.insertNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteRepository.deleteNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        noteRepository.updateNote(note)
    }

    // PERBAIKAN: Parameter userId sekarang String
    fun getAllNotes(userId: String): LiveData<List<Note>> {
        return noteRepository.getAllNotes(userId)
    }

    // PERBAIKAN: Parameter id sekarang String
    fun getNoteById(id: String): LiveData<Note> {
        return noteRepository.getNoteById(id)
    }

    // PERBAIKAN: Parameter userId sekarang String
    fun searchNote(query: String?, userId: String): LiveData<List<Note>> {
        return noteRepository.searchNote(query, userId)
    }

    // --- FITUR AUTH (LOGIN & REGISTER) ---

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> = _loginResult

    fun register(user: User) = viewModelScope.launch {
        noteRepository.registerUser(user)
    }

    fun login(username: String, password: String) = viewModelScope.launch {
        val user = noteRepository.login(username, password)
        _loginResult.postValue(user)
    }

    fun logout() {
        _loginResult.value = null
    }
}