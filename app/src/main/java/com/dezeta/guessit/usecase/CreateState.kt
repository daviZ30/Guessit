package com.dezeta.guessit.usecase

sealed class CreateState {
    data object nameEmtyError : CreateState()
    data object idEmpyError : CreateState()
    data object difficultyEmtyError : CreateState()
    data object imageEmtyError : CreateState()
    data object nameDuplicateError : CreateState()
    data object idDuplicateError : CreateState()
    data object imageEqualsError : CreateState()
    data object Success : CreateState()

    //data object ErrorInsertSerie : CreateState()


}