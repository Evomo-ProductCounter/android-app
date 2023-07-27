package com.evomo.productcounterapp.data.remote

import android.util.Log
import com.evomo.productcounterapp.data.model.OEEResponse
import com.evomo.productcounterapp.data.model.QuantityResponse
import com.evomo.productcounterapp.ui.main.home.OperatorViewModel
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketListener(private val viewModel: OperatorViewModel, private val type: String): WebSocketListener() {
    private val TAG = "WebSocketListener"

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        viewModel.setStatus(true)
        webSocket.send("Android Device Connected")
        Log.d(TAG, "onOpen:")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
//        viewModel.addMessage(Pair(false, text))
        if (type == "OEE") {
            val data = Gson().fromJson(text, OEEResponse::class.java)
//            Log.d("OEE", data.data.data.performance.toString())
            viewModel.addOEEData(data.data)
        }
        else {
            val data = Gson().fromJson(text, QuantityResponse::class.java)
            Log.d("QUANTITY", data.data.toString())
            viewModel.addQuantityData(data.data)
        }
        Log.d(TAG, "onMessage: $text")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.d(TAG, "onClosing: $code $reason")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        viewModel.setStatus(false)
        Log.d(TAG, "onClosed: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d(TAG, "onFailure: ${t.message} $response")
        super.onFailure(webSocket, t, response)
    }
}