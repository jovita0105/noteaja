package com.example.thenotesapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(

    val id: String = "",
    val noteTitle: String = "",
    val noteDesc: String = "",
    val userId: String = ""
) : Parcelable