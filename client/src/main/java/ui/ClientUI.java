package ui;

import chess.*;
import model.GameData;
import network.ServerFacade;
import result.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static java.lang.System.out;
import static java.lang.System.in;

import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ClientUI implements ServerMessageObserver {
    private static ServerFacade SERVER_FACADE;
    private static String authToken = null;
    private static ChessGame game = new ChessGame();
    private static ChessGame.TeamColor teamColor = ChessGame.TeamColor.WHITE;

    public static void entry(String serverName) {
        SERVER_FACADE = new ServerFacade(serverName, new ClientUI());

        welcomeMessage();
        preLoginDisplay();
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> printNotification(((NotificationMessage) message).getMessage(), true);
            case ERROR -> printErrorMessage(((ErrorMessage) message).getErrorMessage(), true);
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }
    }

    private static void welcomeMessage() {
        out.println("Welcome to " + SET_TEXT_COLOR_BLUE + "240 Chess" + RESET_TEXT_COLOR + "!");
        out.println("Type the number of the option that you would like to select.\n");
    }

    private static void printErrorMessage(String errorMsg, boolean isSocket) {
        out.println("\n\n" + SET_TEXT_COLOR_RED + errorMsg + RESET_TEXT_COLOR + "\n");
        if (isSocket) {
            out.print("\n[" + SET_TEXT_COLOR_GREEN + "LOGGED IN" + RESET_TEXT_COLOR + "] >>> ");
        }
    }

    private static void printNotification(String msg, boolean isSocket) {
        out.println("\n\n" + SET_TEXT_COLOR_GREEN + msg + RESET_TEXT_COLOR + "\n");
        if (isSocket) {
            out.print("\n[" + SET_TEXT_COLOR_GREEN + "LOGGED IN" + RESET_TEXT_COLOR + "] >>> ");
        }
    }

    private static void loadGame(ChessGame chessGame) {
        game = chessGame;
        redrawBoard();
        out.print("\n[" + SET_TEXT_COLOR_GREEN + "LOGGED IN" + RESET_TEXT_COLOR + "] >>> ");
    }

    private static void preLoginDisplay() {
        boolean isExiting = false;
        Scanner scanner = new Scanner(in);
        preLoginHelp();

        while (!isExiting) {
            out.print("[" + SET_TEXT_COLOR_RED + "LOGGED OUT" + RESET_TEXT_COLOR + "] >>> ");
            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                switch (input) {
                    case 0:
                        out.println("\nType the number of the option that you would like to select.");
                        preLoginHelp();
                        break;
                    case 1:
                        registerUser();
                        isExiting = true;
                        break;
                    case 2:
                        loginUser();
                        isExiting = true;
                        break;
                    case 3:
                        out.println("\nThanks for playing!");
                        isExiting = true;
                        break;
                    default:
                        printErrorMessage("Please enter a valid number", false);
                        break;
                }
            } else {
                printErrorMessage("Please enter a valid number", false);
                scanner.next();
            }
        }
    }

    private static void registerUser() {
        Scanner scanner = new Scanner(in);
        String username = null;
        String password = null;
        String email = null;

        out.print("Username >>> ");
        if (scanner.hasNext()) {
            username = scanner.next();
        }

        out.print("Password >>> ");
        if (scanner.hasNext()) {
            password = scanner.next();
        }

        out.print("Email >>> ");
        if (scanner.hasNext()) {
            email = scanner.next();
        }

        if (username != null && password != null && email != null) {
            try {
                RegisterResult registerResult = SERVER_FACADE.register(username, password, email);
                authToken = registerResult.authToken();
                printNotification("Successfully logged in as " + registerResult.username(), false);
                postLoginDisplay();
            } catch (Exception e) {
                printErrorMessage(e.getMessage(), false);
                preLoginDisplay();
            }
        } else {
            printErrorMessage("Login failed", false);
            preLoginDisplay();
        }
    }

    private static void loginUser() {
        Scanner scanner = new Scanner(in);
        String username = null;
        String password = null;

        out.print("Username >>> ");
        if (scanner.hasNext()) {
            username = scanner.next();
        }

        out.print("Password >>> ");
        if (scanner.hasNext()) {
            password = scanner.next();
        }

        if (username != null && password != null) {
            try {
                LoginResult loginResult = SERVER_FACADE.login(username, password);
                authToken = loginResult.authToken();
                printNotification("Successfully logged in as " + loginResult.username(), false);
                postLoginDisplay();
            } catch (Exception e) {
                printErrorMessage(e.getMessage(), false);
                preLoginDisplay();
            }
        } else {
            printErrorMessage("Login failed", false);
        }
    }

    private static void preLoginHelp() {
        out.println("0. Help");
        out.println("1. Register");
        out.println("2. Login");
        out.println("3. Quit");
    }

    private static void postLoginDisplay() {
        boolean isLoggedIn = true;
        Scanner scanner = new Scanner(in);
        postLoginHelp();
        while (isLoggedIn) {
            out.print("[" + SET_TEXT_COLOR_GREEN + "LOGGED IN" + RESET_TEXT_COLOR + "] >>> ");

            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                switch (input) {
                    case 0:
                        out.println("\nType the number of the option that you would like to select.");
                        postLoginHelp();
                        break;
                    case 1:
                        logoutUser();
                        isLoggedIn = false;
                        break;
                    case 2:
                        createGame();
                        break;
                    case 3:
                        listGames();
                        break;
                    case 4:
                        playGame();
                        postLoginHelp();
                        break;
                    case 5:
                        observeGame();
                        postLoginHelp();
                        break;
                    default:
                        printErrorMessage("Please enter a valid number", false);
                        break;
                }
            } else {
                printErrorMessage("Please enter a valid number", false);
                scanner.next();
            }
        }
        preLoginDisplay();
    }

    private static void logoutUser() {
        try {
            SERVER_FACADE.logout(authToken);
            authToken = null;
            out.println("\nSuccessfully logged out");
        } catch (Exception e) {
            printErrorMessage(e.getMessage(), false);
        }
    }

    private static void createGame() {
        Scanner scanner = new Scanner(in);
        String gameName = null;

        out.print("Game Name >>> ");
        if (scanner.hasNext()) {
            gameName = scanner.nextLine();
        }

        if (gameName != null) {
            try {
                CreateGameResult createGameResult = SERVER_FACADE.createGame(authToken, gameName);
                int gameID = createGameResult.gameID();
                out.println("\nSuccessfully created game:\n* Name: '" + gameName + "'\n* ID: " + gameID);
            } catch (Exception e) {
                printErrorMessage(e.getMessage(), false);
            }
        } else {
            printErrorMessage("Game creation failed", false);
        }
    }

    private static void listGames() {
        try {
            ListGamesResult listGamesResult = SERVER_FACADE.listGames(authToken);
            Collection<GameData> games = listGamesResult.games();
            if (games.isEmpty()) {
                printErrorMessage("No available games", false);
                return;
            }

            out.println("\nAvailable Games (numbered by ID):");
            for (GameData game : games) {
                out.println(game.gameID() + ". " + "'" + game.gameName() + "'");

                if (game.whiteUsername() == null) {
                    out.println("   * White: [available]");
                } else {
                    out.println("   * White: " + game.whiteUsername());
                }
                if (game.blackUsername() == null) {
                    out.println("   * Black: [available]");
                } else {
                    out.println("   * Black: " + game.blackUsername());
                }
            }
        } catch (Exception e) {
            printErrorMessage(e.getMessage(), false);
        }
    }

    private static void playGame() {
        Scanner scanner = new Scanner(in);
        int gameID = 0;
        boolean colorChosen = false;

        out.print("Game ID >>> ");
        if (scanner.hasNextInt()) {
            gameID = scanner.nextInt();
        }

        out.print("Color ('WHITE' or 'BLACK') >>> ");
        if (scanner.hasNext()) {
            String colorInput = scanner.next().toUpperCase();

            if (Objects.equals(colorInput.toUpperCase(), "BLACK")) {
                teamColor = ChessGame.TeamColor.BLACK;
                colorChosen = true;
            } else if (Objects.equals(colorInput.toUpperCase(), "WHITE")) {
                teamColor = ChessGame.TeamColor.WHITE;
                colorChosen = true;
            }
        }

        if (gameID >= 1 && colorChosen) {
            try {
                SERVER_FACADE.playGame(authToken, teamColor, gameID);
                SERVER_FACADE.connect(authToken, gameID);
                gameplayDisplay(gameID);
            } catch (Exception e) {
                printErrorMessage(e.getMessage(), true);
            }
        } else {
            printErrorMessage("Game join failed", false);
        }
    }

    private static void observeGame() {
        Scanner scanner = new Scanner(in);
        int gameID = 0;

        out.print("Game ID >>> ");
        if (scanner.hasNextInt()) {
            gameID = scanner.nextInt();
        }

        if (gameID >= 1) {
            try {
                SERVER_FACADE.connect(authToken, gameID);
            } catch (Exception e) {
                printErrorMessage(e.getMessage(), true);
            }

            teamColor = ChessGame.TeamColor.WHITE;
            gameplayDisplay(gameID);
        } else {
            printErrorMessage("Game observation failed", false);
        }
    }

    private static void postLoginHelp() {
        out.println("\n0. Help");
        out.println("1. Logout");
        out.println("2. Create Game");
        out.println("3. List Games");
        out.println("4. Play Game");
        out.println("5. Observe Game");
    }

    private static void gameplayDisplay(int gameID) {
        boolean isPlaying = true;
        Scanner scanner = new Scanner(in);
        gameplayHelp();
        while (isPlaying) {
            out.print("[" + SET_TEXT_COLOR_GREEN + "LOGGED IN" + RESET_TEXT_COLOR + "] >>> ");

            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                switch (input) {
                    case 0:
                        out.println("\nType the number of the option that you would like to select.");
                        gameplayHelp();
                        break;
                    case 1:
                        leaveGame(gameID);
                        isPlaying = false;
                        break;
                    case 2:
                        redrawBoard();
                        break;
                    case 3:
                        makeMove(gameID);
                        break;
                    case 4:
                        highlightMoves();
                        break;
                    case 5:
                        resign(gameID);
                        break;
                    default:
                        printErrorMessage("Please enter a valid number", false);
                        break;
                }
            } else {
                printErrorMessage("Please enter a valid number", false);
                scanner.next();
            }
        }
    }

    private static void leaveGame(int gameID) {
        try {
            SERVER_FACADE.leave(authToken, gameID);
        }  catch (Exception e) {
            printErrorMessage(e.getMessage(), true);
        }
    }

    private static void redrawBoard() {
        ChessBoard chessBoard = game.getBoard();
        GameBoardUI boardUI = new GameBoardUI(teamColor, null, chessBoard);
        boardUI.drawGame();
    }

    private static ChessPosition getPositionInput(String pieceDescription) {
        Scanner scanner = new Scanner(in);
        int row = 1;
        int col = 1;
        String columnLetter;

        out.println("Enter the row and column of " + pieceDescription + ". (e.g. row 2, column A)");
        out.print("Row >>> ");
        if (scanner.hasNextInt()) {
            row = scanner.nextInt();
            if (row < 1 || row > 8) {
                row = 1;
            }
        }

        out.print("Column >>> ");
        if (scanner.hasNext()) {
            columnLetter = scanner.next();
            col = switch (columnLetter) {
                case "B" -> 2;
                case "C" -> 3;
                case "D" -> 4;
                case "E" -> 5;
                case "F" -> 6;
                case "G" -> 7;
                case "H" -> 8;
                default -> 1;
            };
        }
        return new ChessPosition(row, col);
    }

    private static ChessPiece.PieceType getPromotionPiece() {
        out.println("Enter the promotion piece type. (e.g. QUEEN)");
        out.print("Promotion Piece >>> ");
        Scanner scanner = new Scanner(in);
        ChessPiece.PieceType promotionPiece = null;
        String promotionInput;

        if (scanner.hasNext()) {
            promotionInput = scanner.next();
            promotionPiece = switch (promotionInput.toUpperCase()) {
                case "QUEEN" -> ChessPiece.PieceType.QUEEN;
                case "BISHOP" -> ChessPiece.PieceType.BISHOP;
                case "ROOK" -> ChessPiece.PieceType.ROOK;
                case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
                default -> null;
            };
        }
        return promotionPiece;
    }

    private static void makeMove(int gameID) {
        ChessPosition fromPosition = getPositionInput("the piece to move");
        ChessPosition toPosition = getPositionInput("the position to move to");
        ChessMove move = new ChessMove(fromPosition, toPosition, null);
        
        ChessPiece piece = game.getBoard().getPiece(fromPosition);
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (toPosition.getRow() == 1 || toPosition.getRow() == 8)) {
            ChessPiece.PieceType promotionPiece = getPromotionPiece();
            move = new ChessMove(fromPosition, toPosition, promotionPiece);
        }

        try {
            SERVER_FACADE.makeMove(authToken, gameID, move);
        }  catch (Exception e) {
            printErrorMessage(e.getMessage(), true);
        }
    }

    private static void highlightMoves() {
        ChessPosition position = getPositionInput("the piece to highlight valid moves for");
        ChessBoard chessBoard = game.getBoard();
        GameBoardUI boardUI = new GameBoardUI(teamColor, position, chessBoard);
        boardUI.drawGame();
    }

    private static void resign(int gameID) {
        out.println("Are you sure you want to resign?");
        out.println("(Y/N) >>> ");

        String resignInput;
        Scanner scanner = new Scanner(in);
        if (scanner.hasNext()) {
            resignInput = scanner.next();

            if (resignInput.equalsIgnoreCase("Y") || resignInput.equalsIgnoreCase("YES")) {
                try {
                    SERVER_FACADE.resign(authToken, gameID);
                }  catch (Exception e) {
                    printErrorMessage(e.getMessage(), true);
                }
            }
        }
    }

    private static void gameplayHelp() {
        out.println("\n0. Help");
        out.println("1. Leave Game");
        out.println("2. Redraw Game Board");
        out.println("3. Make Move");
        out.println("4. Highlight Legal Moves");
        out.println("5. Resign");
    }
}
