package edu.hitsz.application;

import edu.hitsz.application.game.Game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 监测是否连续按下hit或HIT，以触发彩蛋
 *
 * @author SunDocker
 */
public class BonusSceneController implements KeyListener {
    private boolean hOrH = false;
    private boolean iOrI = false;

    public BonusSceneController(Game game) {
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                char letter = e.getKeyChar();
                if (letter == 'h' || letter == 'H') {
                    hOrH = true;
                } else if (hOrH && !iOrI && (letter == 'i' || letter == 'I')) {
                    iOrI = true;
                } else if (iOrI && (letter == 't' || letter == 'T')) {
                    Game.bonusScene = true;
                } else {
                    hOrH = false;
                    iOrI = false;
                }
            }
        };
        game.addKeyListener(keyAdapter);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
