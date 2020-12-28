package com.adriano.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.adriano.entities.Bullet;
import com.adriano.entities.Enemy;
import com.adriano.entities.Entity;
import com.adriano.entities.Lifepack;
import com.adriano.entities.Player;
import com.adriano.entities.Weapon;
import com.adriano.graficos.Spritesheet;
import com.adriano.main.Game;

public class World {
	
	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;
	private final int render_division = TILE_SIZE / 4;
	
	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int[] pixels = new int[map.getWidth() * map.getHeight()];
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			
			tiles = new Tile[map.getWidth() * map.getHeight()];
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			for (int xx = 0; xx < map.getWidth(); xx++) {
				for (int yy = 0; yy < map.getHeight(); yy++) {
					int pixelAtual = pixels[xx + (yy * map.getWidth())];
					tiles[xx + (yy * WIDTH)] = new FloorTile(Tile.TILE_FLOOR, xx * TILE_SIZE, yy * TILE_SIZE);
					switch (pixelAtual) {
					case 0xFF000000: {
						// Floor
						tiles[xx + (yy * WIDTH)] = new FloorTile(Tile.TILE_FLOOR, xx * TILE_SIZE, yy * TILE_SIZE);
						break;
					}
					case 0xFFFFFFFF: {
						// Wall
						tiles[xx + (yy * WIDTH)] = new WallTile(Tile.TILE_WALL, xx * TILE_SIZE, yy * TILE_SIZE);
						break;
					}
					case 0xFF0000FF: {
						// Player
						Game.player.setX(xx * TILE_SIZE);
						Game.player.setY(yy * TILE_SIZE);
						break;
					}
					case 0xFFFF0000: {
						// Enemy
						BufferedImage[] buf = new BufferedImage[2];
						buf[0] = Game.spritesheet.getSprite(7 * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
						buf[1] = Game.spritesheet.getSprite(8 * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
						Enemy enemy = new Enemy(xx * TILE_SIZE, yy * TILE_SIZE, TILE_SIZE, TILE_SIZE, buf);
						Game.entities.add(enemy);
						Game.enemies.add(enemy);
						break;
					}
					case 0xFFFF6A00: {
						// Weapon
						Game.entities.add(new Weapon(xx * TILE_SIZE, yy * TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.WEAPON_EN));
						break;
					}
					case 0xFFFF7F7F: {
						// Life pack
						Game.entities.add(new Lifepack(xx * TILE_SIZE, yy * TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.LIFEPACK_EN));
						break;
					}
					case 0xFFFFD800: {
						// Bullet
						Game.entities.add(new Bullet(xx * TILE_SIZE, yy * TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.BULLET_EN));
						break;
					}
					default:
						// Floor
						tiles[xx + (yy * WIDTH)] = new FloorTile(Tile.TILE_FLOOR, xx * TILE_SIZE, yy * TILE_SIZE);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isFree(int xnext, int ynext) {
		int x1 = xnext / TILE_SIZE;
		int y1 = ynext / TILE_SIZE;
		
		int x2 = (xnext + TILE_SIZE - 1) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;
		
		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext + TILE_SIZE - 1) / TILE_SIZE;;
		
		int x4 = (xnext + TILE_SIZE - 1) / TILE_SIZE;
		int y4 = (ynext + TILE_SIZE - 1) / TILE_SIZE;;
		
		return !(tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile ||
				tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile ||
				tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile ||
				tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile);
	}
	
	public static void restartGame(String level) {
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.player = new Player(0, 0, World.TILE_SIZE, World.TILE_SIZE, Game.spritesheet.getSprite(World.TILE_SIZE * 2, 0, World.TILE_SIZE, World.TILE_SIZE));
		Game.entities.add(Game.player);
		Game.world = new World("/" + level);
		return;
	}
	
	public void render(Graphics g) {
		int xstart = Camera.x >> render_division;
		int ystart = Camera.y >> render_division;
		
		int xfinal = xstart + (Game.WIDTH >> render_division) + 2;
		int yfinal = ystart + (Game.HEIGHT >> render_division) + 2;
		
		for (int xx = xstart; xx < xfinal; xx++) {
			for (int yy = ystart; yy < yfinal; yy++) {
				if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;
				Tile tile = tiles[xx + (yy * WIDTH)];
				tile.render(g);
			}
		}
	}
}
