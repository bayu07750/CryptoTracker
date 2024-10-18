package com.plcoding.cryptotracker.crypto.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinHistoryDto(
    val data: List<CoinPriceDto>
)