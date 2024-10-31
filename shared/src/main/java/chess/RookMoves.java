package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves extends MoveCalculator {
    public Collection<ChessMove> moves = new ArrayList<>();

    public RookMoves(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, ChessBoard board, ChessPosition position) {
        super(pieceColor, type, board, position);
    }

    public Collection<ChessMove> getRookMoves() {
        int row = position.getRow();
        int col = position.getColumn();

        moves = checkLine(row, col, 1, 0, moves);
        moves = checkLine(row, col, -1, 0, moves);
        moves = checkLine(row, col, 0, 1, moves);
        moves = checkLine(row, col, 0, -1, moves);

        return moves;
    }
}
