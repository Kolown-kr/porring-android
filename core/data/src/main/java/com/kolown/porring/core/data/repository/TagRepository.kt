package com.kolown.porring.core.data.repository

import androidx.paging.PagingData
import com.kolown.porring.core.data.datasource.paging.PagingDataSource.Companion.createPager
import com.kolown.porring.core.model.Tag
import com.kolown.porring.core.network.TagDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface TagRepository {
    fun getTagBySearch(search: String): Flow<PagingData<Tag>>
}

class TagRepositoryImpl @Inject constructor(private val tagDataSource: TagDataSource) :
    TagRepository {

    override fun getTagBySearch(search: String): Flow<PagingData<Tag>> {
        return createPager(
            keySelector = { it.name },
        ) { startKey, perPage ->
            tagDataSource.getTagBySearch(
                searchText = search,
                key = startKey,
                perPage = perPage.toLong()
            )
        }
    }
}
