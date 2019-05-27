package ch.bildspur.threescan.model

import com.google.gson.annotations.Expose

data class NumberRange(
        @Expose val lowValue: Double,
        @Expose val highValue: Double)