package com.adriano.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.adriano.main.Game;
import com.adriano.world.Camera;

public class BulletShoot extends Entity {
	
	private double dx, dy;
	private double speed = 4;
	
	private int life = 50, curLife = 0;

	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		
		this.dx = dx;
		this.dy = dy;
	}
	
	public void tick() {
		x += dx * speed;
		y += dy * speed;
		curLife++;
		if (curLife >= life) {
			Game.bullets.remove(this);
			return;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.yellow);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}
}
