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

        checkLine(row, col, 1, 1);
        checkLine(row, col, 1, -1);
        checkLine(row, col, -1, 1);
        checkLine(row, col, -1, -1);

        return moves;
    }

    private void checkLine(int row, int col, int rowSign, int colSign) {
        while (row <= 8 && col <= 8) {
            row += rowSign;
            col += colSign;

            ChessPosition nextPosition = new ChessPosition(row, col);

            if (inBounds(nextPosition)) {
                ChessMove nextMove = new ChessMove(position, nextPosition, null);

                if (isOccupied(nextPosition)) {
                    if (!isSameColor(nextPosition, pieceColor)) {
                        moves.add(nextMove);
                    }
                    break;
                } else {
                    moves.add(nextMove);
                }
            } else {
                break;
            }
        }
    }
}