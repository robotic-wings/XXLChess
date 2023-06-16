package XXLChess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import XXLChess.Game.GameStatus;

public class XXLChessTest {
    private App app;
    private Game game;
    private Human human;
    private Bot bot;

    public void loadApp(String configFolder) throws Exception {
        // initialize the game, board and agents
        app = new App();
        app.loadXXLChess("testcases/" + configFolder + "/config.json");
        Class<App> appClass = App.class;
        Field gameField = appClass.getDeclaredField("game");
        gameField.setAccessible(true);
        game = (Game) gameField.get(app);
        human = game.getHumanAgent();
        bot = game.getBotAgent();
        bot.setStrategy(new TestCheckmateStrategy());
    }

    public void simulateMove(int sourceX, int sourceY, int targetX, int targetY) {
        Tile sourceTile = game.getTile(sourceX, sourceY);
        assertNotNull(sourceTile);
        assertNotNull(sourceTile.getCurrentPiece());
        // select
        app.handlePieceSelection(sourceTile);
        assertNotNull(human.getSelection());
        // move
        Tile target = game.getTile(targetX, targetY);
        // the target should be able to be chosen
        assertTrue(app.getTargetTiles().contains(target));
        // request the movement
        app.handlePieceMovement(target);
        assertNotNull(target.getCurrentPiece());
        waitForState(GameStatus.PLAYER_TURN);
    }

    public void waitForState(GameStatus status) {
        while (game.getGameStatus() != status) {
            if (game.isEnded()) break;
            if (game.getGameStatus() == GameStatus.ENDED) break;
            game.tick();
        }
    }

    public void printBoard() {
        System.out.println("********************************");
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 14; j++) {
                Piece p = game.getTile(j, i).getCurrentPiece();
                if (p != null) {
                    char type = p.type;
                    if (p.getColor() == Piece.Color.BLACK) {
                        type = Character.toUpperCase(type);
                    }
                    System.out.print(type + " ");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
        System.out.println("********************************");

    }

    @Test
    public void testPieces() throws Exception {
        int counter = 0;
        loadApp("normal");
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 14; j++) {
                if (game.getTile(i, j).getCurrentPiece() != null) {
                    counter++;
                }
            }
        }
        assertEquals(counter, 56);
    }


    @Test
    public void testMovePawn() throws Exception {
        loadApp("normal");
        simulateMove(7, 12, 7, 10);
    }

    @Test
    public void testCastling() throws Exception {
        loadApp("castling");
        simulateMove(7, 13, 5, 13);
        assertTrue(game.getTile(5, 13).getCurrentPiece() instanceof King);
        assertTrue(game.getTile(6, 13).getCurrentPiece() instanceof Rook);
    }

    @Test
    public void testCheckmate() throws Exception {
        loadApp("checkmate");
        simulateMove(6, 12, 6, 11);
        // printBoard();
        simulateMove(5, 12, 5, 11);
        // printBoard();
        GameReport report = game.getReport();
        assertNotNull(report);
        assertEquals(report.getWinner(), human);
    }

    @Test
    public void testPawnPromotion() throws Exception {
        loadApp("pawn_promotion");
        // promote
        simulateMove(6,8,6,7);
        assertTrue(game.getTile(6, 7).getCurrentPiece() instanceof Queen);
    }

    @Test
    public void testRoyalDefense() throws Exception {
        loadApp("royal_defense");
        game.tick();  // to make sure incheck is working
        // Making a move that does not protect the king
        Tile bruh = game.getTile(10, 4);
        app.handlePieceSelection(bruh);
        printBoard();
        assertNull(human.getSelection());
        waitForState(GameStatus.RENDERING_WARNING);
        waitForState(GameStatus.PLAYER_TURN);
        // Protect the king
        simulateMove(2,7,1,7);
        // The king should be safe now
        assertNull(game.getInCheck());
        printBoard();
    }

    @Test
    public void testInvalidConfig() throws Exception {
        boolean flag = false;
        try {
            loadApp("invalid_config");
        } catch (Exception e) {
            flag = true;
        }
        assertTrue(flag);
    }

    @Test
    public void testBlackPlayer() throws Exception {
        loadApp("black_player");
        // the computer moves first
        assertEquals(game.getGameStatus(), GameStatus.COMPUTER_TURN);
    }
}
