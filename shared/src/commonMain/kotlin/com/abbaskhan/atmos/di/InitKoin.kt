package com.abbaskhan.atmos.di

import com.abbaskhan.data.remote.dataModule
import com.abbaskhan.features.di.featureModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

expect val platformLocationModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            dataModule,
            featureModule,
            platformLocationModule
        )
    }
}
