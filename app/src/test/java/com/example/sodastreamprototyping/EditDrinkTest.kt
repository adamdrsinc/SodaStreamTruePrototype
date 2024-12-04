package com.example.sodastreamprototyping

import com.example.sodastreamprototyping.viewModel.EditDrinkViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class EditDrinkTest {
    lateinit var ai : TensorFlowAPI

    @Before
    fun setDispatcher(){
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Before
    fun initAI(){
        val mockResults = mapOf(
            listOf(0, 0) to listOf(0, 1),
            listOf(1, 0) to listOf(1),
            listOf(0, 1) to listOf(0)
        )

        ai = mock<TensorFlowAPI>() {
            on { baseSize } doReturn 4
            on { flavorSize } doReturn 2
            on {endDrinkIndex} doReturn 3
            on { generateDrink(any(), isA<IntArray>()) } doAnswer { invocation->
                val input = (invocation.arguments[1] as IntArray).toList()
                if(input in mockResults){
                    mockResults[input]
                } else{
                    listOf(3)
                }
            }
            on {generateDrink(eq(1), any())} doReturn listOf(3)

        }
    }

    @After
    fun cleanUp(){
        Dispatchers.resetMain()

    }

    @Test
    fun changeName(){
        val drink = Drink(emptyList<Pair<Int, Int>>().toMutableList(), "drink1")
        val viewModel = EditDrinkViewModel(ai, drink)
        viewModel.setName("drink2")
        assertEquals("drink2", viewModel.drink.value.name)
    }

    @Test
    fun changeIce(){
        val drink = Drink(emptyList<Pair<Int, Int>>().toMutableList(), "drink1", iceQuantity = 1)
        val viewModel = EditDrinkViewModel(ai, drink)
        assertEquals(1, viewModel.drink.value.iceQuantity)
        viewModel.setIce(3)
        assertEquals(3, viewModel.drink.value.iceQuantity)
    }

    @Test
    fun changeBase(){
        val drink = Drink(emptyList<Pair<Int, Int>>().toMutableList(), "drink1", baseDrink = 1)
        val viewModel = EditDrinkViewModel(ai, drink)
        assertEquals(1, viewModel.drink.value.baseDrink)
        viewModel.setBase(2)
        assertEquals(2, viewModel.drink.value.baseDrink)
    }

    @Test
    fun incrementIngredient(){
        val drink = Drink(mutableListOf(Pair(0, 1)), "drink1")
        val viewModel = EditDrinkViewModel(ai, drink)
        assertEquals(1, viewModel.drink.value.ingredients[0].second)
        viewModel.addIngredient(0)
        assertEquals(2, viewModel.drink.value.ingredients[0].second)
        viewModel.addIngredient(1)
        assertEquals(1, viewModel.drink.value.ingredients[1].second)
    }

    @Test
    fun decrementIngredient(){
        val drink = Drink(mutableListOf(Pair(0, 1)), "drink1")
        val viewModel = EditDrinkViewModel(ai, drink)
        assertEquals(1, viewModel.drink.value.ingredients[0].second)
        viewModel.removeIngredient(0)
        assertEquals(0, viewModel.drink.value.ingredients.size)
    }

    @Test
    fun `gets ai suggestions`() = runTest{
        val drink = Drink(name="drink1")
        val viewModel = EditDrinkViewModel(ai, drink)
        advanceUntilIdle()
        assertEquals(listOf(0, 1), viewModel.suggestion.value)
    }

    @Test
    fun `ai suggestions update when ingredient added`() = runTest{
        val drink = Drink(name="drink1")
        val viewModel = EditDrinkViewModel(ai, drink)
        advanceUntilIdle()
        assertEquals(listOf(0, 1), viewModel.suggestion.value)
        viewModel.addIngredient(0)
        advanceUntilIdle()
        assertEquals(listOf(1), viewModel.suggestion.value)
    }

    @Test
    fun `ai suggestions update when base changed`() = runTest{
        val drink = Drink(name="drink1")
        val viewModel = EditDrinkViewModel(ai, drink)
        advanceUntilIdle()
        assertEquals(listOf(0, 1), viewModel.suggestion.value)
        viewModel.setBase(1)
        advanceUntilIdle()
        assertEquals(listOf(3), viewModel.suggestion.value)
    }
}