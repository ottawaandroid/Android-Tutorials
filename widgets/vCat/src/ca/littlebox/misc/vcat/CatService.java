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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;

public class CatService extends Service {
    // We add an extra to our intents to identify their purpose
	private final static int HAPPINESS_STARTING = 1;
	private final static int HAPPINESS_PURRING = 5;
	private final static int HAPPINESS_MEOWING = -4;
	private final static int HAPPINESS_INCREMENT = 1;
	private final static int HAPPINESS_DECREMENT = 1;
	
	// How frequent (in ms) is the alarm to decrement happiness
	private final static int ALARM_FREQUENCY = 60*60*1000;
		
	// How happy is the cat?
	private int mHappy = HAPPINESS_STARTING;
	
	// Handle to the vibrator service
	private Vibrator mVibrator;

	// Handle to the audio manager
	private AudioManager  mAudioManager;

	// Handle to the alarm manager
	private AlarmManager mAlarmManager;
	
	// Sound pool for meows and purring
	private SoundPool mSoundPool;
	
	// PendingIntent for sending alarm
	PendingIntent mAlarmIntent;
	
	// Handle for the sounds in our soundpool
    private int meowID;
    private int miniPurrID;
    private int purrID;

    // We add an extra to our intents to identify their purpose
	public final static String INTENT_TYPE_KEY = "type";
	public final static int INTENT_START = 0x0;
	public final static int INTENT_TOUCH = 0x1;
	public final static int INTENT_ALARM = 0x2;

	@Override 
	public void onCreate() {
        // Get a handle to the vibrator service
		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		// Get a handle to the audio manager service
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        // Get a handle to the alarm manager
		mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

		// Create a sound pool for our meows and purrs
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        
        // Load the sounds and assign them to IDs
        meowID = mSoundPool.load(this, R.raw.meow, 1);
        miniPurrID = mSoundPool.load(this, R.raw.purr1, 1);
        purrID = mSoundPool.load(this, R.raw.purr2, 1);
        
        // Create a pending intent that we will send to ourselves using the  
        // alarm manager
        Intent intent = new Intent(this, this.getClass());
        intent.putExtra(INTENT_TYPE_KEY, INTENT_ALARM);
        mAlarmIntent = PendingIntent.getService(this, 
        		INTENT_ALARM, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        // We want the alarm to go off 1 minute from now, and repeat every
        // minute.
        long firstTime = SystemClock.elapsedRealtime();
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
        		firstTime, ALARM_FREQUENCY, mAlarmIntent);
	}
	
	@Override 
	public void onDestroy() {
		mAlarmManager.cancel(mAlarmIntent);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {	
		// This happens if the service is restarted
		if (intent == null)
			return START_STICKY;
		
		// The cat reacts differently depending on how happy it is. 
		int type = intent.getIntExtra(INTENT_TYPE_KEY, INTENT_START);
		switch(type) {
		case INTENT_TOUCH:
			mHappy += HAPPINESS_INCREMENT;
			if (mHappy < 0)
				mHappy = 0;
			else if (mHappy >= HAPPINESS_PURRING)
				purr();
			else
				miniPurr();
			break;
		case INTENT_ALARM:
			mHappy -= HAPPINESS_DECREMENT;
			if (mHappy <= HAPPINESS_MEOWING) {
				meow();
				mHappy = 0;
			}
			break;
		case INTENT_START:
		default:
			break;
		}

		// Tell service to persist until stopped
		return START_STICKY;
	}
	
	private void playSound(int soundID) {
		// If the ring mode is set to silent or vibrate, the user probably doesn't
		// want the cat to make any noise
		if (mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			return;
		
    	float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    	mSoundPool.play(soundID, streamVolume, streamVolume, 1, 0, 1f);  		
	}
	
	private void miniPurr() {
		playSound(miniPurrID);
	}
	
	private void purr() {
		long[] pattern = { 30, 400, 30, 400, 30, 400, 30, 400, 30, 400,
						   30, 400, 30, 400, 30, 400, 30, 400, 30, 400,
						   30, 400, 30, 400, 30, 400, 30, 400, 30, 400,
						   30, 400, 30, 400, 30, 400, 30, 400, 30, 400 };
		playSound(purrID);
		mVibrator.vibrate(pattern, -1);
	}
	
	private void meow() {
		playSound(meowID); 		
	}
}
