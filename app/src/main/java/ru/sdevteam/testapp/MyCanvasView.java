package ru.sdevteam.testapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by user on 21.06.2016.
 */
public class MyCanvasView extends View implements Timer.Listener
{
	private static final int PARTICLES_COUNT = 50;
	private static final float FRICTION = 0.96F;
	private static final float TOUCH_ACCEL = 3F;
	public static final int VELOCITY_THRESHOLD = 4;

	private Paint p;
	private ArrayList<Particle> particles;
	private float mx, my;
	private boolean holdingDown;
	private Timer t;


    public MyCanvasView(Context context, AttributeSet attr)
	{
        super(context, attr);
        p = new Paint();
        p.setARGB(255, 247, 34, 9);

		holdingDown = false;
		mx = my = 0;
    }

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		if(particles == null)
		{
			particles = new ArrayList<>(PARTICLES_COUNT);
			for (int i = 0; i < PARTICLES_COUNT; i++)
			{
				Particle another = Particle.createRandom(w, h);
				boolean success = false;
				while(!success)
				{
					success = true;
					for(int j = 0; j < i; j++)
					{
						if (particles.get(j).collides(another))
						{
							success = false;
							break;
						}
					}
					if(!success) Particle.placeRandom(another, w, h);
				}
				particles.add(another);
			}

			t = new Timer(this);
			t.start();
		}
	}

	private void updateParticles()
	{
		// collisions
		for(int i=0; i<PARTICLES_COUNT; i++)
		{
			Particle pi = particles.get(i);
			pi.update();
			for(int j=i+1; j<PARTICLES_COUNT; j++)
			{
				float penetration = pi.findPenetration(particles.get(j));
				if(penetration > 0F)
				{
					Particle.onCollision(pi, particles.get(j), penetration);
				}
			}
		}
		if(holdingDown)
		{
			// attractor
			for (int i = 0; i < PARTICLES_COUNT; i++)
			{
				Particle pi = particles.get(i);
				Vec2F accel = new Vec2F(mx-pi.getLocation().x, my-pi.getLocation().y, TOUCH_ACCEL);
				pi.getVelocity().add(accel);
			}
		}
		for(int i=0; i<PARTICLES_COUNT; i++)
		{
			// screen bounds & friction
			Particle pi = particles.get(i);

			if(pi.getVelocity().getLength2() > VELOCITY_THRESHOLD)
			{
				pi.getVelocity().mul(FRICTION);
			}

			if(pi.getLocation().x > this.getWidth() + Particle.MAX_RADIUS)
				pi.getLocation().x = -Particle.MAX_RADIUS;

			if(pi.getLocation().x < -Particle.MAX_RADIUS)
				pi.getLocation().x = this.getWidth() + Particle.MAX_RADIUS;

			if(pi.getLocation().y > this.getHeight() + Particle.MAX_RADIUS)
				pi.getLocation().y = -Particle.MAX_RADIUS;

			if(pi.getLocation().y < -Particle.MAX_RADIUS)
				pi.getLocation().y = this.getHeight() + Particle.MAX_RADIUS;
		}
	}

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mx, my, 20, p);
		for (Particle p : particles)
		{
			p.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		super.onTouchEvent(event);
		if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			mx = event.getX();
			my = event.getY();
			holdingDown = true;
			invalidate();
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			holdingDown = false;
		}
		return true;
	}

	@Override
	public void onTick()
	{
		updateParticles();
		postInvalidate();
	}
}
