package com.plcoding.cryptotracker.crypto.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinPriceDto(
    val priceUsd: Double,
    val time: Long,
)
