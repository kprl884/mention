package com.techtactoe.mention.screens.detail

import androidx.lifecycle.ViewModel
import com.techtactoe.mention.data.MuseumObject
import com.techtactoe.mention.data.MuseumRepository
import kotlinx.coroutines.flow.Flow

class DetailViewModel(private val museumRepository: MuseumRepository) : ViewModel() {
    fun getObject(objectId: Int): Flow<MuseumObject?> =
        museumRepository.getObjectById(objectId)
}
