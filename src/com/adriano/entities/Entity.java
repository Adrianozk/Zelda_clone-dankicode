package com.adriano.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.adriano.main.Game;
import com.adriano.world.Camera;
import com.adriano.world.World;

public class Entity {
	
	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(6 * World.TILE_SIZE, 0, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(7 * World.TILE_SIZE, 0, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage BULLET_EN = Game.spritesheet.getSprite(6 * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(7 * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage ENEMY_FEEDBACK1 = Game.spritesheet.getSprite(7 * World.TILE_SIZE, 2 * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage ENEMY_FEEDBACK2 = Game.spritesheet.getSprite(8 * World.TILE_SIZE, 2 * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage GUN_RIGHT = Game.spritesheet.getSprite(8 * World.TILE_SIZE, 0, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage GUN_LEFT = Game.spritesheet.getSprite(9 * World.TILE_SIZE, 0, World.TILE_SIZE, World.TILE_SIZE);
	
	protected double x, y;
	
	protected int width, height;
	
	private BufferedImage sprite;
	
	private int maskX, maskY, maskW, maskH;	

	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		
		this.maskX = 0;
		this.maskY = 0;
		this.maskW = width;
		this.maskH = height;
	}

	public void setMask(int maskX, int maskY, int maskW, int maskH) {
		this.maskX = maskX;
		this.maskY = maskY;
		this.maskW = maskW;
		this.maskH = maskH;
	}

	
	public void tick() {
		
	}
	
	public static boolean isColliding(Entity e1, Entity e2) {
		Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskX, e1.getY() + e1.maskY, e1.maskW, e1.maskH);
		Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskX, e2.getY() + e2.maskY, e2.maskW, e2.maskH);
		
		return e1Mask.intersects(e2Mask);
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getX() {
		return (int) x;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return (int) y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
