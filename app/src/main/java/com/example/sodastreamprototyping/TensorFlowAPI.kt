package com.example.sodastreamprototyping

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TensorFlowAPI @Inject constructor(@ApplicationContext context: Context){
    public val baseSize = 4
    public val flavorSize = 18
    public val endDrinkIndex = flavorSize

    private val pumpSize = (1.0f/3.0f) //what value represents a single pump
    private val interpreter: Interpreter

    init {
        interpreter = Interpreter(loadModelFile(context))
    }

    private fun loadModelFile(context: Context): MappedByteBuffer{
        val fileDescriptor = context.assets.openFd("models/model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // soda represents an integer index of the base soda
    // flavors is an array with length of 18 that represents the concentration of each flavor in the drink
    fun generateDrink(soda : Int, flavors: IntArray) : List<Int> {
        val baseInput = FloatArray(baseSize){index ->
            if(index == soda) pumpSize else 0f
        }
        val flavorInput = flavors.map { it.toFloat() * pumpSize}.toFloatArray()

        val inputArray = baseInput + flavorInput
        val outputArray = Array(1) {FloatArray(flavorSize + 1)} //tensorflow outputs output in 2d array

        interpreter.run(inputArray, outputArray)

        return outputArray[0].withIndex().filter { it.value > 0.5 }.map { it.index}

    }

}