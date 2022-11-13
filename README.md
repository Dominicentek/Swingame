# Swingame
A Java library for abstracting Swing, AWT and Audio API for making Java Swing games more efficiently.
It also comes with handling inputs, reading from filess, internal JAR files, byte arrays and InputStreams.
## Examples
### Initializing a window
```java
// Initializing Swingame will automatically open the window
Swingame swingame = new Swingame(1000, 750, "Swingame Example");

// Adding update event
swingame.setUpdate(() -> {
  System.out.println("I updated!");
});

// Setting FPS
swingame.setUpdateRate(60);

// Setting icon
swingame.setIcon(Swingame.readInternalImage("icon.png"));
```
### Drawing
```java
/* Fill the screen with blue */ swingame.clear(Color.BLUE);
/* Drawing filled rectangle  */ swingame.fillRect(50, 50, 100, 100, Color.RED);
/* Drawing a 10px line       */ swingame.setStroke(new BasicStroke(10f));
                                swingame.drawLine(300, 300, 400, 350, Color.GREEN);
/* Drawing image             */ swingame.drawImage(swingame.readInternalImage("image.png"), 500, 100);
```
### Transforming
```java
/* Translate */ swingame.translate(100, 100);
/* Rotate    */ swingame.rotate(-50.75, 100, 100);
/* Scale     */ swingame.scale(0.5, 0.5);
/* Shear     */ swingame.shear(2, 1);
```
### Handling input
```java
int mouseX = swingame.mouseX();
int mouseY = swingame.mouseY();
if (swingame.leftMouseClicked()) System.out.println("Clicked!");
if (swingame.isKeyPressed(KeyEvent.VK_ARROW_RIGHT)) System.out.println("Moving right");
```
### Playing audio
```java
swingame.playInternalAudio("test.wav");
```
