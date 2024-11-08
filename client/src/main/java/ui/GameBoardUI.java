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
    private static TeamColor TEAM_COLOR = TeamColor.BLACK;
    private static final String DARK_COLOR = SET_BG_COLOR_BLUE;
    private static final String LIGHT_COLOR = SET_BG_COLOR_WHITE;
    private static final String BORDER_COLOR = SET_BG_COLOR_LIGHT_GREY;
    private static final String TEXT_COLOR = SET_TEXT_COLOR_WHITE;
    private static final String PIECE_COLOR = SET_TEXT_COLOR_BLACK;

    private static final String[] HEADER_ROW = {EMPTY, " A ", " B ", " C ", " D ", " E ", " F ", " G ", " H ", EMPTY};
    private static final ChessBoard CHESS_BOARD = new ChessBoard();
    private static final Map<ChessPiece.PieceType, String> BLACK_TYPE_MAP = getTypeMap(TeamColor.BLACK);
    private static final Map<ChessPiece.PieceType, String> WHITE_TYPE_MAP = getTypeMap(TeamColor.WHITE);

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
        TEAM_COLOR = TeamColor.WHITE;
        drawGame();

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private static void drawGame() {
        CHESS_BOARD.resetBoard();

        drawHeader();
        drawBoard();
        drawHeader();
    }

    private static void drawHeader() {
        out.print(TEXT_COLOR);
        out.print(BORDER_COLOR);

        if (TEAM_COLOR == TeamColor.WHITE) {
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

        if (TEAM_COLOR == TeamColor.BLACK) {
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
        out.print(BORDER_COLOR);
        out.print(" " + rowNum + " ");

        if (currentRow % 2 == 1) {
            for (int i = 0; i < 4; i++) {
                out.print(LIGHT_COLOR);
                drawSquare(currentRow, currentCol);
                currentCol++;

                out.print(DARK_COLOR);
                drawSquare(currentRow, currentCol);
                currentCol++;
            }
        } else {
            for (int i = 0; i < 4; i++) {
                out.print(DARK_COLOR);
                drawSquare(currentRow, currentCol);
                currentCol++;

                out.print(LIGHT_COLOR);
                drawSquare(currentRow, currentCol);
                currentCol++;
            }
        }

        out.print(BORDER_COLOR);
        out.print(" " + rowNum + " ");
    }

    private static void drawSquare(int currentRow, int currentCol) {
        if (TEAM_COLOR == TeamColor.WHITE) {
            currentRow = 9 - currentRow;
        } else {
            currentCol = 9 - currentCol;
        }

        ChessPosition position = new ChessPosition(currentRow, currentCol);
        ChessPiece piece = CHESS_BOARD.getPiece(position);

        if (piece == null) {
            out.print(EMPTY);
            return;
        }

        TeamColor color = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();

        if (color == TeamColor.WHITE) {
            out.print(PIECE_COLOR);
            out.print(WHITE_TYPE_MAP.get(type));
            out.print(TEXT_COLOR);
        } else {
            out.print(PIECE_COLOR);
            out.print(BLACK_TYPE_MAP.get(type));
            out.print(TEXT_COLOR);
        }
    }
}
