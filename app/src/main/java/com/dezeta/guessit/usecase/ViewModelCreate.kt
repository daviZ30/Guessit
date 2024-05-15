package com.dezeta.guessit.usecase

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.entity.Category
import com.dezeta.guessit.domain.entity.Difficulty
import com.dezeta.guessit.domain.entity.DifficultySpinner
import com.dezeta.guessit.domain.entity.Difficulty_es
import com.dezeta.guessit.domain.entity.Img
import com.dezeta.guessit.domain.entity.Guess

class ViewModelCreate : ViewModel() {
    var img3Uri: String? = null
    var img2Uri: String? = null
    var img1Uri: String? = null
    var id = MutableLiveData<String>()
    var name = MutableLiveData<String>()
    private var state = MutableLiveData<CreateState>()
    var difficulty = MutableLiveData<DifficultySpinner>()

    fun difficultList(language: String): List<DifficultySpinner> {
        val lista: List<DifficultySpinner>?
        val keys = Difficulty.entries.map { it.name }
        lista = if (language == "es") {
            val values = Difficulty_es.entries.map { it.name }
            listOf(
                DifficultySpinner(values[0], keys[0]),
                DifficultySpinner(values[1], keys[1]),
                DifficultySpinner(values[2], keys[2])
            )
        } else {
            listOf(
                DifficultySpinner(keys[0], keys[0]),
                DifficultySpinner(keys[1], keys[1]),
                DifficultySpinner(keys[2], keys[2])
            )
        }
        return lista
    }

    fun getState(): LiveData<CreateState> {
        return state
    }

    fun validateName(): Boolean {
        return when {
            TextUtils.isEmpty(name.value) -> {
                state.value = CreateState.nameEmtyError
                false
            }
            !Repository.isLocalNewName(name.value!!) -> {
                state.value = CreateState.nameDuplicateError
                false
            }
            else -> {
                true
            }
        }
    }

    fun validate() {
        validateName()
        when {
            TextUtils.isEmpty(id.value) -> state.value = CreateState.idEmpyError
            !Repository.isLocalNewId(id.value!!) -> state.value = CreateState.idDuplicateError
            difficulty.value == null -> state.value = CreateState.difficultyEmtyError
            img1Uri == null || img2Uri == null || img3Uri == null -> state.value =
                CreateState.imageEmtyError

            img1Uri == img2Uri || img1Uri == img3Uri || img2Uri == img3Uri -> state.value =
                CreateState.imageEqualsError

            else -> insertSerie()
        }
    }

    private fun insertSerie() {
        if (img1Uri != null && img2Uri != null && img3Uri != null) {
            Repository.insertSerie(
                Guess(
                   id.value!!, name.value!!, Difficulty.valueOf(difficulty.value!!.key), Category.Local,
                )
            )
            Repository.insertImages(
                listOf(
                    Img(id.value!!, img1Uri!!,1),
                    Img(id.value!!, img2Uri!!,2),
                    Img(id.value!!, img3Uri!!,3)
                )
            )
            /*when {
                resultSerie is Resource.Error -> {
                    state.value = CreateState.ErrorInsertSerie
                }

                resultImage is Resource.Error -> {
                    state.value = CreateState.ErrorInsertSerie
                }

                else -> {
                    state.value = CreateState.Success
                }
            }*/
            state.value = CreateState.Success
        } else {
            state.value = CreateState.imageEmtyError
        }


    }
}