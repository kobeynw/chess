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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                output.append(" ");
                output.append(board[i][j]);
            }
            output.append("  | ");
        }

        return output.toString();
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

    private void addEdgePieces(int j, ChessPosition position, ChessGame.TeamColor color) {
        if (j == 1 || j == 8) {
            this.addPiece(position, new ChessPiece(color, ChessPiece.PieceType.ROOK));
        } else if (j == 2 || j == 7) {
            this.addPiece(position, new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        } else if (j == 3 || j == 6) {
            this.addPiece(position, new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        } else if (j == 4) {
            this.addPiece(position, new ChessPiece(color, ChessPiece.PieceType.QUEEN));
        } else {
            this.addPiece(position, new ChessPiece(color, ChessPiece.PieceType.KING));
        }
    }

    private void addWhitePieces(int i, int j, ChessPosition position) {
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;

        if (i == 1) {
            // WHITE OTHERS
            addEdgePieces(j, position, color);
        } else {
            // WHITE PAWNS
            this.addPiece(position, new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    private void addBlackPieces(int i, int j, ChessPosition position) {
        ChessGame.TeamColor color = ChessGame.TeamColor.BLACK;

        if (i == 7) {
            // BLACK PAWNS
            this.addPiece(position, new ChessPiece(color, ChessPiece.PieceType.PAWN));
        } else {
            // BLACK OTHERS
            addEdgePieces(j, position, color);
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);

                if (i <= 2) {
                    // WHITE PIECES
                    addWhitePieces(i, j, position);
                } else if (i <= 6) {
                    // MIDDLE SPACES
                    this.addPiece(position, null);
                } else {
                    // BLACK PIECES
                    addBlackPieces(i, j, position);
                }
            }
        }
    }
}
