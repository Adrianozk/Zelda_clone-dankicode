package com.adriano.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.adriano.entities.BulletShoot;
import com.adriano.entities.Enemy;
import com.adriano.entities.Entity;
import com.adriano.entities.Player;
import com.adriano.graficos.Spritesheet;
import com.adriano.graficos.UI;
import com.adriano.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {
	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	private final int SCALE = 3;
	
	private int CUR_LEVEL = 1, MAX_LEVEL = 2;
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<BulletShoot> bullets;
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	
	public static Random rand;
	
	public UI ui;
	
	public static String gameState = "NORMAL";
	
	public Game() {
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		
		// Inicializando objetos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, World.TILE_SIZE, World.TILE_SIZE, spritesheet.getSprite(World.TILE_SIZE * 2, 0, World.TILE_SIZE, World.TILE_SIZE));
		entities.add(player);
		world = new World("/level1.png");
		
	}
	
	public void initFrame() {
		frame = new JFrame("Game #1");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		Game game = new Game();
		game.start();
	}
	
	public void tick() {
		if (gameState.equals("NORMAL")) {			
			for (int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
			
			for (int i = 0; i < bullets.size(); i++) {
				bullets.get(i).tick();
			}
			
			if (enemies.size() == 0) {
				// Avançar para o próximo nível
				CUR_LEVEL++;
				if (CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
				return;
			}
		} else if (gameState.equals("GAME_OVER" )) {
			System.out.println("Game over!!");
			String curWorld = "level" + CUR_LEVEL + ".png";
			World.restartGame(curWorld);
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(255, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
//		g.setFont(new Font("Arial", Font.BOLD, World.TILE_SIZE));
//		g.setColor(Color.white);
//		g.drawString("Olá mundo!", 19, 19);
		
		/* Renderização do jogo */
//		Graphics2D g2 = (Graphics2D) g;
		world.render(g);
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}
		ui.render(g);
		/***/
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 17));
		g.drawString("Munição: " + player.ammo, 550, 20);
		bs.show();
	}
	
	public void run() {
		// Lê o último momento em alta precisao
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0; // Define o FPS
		double ns = 1000000000 / amountOfTicks; // Define o momento correto para efetuar o update no jogo
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		// Loop do game
		while(isRunning) {
			long now = System.nanoTime(); // Le o tempo atual com alta precisao
			delta += (now - lastTime) / ns; // Subtrai o tempo atual com o anterior e divide por ns
			lastTime = now;
			if (delta >= 1) { // Isso significa que deu o tempo de um tick
				tick();
				render();
				frames++;
				delta--; // reseta o delta para calcular o próximo tick
			}
			
			if (System.currentTimeMillis() - timer >= 1000) {
//				System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}
		
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || 
				e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || 
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_UP || 
				e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || 
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.shoot = true;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || 
				e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || 
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_UP || 
				e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || 
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.shoot = false;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseshoot = true;
		player.mx = e.getX() / 3;
		player.my = e.getY() / 3;
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
