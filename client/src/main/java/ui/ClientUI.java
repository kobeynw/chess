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
        out.println("Welcome to 240 Chess! Type the number of the option that you would like to select.");
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
                    case 1:
                        registerUser();
                        postLoginDisplay();
                        isExiting = true;
                        break;
                    case 2:
                        loginUser();
                        postLoginDisplay();
                        isExiting = true;
                        break;
                    case 3:
                        out.println("\nType the number of the option that you would like to select.");
                        preLoginHelp();
                        break;
                    case 4:
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
        out.println("Logged in");
    }

    private static void loginUser() {
        out.println("Logged in");
    }

    private static void preLoginHelp() {
        out.println("\n1. Register");
        out.println("2. Login");
        out.println("3. Help");
        out.println("4. Quit");
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
                    case 6:
                        out.println("\nType the number of the option that you would like to select.");
                        postLoginHelp();
                        break;
                    default:
                        out.println("Please enter a valid number");
                        scanner.next();
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
        out.println("\n1. Logout");
        out.println("2. Create Game");
        out.println("3. List Games");
        out.println("4. Play Game");
        out.println("5. Observe Game");
        out.println("6. Help");
    }
}
