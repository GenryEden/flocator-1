package com.example.flocator.settings

import android.graphics.Bitmap

data class Friend(
    var userId: Long,
    var icon: Bitmap?,
    var name: String,
    var isChecked: Boolean
)