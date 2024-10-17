package com.plcoding.cryptotracker.crypto.presentation.coin_list

import com.plcoding.cryptotracker.core.domain.util.NetworkError

sealed interface CoinListEvent {
    data class Error(val message: NetworkError): CoinListEvent
    data object Nothing : CoinListEvent
}