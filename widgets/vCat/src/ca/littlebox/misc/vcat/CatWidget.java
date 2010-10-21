/**
 *   Copyright (C) 2010  Little Box Solutions
 *   
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 *   @author James Puderer
 *   @version 1.0
 */

package ca.littlebox.misc.vcat;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class CatWidget extends AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	// Request that the CatService be started
    	context.startService(new Intent(context, CatService.class));
    	
        // Create a PendingIntent to send to our service, when the widget gets clicked
    	//
        // HACK: Setting requestCode of the PendingIntent to the our message 
        // type, works around the problem of reusing PendingIntents when we 
        // don't want to.
    	Intent intent = new Intent(context, CatService.class);
        intent.putExtra(CatService.INTENT_TYPE_KEY, CatService.INTENT_TOUCH);
        PendingIntent pendingIntent = PendingIntent.getService(context, 
        		CatService.INTENT_TOUCH, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Get the layout for the widget and attach an on-click listener to the cat image
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setOnClickPendingIntent(R.id.cat, pendingIntent);

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            // Tell the AppWidgetManager to perform an update on the current App Widget
            appWidgetManager.updateAppWidget(appWidgetId, views);          
        }
    }

    // Stop the service, if there are no more cat widgets
	@Override
	public void onDisabled(Context context) {
		context.stopService(new Intent(context, CatService.class));
		super.onDisabled(context);
	} 
}