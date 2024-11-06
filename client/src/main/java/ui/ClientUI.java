package ui;

import static java.lang.System.out;
import static java.lang.System.in;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ClientUI {
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
                        out.println("Please enter a valid number");
                        break;
                }
            } else {
                out.println("Please enter a valid number");
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
            out.println("\nSuccessfully logged in as " + username);

            postLoginDisplay();
        } else {
            out.println("Login failed");
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
            out.println("\nSuccessfully logged in as " + username);

            postLoginDisplay();
        } else {
            out.println("Login failed");
        }
    }

    private static void preLoginHelp() {
        out.println("\n0. Help");
        out.println("1. Register");
        out.println("2. Login");
        out.println("3. Quit");
    }

    private static void quit() {
        out.println("Thanks for playing!");
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
                        out.println("Please enter a valid number");
                        break;
                }
            } else {
                out.println("Please enter a valid number");
                scanner.next();
            }
        }

        preLoginDisplay();
    }

    private static void logoutUser() {
        out.println("Logged out");
    }

    private static void createGame() {
        out.println("Created game");
    }

    private static void listGames() {
        out.println("Games: None");
    }

    private static void playGame() {
        out.println();
        GameBoardUI.main(null);
        out.println();
    }

    private static void observeGame() {
        out.println();
        GameBoardUI.main(null);
        out.println();
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
