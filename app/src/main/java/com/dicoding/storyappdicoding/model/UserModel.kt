package com.dicoding.storyappdicoding.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var userId: String? = null,
    var name: String? = null,
    var token: String? = null,
    var isLogin: Boolean = false
): Parcelable