package com.adriano.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.adriano.entities.Player;
import com.adriano.main.Game;

public class UI {
	
	public void render(Graphics g) {
		g.setColor(Color.red);
		g.fillRect(20, 4, 70, 9);
		g.setColor(Color.green);
		g.fillRect(20, 4, (int) ((Game.player.life / Player.maxLife) * 70), 9);
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 9));
		g.drawString((int) Game.player.life + " / " + Player.maxLife, 21, 12);
	}
	
}
