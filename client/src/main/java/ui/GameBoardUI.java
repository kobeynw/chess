package ui;

import static ui.EscapeSequences.*;
import static java.lang.System.out;

import chess.ChessGame.TeamColor;
import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashMap;
import java.util.Map;

public class GameBoardUI {
    private static TeamColor teamColor = TeamColor.BLACK;
    private static final String darkColor = SET_BG_COLOR_BLUE;
    private static final String lightColor = SET_BG_COLOR_WHITE;
    private static final String borderColor = SET_BG_COLOR_LIGHT_GREY;
    private static final String textColor = SET_TEXT_COLOR_WHITE;
    private static final String pieceColor = SET_TEXT_COLOR_BLACK;

    private static final String[] HEADER_ROW = {EMPTY, " A ", " B ", " C ", " D ", " E ", " F ", " G ", " H ", EMPTY};
    private static final ChessBoard chessBoard = new ChessBoard();
    private static final Map<ChessPiece.PieceType, String> blackTypeMap = getTypeMap(TeamColor.BLACK);
    private static final Map<ChessPiece.PieceType, String> whiteTypeMap = getTypeMap(TeamColor.WHITE);

    private static Map<ChessPiece.PieceType, String> getTypeMap(TeamColor teamColor) {
        Map<ChessPiece.PieceType, String> typeMap = new HashMap<>();

        if (teamColor == TeamColor.BLACK) {
            typeMap.put(ChessPiece.PieceType.KING, BLACK_KING);
            typeMap.put(ChessPiece.PieceType.QUEEN, BLACK_QUEEN);
            typeMap.put(ChessPiece.PieceType.BISHOP, BLACK_BISHOP);
            typeMap.put(ChessPiece.PieceType.ROOK, BLACK_ROOK);
            typeMap.put(ChessPiece.PieceType.KNIGHT, BLACK_KNIGHT);
            typeMap.put(ChessPiece.PieceType.PAWN, BLACK_PAWN);
        } else {
            typeMap.put(ChessPiece.PieceType.KING, WHITE_KING);
            typeMap.put(ChessPiece.PieceType.QUEEN, WHITE_QUEEN);
            typeMap.put(ChessPiece.PieceType.BISHOP, WHITE_BISHOP);
            typeMap.put(ChessPiece.PieceType.ROOK, WHITE_ROOK);
            typeMap.put(ChessPiece.PieceType.KNIGHT, WHITE_KNIGHT);
            typeMap.put(ChessPiece.PieceType.PAWN, WHITE_PAWN);
        }

        return typeMap;
    }

    public static void main(String[] args) {
        drawGame();
        out.println();
        teamColor = TeamColor.WHITE;
        drawGame();

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private static void drawGame() {
        chessBoard.resetBoard();

        drawHeader();
        drawBoard();
        drawHeader();
    }

    private static void drawHeader() {
        out.print(textColor);
        out.print(borderColor);

        if (teamColor == TeamColor.WHITE) {
            for (String row : HEADER_ROW) {
                out.print(row);
            }
        } else {
            for (int i = HEADER_ROW.length - 1; i >= 0; i--) {
                out.print(HEADER_ROW[i]);
            }
        }

        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static void drawBoard() {
        int currentRow = 1;
        int currentCol = 1;

        if (teamColor == TeamColor.BLACK) {
            for (int i = 1; i <= 8; i++) {
                drawRow(i, currentRow, currentCol);
                out.print(RESET_BG_COLOR);
                out.println();

                currentRow++;
            }
        } else {
            for (int i = 8; i >= 1; i--) {
                drawRow(i, currentRow, currentCol);
                out.print(RESET_BG_COLOR);
                out.println();

                currentRow++;
            }
        }
    }

    private static void drawRow(int rowNum, int currentRow, int currentCol) {
        out.print(borderColor);
        out.print(" " + rowNum + " ");

        if (currentRow % 2 == 1) {
            for (int i = 0; i < 4; i++) {
                out.print(lightColor);
                drawSquare(currentRow, currentCol);
                currentCol++;

                out.print(darkColor);
                drawSquare(currentRow, currentCol);
                currentCol++;
            }
        } else {
            for (int i = 0; i < 4; i++) {
                out.print(darkColor);
                drawSquare(currentRow, currentCol);
                currentCol++;

                out.print(lightColor);
                drawSquare(currentRow, currentCol);
                currentCol++;
            }
        }

        out.print(borderColor);
        out.print(" " + rowNum + " ");
    }

    private static void drawSquare(int currentRow, int currentCol) {
        if (teamColor == TeamColor.WHITE) {
            currentRow = 9 - currentRow;
        } else {
            currentCol = 9 - currentCol;
        }

        ChessPosition position = new ChessPosition(currentRow, currentCol);
        ChessPiece piece = chessBoard.getPiece(position);

        if (piece == null) {
            out.print(EMPTY);
            return;
        }

        TeamColor color = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();

        if (color == TeamColor.WHITE) {
            out.print(pieceColor);
            out.print(whiteTypeMap.get(type));
            out.print(textColor);
        } else {
            out.print(pieceColor);
            out.print(blackTypeMap.get(type));
            out.print(textColor);
        }
    }
}
