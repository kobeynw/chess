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
    private static final ServerFacade serverFacade = new ServerFacade();
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
                RegisterResult registerResult = serverFacade.register(username, password, email);
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
                LoginResult loginResult = serverFacade.login(username, password);
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
                        break;
                    case 5:
                        observeGame();
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
            serverFacade.logout(authToken);
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
                CreateGameResult createGameResult = serverFacade.createGame(authToken, gameName);
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
            ListGamesResult listGameResult = serverFacade.listGames(authToken);
            Collection<GameData> games = listGameResult.games();
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
                serverFacade.playGame(authToken, color, gameID);
                out.println();
                GameBoardUI.main(null);
                out.println();
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
            out.println();
            GameBoardUI.main(null);
            out.println();
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
}
