package com.payable.ipg.model

import java.io.Serializable

data class IPGUIConfig @JvmOverloads constructor(
    val buttonType : Int = 1,
    val statusViewDuration: Int = 5
) : Serializable