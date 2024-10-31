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

        moves = checkPosition(row + 1, col + 2, moves);
        moves = checkPosition(row + 1, col - 2, moves);
        moves = checkPosition(row - 1, col + 2, moves);
        moves = checkPosition(row - 1, col - 2, moves);

        moves = checkPosition(row + 2, col + 1, moves);
        moves = checkPosition(row + 2, col - 1, moves);
        moves = checkPosition(row - 2, col + 1, moves);
        moves = checkPosition(row - 2, col - 1, moves);

        return moves;
    }
}
