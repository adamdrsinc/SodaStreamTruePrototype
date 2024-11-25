package com.example.sodastreamprototyping

import com.example.sodastreamprototyping.viewModel.GenerateDrinksViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
@OptIn(ExperimentalCoroutinesApi::class)
class GenerateDrinksTest {
    lateinit var ai : TensorFlowAPI

    @Before
    fun initViewModel() {
        //create a simple mock AI outputs, output drinks should have two flavors
        val mockResults = mapOf(
            listOf(0, 0, 0) to listOf(0, 1, 2),
            listOf(1, 0, 0) to listOf(0, 1, 2),
            listOf(0, 1, 0) to listOf(0, 1, 2),
            listOf(2, 0, 0) to listOf(1, 4),
            listOf(0, 0, 1) to listOf(0, 1),
        )
        ai = mock<TensorFlowAPI>() {
            on { baseSize } doReturn 4
            on { flavorSize } doReturn 3
            on {endDrinkIndex} doReturn 4
            on { generateDrink(any(), isA<IntArray>()) } doAnswer { invocation->
                val input = (invocation.arguments[1] as IntArray).toList()
                if(input in mockResults){
                    mockResults[input]
                } else{
                    listOf(4)
                }
            }
        }
    }

    @After
    fun cleanUp(){
        Dispatchers.resetMain()
    }

    @Test
    fun `test get drink`(){
        //mostly to test mock
        assertEquals(listOf(0, 1, 2), ai.generateDrink(0, intArrayOf(0, 0, 0)))
    }

    @Test
    fun expand() = runTest{
        Dispatchers.setMain(Dispatchers.Default)
        val testBase = 0
        val viewModel = GenerateDrinksViewModel(ai)

        assertTrue(viewModel.drinks.value[testBase].isEmpty())
        viewModel.expand(testBase)
        advanceUntilIdle()
        assertEquals(1, viewModel.drinks.value[testBase].size)
    }


}