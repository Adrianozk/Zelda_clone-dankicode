package com.adriano.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.adriano.main.Game;
import com.adriano.world.Camera;
import com.adriano.world.World;

public class Enemy extends Entity {
	
	private double speed = 0.4;
	
	private int frames = 0, maxFrames = 15, index = 0, maxIndex = 1;
	private BufferedImage[] sprites;
	private BufferedImage[] damageSprites;
	
	private int life = 10;
	
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;
	
	private int maskX = 8, maskY = 8, maskW = 8, maskH = 11;

	public Enemy(int x, int y, int width, int height, BufferedImage[] sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2];
		sprites[0] = sprite[0];
		sprites[1] = sprite[1];
		
//		damageSprites[0] = ENEMY_FEEDBACK1;
//		damageSprites[1] = ENEMY_FEEDBACK2;
	}
	
	public void tick() {
		if (!isCollidingWithPlayer()) {			
			if (Game.rand.nextInt(100) < 50) {
				if ((int) x < Game.player.getX() && World.isFree((int) (x + speed), this.getY())
						/*!isColliding((int) (x + speed), this.getY())*/) {
					x += speed;
				} else if ((int) x > Game.player.getX() && World.isFree((int) (x - speed), this.getY())
						/*!isColliding((int) (x - speed), this.getY())*/) {
					x -= speed;
				}
				
				if ((int) y < Game.player.getY() && World.isFree(this.getX(), (int) (y + speed))
						/*!isColliding(this.getX(), (int) (y + speed))*/) {
					y += speed;
				} else if ((int) y > Game.player.getY() && World.isFree(this.getX(), (int) (y - speed))
						/*!isColliding(this.getX(), (int) (y - speed))*/) {
					y -= speed;
				}
			}
		} else {
			// Estamos perto do player
			// O que podemos fazer?
			if (Game.rand.nextInt(100) < 10) {	
				Game.player.life -= Game.rand.nextInt(3);
				Game.player.isDamaged = true;
			}
		}
		
		frames++;
		if (frames == maxFrames) {
			frames = 0;
			index++;
			
			if (index > maxIndex)
				index = 0;
		}
		
		collidingBullet();
		
		if (life <= 0) {
			destroySelf();
		}
		
		if (isDamaged) {
			damageCurrent++;
			if (damageCurrent >= damageFrames) {
				damageCurrent = 0;
				isDamaged = false;
			}
		}
	}
	
	public void destroySelf() {
		Game.entities.remove(this);
		Game.enemies.remove(this);
	}
	
	public void collidingBullet() {
		for (int i = 0; i < Game.bullets.size(); i++) {
			Entity entity = Game.bullets.get(i);
			if (entity instanceof BulletShoot) {
				if (Entity.isColliding(this, entity)) {
					isDamaged = true;
					life--;
					Game.bullets.remove(i);
					System.out.println("life = " + life);
					return;
				}
			}
		}
		
		return;
	}
	
	public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskX, this.getY() + maskY, maskW, maskH);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), World.TILE_SIZE, World.TILE_SIZE);
		
		return enemyCurrent.intersects(player);
	}
	
	public boolean isColliding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskX, ynext + maskY, maskW, maskH);
		
		for (int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if (e == this)
				continue;
			Rectangle targetEnemy = new Rectangle(e.getX() + maskX, e.getY() + maskY, maskW, maskH);
			if (enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void render(Graphics g) {
		if (!isDamaged) {			
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		} else {
			if (index == 0) {				
				g.drawImage(Entity.ENEMY_FEEDBACK1, this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
			if (index == 1) {				
				g.drawImage(Entity.ENEMY_FEEDBACK2, this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
		
//		g.setColor(Color.blue);
//		g.fillRect(this.getX() + maskX - Camera.x, this.getY() + maskY - Camera.y, maskW, maskH);
	}

}
