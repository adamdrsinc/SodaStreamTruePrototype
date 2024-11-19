package com.example.sodastreamprototyping.viewModel

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "settings")

class HomeViewModel(private val context: Context) : ViewModel() {
    companion object {
        private val SELECTED_TAB = intPreferencesKey("selected_tab")
    }

    val selectedTabIndex: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_TAB] ?: 0
        }

    fun setSelectedTab(index: Int) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[SELECTED_TAB] = index
            }
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}