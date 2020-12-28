package com.adriano.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.adriano.graficos.Spritesheet;
import com.adriano.graficos.UI;
import com.adriano.main.Game;
import com.adriano.world.Camera;
import com.adriano.world.World;

public class Player extends Entity {
	
	public boolean right, up, left, down;
	public int right_dir = 0, left_dir = 1;
	public int dir = 0;
	public double speed = 1.4;
	
	private int frames2 = 0, frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	private BufferedImage[] playerDamagedLeft;
	private BufferedImage[] playerDamagedRight;
	
	private boolean hasGun = false;
	
	public int ammo = 0;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	public boolean shoot = false, mouseshoot;
	public int mx, my;
	
	public double life = 100;
	public static int maxLife = 100;
	
//	private int maskX = 8, maskY = 8, maskW = 10, maskH = World.TILE_SIZE;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamagedLeft = new BufferedImage[4];
		playerDamagedRight = new BufferedImage[4];
		for (int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(World.TILE_SIZE * 2 + (i * World.TILE_SIZE), 0, World.TILE_SIZE, World.TILE_SIZE);
			leftPlayer[i] = Game.spritesheet.getSprite(World.TILE_SIZE * 2 + (i * World.TILE_SIZE), World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
			
			playerDamagedRight[i] = Game.spritesheet.getSprite(i * World.TILE_SIZE, World.TILE_SIZE * 2, World.TILE_SIZE, World.TILE_SIZE);
			playerDamagedLeft[i] = Game.spritesheet.getSprite(i * World.TILE_SIZE, World.TILE_SIZE * 3, World.TILE_SIZE, World.TILE_SIZE);
		}
	}
	
	public void tick() {
		moved = false;
		if (right && World.isFree((int) (x + speed), getY())) {
			moved = true;
			dir = right_dir;
			x += speed;
		}
		else if (left && World.isFree((int) (x - speed), getY())) {
			moved = true;
			dir = left_dir;
			x -= speed;
		}
		
		if (up && World.isFree(getX(), (int) (y - speed))) {
			moved = true;
			y -= speed;
		}
		else if (down && World.isFree(getX(), (int) (y + speed))) {
			moved = true;
			y += speed;
		}
		
		// Para ele ficar com as pernas para baixo parado
		if (!moved) {
			/**
			 * Essa logica do contador 
			 * repetiu aqui para que 
			 * o timing da animacao seja respeitado
			 */
			frames2++;
			if (frames2 == maxFrames) {
				frames2 = 0;
				index = 0;
			}
		}
		
		if (moved) {
			frames++;
			if (frames == maxFrames) {
				frames = 0;
				index++;
				
				if (index > maxIndex)
					index = 0;
			}
		}
		
		checkLifePack();
		checkAmmo();
		checkGun();
		
		if (isDamaged) {
			this.damageFrames++;
			if (this.damageFrames == 8) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		
		if (shoot) {
			shoot = false;
			if (hasGun && ammo > 0) {				
				// Criar a bala e atirar!
				ammo--;
				BulletShoot bullet;
				int dx, dy = 0;
				int px, py = 8;
				if (dir == right_dir) {
					dx = 1;
					px = 17;
				} else {				
					dx = -1;
					px = -4;
				}
				
				bullet = new BulletShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, dy);
				Game.bullets.add(bullet);
			}
		}
		
		if (mouseshoot) {
			mouseshoot = false;
			if (hasGun && ammo > 0) {
				// Criar a bala e atirar!
				ammo--;
				BulletShoot bullet;
				int px, py = 8;
				if (dir == right_dir) {
					px = 17;
				} else {
					px = -4;
				}
				double angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x));
				double dx = Math.cos(angle); 
				double dy = Math.sin(angle);;
				
				
				bullet = new BulletShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, dy);
				Game.bullets.add(bullet);
			}
		}
		
		if (life <= 0) {
			/*
			 * Game Over!
			 * */
			Game.gameState = "GAME_OVER";
		}
		
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH / 2), 0, (World.WIDTH * World.TILE_SIZE) - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT / 2), 0, (World.HEIGHT * World.TILE_SIZE) - Game.HEIGHT);
	}
	
	public void checkGun() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if (atual instanceof Weapon) {
				if (Entity.isColliding(this, atual)) {
					hasGun = true;
					System.out.println("PEGOU ARMA");
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void checkAmmo() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if (atual instanceof Bullet) {
				if (Entity.isColliding(this, atual)) {
					ammo += 10000;
					System.out.println("Munição atual: " + ammo);
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void checkLifePack() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if (atual instanceof Lifepack) {
				if (Entity.isColliding(this, atual) && life < 100) {
					life += 10;
					if (life > 100)
						life = 100;
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if (!isDamaged) {			
			if(dir == right_dir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if (hasGun) {
					// Desenhar arma para direita
					g.drawImage(Entity.GUN_RIGHT, this.getX() - Camera.x + 6, this.getY() - Camera.y + 2, null);
				}
			} else if (dir == left_dir) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if (hasGun) {
					// Desenhar arma para esquerda
					g.drawImage(Entity.GUN_LEFT, this.getX() - Camera.x - 6, this.getY() - Camera.y + 2, null);
				}
			}
		} else {
			if(dir == right_dir) {
				g.drawImage(playerDamagedRight[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			} else if (dir == left_dir) {
				g.drawImage(playerDamagedLeft[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
	}
	
}
