package XXLChess;

import XXLChess.Game.GameStatus;
import XXLChess.GameReport.EndReason;
import java.io.*;
import java.net.URL;
import java.util.*;
import processing.core.*;
import processing.data.JSONObject;
import processing.event.MouseEvent;

/**
 * This is the main application class.
 * It acts as a renderer for the Game structure.
 */
public class App extends PApplet {

  /**
   * Constant declarations
   */
  public static final int SIDEBAR = 120;
  public static final int FPS = 60;
  public static int WIDTH = Tile.CELLSIZE * Board.BOARD_WIDTH + SIDEBAR;
  public static int HEIGHT = Board.BOARD_WIDTH * Tile.CELLSIZE;
  public static String configPath;

  // The configuration object
  private JSONObject conf;
  private Game game;

  private String errorMessage = null;

    /**
   * Creates a new App with the default configuration file.
   */
  public App() {
    configPath = "config.json";
    SoundPlayer.preloadSoundEffect("bruh.wav");
    SoundPlayer.preloadSoundEffect("capture.wav");
    SoundPlayer.preloadSoundEffect("castle.wav");
    SoundPlayer.preloadSoundEffect("move-check.wav");
    SoundPlayer.preloadSoundEffect("move-self.wav");
    SoundPlayer.preloadSoundEffect("promote.wav");
    SoundPlayer.preloadSoundEffect("win.wav");
    SoundPlayer.preloadSoundEffect("lose.wav");
  }
  // Logic part (unit-testable)

  /**
   * Retrieves the target tiles for the currently selected tile.
   *
   * @return the set of target tiles.
   */
  public Set<Tile> getTargetTiles() {
    Set<Tile> targetTiles;
    Tile sel = game.getHumanAgent().getSelection();
    if (sel != null) {
      Set<Movement> safeMovements = game.getSafeMovements(
        game.getAllLegalMovements(game.getHumanAgent())
      );
      targetTiles = new HashSet<>();
      for (Movement move : safeMovements) {
        if (move.getSourceTile() == sel) targetTiles.add(move.getTargetTile());
      }
    } else {
      targetTiles = null;
    }
    return targetTiles;
  }

  /**
   * Loads the XXLChess game with the specified configuration file.
   *
   * @param configPath the path of the configuration file.
   * @throws Exception if an error occurs during loading.
   */
  public void loadXXLChess(String configPath) throws Exception {
    conf = loadConfigFile(configPath);
    initGame(conf);
    // Load the layout
    String layoutFilename = conf.getString("layout");
    loadLevel(layoutFilename);
    mapValidityCheck();
  }

  /**
   * Initializes the game with the specified configuration.
   *
   * @param conf the configuration object.
   * @throws Exception if an error occurs during initialization.
   */
  public void initGame(JSONObject conf) throws Exception {
    // Load the timer settings
    JSONObject timeControls = conf.getJSONObject("time_controls");
    JSONObject playerTimeControl = timeControls.getJSONObject("player");
    JSONObject cpuTimeControl = timeControls.getJSONObject("cpu");
    int playerSeconds = playerTimeControl.getInt("seconds");
    int playerIncrement = playerTimeControl.getInt("increment");
    int cpuSeconds = cpuTimeControl.getInt("seconds");
    int cpuIncrement = cpuTimeControl.getInt("increment");

    // Load the board settings
    String playerColour = conf.getString("player_colour");
    int pieceMovementSpeed = conf.getInt("piece_movement_speed");
    int maxMovementTime = conf.getInt("max_movement_time");
    game =
      new Game(
        playerColour.equals("white"),
        playerSeconds,
        cpuSeconds,
        playerIncrement,
        cpuIncrement,
        pieceMovementSpeed,
        maxMovementTime
      );
  }

  /**
   * Handles the selection of a piece on the board.
   *
   * @param tile the selected tile.
   */
  public void handlePieceSelection(Tile tile) {
    Human human = game.getHumanAgent();
    // get safe movements
    Set<Movement> safe = game.getSafeMovements(
      game.getAllLegalMovements(human)
    );
    // Must select a player's own piece first
    Piece piece = tile.getCurrentPiece();
    if (piece == null || piece.getColor() != human.getColor()) return;
    if (game.getInCheck() != null) {
      boolean protective = false;
      for (Movement m : safe) {
        if (m.getSourcePiece() == piece) {
          protective = true;
          break;
        }
      }
      if (!protective) {
        game.setWarning(new KingProtectionWarning());
        SoundPlayer.playSound("bruh.wav");
        return;
      }
    }
    human.select(tile);
  }

  /**
   * Handles the movement of a piece on the board.
   *
   * @param tile the target tile for the movement.
   */
  public void handlePieceMovement(Tile tile) {
    Human human = game.getHumanAgent();
    Set<Movement> safe = game.getSafeMovements(
      game.getAllLegalMovements(human)
    );
    try {
      if (tile == human.getSelection()) {
        // Unselect
        throw new InvalidMoveException(human);
      } else if (
        tile.getCurrentPiece() != null &&
        tile.getCurrentPiece().getColor() == human.getColor()
      ) {
        // The player selects one of their other pieces
        human.select(tile);
      } else {
        Piece selectedPiece = human.getSelection().getCurrentPiece();
        Movement todo = new Movement(selectedPiece, tile);
        // Invalid move
        boolean validity = false;
        for (Movement safeMovement : safe) {
          if (safeMovement.equals(todo)) validity = true;
        }
        if (!validity) {
          throw new InvalidMoveException(human);
        }
        // Perform movement
        if (
          selectedPiece instanceof King &&
          game.getDangerTiles(selectedPiece).contains(tile)
        ) {
          throw new InvalidMoveException(human);
        }
        game.movePiece(todo);
      }
    } catch (InvalidMoveException err) {
      System.out.println("Invalid move");
    } catch (RuleViolationException err) {
      System.out.println(err.getMessage());
    } finally {
      human.clearSelection();
    }
  }

  /**
   * Determines the text to be displayed at the end of the game.
   *
   * @param endReason the reason for the game ending.
   * @return the end game text.
   */
  public String determineEndGameText(GameReport.EndReason endReason) {
    switch (endReason) {
      case COMPUTER_CHECKMATED:
        return "You won by \ncheckmate!";
      case COMPUTER_TIMEOUT:
        return "You won on \ntime!";
      case PLAYER_CHECKMATED:
        return "You lost by \ncheckmate!";
      case PLAYER_TIMEOUT:
        return "You lost on time!";
      case PLAYER_RESIGNED:
        return "You resigned!";
      default:
        return "";
    }
  }

  /**
   * Retrieves the list of image files from the specified image folder URL.
   *
   * @param imageFolder the URL of the image folder.
   * @return the list of image files.
   */
  public List<File> getImageFiles(URL imageFolder) {
    List<File> imageFiles = new ArrayList<File>();
    File dir = new File(imageFolder.getPath());
    File[] files = dir.listFiles();
    // Ensure that files were found in the directory
    if (files != null) {
      for (File file : files) {
        // Only process .png files
        if (file.isFile() && file.getName().endsWith(".png")) {
          imageFiles.add(file);
        }
      }
    }
    return imageFiles;
  }

  /**
   * Loads a chess level from a file.
   *
   * @param layoutFilename the name of the file containing the level layout.
   * @throws Exception if an error occurs during loading.
   */
  public void loadLevel(String layoutFilename) throws Exception {
    Scanner sc = null;
    File layout = new File(layoutFilename);
    try {
      sc = new Scanner(layout);
      int x = 0, y = 0;
      // The layout file will contain a grid of text characters, where each character represents the piece that should be in that cell.
      while (sc.hasNextLine()) {
        String row = sc.nextLine();
        for (Character c : row.toCharArray()) {
          // Create a new piece based on the character from the layout file
          Piece cur = Piece.createPiece(c);
          if (c == ' ' || c == '.') {
            x++;
            continue;
          }
          if (cur == null) {
            throw new InvalidObjectException(c.toString());
          }
          // Get the appropriate agent for the piece based on its color
          PlayerAgent agent = game.getBoard().getAgentByColor(cur.getColor());
          // Get the tile corresponding to the current position
          Tile parentTile = game.getTile(x, y);
          if (parentTile == null) {
            throw new ArrayIndexOutOfBoundsException();
          }
          // Set the current tile and piece
          cur.setCurrentTile(parentTile);
          parentTile.setCurrentPiece(cur);
          // Add the piece to the agent's list of pieces
          agent.getPieces().add(cur);
          // If the piece is a king, set it as the agent's king
          if (cur instanceof King) {
            if (agent.getKing() == null) {
              agent.setKing((King) cur);
            } else {
              throw new Exception("A player must not have more than one king.");
            }
          }
          x++;
        }
        y++;
        x = 0;
      }
      // Refresh the available moves for the game
      game.refreshAvailableMoves(game.getBoard());
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new Exception("The map must have the size of 14*14.");
    } catch (InvalidObjectException e) {
      throw new Exception(
        "Unexpected piece character '" + e.getMessage() + "'"
      );
    } catch (FileNotFoundException e) {
      throw new Exception("File '" + layoutFilename + "' not found");
    } finally {
      if (sc != null) sc.close();
    }
  }

  /**
   * Checks the validity of the map.
   * Both kings must be present and the second player must not be checkmated initially.
   *
   * @throws Exception if the map is invalid.
   */
  public void mapValidityCheck() throws Exception {
    // Both kings MUST be present
    if (game.getHumanAgent().getKing() == null) {
      throw new Exception(
        "Unable to find human king in the map. Fix the map and try again."
      );
    } else if (game.getBotAgent().getKing() == null) {
      throw new Exception(
        "Unable to find computer king in the map. Fix the map and try again."
      );
    }
    // The second player MUST not be checkmated initially
    // TODO: Add a check for this condition
  }

  /**
   * Determines the background color of a tile based on its current status.
   *
   * @param t the tile whose color is to be determined.
   * @return the color of the tile as an integer.
   */
  public int getTileBackgroundColor(Tile t) {
    Human human = game.getHumanAgent();
    // Check if the king is in check
    InCheckIncident incheck = game.getInCheck();
    if (incheck != null && t == incheck.getThreatenedKing().getCurrentTile()) {
      KingProtectionWarning warning = game.getWarning();
      // If the king is threatened and either there is no warning or the warning light is red, mark the tile as red
      if (warning == null || warning.isRedLight()) {
        return TileColor.TILE_RED;
      }
    }
    // Highlight the current selection by marking it green
    if (human.getSelection() == t) {
      return TileColor.TILE_GREEN;
    }
    // For other tiles, return the color based on the position of the tile on the board
    if ((t.getX() + t.getY()) % 2 == 0) {
      return TileColor.TILE_LIGHT;
    } else {
      return TileColor.TILE_DARK;
    }
  }

  /**
   * Class containing color definitions for different tile states.
   */
  public static class TileColor {
    public static final int TILE_DARK = -4880285;
    public static final int TILE_LIGHT = -992843;
    public static final int TILE_GREEN = -9860532;
    public static final int TILE_BLUE = -5320224;
    public static final int TILE_RED = -3014397;
    public static final int TILE_CURRENT_SELECTION = -5857993;
    public static final int TILE_ENEMY_LAST_MOVE = -5463747;
    public static final int TILE_ORANGE = -159148;
  }

  // GUI Part (not unit-testable)

  /**
   * Initialise the setting of the window size.
   */
  @Override
  public void settings() {
    size(WIDTH, HEIGHT);
  }

  /**
   * Handle mouse press events.
   */
  @Override
  public void mousePressed(MouseEvent e) {
    if (errorMessage != null) return;
    if (game.getGameStatus() != GameStatus.PLAYER_TURN) return;

    // Get mouse position
    int mouseX = e.getX();
    int mouseY = e.getY();

    // Ignore invalid click events
    if (mouseX > WIDTH - SIDEBAR) return;

    // Get the corresponding tile position
    int x = mouseX / Tile.CELLSIZE;
    int y = mouseY / Tile.CELLSIZE;

    System.out.println("x: " + x + " y: " + y);

    // Get the corresponding tile
    Tile tile = game.getTile(x, y);

    // Check if it's the human's turn
    Human human = game.getHumanAgent();

    if (game.getCurrentPlayer() != human) return;

    // Handle piece selection
    if (human.isPending()) {
      handlePieceSelection(tile);
    }
    // Handle piece movement
    else {
      handlePieceMovement(tile);
    }
  }

  /**
   * Render the game state.
   */
  public void draw() {
    background(200);
    if (errorMessage != null) {
      displayErrorMessage();
      return;
    }

    noStroke();
    if (game.getGameStatus() == GameStatus.ENDED) {
      displayEndGameStatus();
    } else if (game.isEnded()) {
      determineTimeoutEndReason();
    } else {
      game.tick();
      handleInCheckState();
    }

    Set<Tile> targetTiles = getTargetTiles();

    // Draw the tiles and pieces
    for (int i = 0; i < Board.BOARD_WIDTH; i++) {
      for (int j = 0; j < Board.BOARD_WIDTH; j++) {
        drawTile(i, j, targetTiles);
      }
    }
    drawMovingPiece(game.getAnimation());
    drawMovingPiece(game.getRookAnimation());
    displayTimers();
  }

  /**
   * Displays an error message on the screen.
   */
  private void displayErrorMessage() {
    textSize(16);
    fill(255, 10, 10);
    textAlign(CENTER);
    text("ERROR: " + errorMessage, WIDTH / 2, HEIGHT / 2);
  }

  /**
   * Displays the end game status and instructions.
   */
  private void displayEndGameStatus() {
    // Display end game status and instructions
    GameReport report = game.getReport();
    String endGameText = determineEndGameText(report.getReasonForEnd());
    textSize(12);
    textAlign(LEFT, BOTTOM);
    text(endGameText, WIDTH - SIDEBAR + 10, HEIGHT / 2 - 10);
    textAlign(LEFT, TOP);
    text(
      "Press 'r' to\nrestart the game",
      WIDTH - SIDEBAR + 10,
      HEIGHT / 2 + 10
    );
  }

  /**
   * Handles key press events.
   */
  @Override
  public void keyPressed() {
    if (errorMessage != null) return;
    switch (key) {
      case 'e': // ESC
        if (game.getGameStatus() != GameStatus.ENDED) game.setReport(
          new GameReport(game, EndReason.PLAYER_RESIGNED)
        );
        break;
      case 'r': // r
        if (game.getGameStatus() == GameStatus.ENDED) setup();
        break;
    }
  }

  /**
   * Handles the in check state of the game.
   */
  private void handleInCheckState() {
    if (game.getInCheck() != null) {
      if (game.getWarning() != null) {
        textSize(12);
        textAlign(LEFT, BOTTOM);
        text(
          "You must protect\nyour king!",
          WIDTH - SIDEBAR + 10,
          HEIGHT / 2 - 10
        );
      } else {
        textSize(28);
        textAlign(CENTER);
        text("Check!", WIDTH - SIDEBAR / 2, HEIGHT / 2);
      }
    }
  }

  /**
   * Draws a tile on the screen.
   *
   * @param i           the x-coordinate of the tile.
   * @param j           the y-coordinate of the tile.
   * @param targetTiles the set of target tiles for possible moves.
   */
  private void drawTile(int i, int j, Set<Tile> targetTiles) {
    Tile t = game.getTile(i, j);
    fill(getTileBackgroundColor(t));
    rect(t.getLeft(), t.getTop(), Tile.CELLSIZE, Tile.CELLSIZE);

    handleLastMoveHighlighting(t);
    handlePossibleMoveHighlighting(t, targetTiles);
    drawPieceOnTile(t);
  }

  /**
   * Handles highlighting of the last move on a tile.
   *
   * @param t the tile to be checked.
   */
  private void handleLastMoveHighlighting(Tile t) {
    PlayerAgent opponent = game.getCurrentPlayer().getOpponent();
    if (
      opponent.getLastMoveSource() == t || opponent.getLastMoveTarget() == t
    ) {
      // a tile which has a capturable enemy piece
      fill(TileColor.TILE_ENEMY_LAST_MOVE, 100);
      rect(t.getLeft(), t.getTop(), Tile.CELLSIZE, Tile.CELLSIZE);
    }
  }

  /**
   * Handles highlighting of possible moves on a tile.
   *
   * @param t           the tile to be checked.
   * @param targetTiles the set of target tiles for possible moves.
   */
  private void handlePossibleMoveHighlighting(Tile t, Set<Tile> targetTiles) {
    // highlight all possible moves (excluding tiles of capturable pieces)
    if (targetTiles != null && targetTiles.contains(t)) {
      highlightValidMoves(t);
    }
  }

  /**
   * Highlights valid moves on a tile.
   *
   * @param t the tile to be highlighted.
   */
  private void highlightValidMoves(Tile t) {
    if (t.getCurrentPiece() == null) {
      fill(TileColor.TILE_BLUE, 200);
      rect(t.getLeft(), t.getTop(), Tile.CELLSIZE, Tile.CELLSIZE);
    } else if (
      t.getCurrentPiece().getColor() != game.getHumanAgent().getColor()
    ) {
      // highlight capturable pieces
      if (
        game.getHumanAgent().getAllTargetTiles() != null &&
        game.getHumanAgent().getAllTargetTiles().contains(t)
      ) {
        fill(TileColor.TILE_ORANGE, 255);
        rect(t.getLeft(), t.getTop(), Tile.CELLSIZE, Tile.CELLSIZE);
      }
    }
  }

  /**
   * Draws a piece on a tile.
   *
   * @param t the tile to draw the piece on.
   */
  private void drawPieceOnTile(Tile t) {
    Piece p = t.getCurrentPiece();
    if (p != null) {
      if (
        game.getAnimation() != null && p == game.getAnimation().getMovingPiece()
      ) return; // I will draw you later!
      if (
        game.getRookAnimation() != null &&
        p == game.getRookAnimation().getMovingPiece()
      ) return; // I will draw you later too!
      image(p.getImage(), p.getLeft(), p.getTop());
    }
  }

  /**
   * Draws a moving piece on the screen.
   *
   * @param animation the animation representing the moving piece.
   */
  private void drawMovingPiece(AnimationVehicle animation) {
    if (animation != null) {
      PImage afterimage = animation.getAfterimage();
      if (afterimage != null) {
        image(
          afterimage,
          (float) animation.getTargetLeft(),
          (float) animation.getTargetTop()
        );
      }
      Piece movingPiece = animation.getMovingPiece();
      image(
        movingPiece.getImage(),
        (float) animation.getLeft(),
        (float) animation.getTop()
      );
    }
  }

  /**
   * Displays the timers for the game.
   */
  private void displayTimers() {
    textSize(24);
    fill(color(255, 255, 255));
    textAlign(CENTER);
    // display the computer timer
    int left = WIDTH - SIDEBAR / 2;
    text(Utils.formatSeconds(game.getBotAgent().getRemainingTime()), left, 50);
    // display the player timer
    text(
      Utils.formatSeconds(game.getHumanAgent().getRemainingTime()),
      left,
      HEIGHT - 50
    );
  }

  /**
   * Initializes the application by loading the configuration and images.
   */
  @Override
  public void setup() {
    frameRate(FPS);
    // Load configuration using the simple JSON library
    try {
      loadXXLChess(configPath);
      // Load images
      preloadPieceImages();
    } catch (Exception e) {
      errorMessage = e.getMessage();
    }
  }

  /**
   * Handles the main entry point for the application.
   *
   * @param args the command line arguments.
   */
  public static void main(String[] args) {
    // Launch the application
    PApplet.main("XXLChess.App");
  }

  /**
   * Loads the configuration file.
   *
   * @param configPath the path to the configuration file.
   * @return the loaded JSON configuration object.
   * @throws Exception if the configuration file cannot be found or loaded.
   */
  public static JSONObject loadConfigFile(String configPath) throws Exception {
    File configFile = new File(configPath);
    if (!configFile.exists()) {
      throw new Exception("Could not find the config file");
    }
    return loadJSONObject(configFile);
  }

  /**
   * Preloads the images for the chess pieces from the resources directory.
   */
  public void preloadPieceImages() {
    URL imageFolder = App.class.getClassLoader().getResource("XXLChess/images");
    for (File file : getImageFiles(imageFolder)) {
      PImage img = loadImage(imageFolder.getPath() + "/" + file.getName());
      img.resize(Tile.CELLSIZE, Tile.CELLSIZE);
      Piece.imageSources.put(file.getName(), img);
    }
  }

  private void determineTimeoutEndReason() {
    game.setReport(
      new GameReport(
        game,
        game.getHumanAgent().isEnded()
          ? GameReport.EndReason.PLAYER_TIMEOUT
          : GameReport.EndReason.COMPUTER_TIMEOUT
      )
    );
  }
}
