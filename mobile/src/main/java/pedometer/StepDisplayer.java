/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pedometer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import controlvariable.MyPref;

/**
 * Counts steps provided by StepDetector and passes the current
 * step count to the activity.
 */
public class StepDisplayer implements StepListener {

    private int mCount = 0;
    private int mCountOn = 0;
    PedometerSettings mSettings;

    Context context;

    public StepDisplayer(PedometerSettings settings, Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mSettings = settings;
        notifyListener();
    }


    public void setSteps(int steps) {
        mCount = steps;
        notifyListener();
    }

    public void setStepsOn(int steps) {
        mCountOn = steps;
        notifyListener();
    }

    SharedPreferences preferences;

    public void onStep() {
        mCount++;

        if (preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(MyPref.pref_onScreen, false)) {
            mCountOn++;
        }

        notifyListener();
    }

    public void reloadSettings() {
        notifyListener();
    }

    public void passValue() {
    }


    //-----------------------------------------------------
    // Listener

    public interface Listener {
        public void stepsChanged(int value, int valueon);

        public void passValue();
    }

    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }

    public void notifyListener() {
        for (Listener listener : mListeners) {
            listener.stepsChanged((int) mCount, (int) mCountOn);
        }
    }


}
