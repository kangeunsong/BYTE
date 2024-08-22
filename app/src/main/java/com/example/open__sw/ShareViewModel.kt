package com.example.open__sw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShareViewModel : ViewModel() {
    private val _selected = MutableLiveData<String>()
    val selected: LiveData<String> get() = _selected

    fun select(item: String) {
        _selected.value = item
    }
}