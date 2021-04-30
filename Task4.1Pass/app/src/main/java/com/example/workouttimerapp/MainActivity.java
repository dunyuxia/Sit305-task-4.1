package com.example.workouttimerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
	private final String PREF_NAME = "WorkoutTimerApp";
	private final String WORKOUT_TYPE = "Workout_Type";
	private final String TIME_SPENT = "Time_Spent";
	private final String CACHED_TIME = "Cached_Time";
	private final String CACHED_STATE = "Cached_State";

	private Timer timer;
	private int time_spent;
	private boolean bCounting;

	private TextView message;
	private TextView time;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		String latestType = sharedPreferences.getString(WORKOUT_TYPE, "");
		int latestSeconds = sharedPreferences.getInt(TIME_SPENT, 0);

		message = findViewById(R.id.message);
		message.setText(String.format(Locale.UK, "You spent %02d:%02d:%02d on %s", latestSeconds / 3600, latestSeconds % 3600 / 60, latestSeconds % 60, latestType));

		time = findViewById(R.id.timer);

		if (latestType.isEmpty())
		{
			message.setVisibility(View.INVISIBLE);
		}

		if (savedInstanceState != null)
		{
			bCounting = savedInstanceState.getBoolean(CACHED_STATE);
			time_spent = savedInstanceState.getInt(CACHED_TIME);

			if (bCounting)
			{
				startTiming();
			}
		}
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putBoolean(CACHED_STATE, bCounting);
		outState.putInt(CACHED_TIME, time_spent);
	}

	private void writeSharedPreferences()
	{
		SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(WORKOUT_TYPE, ((EditText)findViewById(R.id.typeET)).getText().toString());
		editor.putInt(TIME_SPENT, time_spent);
		editor.apply();
	}

	private void startTiming()
	{
		timer = new Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				bCounting = true;
				time_spent++;
				runOnUiThread(() -> time.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", time_spent / 3600, time_spent % 3600 / 60, time_spent % 60)));
			}
		}, 1000, 1000);
	}

	public void onStart(View view)
	{
		startTiming();
	}

	public void onPause(View view)
	{
		timer.cancel();
		bCounting = false;
	}

	private void readPreference()
	{
		SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		String latestType = sharedPreferences.getString(WORKOUT_TYPE, "");
		int latestSeconds = sharedPreferences.getInt(TIME_SPENT, 0);

		message.setText(String.format(Locale.UK, "You spent %02d:%02d:%02d on %s", latestSeconds / 3600, latestSeconds % 3600 / 60, latestSeconds % 60, latestType));
	}

	public void onStop(View view)
	{
		timer.cancel();
		bCounting = false;
		writeSharedPreferences();
		readPreference();

		time_spent = 0;
		time.setText(R.string.zero);
	}
}