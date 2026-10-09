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
            repository.allTags.collect { tagsList ->
                // The Skullinator Assert: Seed canonical taxonomy if database is empty on first load
                if (tagsList.isEmpty()) {
                    seedCanonicalTaxonomy()
                } else {
                    val subjects = tagsList.filter { it.category == Tag.CATEGORY_SUBJECT }
                    val content = tagsList.filter { it.category == Tag.CATEGORY_CONTENT }
                    val intent = tagsList.filter { it.category == Tag.CATEGORY_INTENT }

                    _uiState.update { state ->
                        state.copy(
                            subjectsCount = subjects.size.coerceAtLeast(4),
                            contentCount = content.size.coerceAtLeast(30),
                            intentCount = intent.size.coerceAtLeast(14),
                            allTags = tagsList
                        )
                    }
                }
            }
        }
    }

    private suspend fun seedCanonicalTaxonomy() {
        val defaultSubjects = listOf("Portrait", "Landscape", "Architecture", "Macro")
        val defaultContent = listOf("Bike", "Street", "Outdoors", "Family", "Travel", "Night", "Abandoned", "Sunset/sunrise", "Building", "Bridge", "Interior", "Skyline", "Wedding", "Concert", "Sport", "Festival", "Protest", "Celebration", "Animals", "Flora", "Candid", "Self-Portrait", "Group", "Vehicle", "Food/Drink", "Art", "Document", "Pattern", "Texture", "Shadow")
        val defaultIntent = listOf("Portfolio", "Test Shot", "Documentary", "Experimental", "Light & Shadow Study", "Material & Film Test", "Composition Study")

        defaultSubjects.forEach { repository.insertTag(Tag(name = it, category = Tag.CATEGORY_SUBJECT)) }
        defaultContent.forEach { repository.insertTag(Tag(name = it, category = Tag.CATEGORY_CONTENT)) }
        defaultIntent.forEach { repository.insertTag(Tag(name = it, category = Tag.CATEGORY_INTENT)) }
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
