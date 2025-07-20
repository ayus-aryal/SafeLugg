package com.example.safelugg.myviewmodels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences as DataStorePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Create DataStore instance
private val Context.dataStore: DataStore<DataStorePreferences> by preferencesDataStore(name = "search_history")

class CustomerViewModel(private val context: Context? = null) : ViewModel() {

    // Existing search functionality
    private val _searchResults = mutableStateOf<List<VendorResponse>>(emptyList())
    val searchResults: State<List<VendorResponse>> = _searchResults

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun searchVendors(city: String, date: String, noOfBags: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response =
                    RetrofitInstance.api.searchVendors(SearchRequest(city, date, noOfBags))
                if (response.isSuccessful) {
                    _searchResults.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown Error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Enhanced recent searches with persistence
    private val SEARCH_HISTORY_KEY = stringSetPreferencesKey("recent_searches")
    private val _recentSearches = mutableStateListOf<String>()
    val recentSearches: List<String> = _recentSearches

    init {
        // Load search history when ViewModel is created (only if context is provided)
        context?.let {
            loadSearchHistory()
        }
    }

    // Load search history from DataStore
    private fun loadSearchHistory() {
        context?.let { ctx ->
            viewModelScope.launch {
                try {
                    val searchHistoryFlow = ctx.dataStore.data.map { preferences ->
                        preferences[SEARCH_HISTORY_KEY] ?: emptySet()
                    }
                    val searchHistorySet = searchHistoryFlow.first()
                    _recentSearches.clear()
                    _recentSearches.addAll(searchHistorySet.toList())
                } catch (e: Exception) {
                    // Handle error - fallback to empty list
                    _recentSearches.clear()
                }
            }
        }
    }

    // Enhanced addSearchQuery with persistence
    fun addSearchQuery(query: String) {
        if (query.isNotBlank()) {
            // Remove if already exists to avoid duplicates
            _recentSearches.remove(query)
            // Add to the beginning of the list
            _recentSearches.add(0, query)

            // Keep only the latest 10 searches
            while (_recentSearches.size > 10) {
                _recentSearches.removeAt(_recentSearches.size - 1)
            }

            // Persist to DataStore if context is available
            context?.let { ctx ->
                viewModelScope.launch {
                    try {
                        ctx.dataStore.edit { preferences ->
                            preferences[SEARCH_HISTORY_KEY] = _recentSearches.toSet()
                        }
                    } catch (e: Exception) {
                        // Handle error - search still works without persistence
                    }
                }
            }
        }
    }

    // Clear search history
    fun clearSearchHistory() {
        _recentSearches.clear()
        context?.let { ctx ->
            viewModelScope.launch {
                try {
                    ctx.dataStore.edit { preferences ->
                        preferences.remove(SEARCH_HISTORY_KEY)
                    }
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    // Remove specific search from history
    fun removeSearchQuery(query: String) {
        _recentSearches.remove(query)
        context?.let { ctx ->
            viewModelScope.launch {
                try {
                    ctx.dataStore.edit { preferences ->
                        preferences[SEARCH_HISTORY_KEY] = _recentSearches.toSet()
                    }
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }
}

// ViewModelFactory to provide context to ViewModel
class CustomerViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomerViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}