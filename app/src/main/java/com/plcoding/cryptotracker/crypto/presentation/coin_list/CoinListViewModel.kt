package com.plcoding.cryptotracker.crypto.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.presentation.coin_detail.DataPoint
import com.plcoding.cryptotracker.crypto.presentation.models.CoinUi
import com.plcoding.cryptotracker.crypto.presentation.models.toCoinUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CoinListViewModel(
    private val coinDataSource: CoinDataSource,
) : ViewModel() {

    private var _state = MutableStateFlow(CoinListState())
    val state get() = _state.asStateFlow()

    private var _events = MutableStateFlow<CoinListEvent>(CoinListEvent.Nothing)
    val events get() = _events.asStateFlow()

    fun resetEvents() {
        _events.update { CoinListEvent.Nothing }
    }

    init {
        loadCoins()
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            coinDataSource.getCoins()
                .onSuccess { coins ->
                    _state.update { it.copy(isLoading = false, coins = coins.map { it.toCoinUi() }) }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.update { CoinListEvent.Error(error) }
                }
        }
    }

    fun onAction(action: CoinListAction) {
        when (action) {
            is OnCoinCLicked -> {
                selectCoin(action.coinUi)
            }

            OnBack -> {
                _state.update { it.copy(selectedCoin = null) }
            }
        }
    }

    private fun selectCoin(coinUi: CoinUi) {
        _state.update { it.copy(selectedCoin = coinUi) }

        viewModelScope.launch {
            coinDataSource.getCoinHistory(
                coinId = coinUi.id,
                start = ZonedDateTime.now().minusDays(5),
                end = ZonedDateTime.now()
            ).onSuccess { history ->
                val dataPoints = history
                    .sortedBy { it.dateTime }
                    .map {
                        DataPoint(
                            x = it.dateTime.hour.toFloat(),
                            y = it.priceUsd.toFloat(),
                            xLabel = DateTimeFormatter.ofPattern("ha\nM/d").format(it.dateTime)
                        )
                    }

                _state.update { it.copy(selectedCoin = it.selectedCoin?.copy(coinPriceHistory = dataPoints)) }
            }.onError { error ->
                _events.update { CoinListEvent.Error(error) }
            }
        }
    }
}