//package com.example.sodastreamprototyping
//
//import android.content.Context
//import org.tensorflow.lite.Interpreter
//import java.nio.ByteBuffer
//import java.nio.MappedByteBuffer
//import java.nio.channels.FileChannel
//import java.io.FileInputStream
//
//class DrinkGenerator(context: Context){
//
//    private val interpreter: Interpreter
//
//    init {
//        interpreter = Interpreter(loadModelFile(context))
//    }
//
//    private fun loadModelFile(context: Context): MappedByteBuffer{
//        val fileDescriptor = context.assets.openFd("models/model.tflite")
//        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
//        val fileChannel = inputStream.channel
//        val startOffset = fileDescriptor.startOffset
//        val declaredLength = fileDescriptor.declaredLength
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//    }
//
//    // soda represents an integer index of the base soda
//    // flavors is an array with length of 18 that represents the concentration of each flavor in the drink
//    fun generateDrink(soda : Int, flavors: FloatArray) : List<String> {
//
//        // allocates 1 byte for each soda type and 4 bytes for each flavor
//        // might be an issue if sodas should not be expressed as 1
//        val inputBuffer = ByteBuffer.allocateDirect(4 + flavors.size * 4).apply {
//            putInt(soda) // makes soda index 1
//            flavors.forEach { putFloat(it) } // adds each flavor index
//        }
//
//        // 4 bytes for each flavor + finish drink
//        val outputBuffer = ByteBuffer.allocateDirect(4 * 19)
//
//        interpreter.run(inputBuffer, outputBuffer)
//
//        outputBuffer.rewind()
//        val allIngredients = arrayOf("Coconut", "Vanilla", "Strawberry", "Cream", "Pineapple", "Raspberry", "Cranberry", "lime", "Cherry", "Cinnamon", "Pomegranate", "Peach", "Blackberry", "Caramel", "Orange", "Watermelon", "Mango", "Passion Fruit")
//        val newIngredients = mutableListOf<String>()
//
//        for (i in 0 until 19) {
//            val score = outputBuffer.float
//            if (score > 0.4) { // what score should be the threshold?
//                newIngredients.add(allIngredients[i])
//            }
//        }
//
//        return newIngredients
//    }
//
//}