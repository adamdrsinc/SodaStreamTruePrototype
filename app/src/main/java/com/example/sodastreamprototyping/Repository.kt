package com.example.sodastreamprototyping

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.practice.ApiRequestHelper.Companion.getIngredients
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(@ApplicationContext context: Context) {
    var drinkFlavorsFromDB = MutableStateFlow<List<String>>(emptyList())
    var basesFromDB = MutableStateFlow<List<String>>(emptyList())

    init{
        getIngredients(
            context = context,
            onSuccess = { ingredients ->
                drinkFlavorsFromDB.value = ingredients["ingredients"]!!
                basesFromDB.value        = ingredients["bases"]!!
            },
            onError = { error ->
                Toast.makeText(context, "Error fetching ingredients: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
