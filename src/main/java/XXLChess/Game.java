package XXLChess;

import XXLChess.GameReport.EndReason;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a game of chess. It includes the players, the board, the current game state,
 * and any animations or warnings that need to be displayed. It also implements the Tickable interface
 * to allow the game state to be updated regularly.
 */
public class Game implements Tickable {

  // Each counterpart's agents
  private final Human humanAgent;
  private final Bot botAgent;

  // Current player
  private PlayerAgent currentPlayer;

  // In-check incident
  private InCheckIncident inCheck;

  // Animation management
  private AnimationVehicle animation;

  // Animation management (specifically for the rook)
  private AnimationVehicle rookAnimation;

  // King protection warning
  private KingProtectionWarning warning;

  // Game report (if the game ends)
  private GameReport report;

  // Expired frames
  private final int expiredFramesCount = 0;

  // Chess board
  private final Board board;

  // Piece movement speed
  private final int pieceMovementSpeed;

  // Max movement time
  private final int maxMovementTime;

  /**
   * Constructs a Game with the provided parameters.
   *
   * @param isPlayerWhite       determines if the player is using the white pieces
   * @param playerSeconds       player's time in seconds
   * @param cpuSeconds          cpu's time in seconds
   * @param playerTimeIncrement player's time increment per move
   * @param cpuTimeIncrement    cpu's time increment per move
   * @param pieceMovementSpeed  speed of piece movement
   * @param maxMovementTime     maximum time for a movement
   */
  public Game(
    boolean isPlayerWhite,
    int playerSeconds,
    int cpuSeconds,
    int playerTimeIncrement,
    int cpuTimeIncrement,
    int pieceMovementSpeed,
    int maxMovementTime
  ) {
    // Load the human & bot agent
    // The “player_colour” property denotes the colour of the pieces of player 1
    // (the human player). It should either have the value “white” or “black”.
    if (isPlayerWhite) {
      humanAgent =
        new Human(Piece.Color.WHITE, playerSeconds, playerTimeIncrement);
      botAgent = new Bot(Piece.Color.BLACK, cpuSeconds, cpuTimeIncrement);
      this.board = new Board(humanAgent, botAgent);
    } else {
      humanAgent =
        new Human(Piece.Color.BLACK, playerSeconds, playerTimeIncrement);
      botAgent = new Bot(Piece.Color.WHITE, cpuSeconds, cpuTimeIncrement);
      this.board = new Board(botAgent, humanAgent);
    }
    humanAgent.setOpponent(botAgent);
    botAgent.setOpponent(humanAgent);
    // Whoever is white has the first move, as in regular chess
    currentPlayer = board.getAgentByColor(Piece.Color.WHITE);
    this.pieceMovementSpeed = pieceMovementSpeed;
    this.maxMovementTime = maxMovementTime;
  }

  /**
   * Returns the current in-check incident.
   *
   * @return the current in-check incident.
   */
  public InCheckIncident getInCheck() {
    return inCheck;
  }

  /**
   * Sets the current in-check incident.
   *
   * @param inCheck the in-check incident to be set.
   */
  public void setInCheck(InCheckIncident inCheck) {
    this.inCheck = inCheck;
  }

  /**
   * Returns the current rook animation.
   *
   * @return the current rook animation.
   */
  public AnimationVehicle getRookAnimation() {
    return rookAnimation;
  }

  /**
   * Sets the current rook animation.
   *
   * @param rookAnimation the current rook animation to be set.
   */
  public void setRookAnimation(AnimationVehicle rookAnimation) {
    this.rookAnimation = rookAnimation;
  }

  /**
   * Returns the current warning.
   *
   * @return the current warning.
   */
  public KingProtectionWarning getWarning() {
    return warning;
  }

  /**
   * Sets the current warning.
   *
   * @param warning the warning to be set.
   */
  public void setWarning(KingProtectionWarning warning) {
    this.warning = warning;
  }

  /**
   * Returns the current animation.
   *
   * @return the current animation.
   */
  public AnimationVehicle getAnimation() {
    return animation;
  }

  /**
   * Sets the current animation.
   *
   * @param a the animation to set
   */
  public void setAnimation(AnimationVehicle a) {
    this.animation = a;
  }

  /**
   * Returns the game report.
   *
   * @return the game report.
   */
  public GameReport getReport() {
    return report;
  }

  /**
   * Sets the game report.
   *
   * @param report the game report to be set.
   */
  public void setReport(GameReport report) {
    this.report = report;
  }


  /**
   * Returns the game board.
   *
   * @return the game board.
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Returns a specific tile from the board.
   *
   * @param x the x-coordinate of the tile.
   * @param y the y-coordinate of the tile.
   * @return the tile at the specified coordinates.
   */
  public Tile getTile(int x, int y) {
    return board.getTile(x, y);
  }

  /**
   * Returns the current player.
   *
   * @return the current player.
   */
  public PlayerAgent getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * Sets the current player.
   *
   * @param currentPlayer the player to be set as the current player.
   */
  public void setCurrentPlayer(PlayerAgent currentPlayer) {
    this.currentPlayer = currentPlayer;
  }

  /**
   * Returns the Bot agent.
   *
   * @return the Bot agent.
   */
  public Bot getBotAgent() {
    return botAgent;
  }

  /**
   * Returns the Human agent.
   *
   * @return the Human agent.
   */
  public Human getHumanAgent() {
    return humanAgent;
  }

  /**
   * Returns the count of expired frames.
   *
   * @return the count of expired frames.
   */
  public int getExpiredFramesCount() {
    return expiredFramesCount;
  }

  /**
   * The tick method is called every frame and handles the game logic. This includes checking if the game has ended,
   * checking for in-check incidents, rendering warnings and animations, and handling player and computer turns.
   */
  public void tick() {
    // Check if the game has ended
    if (isEnded()) {
      // If the game has ended and a report hasn't been created yet, create one
      if (report == null) {
        report =
          new GameReport(
            this,
            humanAgent.isEnded()
              ? GameReport.EndReason.PLAYER_TIMEOUT
              : GameReport.EndReason.COMPUTER_TIMEOUT
          );
      }
    }

    InCheckIncident inc = detectInCheck(currentPlayer);
    setInCheck(inc);

    // Handle game logic based on the current game status
    switch (getGameStatus()) {
      case ENDED:
        // Nothing to do if the game has ended
        break;
      case RENDERING_WARNING:
        // Handle warning rendering
        if (warning.isEnded()) {
          warning = null;
        } else {
          warning.tick();
        }
        break;
      case RENDERING_ANIMATION:
        // Handle animation rendering
        if (animation == null || animation.isEnded()) {
          animation = null;
        } else {
          animation.tick();
        }
        if (rookAnimation == null || rookAnimation.isEnded()) {
          rookAnimation = null;
        } else {
          rookAnimation.tick();
        }
        break;
      case PLAYER_TURN:
        // Handle player's turn
        checkmateInspection();
        if (getGameStatus() == GameStatus.ENDED) {
          return;
        }
        humanAgent.tick();
        break;
      case COMPUTER_TURN:
        // Handle computer's turn
        Movement m;
        if (inCheck != null) {
          // If the computer is in check, inspect for checkmate
          Set<Movement> solution = checkmateInspection();
          if (getGameStatus() == GameStatus.ENDED) {
            return;
          }
          m = solution.iterator().next();
        } else {
          // If the computer is not in check, make a decision
          m = botAgent.makeDecision(this);
        }
        if (m != null) {
          try {
            movePiece(m);
          } catch (RuleViolationException err) {
            System.out.println(
              "Error: Oh no.. A computer can also break rules! THE COMPUTER IS UPRISING!!"
            );
          }
        } else {
          report = new GameReport(this, EndReason.PLAYER_RESIGNED);
        }
        botAgent.tick();
        break;
    }
  }

  /**
   * Inspects the game for a checkmate situation. If the current player is in check and there are no legal moves,
   * a GameReport is created with a checkmate end reason.
   *
   * @return a Set of legal movements if the current player is in check, or null if not.
   */
  public Set<Movement> checkmateInspection() {
    Set<Movement> solution = new HashSet<>();
    if (inCheck != null) {
      solution = solveIncident(inCheck);
      // If there are no legal moves, the player is checkmated
      if (solution.size() == 0) {
        setReport(
          new GameReport(
            this,
            inCheck.getThreatenedKing().getColor() == humanAgent.getColor()
              ? EndReason.PLAYER_CHECKMATED
              : EndReason.COMPUTER_CHECKMATED
          )
        );
      }
      return solution;
    }
    return null;
  }

  /**
   * Returns the current game status. The game status can be one of the following: rendering an animation,
   * rendering a warning, the game has ended, it's the human player's turn, or it's the bot's turn.
   *
   * @return the current game status.
   */
  public GameStatus getGameStatus() {
    if (animation != null || rookAnimation != null) {
      // If there's an active animation, the game is rendering the animation
      return GameStatus.RENDERING_ANIMATION;
    } else if (warning != null) {
      // If there's an active warning, the game is rendering the warning
      return GameStatus.RENDERING_WARNING;
    } else if (report != null) {
      // If there's a game report, the game has ended
      return GameStatus.ENDED;
    } else if (currentPlayer == humanAgent) {
      // If the current player is the human agent, it's the human player's turn
      return GameStatus.PLAYER_TURN;
    } else if (currentPlayer == botAgent) {
      // If the current player is the bot agent, it's the bot's turn
      return GameStatus.COMPUTER_TURN;
    }
    return null;
  }

  /**
   * Executes a movement for the current player. Throws an exception if the move would put the player's king in check.
   *
   * @param m the movement to execute
   * @throws RuleViolationException if the move is illegal
   */
  public void movePiece(Movement m) throws RuleViolationException {
    // Predict if the king will be in check after the move
    InCheckIncident inc = predictInCheck(
      board.getPieceOwner(m.getSourcePiece()),
      m
    );
    Piece piece = m.getSourcePiece();
    Tile target = m.getTargetTile();
    PlayerAgent mover = board.getAgentByColor(m.getSourcePiece().getColor());

    // If the king would be in check after the move, throw an exception
    if (inc != null) {
      throw new KingInDangerException(mover);
    }

    AnimationVehicle animation = new AnimationVehicle(
      pieceMovementSpeed,
      maxMovementTime,
      m
    );
    Piece capturedPiece = target.getCurrentPiece();

    // If the target tile is occupied by a piece
    if (capturedPiece != null) {
      // If the piece is the opponent's king, throw an exception
      if (
        capturedPiece == mover.getOpponent().getKing()
      ) throw new KingDignityException(mover);

      // Remove the opponent's piece from the game
      board.getPieceOwner(capturedPiece).getPieces().remove(capturedPiece);
    }

    Movement castlingMove = getCastlingMovement(m);

    mover.setLastMove(m);
    setAnimation(animation);
    m.perform();

    boolean pawnPromotion =
      piece instanceof Pawn && target != null && target.getY() == 7;
    // If a pawn has reached the opponent's side of the board, promote it to a queen
    if (pawnPromotion) {
      Queen queen = new Queen(piece.getColor());
      target.setCurrentPiece(queen);
      queen.setCurrentTile(target);
      mover.getPieces().remove(piece);
      mover.getPieces().add(queen);
    }

    refreshAvailableMoves(this.board);
    mover.increaseRemainingTime();

    if (castlingMove != null) {
      AnimationVehicle castlingAnimationVehicle = new AnimationVehicle(
        pieceMovementSpeed,
        maxMovementTime,
        castlingMove
      );
      setRookAnimation(castlingAnimationVehicle);
      mover.getPieces().remove(castlingMove.perform());
    }

    // After the move, it is the opponent's turn
    currentPlayer = mover.getOpponent();

    // Detect if the current player is in check
    InCheckIncident incident = detectInCheck(currentPlayer);
    setInCheck(incident);

    // Play sound
    if (incident != null) {
      SoundPlayer.playSound("move-check.wav");
    } else if (capturedPiece != null) {
      // Play capture sound
      SoundPlayer.playSound("capture.wav");
    } else if (castlingMove != null) {
      SoundPlayer.playSound("castle.wav");
    } else if (pawnPromotion) {
      SoundPlayer.playSound("promote.wav");
    } else {
      SoundPlayer.playSound("move-self.wav");
    }
  }

  /**
   * Refreshes the set of available moves for all pieces on the board.
   *
   * @param board the game board to refresh
   */
  public void refreshAvailableMoves(Board board) {
    for (int i = 0; i < Board.BOARD_WIDTH; i++) {
      for (int j = 0; j < Board.BOARD_WIDTH; j++) {
        Piece piece = board.getTile(i, j).getCurrentPiece();
        if (piece != null) {
          piece.updatePossibleTargets(board);
        }
      }
    }
  }

  /**
   * Returns a set of tiles that would be dangerous for a given piece to move to.
   *
   * @param p the piece to check
   * @return a set of dangerous tiles
   */
  public Set<Tile> getDangerTiles(Piece p) {
    Set<Tile> moves = p.getPossibleTargets();
    Set<Tile> illegal = new HashSet<Tile>();
    for (Tile move : moves) {
      // If the piece would be threatened by an opponent's piece on the tile, add it to the set
      if (
        predictThreats(
          board.getPieceOwner(p).getOpponent(),
          new Movement(p, move),
          p
        )
          .size() >
        0
      ) {
        illegal.add(move);
      }
    }
    return illegal;
  }

  /**
   * Predicts the pieces that would threaten a piece after a given move.
   *
   * @param attacker the player making the move
   * @param move     the move to consider
   * @param subject  the piece to check for threats against
   * @return a set of pieces that would threaten the subject piece after the move
   */
  public Set<Piece> predictThreats(
    PlayerAgent attacker,
    Movement move,
    Piece subject
  ) {
    // Start a simulation of the move
    int sourceX = move.getSourceX();
    int sourceY = move.getSourceY();
    int targetX = move.getTargetX();
    int targetY = move.getTargetY();
    int subjectX = subject.getX();
    int subjectY = subject.getY();

    // Create a clone of the board to simulate the move on
    Board clonedBoard = board.clone();
    Tile simSourceTile = clonedBoard.getTile(sourceX, sourceY);
    Piece simSourcePiece = simSourceTile.getCurrentPiece();
    Tile simTargetTile = clonedBoard.getTile(targetX, targetY);
    Piece simSubject = clonedBoard.getPiece(subjectX, subjectY);

    // Execute the move in the simulation
    new Movement(simSourcePiece, simTargetTile).perform();

    refreshAvailableMoves(clonedBoard);

    // Get the pieces that could attack the subject piece after the move
    Set<Piece> threats = detectThreats(clonedBoard, simSubject);

    // Return the threats
    return threats;
  }

  /**
   * Detects the pieces that could threaten a piece on a given board.
   *
   * @param board   the board to check
   * @param subject the piece to check for threats against
   * @return a set of pieces that could threaten the subject piece
   */
  public Set<Piece> detectThreats(Board board, Piece subject) {
    int attackeeX = subject.getX();
    int attackeeY = subject.getY();
    Set<Piece> attackerPieces = new HashSet<>();
    Piece attackee = board.getPiece(attackeeX, attackeeY);

    if (attackee == null) {
      throw new RuntimeException("attackee is not found");
    }

    Piece.Color attackeeColor = attackee.getColor();

    // Find all the pieces that could attack the subject piece
    for (int i = 0; i < Board.BOARD_WIDTH; i++) {
      for (int j = 0; j < Board.BOARD_WIDTH; j++) {
        Piece p = board.getTile(i, j).getCurrentPiece();
        if (p != null && p.getColor() != attackeeColor) {
          attackerPieces.add(p);
        }
      }
    }

    // Check if any of the attacker's pieces could reach the subject piece
    Set<Piece> threats = new HashSet<>();
    for (Piece piece : attackerPieces) {
      Set<Tile> moves = piece instanceof Pawn
        ? ((Pawn) piece).getAttackRange(board)
        : piece.getPossibleTargets();
      if (moves.contains(attackee.getCurrentTile())) {
        threats.add(piece);
      }
    }

    return threats;
  }

  /**
   * Generates all possible legal moves for a given player.
   *
   * @param agent the player for which to generate moves
   * @return a set of all possible legal moves
   */
  public Set<Movement> getAllLegalMovements(PlayerAgent agent) {
    Set<Movement> allLegalMovements = new HashSet<>();
    for (Piece p : agent.getPieces()) {
      Set<Tile> targets = p.getPossibleTargets();
      for (Tile t : targets) {
        allLegalMovements.add(new Movement(p, t));
      }
      if (p instanceof King) {
        allLegalMovements.addAll(
          ((King) p).getPotentialCastlingMovements(this)
        );
      }
    }
    return allLegalMovements;
  }

  /**
   * Solves a given InCheckIncident by finding all moves that would remove the king from check.
   *
   * @param inc the InCheckIncident to solve
   * @return a set of all moves that would remove the king from check
   */
  public Set<Movement> solveIncident(InCheckIncident inc) {
    Set<Movement> solutions = new HashSet<>();
    King k = inc.getThreatenedKing();
    Set<Movement> allLegalMovements = getAllLegalMovements(
      board.getAgentByColor(k.getColor())
    );

    for (Movement move : allLegalMovements) {
      Set<Piece> threats = predictThreats(
        board.getPieceOwner(k).getOpponent(),
        move,
        k
      );
      if (threats.size() == 0) {
        solutions.add(move);
      }
    }
    return solutions;
  }

  /**
   * Detects if a player's king is in check.
   *
   * @param agent the player to check
   * @return an InCheckIncident if the king is in check, or null if not
   */
  public InCheckIncident detectInCheck(PlayerAgent agent) {
    Set<Piece> threats = detectThreats(getBoard(), agent.getKing());
    if (threats.size() > 0) {
      return new InCheckIncident(agent.getKing());
    }
    return null;
  }

  /**
   * Predicts if a player's king would be in check after a given move.
   *
   * @param agent the player to check
   * @param m     the move to consider
   * @return an InCheckIncident if the king would be in check after the move, or null if not
   */
  public InCheckIncident predictInCheck(PlayerAgent agent, Movement m) {
    Set<Piece> threats = predictThreats(agent, m, agent.getKing());
    if (threats.size() > 0) {
      return new InCheckIncident(agent.getKing());
    }
    return null;
  }

  /**
   * Checks if the game is ended.
   *
   * @return true if the game is ended, false otherwise
   */
  @Override
  public boolean isEnded() {
    return humanAgent.isEnded() || botAgent.isEnded();
  }

  /**
   * Checks if the game is ended.
   *
   * @return true if the game is ended, false otherwise
   */

  /**
   * Get all movements that don't impose a threat to the king
   *
   * @return all movements that don't impose a threat to the king
   */
  public Set<Movement> getSafeMovements(Set<Movement> allMovements) {
    if (allMovements == null) return null;
    Set<Movement> safeMovements = new HashSet<>();
    for (Movement move : allMovements) {
      if (
        predictInCheck(board.getPieceOwner(move.getSourcePiece()), move) == null
      ) safeMovements.add(move);
    }
    return safeMovements;
  }

  // Game status enumeration
  public enum GameStatus {
    ENDED,
    RENDERING_ANIMATION,
    PLAYER_TURN,
    COMPUTER_TURN,
    RENDERING_WARNING,
  }

  /**
   * Retrieves the castling movement based on the given movement.
   *
   * @param m the original movement
   * @return the castling movement if valid, or null if not applicable
   */
  public Movement getCastlingMovement(Movement m) {
    // Castling
    if (
      m.getSourcePiece() instanceof King &&
      !m.getSourcePiece().getHasMovedBefore() &&
      m.getTargetPiece() == null &&
      Math.abs(m.getTargetX() - m.getSourceX()) == 2 &&
      getInCheck() == null
    ) {
      int unitDirection = m.getTargetX() - m.getSourceX() > 0 ? 1 : -1;
      int curX = m.getSourceX();
      Rook r = null;

      while (curX >= 0 && curX < Board.BOARD_WIDTH) {
        Piece p = board.getPiece(curX, m.getSourceY());
        if (p instanceof Rook && !p.getHasMovedBefore()) {
          r = (Rook) p;
        }
        curX += unitDirection;
      }

      if (r != null) {
        return new Movement(
          r,
          board.getTile(m.getTargetX() - unitDirection, m.getSourceY())
        );
      }
    }

    return null;
  }
}
