package com.dezeta.guessit.ui.friend

import com.dezeta.guessit.domain.Repository.Resource

sealed class FriendState {
    data object AddFriend : FriendState()
}