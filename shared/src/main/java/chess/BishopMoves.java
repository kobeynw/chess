package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoves extends MoveCalculator {
    public Collection<ChessMove> moves = new ArrayList<>();

    public BishopMoves(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, ChessBoard board, ChessPosition position) {
        super(pieceColor, type, board, position);
    }

    public Collection<ChessMove> getBishopMoves() {
        int row = position.getRow();
        int col = position.getColumn();

        moves = checkLine(row, col, 1, 1, moves);
        moves = checkLine(row, col, 1, -1, moves);
        moves = checkLine(row, col, -1, 1, moves);
        moves = checkLine(row, col, -1, -1, moves);

        return moves;
    }
}