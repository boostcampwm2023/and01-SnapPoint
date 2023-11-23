package com.boostcampwm2023.snappoint.presentation.createpost

interface BlockItemEventListener {
    val onTextChange: (Int, String) -> Unit
    val onDeleteButtonClick: (Int) -> Unit
    val onEditButtonClick: (Int) -> Unit
    val onCheckButtonClick: (Int) -> Unit
    val onUpButtonClick: (Int) -> Unit
    val onDownButtonClick: (Int) -> Unit
    val onAddressIconClick: (Int) -> Unit
}