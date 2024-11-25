package com.example.sodastreamprototyping.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.sodastreamprototyping.Drink
import com.example.sodastreamprototyping.TensorFlowAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GenerateDrinksViewModel @Inject constructor(private val ai: TensorFlowAPI): ViewModel() {
    private val _drinks = MutableStateFlow<List<List<Drink>>>(emptyList())
    var exhaustedDrinks: SnapshotStateList<Boolean> = mutableStateListOf(*Array(ai.baseSize){false})

    val drinks = _drinks.asStateFlow()
    val defaultName = "AI Creation"
    val flavorOptions: Array<MutableMap<List<Int>, MutableList<Int>>> = Array(ai.baseSize){
        mutableMapOf()
    } //use memoized approach and store results of AI for better efficiency.
    init{
        _drinks.value = List(ai.baseSize){ base ->
            emptyList()
        }
    }

    /**
     * removes all drinks from list of ai generated drinks for a fresh start.
     */
    fun clear(){
        _drinks.value = List(ai.baseSize){
            emptyList()
        }
        flavorOptions.forEach {
            it.clear()
        }
        exhaustedDrinks.forEachIndexed{index, _ ->
            exhaustedDrinks[index] = false
        }
    }

    /**
     * adds another unique AI generated drink to the [base]'s list. Gives up if the AI is not able to make a unique
     * drink in a limited number of attempts
     */
    fun expand(base: Int){
        var newDrink = makeRandDrink(base)
        if(newDrink != null){
            addDrink(base, newDrink)
        }
        else exhaustedDrinks[base] = true
    }

    /**
     * Ai will add flavors to the provided [flavors]until it thinks it is complete. If no recipe is provided, a base
     * will be chosen at random, starting a new recipe. Returns the completed recipe. All generated drinks will be
     * unique until [clear] is called.
     */
    private fun makeRandDrink(base: Int, flavors: IntArray = IntArray(ai.flavorSize)): Drink? {
        val options = getOptions(base, flavors)
        if(options.isEmpty()){
            return null
        }
        val chosenFlavor = options.random()

        if(chosenFlavor == ai.endDrinkIndex){
            flavorOptions[base][flavors.toList()]?.remove(chosenFlavor)
            return Drink(baseDrink = base, name = defaultName, ingredients = reformatFlavors(flavors))
        }

        val nextFlavors = flavors.copyOf()
        nextFlavors[chosenFlavor]++
        val nextDrink = makeRandDrink(base, nextFlavors)

        if(nextDrink != null){
            return nextDrink
        }
        //dead end reached, no unique drink can be made with this flavor, so remove it and try again.
        flavorOptions[base][flavors.toList()]?.remove(chosenFlavor)
        return makeRandDrink(base, flavors)
    }

    /**
     * retrieves the AI recommended flavors for a drink with [base] and [flavors]. Caches all AI recommendations for
     * faster retrieval
     */
    private fun getOptions(base: Int, flavors: IntArray): MutableList<Int>{
        val flavorMap = flavorOptions[base]
        val options = flavorMap[flavors.toList()]
        if(options == null){
            val options = ai.generateDrink(base, flavors).toMutableList()
            flavorMap[flavors.toList()] = options
            return options
        }
        return options
    }

    /**
     * adds [drink] to the [base]'s list of available drinks.
     */
    private fun addDrink(base: Int, drink: Drink){
        _drinks.value = List(_drinks.value.size){
            if(it == base){
                drinks.value[it] + drink
            }
            else{
                drinks.value[it]
            }
        }
    }

    /**
     * reformats a list of [flavors], containing the quantity of ALL ingredients, and converts it to a format that the
     * Drink class expects
     */
    private fun reformatFlavors(flavors: IntArray): SnapshotStateList<Pair<Int, Int>>{
        val reformatedFlavors: MutableList<Pair<Int, Int>> = mutableListOf()
        flavors.forEachIndexed{ index, quantity ->
            if(quantity >= 1) reformatedFlavors.add(Pair(index, quantity))
        }
        return reformatedFlavors.toMutableStateList()
    }
}
