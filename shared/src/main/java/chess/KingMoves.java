package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoves extends MoveCalculator {
    public Collection<ChessMove> moves = new ArrayList<>();

    public KingMoves(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, ChessBoard board, ChessPosition position) {
        super(pieceColor, type, board, position);
    }

    public Collection<ChessMove> getKingMoves() {
        int row = position.getRow();
        int col = position.getColumn();


        moves = checkPosition(row + 1, col + 1, moves);
        moves = checkPosition(row + 1, col - 1, moves);
        moves = checkPosition(row - 1, col + 1, moves);
        moves = checkPosition(row - 1, col - 1, moves);

        moves = checkPosition(row + 1, col, moves);
        moves = checkPosition(row, col + 1, moves);
        moves = checkPosition(row - 1, col, moves);
        moves = checkPosition(row, col - 1, moves);

        return moves;
    }
}
