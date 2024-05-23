package com.dezeta.guessit.ui.menu

import android.view.View
import android.widget.ImageView
import com.google.firebase.firestore.auth.User

sealed class ExtraState {
    data object refreshUserList : ExtraState()
    data class refreshUserProfile(val view: ImageView,val url:String) : ExtraState()
}