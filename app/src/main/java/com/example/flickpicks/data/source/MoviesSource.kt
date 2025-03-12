package com.example.flickpicks.data.source

import com.example.flickpicks.data.model.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

const val TMDB_API_KEY = "2a3ee3402420a1a53993248b0bfbe408"
const val TMDB_BASE_URL = "https://api.themoviedb.org/3"

class MoviesSource {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getTrendingMovies(): List<Movie> {
        return try {
            val fullUrl = "https://api.themoviedb.org/3/trending/movie/week?api_key=2a3ee3402420a1a53993248b0bfbe408"
            val response: JsonObject = client.get(fullUrl).body()
            println("API RESPONSE: $response")
            response["results"]?.jsonArray?.map { jsonElement ->
                val obj = jsonElement.jsonObject
                Movie(
                    id = obj["id"]?.jsonPrimitive?.content ?: "",
                    title = obj["title"]?.jsonPrimitive?.content ?: "",
                    release_date = obj["release_date"]?.jsonPrimitive?.content ?: "",
                    overview = obj["overview"]?.jsonPrimitive?.content ?: "",
                    tagline = "", // TMDB doesn't provide tagline in trending API
                    genres = emptyList(), // Fetch genre separately if needed
                    poster_path = "https://image.tmdb.org/t/p/w500${obj["poster_path"]?.jsonPrimitive?.content}",
                    vote_average = obj["vote_average"]?.jsonPrimitive?.content ?: "",
                    trailer = null
                )
            } ?: emptyList()
        } catch (e: Exception) {
            println("Error fetching trending movies: ${e.localizedMessage}")
            emptyList()
        }
    }

    suspend fun getMovieDetails(movieId: String): Movie {
        return try {
            val obj: JsonObject = client.get("$TMDB_BASE_URL/movie/$movieId") {
                parameter("api_key", TMDB_API_KEY)
            }.body()

            val trailer = getMovieTrailer(movieId)

            Movie(
                id = obj["id"]?.jsonPrimitive?.content ?: "",
                title = obj["title"]?.jsonPrimitive?.content ?: "",
                release_date = obj["release_date"]?.jsonPrimitive?.content ?: "",
                overview = obj["overview"]?.jsonPrimitive?.content ?: "",
                tagline = obj["tagline"]?.jsonPrimitive?.content ?: "",
                genres = obj["genres"]?.jsonArray?.map { it.jsonObject["name"]?.jsonPrimitive?.content ?: "" } ?: emptyList(),
                poster_path = "https://image.tmdb.org/t/p/w500${obj["poster_path"]?.jsonPrimitive?.content}",
                vote_average = obj["vote_average"]?.jsonPrimitive?.content ?: "",
                trailer = trailer
            )
        } catch (e: Exception) {
            println("Error fetching movie details: ${e.localizedMessage}")
            throw e
        }
    }

    suspend fun getMovieWatchProviders(movieId: String): List<String> {
        val url = "https://api.themoviedb.org/3/movie/$movieId/watch/providers?api_key=$TMDB_API_KEY"

        return try {
            val response: JsonObject = client.get(url).body()
            val results = response["results"]?.jsonObject

            // (change "US" to Canada)
            val usProviders = results?.get("US")?.jsonObject?.get("flatrate")?.jsonArray

            usProviders?.map { provider ->
                provider.jsonObject["provider_name"]?.jsonPrimitive?.content ?: "Unknown"
            } ?: emptyList()

        } catch (e: Exception) {
            println("Error fetching watch providers: ${e.localizedMessage}")
            emptyList()
        }
    }

    suspend fun getMoviesByGenres(genreIds: List<String>): List<Movie> {
        return try {
            val genreQuery = genreIds.joinToString("|") // Format as "28|12|16" for API
            val fullUrl = "$TMDB_BASE_URL/discover/movie?api_key=$TMDB_API_KEY&with_genres=$genreQuery"

            val response: JsonObject = client.get(fullUrl).body()

            println("API RESPONSE: $response")

            response["results"]?.jsonArray?.map { jsonElement ->
                val obj = jsonElement.jsonObject
                Movie(
                    id = obj["id"]?.jsonPrimitive?.content ?: "",
                    title = obj["title"]?.jsonPrimitive?.content ?: "",
                    release_date = obj["release_date"]?.jsonPrimitive?.content ?: "",
                    overview = obj["overview"]?.jsonPrimitive?.content ?: "",
                    tagline = "", // TMDB doesn't provide tagline in discover API
                    genres = emptyList(), // Fetch genre separately if needed
                    poster_path = "https://image.tmdb.org/t/p/w500${obj["poster_path"]?.jsonPrimitive?.content}",
                    vote_average = obj["vote_average"]?.jsonPrimitive?.content ?: "",
                    trailer = null
                )
            } ?: emptyList()
        } catch (e: Exception) {
            println("Error fetching movies by genres: ${e.localizedMessage}")
            emptyList()
        }
    }

    suspend fun getMovieTrailer(movieId: String): String? {
        val url = "$TMDB_BASE_URL/movie/$movieId/videos?api_key=$TMDB_API_KEY"

        return try {
            val response: JsonObject = client.get(url).body()
            val results = response["results"]?.jsonArray ?: return null

            // Filter for the official trailer on YouTube
            val trailer = results.firstOrNull { video ->
                val obj = video.jsonObject
                obj["site"]?.jsonPrimitive?.content == "YouTube" &&
                        obj["type"]?.jsonPrimitive?.content == "Trailer"
            }

            // Return the YouTube key if a trailer is found
            trailer?.jsonObject?.get("key")?.jsonPrimitive?.content?.let { key ->
                "https://www.youtube.com/watch?v=$key"
            }
        } catch (e: Exception) {
            println("Error fetching movie trailer: ${e.localizedMessage}")
            null
        }
    }

}
