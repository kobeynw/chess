package ui;

import static ui.EscapeSequences.*;
import static java.lang.System.out;

import chess.*;
import chess.ChessGame.TeamColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameBoardUI {
    private final TeamColor teamColor;
    private final ChessPosition highlightPosition;
    private final ChessBoard chessBoard;
    private Collection<ChessPosition> highlightPositions;

    private static final String DARK_COLOR = SET_BG_COLOR_BLUE;
    private static final String LIGHT_COLOR = SET_BG_COLOR_WHITE;
    private static final String DARK_HIGHLIGHT = SET_BG_COLOR_DARK_GREEN;
    private static final String LIGHT_HIGHLIGHT = SET_BG_COLOR_GREEN;
    private static final String BORDER_COLOR = SET_BG_COLOR_LIGHT_GREY;
    private static final String TEXT_COLOR = SET_TEXT_COLOR_WHITE;
    private static final String PIECE_COLOR = SET_TEXT_COLOR_BLACK;

    private static final String[] HEADER_ROW = {EMPTY, " A ", " B ", " C ", " D ", " E ", " F ", " G ", " H ", EMPTY};
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

    public GameBoardUI(TeamColor teamColor, ChessPosition highlightPosition, ChessBoard chessBoard) {
        this.teamColor = teamColor;
        this.highlightPosition = highlightPosition;
        this.chessBoard = chessBoard;
    }

    private Collection<ChessPosition> getHighlightPositions() {
        Collection<ChessPosition> chessPositions = new ArrayList<>();

        if (highlightPosition != null) {
            ChessGame game = new ChessGame();
            game.setBoard(chessBoard);
            Collection<ChessMove> validMoves = game.validMoves(highlightPosition);

            if (validMoves != null) {
                for (ChessMove move : validMoves) {
                    ChessPosition pos = move.getEndPosition();
                    chessPositions.add(pos);
                }
            }
        }

        return chessPositions;
    }

    public void drawGame() {
        highlightPositions = getHighlightPositions();

        out.println();
        drawHeader();
        drawBoard();
        drawHeader();
        out.println();

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private void drawHeader() {
        out.print(TEXT_COLOR);
        out.print(BORDER_COLOR);

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

    private void drawBoard() {
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

    private boolean isInHighlightPositions(ChessPosition currentPosition) {
        for (ChessPosition position : highlightPositions) {
            if (position.equals(currentPosition)) {
                return true;
            }
        }

        return false;
    }

    private void drawRow(int rowNum, int currentRow, int currentCol) {
        out.print(BORDER_COLOR);
        out.print(" " + rowNum + " ");

        if (currentRow % 2 == 1) {
            for (int i = 0; i < 4; i++) {
                drawSquare(currentRow, currentCol, "LIGHT");
                currentCol++;

                drawSquare(currentRow, currentCol, "DARK");
                currentCol++;
            }
        } else {
            for (int i = 0; i < 4; i++) {
                drawSquare(currentRow, currentCol, "DARK");
                currentCol++;

                drawSquare(currentRow, currentCol, "LIGHT");
                currentCol++;
            }
        }

        out.print(BORDER_COLOR);
        out.print(" " + rowNum + " ");
    }

    private void drawSquare(int currentRow, int currentCol, String squareColor) {
        if (teamColor == TeamColor.WHITE) {
            currentRow = 9 - currentRow;
        } else {
            currentCol = 9 - currentCol;
        }

        ChessPosition position = new ChessPosition(currentRow, currentCol);
        ChessPiece piece = chessBoard.getPiece(position);

        if (squareColor.equals("LIGHT")) {
            if (isInHighlightPositions(position)) {
                out.print(LIGHT_HIGHLIGHT);
            } else {
                out.print(LIGHT_COLOR);
            }
        } else {
            if (isInHighlightPositions(position)) {
                out.print(DARK_HIGHLIGHT);
            } else {
                out.print(DARK_COLOR);
            }
        }

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
