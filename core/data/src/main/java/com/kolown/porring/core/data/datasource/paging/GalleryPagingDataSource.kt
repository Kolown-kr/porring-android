package com.kolown.porring.core.data.datasource.paging

import com.kolown.porring.core.data.datasource.fake.GalleryDataSource
import com.kolown.porring.core.data.di.Fake
import com.kolown.porring.core.model.GalleryThumbnail
import javax.inject.Inject

class GalleryPagingDataSource @Inject constructor(
    @Fake private val galleryDataSource: GalleryDataSource,
    //임시
    private val userId: Long,
) : CommonPagingDataSource<GalleryThumbnail>() {
    override suspend fun providePage(page: Int): Result<List<GalleryThumbnail>> {
        return galleryDataSource.getGalleryThumbnailList(userId, page)
    }


}
