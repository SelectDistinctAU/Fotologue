package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.FotologueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaxonomyUiState(
    val subjectsCount: Int = 4,
    val contentCount: Int = 30,
    val intentCount: Int = 14,
    val selectedCategoryTab: String = "All", // "All", "Subjects", "Content", "Intent"
    val allTags: List<Pair<String, String>> = emptyList() // Pair(tagName, category)
)

@HiltViewModel
class TaxonomyViewModel @Inject constructor(
    private val repository: FotologueRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaxonomyUiState())
    val uiState: StateFlow<TaxonomyUiState> = _uiState.asStateFlow()

    init {
        loadTaxonomy()
    }

    private fun loadTaxonomy() {
        viewModelScope.launch {
            repository.allTags.collect { tagsList ->
                val flatTags = tagsList.map { Pair(it.name, "Content") }
                _uiState.update { state ->
                    state.copy(
                        contentCount = tagsList.size.coerceAtLeast(30),
                        allTags = flatTags
                    )
                }
            }
        }
    }

    fun selectCategoryTab(tab: String) {
        _uiState.update { it.copy(selectedCategoryTab = tab) }
    }
}
