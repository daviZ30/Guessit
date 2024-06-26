package com.dezeta.guessit.ui.menu

import android.view.View
import android.widget.ImageView
import com.google.firebase.firestore.auth.User

sealed class ExtraState {
    data object refreshUserList : ExtraState()
    data object Country24 : ExtraState()
    data object UserSuccess : ExtraState()
    data class refreshUserProfile(val view: ImageView,val url:String) : ExtraState()
}