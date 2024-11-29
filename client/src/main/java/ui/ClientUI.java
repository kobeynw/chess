package ui;

import chess.ChessGame;
import model.GameData;
import network.ServerFacade;
import result.*;

import static java.lang.System.out;
import static java.lang.System.in;

import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ClientUI {
    private static final ServerFacade SERVER_FACADE = new ServerFacade(8080);
    private static String authToken = null;

    public static void main(String[] args) {
        welcomeMessage();
        preLoginDisplay();
    }

    private static void welcomeMessage() {
        out.print("Welcome to ");
        out.print(SET_TEXT_BOLD);
        out.print(SET_TEXT_COLOR_BLUE);
        out.print("240 Chess");
        out.print(RESET_TEXT_BOLD_FAINT);
        out.print(RESET_TEXT_COLOR);
        out.println("!");
        out.println("Type the number of the option that you would like to select.");
    }

    private static void printErrorMessage(String errorMsg) {
        out.println(SET_TEXT_COLOR_RED);
        out.println(errorMsg);
        out.println(RESET_TEXT_COLOR);
    }

    private static void preLoginDisplay() {
        boolean isExiting = false;
        Scanner scanner = new Scanner(in);

        preLoginHelp();

        while (!isExiting) {
            out.print("[");
            out.print(SET_TEXT_COLOR_RED);
            out.print("LOGGED OUT");
            out.print(RESET_TEXT_COLOR);
            out.print("] >>> ");

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
                        quit();
                        isExiting = true;
                        break;
                    default:
                        printErrorMessage("Please enter a valid number");
                        break;
                }
            } else {
                printErrorMessage("Please enter a valid number");
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
                out.println("\nSuccessfully logged in as " + registerResult.username());
                postLoginDisplay();
            } catch (Exception e) {
                printErrorMessage(e.getMessage());
                preLoginDisplay();
            }
        } else {
            printErrorMessage("Login failed");
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
                out.println("\nSuccessfully logged in as " + loginResult.username());
                postLoginDisplay();
            } catch (Exception e) {
                printErrorMessage(e.getMessage());
                preLoginDisplay();
            }
        } else {
            printErrorMessage("Login failed");
        }
    }

    private static void preLoginHelp() {
        out.println("0. Help");
        out.println("1. Register");
        out.println("2. Login");
        out.println("3. Quit");
    }

    private static void quit() {
        out.println("\nThanks for playing!");
    }

    private static void postLoginDisplay() {
        boolean isLoggedIn = true;
        Scanner scanner = new Scanner(in);

        postLoginHelp();

        while (isLoggedIn) {
            out.println("(POST LOGIN)");
            out.print("[");
            out.print(SET_TEXT_COLOR_GREEN);
            out.print("LOGGED IN");
            out.print(RESET_TEXT_COLOR);
            out.print("] >>> ");

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
                        printErrorMessage("Please enter a valid number");
                        break;
                }
            } else {
                printErrorMessage("Please enter a valid number");
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
            printErrorMessage(e.getMessage());
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
                printErrorMessage(e.getMessage());
            }
        } else {
            printErrorMessage("Game creation failed");
        }
    }

    private static void listGames() {
        try {
            ListGamesResult listGamesResult = SERVER_FACADE.listGames(authToken);
            Collection<GameData> games = listGamesResult.games();
            if (games.isEmpty()) {
                printErrorMessage("No available games");
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
            printErrorMessage(e.getMessage());
        }
    }

    private static void playGame() {
        Scanner scanner = new Scanner(in);
        int gameID = 0;
        ChessGame.TeamColor color = null;

        out.print("Game ID >>> ");
        if (scanner.hasNextInt()) {
            gameID = scanner.nextInt();
        }

        out.print("Color ('WHITE' or 'BLACK') >>> ");
        if (scanner.hasNext()) {
            String colorInput = scanner.next().toUpperCase();

            if (Objects.equals(colorInput.toUpperCase(), "BLACK")) {
                color = ChessGame.TeamColor.BLACK;
            } else if (Objects.equals(colorInput.toUpperCase(), "WHITE")) {
                color = ChessGame.TeamColor.WHITE;
            }
        }

        if (gameID >= 1 && color != null) {
            try {
                SERVER_FACADE.playGame(authToken, color, gameID);
                gameplayDisplay();
            } catch (Exception e) {
                printErrorMessage(e.getMessage());
            }
        } else {
            printErrorMessage("Game join failed");
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
            // TODO: server facade observe game via websocket
            gameplayDisplay();
        } else {
            printErrorMessage("Game observation failed");
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

    private static void gameplayDisplay() {
        boolean isPlaying = true;
        Scanner scanner = new Scanner(in);

        gameplayHelp();

        while (isPlaying) {
            out.println("(GAMEPLAY)");
            out.print("[");
            out.print(SET_TEXT_COLOR_GREEN);
            out.print("LOGGED IN");
            out.print(RESET_TEXT_COLOR);
            out.print("] >>> ");

            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();

                switch (input) {
                    case 0:
                        out.println("\nType the number of the option that you would like to select.");
                        gameplayHelp();
                        break;
                    case 1:
                        leaveGame();
                        isPlaying = false;
                        break;
                    case 2:
                        redrawBoard();
                        break;
                    case 3:
                        makeMove();
                        break;
                    case 4:
                        highlightMoves();
                        break;
                    case 5:
                        resign();
                        break;
                    default:
                        printErrorMessage("Please enter a valid number");
                        break;
                }
            } else {
                printErrorMessage("Please enter a valid number");
                scanner.next();
            }
        }
    }

    private static void leaveGame() {
        // TODO: leave game functionality
    }

    private static void redrawBoard() {
        // TODO: redraw board functionality
    }

    private static void makeMove() {
        // TODO: make move functionality
    }

    private static void highlightMoves() {
        // TODO: highlight legal moves functionality
    }

    private static void resign() {
        // TODO: resign functionality
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
