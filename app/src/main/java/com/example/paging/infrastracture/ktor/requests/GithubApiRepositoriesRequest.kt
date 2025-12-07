package com.example.paging.infrastracture.ktor.requests

import io.ktor.resources.Resource

@Resource("/repositories")
class GithubApiRepositoriesRequest {
    @Resource("{id}")
    class Id(
        val id: Long,
        val parent: GithubApiRepositoriesRequest = GithubApiRepositoriesRequest(),
    )
}