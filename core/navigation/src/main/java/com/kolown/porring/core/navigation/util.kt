package com.kolown.porring.core.navigation

import kotlin.reflect.KClass

fun String.toRouteClassOrNull(): KClass<out Route>? =
    when(this) {
        Route.Setting::class.qualifiedName -> Route.Setting::class
        Route.DetailSearch::class.qualifiedName -> Route.DetailSearch::class
        Route.DeletedAccount::class.qualifiedName -> Route.DeletedAccount::class
        Route.Privacy::class.qualifiedName -> Route.Privacy::class
        Route.UserInfo::class.qualifiedName -> Route.UserInfo::class
        Route.Their::class.qualifiedName -> Route.Their::class
        Route.DetailMy::class.qualifiedName -> Route.DetailMy::class

        MainMenuRoute.Home::class.qualifiedName -> MainMenuRoute.Home::class
        MainMenuRoute.Search::class.qualifiedName -> MainMenuRoute.Search::class
        MainMenuRoute.Follower::class.qualifiedName -> MainMenuRoute.Follower::class
        MainMenuRoute.My::class.qualifiedName -> MainMenuRoute.My::class

        OnBoardRoute.Login::class.qualifiedName -> OnBoardRoute.Login::class
        OnBoardRoute.Join::class.qualifiedName -> OnBoardRoute.Join::class

        CameraRoute.Camera::class.qualifiedName -> CameraRoute.Camera::class
        CameraRoute.ImageEdit::class.qualifiedName -> CameraRoute.ImageEdit::class
        CameraRoute.Upload::class.qualifiedName -> CameraRoute.Upload::class

        else -> null
    }