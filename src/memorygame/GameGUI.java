package memorygame;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class creates a graphical user interface
 * for the pair game.
 * 
 * @author Daniel Krastev
 * @version 01/04/2016
 */
public class GameGUI
{
    private final static String GAME_TITLE = "Photographic Memory Puzzle";
    private final static String HELP_MESSAGE = "This is a simple pair game.\nOpen the cards, until you match all pairs.";

    private Board board;
    private JFrame frame;
    private JPanel container;

    //Buttons that are used from the user to select different difficulty for the game.
    private JRadioButtonMenuItem bgnButton, intButton, advButton;
    private JCheckBoxMenuItem pauseBox, soundBox;
    private JLabel timerLabel, attemptsLabel;
    private Timer tR;
    private int seconds;
    private boolean isFirstStart = true;

    /**
     * Create the new game instance and initialize it's state.
     */
    public GameGUI()
    {
        makeFrame();
        createTimer();
        newGame();
    }

    /**
     * Create the frame for the interface.
     */
    private void makeFrame()
    {
        frame = new JFrame(GAME_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   

        makeMenuBar();

        container = (JPanel)frame.getContentPane();
        container.setLayout(new BorderLayout(6,6));

        JPanel labels = new JPanel(new GridLayout());
        timerLabel = new JLabel("TIME: 000", JLabel.CENTER);
        attemptsLabel = new JLabel("ATTEMPTS: 000", JLabel.CENTER);
        labels.add(timerLabel);
        labels.add(attemptsLabel);
        container.add(labels, BorderLayout.SOUTH);

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }

    /**
     * Create the menu bar for the interface.
     */    
    private void makeMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGameItem = new JMenuItem("New");
        newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0));
        newGameItem.addActionListener((ActionEvent e) -> {
            newGame();
        });
        gameMenu.add(newGameItem);  

        JMenu levelMenu = new JMenu("Level");

        ButtonGroup buttonGroup = new ButtonGroup();
        bgnButton = new JRadioButtonMenuItem("Beginner");
        bgnButton.setSelected(true);
        bgnButton.addItemListener((ItemEvent e) -> {
            startGame(BoardType.BEGINNER);
        });

        intButton = new JRadioButtonMenuItem("Intermediate");
        intButton.addItemListener((ItemEvent e) -> {
            startGame(BoardType.INTERMEDIATE);
        });

        advButton = new JRadioButtonMenuItem("Advanced");
        advButton.addItemListener((ItemEvent e) -> {
            startGame(BoardType.ADVANCED);
        });

        buttonGroup.add(bgnButton);
        buttonGroup.add(intButton);
        buttonGroup.add(advButton);

        levelMenu.add(bgnButton);
        levelMenu.add(intButton);
        levelMenu.add(advButton);

        gameMenu.add(levelMenu);    
        gameMenu.addSeparator();

        pauseBox = new JCheckBoxMenuItem("Pause");
        pauseBox.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
        pauseBox.addActionListener((ActionEvent e) -> {
            pauseSelectDeselect();
        });
        gameMenu.add(pauseBox);

        soundBox = new JCheckBoxMenuItem("Sound");
        soundBox.setSelected(true);
        soundBox.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
        gameMenu.add(soundBox);
        gameMenu.addSeparator();

        menuBar.add(gameMenu);

        final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        quitItem.addActionListener((ActionEvent e) -> {
            quit();
        });
        gameMenu.add(quitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpItem = new JMenuItem("View Help");
        helpItem.addActionListener((ActionEvent e) -> {
            displayHelp();
        });

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener((ActionEvent e) -> {
            displayAbout();
        });

        helpMenu.add(helpItem);
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
    }

    /**
     * Start a new game depending on the selection
     * for the type of difficulty.
     */
    private void newGame()
    {
        if(advButton.isSelected()) {
            startGame(BoardType.ADVANCED); 
        } else if (intButton.isSelected()) {
            startGame(BoardType.INTERMEDIATE);
        } else {
            startGame(BoardType.BEGINNER);
        }     
    }

    /**
     * Initialize the state of the 'time'
     * and 'attempts' labels.
     */
    private void initializeCounters()
    {
        seconds = -1;
        updateTimer();
        board.updateAttempts();
        tR.restart(); 
    }

    /**
     * Create the timer for the Time section of the game.
     */
    private void createTimer()
    {
        tR = new Timer(1000, (ActionEvent ae) -> {
            updateTimer();
        });
        tR.setRepeats(true);
        tR.setInitialDelay(1000);
    }

    /**
     * Updates the timer with the new count
     * of the seconds.
     */
    private void updateTimer()
    {     
        seconds++;
        if(seconds < 10 ) {
            timerLabel.setText("TIME: 00"+seconds);
        } else if(seconds < 100) {
            timerLabel.setText("TIME: 0"+seconds);
        } else {
            timerLabel.setText("TIME: "+seconds);
        }   
    }

    /**
     * Implements the pause in the game.
     */
    private void pauseSelectDeselect()
    {
        if(pauseBox.isSelected()) {
            tR.stop();
            disableBoardPanel();
        } else {
            enableBoardPanel();
            tR.start();
        }
    }

    /**
     * Enable the board panel, after the pauseBox
     * has been deselected.
     */
    private void enableBoardPanel()
    {
        board.setEnabled(false);
        for(Component component : board.getComponents()) {
            if(!component.getName().equals("d") && !component.getName().endsWith("F")) {
                component.setEnabled(true);
            }
        }
        board.setEnabled(true);
    }

    /**
     * Disable the board panel, after the pauseBox
     * has been selected.
     */
    private void disableBoardPanel()
    {
        board.setEnabled(false);
        for(Component component : board.getComponents()) {
            if(!component.getName().equals("d")) {
                component.setEnabled(false);
            }
        }
        board.setEnabled(true);
    }

    /**
     * Used to clear the central part of the GUI,
     * so that the new panel can be redrawn.
     */
    private void clearPreviousCentralContainer()
    {
        BorderLayout layout = (BorderLayout)container.getLayout();
        container.remove(layout.getLayoutComponent(BorderLayout.CENTER));
    }

    /**
     * Starts the game with specific BoardType.
     * @param type The specific BoardType to be implemented.
     */
    private void startGame(BoardType type)
    {
        board = new Board(type); 
        if(!isFirstStart) {
            clearPreviousCentralContainer();
            container.add(board, BorderLayout.CENTER);
            frame.pack();
        } else {
            isFirstStart = false;
            container.add(board, BorderLayout.CENTER);
            frame.pack();
        }
        initializeCounters();
        pauseBox.setEnabled(true);
        soundBox.setEnabled(true);
        pauseBox.setSelected(false);
    }

    /**
     * Exit the game.
     */
    private void quit()
    {
        System.exit(0);
    }

    /**
     * Action performed when the player win
     * the game.
     */
    private void onWin()
    {
        tR.stop();
        pauseBox.setEnabled(false);
        soundBox.setEnabled(false);
        if(displayWinMessage() == 0) {
            newGame();
        }
    }

    /**
     * Display win message and ask the user if he/she
     * would like to start a new game.
     * @return The result of the query.
     */
    private int displayWinMessage()
    {
        return JOptionPane.showConfirmDialog(frame, board.generateWinMessage(), "Game Won", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Display message with information about the game.
     */
    private void displayAbout()
    {
        JOptionPane.showMessageDialog(frame, board.getAbout(), "About", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Display help message.
     */
    private void displayHelp()
    {
        JOptionPane.showMessageDialog(frame, HELP_MESSAGE, "Help", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * This class is responsible for constructing the board of the game.
     * The board is a panel with button.
     * 
     * @author Daniel Krastev
     * @version 01/04/2016
     */
    private class Board extends JPanel
    {
        private final static String PATH_BACK_FILE = "/memorygame/resources/images/active/back.jpg";
        private final static String PATH_SOUND_FILE = "/memorygame/resources/sounds/button.wav";
        //The game logic used to manipulate the buttons.
        private final PairEngine eng;
        private Timer t;
        private JButton firstButton, secondButton;
        private int result;
        private String imageFile;
        
        //Sound
        private AudioInputStream audioInputStream;
        Clip clip;
        FloatControl volCtrl;
        
        /**
         * Create the new board instance.
         * @param bt The type of the board to be used.
         */
        public Board(BoardType bt)
        {
            super(new GridLayout(bt.getRows(), bt.getCols()));
            eng = new PairEngine(bt.getRows(), bt.getCols());
            addButtons(bt.getRows(), bt.getCols());
            prepareTimer();
        }

        /**
         * Add new buttons to the board with specific views and names.
         * @param r The rows of the engine's array.
         * @param c The columns of the engine's array.
         */
        private void addButtons(int r, int c)
        {
            for(int x = 0; x < r; x++) {
                for(int y = 0; y < c; y++) {
                    String imgFile = "/memorygame/resources/images/active/" + eng.getValueAt(x,y) + ".jpg";
                    final JButton b = new JButton();
                    b.setIcon(new ImageIcon(getClass().getResource(PATH_BACK_FILE)));
                    b.setPreferredSize(new Dimension(160, 107));
                    b.setName(Integer.toString(x)+Integer.toString(y));
                    b.addActionListener((ActionEvent e) -> {
                        pressButton(b, imgFile);
                    });
                    add(b);          
                }
            }
        }

        /**
         * Prepare the timer.
         */
        private void prepareTimer()
        {
            t = new Timer(500, (ActionEvent ae) -> {
                viewResult(result);
            });
            t.setRepeats(false);
        }

        /**
         * Method used to evaluate the result of the comparison returned 
         * from the PairEngine.This method is used after the second button 
         * that has been pressed.
         * @param result The result which is to be evaluated.
         */
        private void viewResult(int result)
        {
            switch(result) {
                case -1: notMatching();
                break;
                case 1: matching();
                break;
            }
        }

        /**
         * The action which is executed after the button is pressed.
         * Receive from the PairEngine the result of the comparison,
         * between the two values, represented by the buttons and then
         * evaluate the result.
         * @param b The button currently pressed.
         * @param file The string representation of the image file to be used for the certain button.
         */
        private void pressButton(JButton b, String file)
        {
            if(!(firstButton != null && secondButton != null)) {
                clearSound();
                imageFile = file;
                result = eng.checkResult(Integer.parseInt(b.getName().substring(0,1)), Integer.parseInt(b.getName().substring(1,2)));
                playSound();

                if(result == -1 || result == 1) {   // If this is the second of the pair buttons pressed.
                    updateAttempts();
                    secondButton = b;
                    secondButton.setIcon(new ImageIcon(getClass().getResource(imageFile)));
                    t.start();
                } 

                if(result == 0) {   // If this is the first button of the pair buttons that is pressed.
                    firstButton = b;
                    firstButton();
                }
            }
        }

        /**
         * If the result from the PairEngine is that these buttons do not match,
         * then this method is executed. Close the two buttons and hide the images.
         */
        private void notMatching()
        {
            firstButton.setName(firstButton.getName().substring(0,2));
            firstButton.setDisabledIcon(null);
            firstButton.setIcon(new ImageIcon(getClass().getResource(PATH_BACK_FILE)));
            secondButton.setIcon(new ImageIcon(getClass().getResource(PATH_BACK_FILE)));
            if(!pauseBox.isSelected()) {
                firstButton.setEnabled(true);
            }
            secondButton = null;
            firstButton = null;
        }

        /**
         * If the button pressed is the first button, then this
         * method is executed.
         */
        private void firstButton()
        {    
            firstButton.setIcon(new ImageIcon(getClass().getResource(imageFile)));
            firstButton.setDisabledIcon(new ImageIcon(getClass().getResource(imageFile)));
            firstButton.setName(firstButton.getName() + "F");
            firstButton.setEnabled(false);
        }

        /**
         * If the result from the PairEngine is that these buttons match,
         * then this method is executed. Leave these buttons open and show new images.
         */
        private void matching()
        {
            firstButton.setDisabledIcon(new ImageIcon(getClass().getResource(imageFile.replace("active", "inactive"))));
            secondButton.setDisabledIcon(new ImageIcon(getClass().getResource(imageFile.replace("active", "inactive"))));
            firstButton.setName("d");
            secondButton.setName("d");
            firstButton.setEnabled(false);
            secondButton.setEnabled(false);
            secondButton = null;
            firstButton = null;
            if(eng.isWon()) {
                clearSound();
                onWin();
            }
        }

        /**
         * Update the label for the attempts, using the value of the 'moves'
         * from the PairEngine.
         */
        private void updateAttempts()
        {
            if(seconds < 10 ) {
                attemptsLabel.setText("ATTEMPTS: 00" + eng.getMoves() / 2);
            } else if(seconds < 100) {
                attemptsLabel.setText("ATTEMPTS: 0" + eng.getMoves() / 2);
            } else {
                attemptsLabel.setText("ATTEMPTS: " + eng.getMoves() / 2);
            }
        }

        /**
         * Generate message, that will be used for the 'Game Won' pop up message.
         */
        private String generateWinMessage()
        {
            return "Congratulations, you won the game!\n\n" + "Attempts: " + eng.getMoves()/2 + "\n" + "Time: " + seconds + " seconds\n\n\n"
            + "Would you like to start a new game ?"; 
        }

        /**
         * Try to play sound when the button is clicked.
         */
        private void playSound() {
            if (soundBox.isSelected()) {
                try {
                    clip = AudioSystem.getClip();
                    audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(PATH_SOUND_FILE));
                    clip.open(audioInputStream);
                    volCtrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    volCtrl.setValue(-25.0f);
                    clip.setFramePosition(0);
                    clip.start();
                } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                    System.err.println(e.getMessage());
                    soundBox.setSelected(false);
                }
            } 
        }

        /**
         * Get the about information from the PairEngine.
         */
        private String getAbout()
        {
            return eng.aboutGame();
        }

        private void clearSound() {
            if(clip != null) {
                clip.stop();
                clip.flush();
                clip.close();
            }
        }
    }
}
