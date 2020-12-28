package com.adriano.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.adriano.main.Game;

public class Tile {
	
	public static BufferedImage TILE_FLOOR = Game.spritesheet.getSprite(0, 0, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage TILE_WALL = Game.spritesheet.getSprite(World.TILE_SIZE, 0, World.TILE_SIZE, World.TILE_SIZE);
	
	private BufferedImage sprite;
	private int x, y;
	
	public Tile(BufferedImage sprite, int x, int y) {
		super();
		this.sprite = sprite;
		this.x = x;
		this.y = y;
	}

	public void render(Graphics g) {
		g.drawImage(this.sprite, x - Camera.x, y - Camera.y, null);
	}
}
