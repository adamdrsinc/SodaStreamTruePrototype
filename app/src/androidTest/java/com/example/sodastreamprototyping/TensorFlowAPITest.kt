package com.example.sodastreamprototyping

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TensorFlowAPITest {
    private lateinit var api: TensorFlowAPI
    @Before
    fun initializeAPI(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        api = TensorFlowAPI(context)
    }


    @Test
    fun outputIsReasonable(){
        //because the AI is a black box, we can't tests specifics, but a few things should hold true,
        // otherwise the model is flawed:

        //if the drink has not ingredients, it should get some output.
        for(i in 0 until 4){ //test with all bases
            val output = api.generateDrink(i, IntArray(api.flavorSize))
            assertTrue(output.isNotEmpty())
        }

        //if the drink has an insane amount of flavors, it shouldn't get any new recommendations
        for(i in 0 until 4){ //test with all bases
            val output = api.generateDrink(i, IntArray(api.flavorSize){3})
            //the AI should either give no outputs, or only recommend to end the drink.
            assertTrue(output.isEmpty() || (output.size == 1 && output.contains(api.endDrinkIndex)))
        }
    }

}