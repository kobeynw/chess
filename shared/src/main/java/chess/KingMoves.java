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


        checkPosition(row + 1, col + 1);
        checkPosition(row + 1, col - 1);
        checkPosition(row - 1, col + 1);
        checkPosition(row - 1, col - 1);

        checkPosition(row + 1, col);
        checkPosition(row, col + 1);
        checkPosition(row - 1, col);
        checkPosition(row, col - 1);

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
