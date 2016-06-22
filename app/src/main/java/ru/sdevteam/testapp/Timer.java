package ru.sdevteam.testapp;

/**
 * Created by user on 22.06.2016.
 */
public class Timer implements Runnable
{
	private Thread t;
	private boolean running;
	private int dt = 33;
	private Listener l;

	public Timer(Listener listener)
	{
		t = new Thread(this);
		t.setDaemon(true);
		l = listener;
		running = false;
	}
	public Timer(Listener listener, int frameDuration)
	{
		this(listener);
		dt = frameDuration;
	}

	public void start()
	{
		running = true;
		t.start();
	}

	public void stop()
	{
		running = false;
	}

	@Override
	public void run()
	{
		while(running)
		{
			try
			{
				Thread.sleep(dt);
				l.onTick();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public interface Listener
	{
		void onTick();
	}
}
