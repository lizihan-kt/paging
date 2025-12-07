package com.example.paging

import com.example.paging.infrastracture.InfrastructureModule
import com.example.paging.domain.RoomModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [RoomModule::class, InfrastructureModule::class])
@ComponentScan
class AppModule {
}