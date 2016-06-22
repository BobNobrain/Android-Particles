package ru.sdevteam.testapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by user on 22.06.2016.
 */
public class Particle
{
	private static final int MAX_TEMPERATURE = 100;
	public static final int MAX_RADIUS = 10;
	public static final int MIN_RADIUS = 3;

	private Vec2F coords;
	private int rad;
	public Vec2F getLocation() { return coords; }

	private Vec2F v;
	public Vec2F getVelocity() { return v; }

	private int baseColor;
	private static Random rnd = new Random();;
	private Paint brush;
	private int temperature;

	public Particle(int x, int y)
	{
		coords = new Vec2F(x, y);
		this.rad = 5;
		this.baseColor = Color.GRAY;
		v = new Vec2F(0, 0);
		brush = new Paint();
		temperature = 0;
	}


	public void update()
	{
		coords.add(v);
		if(temperature > 0) --temperature;
	}

	public float findPenetration(Particle with)
	{
		float dx = coords.x - with.coords.x, dy = coords.y - with.coords.y;
		int r = rad + with.rad;
		return r - (float)Math.sqrt(dx*dx + dy*dy);
	}
	public boolean collides(Particle with)
	{
		float dx = coords.x - with.coords.x, dy = coords.y - with.coords.y;
		int r = rad + with.rad;
		return r*r > dx*dx + dy*dy;
	}

	public int getColor()
	{
		if(temperature == 0) return baseColor;
		int red =	(baseColor>>16) & 255,
			green = (baseColor>>8) & 255,
			blue =	(baseColor) & 255;

		red = red + (255 - red)*temperature/MAX_TEMPERATURE;
		green = green + (0 - green)*temperature/MAX_TEMPERATURE;
		blue = blue + (0 - blue)*temperature/MAX_TEMPERATURE;
		return Color.rgb(red, green, blue);
	}

	public void draw(Canvas canvas)
	{
		brush.setColor(getColor());
		canvas.drawCircle(coords.x, coords.y, rad, brush);
	}

	public void heatUp()
	{
		temperature = MAX_TEMPERATURE;
	}

	public static void onCollision(Particle p1, Particle p2, float penetration)
	{
		p1.heatUp(); p2.heatUp();
		/*Vec2F tmpV = p1.v;
		p1.v = p2.v;
		p2.v = tmpV;*/

		Vec2F r12 = new Vec2F(p2.coords.x, p2.coords.y);
		r12.sub(p1.coords);

		r12.setLength(penetration/2);
		p2.coords.add(r12);
		p1.coords.sub(r12);

		r12.setLength(1);
		Vec2F p1y = new Vec2F(r12, r12.scalar(p1.v));
		Vec2F p2y = new Vec2F(r12, r12.scalar(p2.v));
		//System.out.println(penetration);
		p2.v.sub(p2y);
		p2.v.add(p1y);

		p1.v.sub(p1y);
		p1.v.add(p2y);
	}


	public static Particle createRandom(int limX, int limY)
	{
		//if(rnd == null) rnd = new Random();
		Particle random = new Particle(rnd.nextInt(limX), rnd.nextInt(limY));
		random.rad = MIN_RADIUS + rnd.nextInt(MAX_RADIUS - MIN_RADIUS);
		int gray = 64 + rnd.nextInt(128);
		random.baseColor = Color.rgb(gray, gray, gray);
		return random;
	}

	public static void placeRandom(Particle p, int limX, int limY)
	{
		p.coords.x = rnd.nextInt(limX);
		p.coords.y = rnd.nextInt(limY);
	}
}
