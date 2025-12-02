package it.unibo.oop.lab.streams;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Modify this small program adding new filters.
 * Realize this exercise using as much as possible the Stream library.
 * <br>
 * 1) Convert to lowercase
 * <br>
 * 2) Count the number of chars
 * <br>
 * 3) Count the number of lines
 * <br>
 * 4) List all the words in alphabetical order
 * <br>
 * 5) Write the count for each word, e.g. "word word pippo" should output "pippo -> 1 word -> 2"
 *
 */
public final class LambdaFilter extends JFrame {

    @Serial
    private static final long serialVersionUID = 1760990730218643730L;

    private enum Command {
        /**
         * Commands.
         */
        IDENTITY("No modifications", Function.identity()),
        TOLOWER("To Lower", String::toLowerCase),
        COUNT_CHARS("Count Characters", s -> String.valueOf(s.length() - s.lines().count() + 1)),
        COUNT_LINES("Count Lines", s -> String.valueOf(s.lines().count())),
        ALPHABETICAL_ORDER("Sort in Alphabetical Order", s -> {
            return s.replace(" ", " \n")
            .lines()
            .sorted()
            .reduce(String::concat)
            .get();
        }),
        WORD_COUNTER("Word Counter", s -> {
            // It works, but it's not done the way it was intended to be done.
            // Replace with single instruction that uses the 'collect()' Stream method.
            final Map<String, Integer> counter = new HashMap<>();
            s.replace(" ", " \n")
            .lines()
            .forEach(word -> counter.merge(word, counter.getOrDefault(word, 1), (older, newer) -> older++));

            return counter.toString();
        });

        private final String commandName;
        private final Function<String, String> fun;

        Command(final String name, final Function<String, String> process) {
            commandName = name;
            fun = process;
        }

        @Override
        public String toString() {
            return commandName;
        }

        public String translate(final String s) {
            return fun.apply(s);
        }
    }

    private LambdaFilter() {
        super("Lambda filter GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel1 = new JPanel();
        final LayoutManager layout = new BorderLayout();
        panel1.setLayout(layout);
        final JComboBox<Command> combo = new JComboBox<>(Command.values());
        panel1.add(combo, BorderLayout.NORTH);
        final JPanel centralPanel = new JPanel(new GridLayout(1, 2));
        final JTextArea left = new JTextArea();
        left.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        final JTextArea right = new JTextArea();
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        right.setEditable(false);
        centralPanel.add(left);
        centralPanel.add(right);
        panel1.add(centralPanel, BorderLayout.CENTER);
        final JButton apply = new JButton("Apply");
        apply.addActionListener(ev ->
            right.setText(
                ((Command) Objects.requireNonNull(combo.getSelectedItem()))
                    .translate(left.getText())
            )
        );
        panel1.add(apply, BorderLayout.SOUTH);
        setContentPane(panel1);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int sw = (int) screen.getWidth();
        final int sh = (int) screen.getHeight();
        setSize(sw / 4, sh / 4);
        setLocationByPlatform(true);
    }

    /**
     * @param a unused
     */
    public static void main(final String... a) {
        final LambdaFilter gui = new LambdaFilter();
        gui.setVisible(true);
    }
}
