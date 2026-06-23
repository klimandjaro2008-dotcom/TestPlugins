package com.example

import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.TvType

class ExampleProvider : MainAPI() { // All providers must be an instance of MainAPI
    override var mainUrl = "https://example.com/" 
    override var name = "Example provider"
    override val supportedTypes = setOf(TvType.Movie)
    private val token = "PUT_YOUR_TOKEN"
    override var lang = "en"

    // Enable this when your provider has a main page
    override val hasMainPage = true

    // This function gets called when you search for something
    
    override suspend fun search(query: String): List<SearchResponse> {

        val res = app.get(
            "https://graph.microsoft.com/v1.0/me/drive/root/children",
            headers = mapOf(
                "Authorization" to "Bearer $token"
            )
        ).text

        val items = parseJson(res)

        return items.files.map {
            newMovieSearchResponse(
                it.name,
                it.downloadUrl,
                TvType.Movie
            )
        }
    }

    override suspend fun load(url: String): LoadResponse {
        return newMovieLoadResponse(
            name = "OneDrive Movie",
            url = url,
            type = TvType.Movie,
            dataUrl = url
        )
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {

        callback(
            ExtractorLink(
                source = "OneDrive",
                name = "HD",
                url = data,
                referer = "",
                quality = Qualities.P1080.value,
                isM3u8 = false
            )
        )
        return true
    }
}
