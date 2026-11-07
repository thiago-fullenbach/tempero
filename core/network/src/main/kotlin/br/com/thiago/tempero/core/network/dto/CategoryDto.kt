package br.com.thiago.tempero.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryNamesResponseDto(
    @Json(name = "meals") val meals: List<CategoryNameDto>?
)

@JsonClass(generateAdapter = true)
data class CategoryNameDto(
    @Json(name = "strCategory") val strCategory: String
)

@JsonClass(generateAdapter = true)
data class CategoriesResponseDto(
    @Json(name = "categories") val categories: List<CategoryDto>?
)

@JsonClass(generateAdapter = true)
data class CategoryDto(
    @Json(name = "idCategory") val idCategory: String,
    @Json(name = "strCategory") val strCategory: String,
    @Json(name = "strCategoryThumb") val strCategoryThumb: String?,
    @Json(name = "strCategoryDescription") val strCategoryDescription: String?
)
