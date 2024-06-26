package com.example.thenewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.thenewsapp.models.Article
import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.repository.NewsRepository
import com.example.thenewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(app) {
    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinesPage = 1
    private var headLinesResponse: NewsResponse? = null
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var searchNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null
    private var newSearchQuery:String? = null
    private var oldSearchQuery:String? = null
    private var countryCode: String = "us"
    private var categ: String = "business"
    init {
        getHeadlines(countryCode)
    }
    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlinesInternet(countryCode)
    }
    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsCall(searchQuery)
    }

    fun setCountry(countryCode: String) {
        this.countryCode = countryCode
        getHeadlines(countryCode)
    }
    fun getCountry(): String{
        return this.countryCode
    }

    fun setCategory(category: String) {
        this.categ = category
        categorySearch(category)
    }

    fun getCategories(): Array<String> {
        return arrayOf("business","entertainment","general","health","science","sports","technology")
    }
    fun getCategory(): String {
        return this.categ
    }
    private fun categorySearch(category: String) = viewModelScope.launch {
        headlinesCategoryInternet(category)
    }

    private fun handleHeadLinesResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                val filteredArticles = resultResponse.articles.filter { it.url != "https://removed.com" }
                resultResponse.articles = filteredArticles.toMutableList()

                headLinesResponse = resultResponse
                //headlinesPage++
                //if (headLinesResponse == null) {
                //    headLinesResponse = resultResponse
                //} else {
                //    val oldArticles = headLinesResponse?.articles ?: mutableListOf()
                //    val newArticles = resultResponse.articles
                //    oldArticles.addAll(newArticles)
                //    headLinesResponse?.articles = oldArticles
                //}
                return Resource.Success(headLinesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                val filteredArticles = resultResponse.articles.filter { it.url != "https://removed.com" }
                resultResponse.articles = filteredArticles.toMutableList()

                searchNewsResponse = if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    resultResponse
                } else {
                    searchNewsPage++
                    resultResponse
                }
                //if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                //    searchNewsPage = 1
                //    oldSearchQuery = newSearchQuery
                //    searchNewsResponse = resultResponse
                //} else {
                //    searchNewsPage++
                //    val oldArticles = searchNewsResponse?.articles ?: mutableListOf()
                //    val newArticles = resultResponse.articles
                //    oldArticles.addAll(newArticles)
                //    searchNewsResponse?.articles = oldArticles
                //}
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    fun addToFavorites(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }
    fun getFavoriteNews() = newsRepository.getFavoriteNews()
    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }
    private fun internetConnection(context: Context) : Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }
    private suspend fun searchNewsCall(searchQuery: String) {
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if(internetConnection(this.getApplication())) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private suspend fun headlinesInternet(countryCode: String) {
        headlines.postValue(Resource.Loading())
        try {
            if(internetConnection(this.getApplication())) {
                val response = newsRepository.getHeadlines(countryCode, headlinesPage)
                headlines.postValue(handleHeadLinesResponse(response))
            } else {
                headlines.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> headlines.postValue(Resource.Error("Network Failure"))
                else -> headlines.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private suspend fun headlinesCategoryInternet(category: String) {
        headlines.postValue(Resource.Loading())
        try {
            if(internetConnection(this.getApplication())) {
                val response = newsRepository.getHeadlinesByCategory(category)
                headlines.postValue(handleHeadLinesResponse(response))
            } else {
                headlines.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> headlines.postValue(Resource.Error("Network Failure"))
                else -> headlines.postValue(Resource.Error("Conversion ErrorAA"))
            }
        }
    }

    fun getCountries(): Array<String> {
        return arrayOf("us", "ru", "kz","ae","ar","au","be","bg","br","ca","ch","cn","co")
    }
}