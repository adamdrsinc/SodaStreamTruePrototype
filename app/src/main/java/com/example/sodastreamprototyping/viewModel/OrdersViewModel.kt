package com.example.sodastreamprototyping.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.practice.ApiRequestHelper
import com.example.sodastreamprototyping.Basket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(val api: ApiRequestHelper) : ViewModel() {
    private val _orders = MutableStateFlow<List<Pair<Int, Boolean>>?>(null)
    val orders = _orders.asStateFlow()

    val orderNotified = mutableStateOf(false)

    init {
//        api.getOrderHistory(onSuccess = { _orders.value = it }, {})
        _orders.value = Basket.history.toList()
    }

    fun amHere(id: Int) {
        Basket.history[id] = Pair(id, true)
        _orders.value = Basket.history.toList()
        orderNotified.value = true
//        api.completeOrder(id, {
//            orderNotified.value = true
//            api.getOrderHistory(onSuccess = { _orders.value = it }, {})
//        }, { orderNotified.value = true })
    }
}