package com.wherefam.android.data.local

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GenericAction(val action: String, val data: JsonElement? = null)