package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves extends MoveCalculator {
    public Collection<ChessMove> moves = new ArrayList<>();

    public QueenMoves(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, ChessBoard board, ChessPosition position) {
        super(pieceColor, type, board, position);
    }

    public Collection<ChessMove> getQueenMoves() {
        int row = position.getRow();
        int col = position.getColumn();

        moves = checkLine(row, col, 1, 0, moves);
        moves = checkLine(row, col, -1, 0, moves);
        moves = checkLine(row, col, 0, 1, moves);
        moves = checkLine(row, col, 0, -1, moves);

        moves = checkLine(row, col, 1, 1, moves);
        moves = checkLine(row, col, -1, 1, moves);
        moves = checkLine(row, col, 1, -1, moves);
        moves = checkLine(row, col, -1, -1, moves);

        return moves;
    }
}
