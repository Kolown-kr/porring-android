package com.kolown.porring.core.data.mapper

import com.kolown.porring.core.data.model.FollowData
import com.kolown.porring.core.model.Follow

internal fun Follow.toData() = FollowData(
    id = this.id,
    name = this.name
)