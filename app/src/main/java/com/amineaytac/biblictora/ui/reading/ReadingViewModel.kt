package com.amineaytac.biblictora.ui.reading

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amineaytac.biblictora.core.data.repo.BookRepository
import com.amineaytac.biblictora.core.database.entity.ReadingStatusEntity
import com.amineaytac.biblictora.core.domain.readingstatus.UpdatePercentageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingViewModel @Inject constructor(
    private val updatePercentageUseCase: UpdatePercentageUseCase,
    private val bookRepository: BookRepository
) : ViewModel() {

    fun updatePercentage(id: Int, percentage: Int, readingProgress: Int) {
        viewModelScope.launch {
            updatePercentageUseCase(id, percentage, readingProgress)
        }
    }

    fun getBookItemReading(itemId: String): LiveData<ReadingStatusEntity> {
        return bookRepository.getBookItemReading(itemId)
    }

    fun isBookItemReading(itemId: String): LiveData<Boolean> {
        return bookRepository.isBookItemReading(itemId)
    }
}