import chess.*;
import ui.ClientUI;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        var serverName = args.length > 0 ? args[0] : "localhost:8080";
        ClientUI.entry(serverName);
    }
}