package com.plcoding.cryptotracker.core.presentation.util

import android.content.Context
import com.plcoding.cryptotracker.R
import com.plcoding.cryptotracker.core.domain.util.NetworkError

fun NetworkError.toString(context: Context): String {
    val resId =  when(this) {
        NetworkError.REQUEST_TIMEOUT -> R.string.error_request_timeout
        NetworkError.TOO_MANY_REQUEST -> R.string.error_to_many_request
        NetworkError.NO_INTERNET -> R.string.error_no_internet
        NetworkError.SERVER_ERROR -> R.string.error_server_error
        NetworkError.SERIALIZATION -> R.string.error_serialization
        NetworkError.UNKNOWN -> R.string.error_unknown
    }
    return context.getString(resId)
}