package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);

                if (i == 1 && (j == 1 || j == 8)) {
                    ChessPiece blackRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                    addPiece(position, blackRook);
                } else if (i == 1 && (j == 2 || j == 7)) {
                    ChessPiece blackKnight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                    addPiece(position, blackKnight);
                } else if (i == 1 && (j == 3 || j == 6)) {
                    ChessPiece blackBishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                    addPiece(position, blackBishop);
                } else if (i == 1 && j == 4) {
                    ChessPiece blackQueen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                    addPiece(position, blackQueen);
                } else if (i == 1) {
                    ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                    addPiece(position, blackKing);
                } else if (i == 2) {
                    ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                    addPiece(position, blackPawn);
                } else if (i == 7) {
                    ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                    addPiece(position, whitePawn);
                } else if (i == 8 && (j == 1 || j == 8)) {
                    ChessPiece whiteRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                    addPiece(position, whiteRook);
                } else if (i == 8 && (j == 2 || j == 7)) {
                    ChessPiece whiteKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                    addPiece(position, whiteKnight);
                } else if (i == 8 && (j == 3 || j == 6)) {
                    ChessPiece whiteBishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                    addPiece(position, whiteBishop);
                } else if (i == 8 && j == 4) {
                    ChessPiece whiteQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                    addPiece(position, whiteQueen);
                } else if (i == 8) {
                    ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                    addPiece(position, whiteKing);
                } else {
                    addPiece(position, null);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
