package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves extends MoveCalculator {
    public Collection<ChessMove> moves = new ArrayList<>();

    public KnightMoves(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, ChessBoard board, ChessPosition position) {
        super(pieceColor, type, board, position);
    }

    public Collection<ChessMove> getKnightMoves() {
        int row = position.getRow();
        int col = position.getColumn();

        checkPosition(row + 1, col + 2);
        checkPosition(row + 1, col - 2);
        checkPosition(row - 1, col + 2);
        checkPosition(row - 1, col - 2);

        checkPosition(row + 2, col + 1);
        checkPosition(row + 2, col - 1);
        checkPosition(row - 2, col + 1);
        checkPosition(row - 2, col - 1);

        return moves;
    }

    private void checkPosition(int row, int col) {
        ChessPosition nextPosition = new ChessPosition(row, col);

        if (inBounds(nextPosition)) {
            ChessMove nextMove = new ChessMove(position, nextPosition, null);

            if (isOccupied(nextPosition)) {
                if (!isSameColor(nextPosition, pieceColor)) {
                    moves.add(nextMove);
                }
            } else {
                moves.add(nextMove);
            }
        }
    }
}
