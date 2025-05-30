package com.amineaytac.biblictora.ui.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.amineaytac.biblictora.R
import com.amineaytac.biblictora.core.common.ResponseState
import com.amineaytac.biblictora.core.domain.randomquote.GetRandomQuoteUseCase
import com.amineaytac.biblictora.core.network.dto.quotes.QuoteResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class WidgetUpdateReceiver : BroadcastReceiver() {

    @Inject
    lateinit var getRandomQuoteUseCase: GetRandomQuoteUseCase

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            updateWidgetWithNewQuote(context)
        } else {
            Log.e("Widget", "Context or Intent null")
        }
    }

    private fun updateWidgetWithNewQuote(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, WidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        if (appWidgetIds.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                fetchRandomWord(context)
            }
        }
    }

    private fun updateWidgetUI(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        quote: QuoteResponse
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        views.setTextViewText(R.id.tv_quote, quote.quote)
        views.setTextViewText(R.id.tv_author, quote.author)
        for (appWidgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private suspend fun fetchRandomWord(context: Context): QuoteResponse {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, WidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        var result = QuoteResponse("Loading...", "Loading...")
        try {
            getRandomQuoteUseCase().collect { responseState ->
                when (responseState) {
                    is ResponseState.Success -> {
                        result = responseState.data
                        withContext(Dispatchers.Main) {
                            updateWidgetUI(context, appWidgetManager, appWidgetIds, result)
                        }
                    }

                    is ResponseState.Error -> {
                        Log.e("Widget", " ${responseState.message}")
                    }

                    is ResponseState.Loading -> {
                        Log.d("Widget", "Loading")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Widget", e.message.toString())
        }
        return result
    }
}