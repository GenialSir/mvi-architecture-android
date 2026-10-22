package com.genialeast.mviarchitecture.core.network.api

import com.genialeast.mviarchitecture.core.network.model.RecipeDto
import retrofit2.http.GET

/**
 * 与 Mvvm 演练工程相同的测试接口：android-test/recipes.json。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
interface RecipesApi {
    @GET("recipes.json")
    suspend fun fetchRecipes(): List<RecipeDto>
}
