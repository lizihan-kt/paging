package com.example.paging.infrastracture.ktor.requests

import io.ktor.resources.Resource

// use 'ktorGithubApiClient' with this Resource
// https://api.github.com/search/repositories?sort=stars&q=Android%20in%3Aname%2Cdescription&page=1&per_page=10
@Resource("/search")
class GithubApiSearchRequest {
    @Resource("repositories")
    class Repository(
        val parent: GithubApiSearchRequest = GithubApiSearchRequest(),
        // query parameters
        val q: String,
        val page: Int = 1,
        val per_page: Int = 20,
        val sort: String = "stars"
    ) {
        companion object {
            fun toRequestObject(
                queryInput: String, // user input in search bar
                page: Int,
                perPage: Int
            ): Repository {
                return Repository(
                    q = "$queryInput in:name", // in:name search `full_name` field
                    page = page,
                    per_page = perPage
                )
            }
        }
    }
}