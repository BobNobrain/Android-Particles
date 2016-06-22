package ru.sdevteam.testapp;

/**
 * Created by user on 22.06.2016.
 */
public class Vec2F
{
	public float x, y;

	public Vec2F(float x, float y)
	{
		this.x = x; this.y = y;
	}

	public Vec2F(float dx, float dy, float len)
	{
		this(dx, dy);
		setLength(len);
	}

	public Vec2F(Vec2F original, float len)
	{
		this(original.x, original.y, len);
	}


	public void setLength(float len)
	{
		float curLen = x*x+y*y;
		float coeff = len/(float)Math.sqrt(curLen);
		x *= coeff; y *= coeff;
	}

	public void add(Vec2F value)
	{
		x += value.x;
		y += value.y;
	}
	public void sub(Vec2F value)
	{
		x -= value.x;
		y -= value.y;
	}

	public void mul(float by)
	{
		x *= by; y *= by;
	}

	public float getLength2()
	{
		return x*x+y*y;
	}

	public float scalar(Vec2F v)
	{
		return x*v.x + y*v.y;
	}
}
