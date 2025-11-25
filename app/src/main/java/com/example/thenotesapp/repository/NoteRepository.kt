package com.example.thenotesapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

class NoteRepository {
    private val database = FirebaseDatabase.getInstance("https://the-note-app-c99e4-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private val usersRef = database.getReference("users")
    private val notesRef = database.getReference("notes")

    suspend fun registerUser(user: User) {
        val id = usersRef.push().key ?: return
        val newUser = user.copy(id = id)
        usersRef.child(id).setValue(newUser).await()
    }
    suspend fun login(username: String, password: String): User? {
        val snapshot = usersRef.orderByChild("username").equalTo(username).get().await()

        for (child in snapshot.children) {
            val user = child.getValue(User::class.java)
            if (user != null && user.password == password) {
                return user
            }
        }
        return null
    }

    suspend fun checkUsername(username: String): User? {
        val snapshot = usersRef.orderByChild("username").equalTo(username).get().await()
        if (snapshot.exists()) {
            return snapshot.children.first().getValue(User::class.java)
        }
        return null
    }

    suspend fun insertNote(note: Note) {
        val id = notesRef.push().key ?: return
        val newNote = note.copy(id = id)
        notesRef.child(id).setValue(newNote).await()
    }

    suspend fun updateNote(note: Note) {
        notesRef.child(note.id).setValue(note).await()
    }

    suspend fun deleteNote(note: Note) {
        notesRef.child(note.id).removeValue().await()
    }

    fun getAllNotes(userId: String): LiveData<List<Note>> {
        val liveData = MutableLiveData<List<Note>>()

        notesRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notes = mutableListOf<Note>()
                    for (item in snapshot.children) {
                        val note = item.getValue(Note::class.java)
                        if (note != null) {
                            notes.add(note)
                        }
                    }
                    liveData.value = notes
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        return liveData
    }

    fun getNoteById(id: String): LiveData<Note> {
        val liveData = MutableLiveData<Note>()

        notesRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val note = snapshot.getValue(Note::class.java)
                liveData.value = note
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return liveData
    }

    fun searchNote(query: String?, userId: String): LiveData<List<Note>> {
        val liveData = MutableLiveData<List<Note>>()

        notesRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notes = mutableListOf<Note>()
                    for (item in snapshot.children) {
                        val note = item.getValue(Note::class.java)
                        if (note != null && query != null) {
                            if (note.noteTitle.contains(query, true) ||
                                note.noteDesc.contains(query, true)) {
                                notes.add(note)
                            }
                        }
                    }
                    liveData.value = notes
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        return liveData
    }
}