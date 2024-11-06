package ui;

import static java.lang.System.out;
import static java.lang.System.in;
import java.util.Scanner;

public class ClientUI {
    public static void main(String[] args) {
        welcomeMessage();
        preLoginDisplay();
        postLoginDisplay();
    }

    private static void welcomeMessage() {
        out.println("Welcome to 240 Chess! Type the number of the option that you would like to select.");
        help();
    }

    private static void preLoginDisplay() {
        boolean isLoggedIn = false;
        Scanner scanner = new Scanner(in);

        while (!isLoggedIn) {
            out.print(">>> ");

            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();

                switch (input) {
                    case 1:
                        registerUser();
                        isLoggedIn = true;
                        break;
                    case 2:
                        loginUser();
                        isLoggedIn = true;
                        break;
                    case 3:
                        help();
                        break;
                    case 4:
                        quit();
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
    }

    private static void registerUser() {
        out.println("Logged in");
    }

    private static void loginUser() {
        out.println("Logged in");
    }

    private static void help() {
        out.println("1. Register");
        out.println("2. Login");
        out.println("3. Help");
        out.println("4. Quit");
    }

    private static void quit() {
        out.println("Thanks for playing!");
    }

    private static void postLoginDisplay() {

    }
}
