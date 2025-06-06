package com.kolown.porring.core.local.mapper

import com.kolown.porring.core.data.model.FollowData
import com.kolown.porring.core.local.entity.FollowEntity

internal fun FollowData.toEntity(): FollowEntity = FollowEntity(
    id = this.id,
    name = this.name,
)