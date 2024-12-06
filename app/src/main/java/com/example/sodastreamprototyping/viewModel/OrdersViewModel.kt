package com.example.sodastreamprototyping.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.practice.ApiRequestHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(val api: ApiRequestHelper) : ViewModel() {
    private val _orders = MutableStateFlow<Array<Pair<Int, Boolean>>?>(null)
    val orders = _orders.asStateFlow()

    val orderNotified = mutableStateOf(false)

    init {
        api.getOrderHistory(onSuccess = { _orders.value = it }, {})
    }

    fun amHere(id: Int) {
        api.completeOrder(id, {
            orderNotified.value = true
            api.getOrderHistory(onSuccess = { _orders.value = it }, {})
        }, { orderNotified.value = true })
    }
}