import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * <b>SWINGAME</b><br>
 * A Java library for abstracting Swing, AWT and Audio API for making Java Swing games more efficiently.
 * It also comes with handling inputs, reading from {@link File}s, internal JAR files, byte arrays and {@link InputStream}s<br><br>
 * <u>Library created by: <b>Dominicentek</b></u><br>
 * <a href="https://github.com/Dominicentek/Swingame">GitHub</a>
 */
public class Swingame {
    private final int width;
    private final int height;
    private final JFrame frame;
    private final BufferedImage image;
    private final Graphics2D g;
    private JPanel panel;
    private int fps = 60;
    private Runnable update = () -> {};
    private boolean alive = true;
    private boolean[] pressedKeys = new boolean[256];
    private boolean[] justPressedKeys = new boolean[256];
    private int mouseX;
    private int mouseY;
    private boolean leftMousePressed;
    private boolean rightMousePressed;
    private boolean middleMousePressed;
    private boolean leftMouseClicked;
    private boolean rightMouseClicked;
    private boolean middleMouseClicked;
    private int mouseScroll;
    private boolean[] nextPressedKeys = new boolean[256];
    private int nextMouseX;
    private int nextMouseY;
    private boolean nextLeftMousePressed;
    private boolean nextRightMousePressed;
    private boolean nextMiddleMousePressed;
    private int nextMouseScroll;
    /**
     * Constructs a Swingame instance, opens window automatically
     * @param width Width of the window
     * @param height Height of the window
     * @param title Title of the window
     */
    public Swingame(int width, int height, String title) {
        this.width = width;
        this.height = height;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        clear(Color.BLACK);
        frame = new JFrame(title);
        frame.setLocation(screen.width / 2 - width / 2, screen.height / 2 - height / 2);
        frame.setDefaultCloseOperation(3);
        frame.getContentPane().setPreferredSize(new Dimension(width, height));
        frame.pack();
        frame.setResizable(false);
        panel = new JPanel() {
            public void paint(Graphics g) {
                g.drawImage(image, 0, 0, this);
            }
        };
        frame.add(panel);
        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) nextLeftMousePressed = true;
                if (e.getButton() == MouseEvent.BUTTON3) nextRightMousePressed = true;
                if (e.getButton() == MouseEvent.BUTTON2) nextMiddleMousePressed = true;
            }
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) nextLeftMousePressed = false;
                if (e.getButton() == MouseEvent.BUTTON3) nextRightMousePressed = false;
                if (e.getButton() == MouseEvent.BUTTON2) nextMiddleMousePressed = false;
            }
        });
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }
            public void mouseMoved(MouseEvent e) {
                nextMouseX = e.getX();
                nextMouseY = e.getY();
            }
        });
        frame.addMouseWheelListener(e -> {
            nextMouseScroll += e.getUnitsToScroll();
        });
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                nextPressedKeys[e.getKeyCode()] = true;
            }
            public void keyReleased(KeyEvent e) {
                nextPressedKeys[e.getKeyCode()] = false;
            }
        });
        frame.setVisible(true);
        new Thread(() -> {
            try {
                long time = System.currentTimeMillis();
                while (alive) {
                    Thread.sleep(Math.max(0, (1000 / fps) - System.currentTimeMillis() + time));
                    for (int i = 0; i < 256; i++) {
                        boolean keyPressed = pressedKeys[i];
                        pressedKeys[i] = nextPressedKeys[i];
                        justPressedKeys[i] = !keyPressed && pressedKeys[i];
                    }
                    boolean left = leftMousePressed;
                    boolean right = rightMousePressed;
                    boolean middle = middleMousePressed;
                    leftMousePressed = nextLeftMousePressed;
                    rightMousePressed = nextRightMousePressed;
                    middleMousePressed = nextMiddleMousePressed;
                    leftMouseClicked = !left && leftMousePressed;
                    rightMouseClicked = !right && leftMousePressed;
                    middleMouseClicked = !middle && leftMousePressed;
                    mouseX = nextMouseX;
                    mouseY = nextMouseY;
                    mouseScroll = nextMouseScroll;
                    nextMouseScroll = 0;
                    update.run();
                    frame.repaint();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            alive = false;
        }));
    }
    /**
     * Sets the icon of the window to an {@link java.awt.Image}
     * @param icon Icon of the window
     */
    public void setIcon(Image icon) {
        frame.setIconImage(icon);
    }
    /**
     * Updates the title of the window
     * @param title Title of the window
     */
    public void setTitle(String title) {
        frame.setTitle(title);
    }
    /**
     * Sets how many times per second the frame/game update (FPS)
     * @param rate Rate of updating
     */
    public void setUpdateRate(int rate) {
        fps = rate;
    }
    /**
     * Assigns a {@link java.lang.Runnable} as an update event for the game
     * @param update The update event
     */
    public void setUpdate(Runnable update) {
        this.update = update;
    }
    /**
     * Clears the entire screen with a singular color
     * @param color The color
     */
    public void clear(Color color) {
        g.setColor(color);
        g.fillRect(0, 0, width, height);
    }
    /**
     * Draws a filled rectangle onto the screen
     * @param x X position of the rectangle
     * @param y Y position of the rectangle
     * @param width Width of the rectangle
     * @param height Height of the rectangle
     * @param color Color of the rectangle
     */
    public void fillRect(int x, int y, int width, int height, Color color) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
    /**
     * Draws an outlined rectangle onto the screen
     * @param x X position of the rectangle
     * @param y Y position of the rectangle
     * @param width Width of the rectangle
     * @param height Height of the rectangle
     * @param color Color of the rectangle
     */
    public void drawRect(int x, int y, int width, int height, Color color) {
        g.setColor(color);
        g.drawRect(x, y, width, height);
    }
    /**
     * Draws a filled circle (or oval) onto the screen
     * @param x X position of the circle
     * @param y Y position of the circle
     * @param width Width of the circle
     * @param height Height of the circle
     * @param color Color of the circle
     */
    public void fillCircle(int x, int y, int width, int height, Color color) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
    }
    /**
     * Draws an outlined circle (or oval) onto the screen
     * @param x X position of the circle
     * @param y Y position of the circle
     * @param width Width of the circle
     * @param height Height of the circle
     * @param color Color of the circle
     */
    public void drawCircle(int x, int y, int width, int height, Color color) {
        g.setColor(color);
        g.drawOval(x, y, width, height);
    }
    /**
     * Draws a line connecting one point on the screen to another point
     * @param x1 X position of the first point
     * @param y1 Y position of the first point
     * @param x2 X position of the second point
     * @param y2 Y position of the second point
     * @param color Color of the line
     */
    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        g.setColor(color);
        g.drawLine(x1, y1, x2, y2);
    }
    /**
     * Draws a string of text onto the screen
     * @param x X coordinate of the text
     * @param y Y coordinate of the text
     * @param text The text itself
     * @param font The font
     * @param color The color of the text
     */
    public void drawText(int x, int y, String text, Font font, Color color) {
        g.setColor(color);
        g.setFont(font);
        g.drawString(text, x, y);
    }
    /**
     * Draws an outline of a polygon onto the screen
     * @param polygon The polygon
     * @param color Color of the line
     */
    public void drawPolygon(Polygon polygon, Color color) {
        g.setColor(color);
        g.drawPolygon(polygon);
    }
    /**
     * Fills a polygon onto the screen
     * @param polygon The polygon
     * @param color Color of the polygon
     */
    public void fillPolygon(Polygon polygon, Color color) {
        g.setColor(color);
        g.fillPolygon(polygon);
    }
    /**
     * Draws an image onto the screen
     * @param image The image
     * @param x X position of the image
     * @param y Y position of the image
     */
    public void drawImage(Image image, int x, int y) {
        g.drawImage(image, x, y, panel);
    }
    /**
     * Draws an image onto the screen with specified width and height
     * @param image The image
     * @param x X position of the image
     * @param y Y position of the image
     * @param width Width of the image
     * @param height Height of the image
     */
    public void drawImage(Image image, int x, int y, int width, int height) {
        g.drawImage(image, x, y, width, height, panel);
    }
    /**
     * Draws a cropped image onto the screen
     * @param image The image
     * @param x X position of the image
     * @param y Y position of the image
     * @param srcX X position of the crop
     * @param srcY Y position of the crop
     * @param srcWidth Width of the crop
     * @param srcHeight Height of the crop
     */
    public void drawImage(Image image, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight) {
        g.drawImage(image, x, y, x + image.getWidth(panel) - 1, y + image.getHeight(panel) - 1, srcX, srcY, srcX + srcWidth - 1, srcY + srcHeight - 1, panel);
    }
    /**
     * Draws a cropped image onto the screen with specified width and height
     * @param image The image
     * @param x X position of the image
     * @param y Y position of the image
     * @param width Width of the image
     * @param height Height of the image
     * @param srcX X position of the crop
     * @param srcY Y position of the crop
     * @param srcWidth Width of the crop
     * @param srcHeight Height of the crop
     */
    public void drawImage(Image image, int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight) {
        g.drawImage(image, x, y, x + width - 1, y + height - 1, srcX, srcY, srcX + srcWidth - 1, srcY + srcHeight - 1, panel);
    }
    /**
     * Sets the settings for a stroke (line) for this instance
     * @param stroke The stroke settings
     */
    public void setStroke(Stroke stroke) {
        g.setStroke(stroke);
    }
    /**
     * Offsets the 0x0 position by specified amount
     * @param x X offset
     * @param y Y offset
     */
    public void translate(int x, int y) {
        g.translate(x, y);
    }
    /**
     * Multiplies the distance between 1x1 and 0x0 coordinates by specified amount
     * @param x X multiplier
     * @param y Y multiplier
     */
    public void scale(double x, double y) {
        g.scale(x, y);
    }
    /**
     * Rotates the current transform by certain amount of degrees
     * @param degrees The amount of degrees
     */
    public void rotate(double degrees) {
        g.rotate(Math.toRadians(degrees));
    }
    /**
     * Rotates the current transform by certain amount of degrees around an origin X and Y
     * @param degrees The amount of degrees
     * @param x X origin
     * @param y Y origin
     */
    public void rotate(double degrees, double x, double y) {
        g.rotate(Math.toRadians(degrees), x, y);
    }
    /**
     * Shears the current transformation by certain amount
     * @param x X shear
     * @param y Y shear
     */
    public void shear(double x, double y) {
        g.shear(x, y);
    }
    /**
     * Defines a custom transform on how the image should be transformed
     * @param transform The transform
     */
    public void transform(AffineTransform transform) {
        g.transform(transform);
    }
    /**
     * Checks if a key is being held
     * @param keycode The keycode of the key, use {@link java.awt.event.KeyEvent}'s magic constants
     * @return <code>true</code> if the key is being held, <code>false</code> if not
     */
    public boolean isKeyPressed(int keycode) {
        return pressedKeys[keycode];
    }
    /**
     * Checks if a key is just pressed on the current frame
     * @param keycode The keycode of the key, use {@link java.awt.event.KeyEvent}'s magic constants
     * @return <code>true</code> if the key is being just pressed, <code>false</code> if not
     */
    public boolean isKeyJustPressed(int keycode) {
        return justPressedKeys[keycode];
    }
    /**
     * Gets the current X position of the mouse relative to the window
     * @return The X position
     */
    public int mouseX() {
        return mouseX;
    }
    /**
     * Gets the current Y position of the mouse relative to the window
     * @return The Y position
     */
    public int mouseY() {
        return mouseY;
    }
    /**
     * Checks if the left mouse button is currently being held
     * @return <code>true</code> if the button is being held, <code>false</code> if not
     */
    public boolean leftMousePressed() {
        return leftMousePressed;
    }
    /**
     * Checks if the middle scroll wheel is currently being held
     * @return <code>true</code> if the scroll wheel is being held, <code>false</code> if not
     */
    public boolean middleMousePressed() {
        return middleMousePressed;
    }
    /**
     * Checks if the right mouse button is currently being held
     * @return <code>true</code> if the button is being held, <code>false</code> if not
     */
    public boolean rightMousePressed() {
        return rightMousePressed;
    }
    /**
     * Checks if the left mouse button just clicked on this frame
     * @return <code>true</code> if the button is being clicked, <code>false</code> if not
     */
    public boolean leftMouseClicked() {
        return leftMouseClicked;
    }
    /**
     * Checks if the middle scroll wheel just clicked on this frame
     * @return <code>true</code> if the scroll wheel is being clicked, <code>false</code> if not
     */
    public boolean middleMouseClicked() {
        return middleMouseClicked;
    }
    /**
     * Checks if the right mouse button just clicked on this frame
     * @return <code>true</code> if the button is being clicked, <code>false</code> if not
     */
    public boolean rightMouseClicked() {
        return rightMouseClicked;
    }
    /**
     * Gets the amount of units scrolled with the middle scroll wheel between the last frame and this frame
     * @return Units scrolled
     */
    public int mouseScroll() {
        return mouseScroll;
    }
    /**
     * Reads the entire file and returns the bytes
     * @param file The file
     * @return The bytes contained in the file
     */
    public static byte[] readFileBytes(File file) {
        try {
            return readAllBytes(new FileInputStream(file));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Reads the entire file that is inside the JAR
     * @param path Path to the file, must use exactly 1 forward slash between folders
     * @return The bytes contained in the file
     */
    public static byte[] readInternalFileBytes(String path) {
        return readAllBytes(Swingame.class.getResourceAsStream("/" + path));
    }
    /**
     * Reads the entire file and returns the <code>String</code> as it was a text file
     * @param file The file
     * @return The text contained in the file
     */
    public static String readFileString(File file) {
        return new String(readFileBytes(file));
    }
    /**
     * Reads the entire file that is inside the JAR and returns the <code>String</code> as it was a text file
     * @param path Path to the file, must use exactly 1 forward slash between folders
     * @return The text contained in the file
     */
    public static String readInternalFileString(String path) {
        return new String(readInternalFileBytes(path));
    }
    /**
     * Reads every remaining byte in a <code>InputStream</code> and returns it
     * @param stream The stream
     * @return The bytes
     */
    public static byte[] readAllBytes(InputStream stream) {
        try {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) >= 0) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Reads a file as an image file and returns the image instance
     * @param file The file
     * @return The image
     */
    public static BufferedImage readImage(File file) {
        try {
            return readImage(new FileInputStream(file));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Reads a file that is inside the JAR as an image file and returns the image instance
     * @param path Path to the file, must use exactly 1 forward slash between folders
     * @return The image
     */
    public static BufferedImage readInternalImage(String path) {
        try {
            return readImage(Swingame.class.getResourceAsStream("/" + path));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Reads a byte array as image data and returns the image instance
     * @param array The image data
     * @return The image
     */
    public static BufferedImage readImage(byte[] array) {
        return readImage(new ByteArrayInputStream(array));
    }
    /**
     * Reads an <code>InputStream</code> as image data and returns the image instance
     * @param in The <code>InputStream</code>
     * @return The image
     */
    public static BufferedImage readImage(InputStream in) {
        try {
            return ImageIO.read(in);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Reads a file as an audio file and automatically plays it
     * @param file The file
     * @return The audio instance
     */
    public static Clip playAudio(File file) {
        try {
            return playAudio(new FileInputStream(file), 0, -1);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Reads a file that is inside the JAR as an audio file and automatically plays it
     * @param path Path to the file, must use exactly 1 forward slash between folders
     * @return The audio instance
     */
    public static Clip playInternalAudio(String path) {
        return playAudio(Swingame.class.getResourceAsStream("/" + path), 0, -1);
    }
    /**
     * Reads a byte array as audio data and automatically plays it
     * @param array The audio data
     * @return The audio instance
     */
    public static Clip playAudio(byte[] array) {
        return playAudio(new ByteArrayInputStream(array), 0, -1);
    }
    /**
     * Reads an <code>InputStream</code> as audio data and automatically plays it
     * @param in The <code>InputStream</code>
     * @return The audio instance
     */
    public static Clip playAudio(InputStream in) {
        return playAudio(in, 0, -1);
    }
    /**
     * Reads a file as an audio file and automatically plays it
     * @param file The file
     * @param loopCount Amount of tiles the audio should loop, -1 for infinite amount of times
     * @return The audio instance
     */
    public static Clip playAudio(File file, int loopCount) {
        try {
            return playAudio(new FileInputStream(file), loopCount, -1);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Reads a file that is inside the JAR as an audio file and automatically plays it
     * @param path Path to the file, must use exactly 1 forward slash between folders
     * @param loopCount Amount of tiles the audio should loop, -1 for infinite amount of times
     * @return The audio instance
     */
    public static Clip playInternalAudio(String path, int loopCount) {
        return playAudio(Swingame.class.getResourceAsStream("/" + path), loopCount);
    }
    /**
     * Reads a byte array as audio data and automatically plays it
     * @param array The audio data
     * @param loopCount Amount of tiles the audio should loop, -1 for infinite amount of times
     * @return The audio instance
     */
    public static Clip playAudio(byte[] array, int loopCount) {
        return playAudio(new ByteArrayInputStream(array), loopCount);
    }
    /**
     * Reads an <code>InputStream</code> as audio data and automatically plays it
     * @param in The <code>InputStream</code>
     * @param loopCount Amount of tiles the audio should loop, -1 for infinite amount of times
     * @return The audio instance
     */
    public static Clip playAudio(InputStream in, int loopCount) {
        return playAudio(in, loopCount, -1);
    }
    /**
     * Reads a file as an audio file and automatically plays it
     * @param file The file
     * @param loopCount Amount of tiles the audio should loop, -1 for infinite amount of times
     * @param loopPoint The position in samples where the playback will jump to when it reaches the end when looping
     * @return The audio instance
     */
    public static Clip playAudio(File file, int loopCount, int loopPoint) {
        try {
            return playAudio(new FileInputStream(file), loopCount, loopPoint);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Reads a file that is inside the JAR as an audio file and automatically plays it
     * @param path Path to the file, must use exactly 1 forward slash between folders
     * @param loopCount Amount of tiles the audio should loop, -1 for infinite amount of times
     * @param loopPoint The position in samples where the playback will jump to when it reaches the end when looping
     * @return The audio instance
     */
    public static Clip playAudio(String path, int loopCount, int loopPoint) {
        return playAudio(Swingame.class.getResourceAsStream("/" + path), loopCount, loopPoint);
    }
    /**
     * Reads a byte array as audio data and automatically plays it
     * @param array The audio data
     * @param loopCount Amount of tiles the audio should loop, -1 for infinite amount of times
     * @param loopPoint The position in samples where the playback will jump to when it reaches the end when looping
     * @return The audio instance
     */
    public static Clip playAudio(byte[] array, int loopCount, int loopPoint) {
        return playAudio(new ByteArrayInputStream(array), loopCount, loopPoint);
    }
    /**
     * Reads an <code>InputStream</code> as audio data and automatically plays it
     * @param in The <code>InputStream</code>
     * @param loopCount Amount of tiles the audio should loop, -1 for infinite amount of times
     * @param loopPoint The position in samples where the playback will jump to when it reaches the end when looping
     * @return The audio instance
     */
    public static Clip playAudio(InputStream in, int loopCount, int loopPoint) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(in));
            clip.loop(loopCount);
            clip.setLoopPoints(0, loopPoint);
            clip.start();
            return clip;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
