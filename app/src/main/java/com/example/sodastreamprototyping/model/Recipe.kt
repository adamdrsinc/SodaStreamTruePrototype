package com.example.sodastreamprototyping.model

data class Recipe(val base: Int, val flavors: MutableList<Int> = MutableList(18){0}){

    /**
     * returns all non-zero flavors mapped to a [Pair] with the index of the
     * flavor as the first value in the pair, and the quantity as the second
     */
    fun getUsedFlavors(): List<Pair<Int, Int>>{
        return flavors.withIndex()
            .filter { it.value > 0 }
            .map{it.index to it.value}
    }

    fun addFlavor(flavorIndex: Int){
        flavors[flavorIndex]++
    }
}