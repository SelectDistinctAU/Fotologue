package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.FotologueRepository
import com.fotologue.core.data.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaxonomyUiState(
    val subjectsCount: Int = 0,
    val contentCount: Int = 0,
    val intentCount: Int = 0,
    val selectedCategoryTab: String = "All", // "All", "Subjects", "Content", "Intent"
    val allTags: List<Tag> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingTag: Tag? = null,
    val deletingTag: Tag? = null
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
            repository.insertDefaultTagsIfNeeded()
            repository.allTags.collect { tagsList ->
                val subjects = tagsList.filter { it.category.equals(Tag.CATEGORY_SUBJECT, ignoreCase = true) || it.category.equals("Subject", ignoreCase = true) || it.category.equals("Subjects", ignoreCase = true) }
                val content = tagsList.filter { it.category.equals(Tag.CATEGORY_CONTENT, ignoreCase = true) }
                val intent = tagsList.filter { it.category.equals(Tag.CATEGORY_INTENT, ignoreCase = true) }

                _uiState.update { state ->
                    state.copy(
                        subjectsCount = subjects.size,
                        contentCount = content.size,
                        intentCount = intent.size,
                        allTags = tagsList
                    )
                }
            }
        }
    }

    fun selectCategoryTab(tab: String) {
        _uiState.update { it.copy(selectedCategoryTab = tab) }
    }

    fun showAddTagDialog() {
        _uiState.update { it.copy(showAddDialog = true, editingTag = null) }
    }

    fun showEditTagDialog(tag: Tag) {
        _uiState.update { it.copy(editingTag = tag, showAddDialog = false) }
    }

    fun showDeleteTagDialog(tag: Tag) {
        _uiState.update { it.copy(deletingTag = tag) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(showAddDialog = false, editingTag = null, deletingTag = null) }
    }

    fun addTag(name: String, category: String, parentId: Long? = null) {
        val clean = name.trim().removePrefix("#")
        if (clean.isNotBlank()) {
            viewModelScope.launch {
                repository.insertTag(Tag(name = clean, category = category, parentId = parentId))
                dismissDialog()
            }
        }
    }

    fun updateTag(tag: Tag) {
        viewModelScope.launch {
            repository.updateTag(tag)
            dismissDialog()
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            repository.deleteTags(listOf(tag.id))
            dismissDialog()
        }
    }
}
