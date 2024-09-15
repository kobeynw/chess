package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves extends MoveCalculator {
    private final Collection<ChessMove> moves = new ArrayList<>();

    public KnightMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        super(piece, board, position);
    }

    public Collection<ChessMove> getKnightMoves() {
        int row = position.getRow();
        int col = position.getColumn();

        checkPosition(row + 1, col + 2);
        checkPosition(row - 1, col + 2);
        checkPosition(row + 1, col - 2);
        checkPosition(row - 1, col - 2);
        checkPosition(row + 2, col + 1);
        checkPosition(row - 2, col + 1);
        checkPosition(row + 2, col - 1);
        checkPosition(row - 2, col - 1);

        return moves;
    }

    private void checkPosition(int nextRow, int nextCol) {
        ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
        ChessMove nextMove = new ChessMove(position, nextPosition, null);

        if (!moveBlocked(nextPosition)) {
            moves.add(nextMove);
        }
    }
}