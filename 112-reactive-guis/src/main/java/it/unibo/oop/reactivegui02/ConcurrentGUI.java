package it.unibo.oop.reactivegui02;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.LoggerFactory;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private final JLabel txt = new JLabel("0");

    /**
     *  Ignore.
     */
    public ConcurrentGUI() {
        super();
        JFrameUtil.dimensionJFrame(this);

        final JPanel canvas = new JPanel();
        canvas.setLayout(new BoxLayout(canvas, BoxLayout.X_AXIS));
        canvas.add(txt);

        final JButton btnUp = new JButton("Up");
        final JButton btnDown = new JButton("Down");
        final JButton btnStop = new JButton("Stop");
        canvas.add(btnUp);
        canvas.add(btnDown);
        canvas.add(btnStop);

        this.add(canvas);
        this.setVisible(true);

        final Counter c = new Counter();
        new Thread(c).start();

        btnUp.addActionListener(e -> c.changeDirection(true));
        btnDown.addActionListener(e -> c.changeDirection(false));
        btnStop.addActionListener(e -> {
            btnUp.setEnabled(false);
            btnDown.setEnabled(false);
            btnStop.setEnabled(false);
            c.stopCounter();
        });
    }

    private void updateText(final String newTxt) {
        this.txt.setText(newTxt);
    }

    private final class Counter implements Runnable {

        private volatile boolean direction;
        private int current;
        private volatile boolean stop;
        private final Object lock;

        private Counter() {
            this.current = 0;
            this.direction = true;
            this.stop = false;
            this.lock = new Object();
        }

        @Override
        public void run() {
            while (!stop) {
                try {
                    synchronized (lock) {
                        if (direction) {
                        this.add();
                    } else {
                        this.sub();
                    }
                    updateText(String.valueOf(current));
                    }
                    Thread.sleep(100);
                } catch (final InterruptedException ex) {
                    LoggerFactory.getLogger(ConcurrentGUI.class).error(ex.getMessage(), ex);
                }
            }
        }

        private void changeDirection(final boolean dir) {
            this.direction = dir;
        }

        private void stopCounter() {
            this.stop = true;
        }

        private void add() {
            this.current++;
        }

        private void sub() {
            this.current--;
        }
    }
}
