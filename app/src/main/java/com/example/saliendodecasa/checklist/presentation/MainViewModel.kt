package com.example.saliendodecasa.checklist.presentation

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saliendodecasa.checklist.domain.ItemModel
import com.example.saliendodecasa.checklist.domain.ItemRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ItemRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var currentJob: Job? = null

    companion object {
        private const val KEY_LAST_LIST: String = "KEY_LAST_LIST"
    }

    init {
        val lastList: String = sharedPreferences.getString(KEY_LAST_LIST, "General") ?: "General"
        changeList(lastList)
    }

    fun changeList(listName: String) {
        sharedPreferences.edit {
            putString(KEY_LAST_LIST, listName)
        }

        _uiState.update {
            it.copy(currentListName = listName)
        }

        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            repository.getItemByList(listName).collect { listFromDb ->
                _uiState.update {
                    it.copy(items = listFromDb)
                }
            }
        }
    }

    fun addItem(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            return
        }

        val isDuplicate = _uiState.value.items.any { item ->
            item.name.equals(trimmedName, ignoreCase = true)
        }

        if (isDuplicate) {
            return
        }

        viewModelScope.launch {
            val newItem: ItemModel =
                ItemModel(name = name, listName = uiState.value.currentListName)
            repository.insertItem(newItem)
        }
    }

    fun deleteItem(item: ItemModel) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun toggleCheck(item: ItemModel) {
        viewModelScope.launch {
            val updatedItem = item.copy(isChecked = !item.isChecked)
            repository.updateItem(updatedItem)
        }
    }

    fun resetChecks() {
        viewModelScope.launch {
            repository.resetChecks(uiState.value.currentListName)
        }
    }
}