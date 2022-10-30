package com.example.widget.test

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews


private const val ON_CLICK_PLUS = "on_click_plus"
private const val ON_CLICK_MINUS = "on_click_minus"
var counter = 0

/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, counter)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        intent?.action?.let { action ->
            when (action) {
                ON_CLICK_PLUS -> {
                    if (counter <= 99) {
                        counter++
                    }
                    context?.let { updateWidget(it) }
                }
                ON_CLICK_MINUS -> {
                    if (counter >= -99) {
                        counter--
                    }
                    context?.let { updateWidget(it) }
                }
                else -> {
                    // Do nothing
                }
            }
        }
    }

    private fun updateWidget(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisAppWidgetComponentName = ComponentName(context.packageName, javaClass.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName)
        onUpdate(context, appWidgetManager, appWidgetIds)
    }
}

@SuppressLint("RemoteViewLayout")
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    counter: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.new_app_widget)

    views.setOnClickPendingIntent(R.id.btn_plus, getPendingSelfIntent(context, ON_CLICK_PLUS))
    views.setOnClickPendingIntent(R.id.btn_minus, getPendingSelfIntent(context, ON_CLICK_MINUS))

    views.setTextViewText(R.id.btn_plus, context.getString(R.string.plus))
    views.setTextViewText(R.id.btn_minus, context.getString(R.string.minus))
    views.setTextViewText(R.id.text_number, counter.toString())
    views.setProgressBar(R.id.determinate_bar, 100, counter, false)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

fun getPendingSelfIntent(context: Context?, action: String?): PendingIntent? {
    val intent = Intent(context, NewAppWidget::class.java)
    intent.action = action
    return PendingIntent.getBroadcast(context, 0, intent, 0)
}