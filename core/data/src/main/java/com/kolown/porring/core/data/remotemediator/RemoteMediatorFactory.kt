package com.kolown.porring.core.data.remotemediator

import com.kolown.porring.core.model.PageState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import kotlinx.coroutines.flow.StateFlow

@AssistedFactory
interface UserDetailRemoteMediatorFactory {
    fun create(
        @Assisted("author_id") authorId: String,
        @Assisted("post_id") postId: String?,
        @Assisted pageState: StateFlow<PageState>
    ): UserDetailPostRemoteMediator
}

@AssistedFactory
interface RandomPostRemoteMediatorFactory {
    fun create(
        @Assisted pageState: StateFlow<PageState>
    ): RandomDetailPostRemoteMediator
}

@AssistedFactory
interface UserGalleryPostRemoteMediatorFactory {
    fun create(
        @Assisted("author_id") authorId: String,
    ): UserGalleryPostRemoteMediator
}