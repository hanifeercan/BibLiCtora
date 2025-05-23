package com.amineaytac.biblictora.ui.reading

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amineaytac.biblictora.core.data.model.ReadingBook
import com.amineaytac.biblictora.core.domain.readingstatus.GetBookItemReadingUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.IsBookItemReadingUseCase
import com.amineaytac.biblictora.core.domain.readingstatus.UpdatePercentageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingViewModel @Inject constructor(
    private val updatePercentageUseCase: UpdatePercentageUseCase,
    private val getBookItemReadingUseCase: GetBookItemReadingUseCase,
    private val isBookItemReadingUseCase: IsBookItemReadingUseCase
) : ViewModel() {

    fun updatePercentage(id: Int, percentage: Int, readingProgress: Int) {
        viewModelScope.launch {
            updatePercentageUseCase(id, percentage, readingProgress)
        }
    }

    fun getBookItemReading(itemId: String): LiveData<ReadingBook> {
        return getBookItemReadingUseCase(itemId)
    }

    fun isBookItemReading(itemId: String): LiveData<Boolean> {
        return isBookItemReadingUseCase(itemId)
    }
}