package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoves extends MoveCalculator {
    private final Collection<ChessMove> moves = new ArrayList<>();

    public KingMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        super(piece, board, position);
    }

    public Collection<ChessMove> getKingMoves() {
        int row = position.getRow();
        int col = position.getColumn();

        for (int i = 0; i <= 1; i++) {
            for (int j = 0; j <= 1; j++) {
                checkPosition(row + i, col + j);
                checkPosition(row + i, col - j);
                checkPosition(row - i, col + j);
                checkPosition(row - i, col - j);
            }
        }

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