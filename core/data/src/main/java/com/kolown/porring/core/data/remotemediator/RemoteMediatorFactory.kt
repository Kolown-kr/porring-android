package com.kolown.porring.core.data.remotemediator

import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.model.PostContentModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import kotlinx.coroutines.flow.StateFlow

@AssistedFactory
interface UserPostRemoteMediatorFactory {
    fun create(
        @Assisted postItem: PostContentModel,
        @Assisted pageState: StateFlow<PageState>
    ): UserPostRemoteMediator
}

@AssistedFactory
interface RandomPostRemoteMediatorFactory {
    fun create(
        @Assisted pageState: StateFlow<PageState>
    ): RandomPostRemoteMediator
}