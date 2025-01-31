package com.kolown.porring.core.data.datasource.fake

import com.kolown.porring.core.data.mock.MockDataProvider
import com.kolown.porring.core.model.Post
import com.kolown.porring.core.model.Tag

interface PostDataSource {
    suspend fun getPostListByTag(tag: Tag): Result<List<Post>>
}

class FakePostDataSource() : PostDataSource {
    override suspend fun getPostListByTag(tag: Tag): Result<List<Post>> {
        return Result.success(MockDataProvider.getRandomPostList())
    }
}
