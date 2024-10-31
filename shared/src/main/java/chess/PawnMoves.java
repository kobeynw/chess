package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves extends MoveCalculator {
    public Collection<ChessMove> moves = new ArrayList<>();

    public PawnMoves(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, ChessBoard board, ChessPosition position) {
        super(pieceColor, type, board, position);
    }

    public Collection<ChessMove> getPawnMoves() {
        int row = position.getRow();
        int col = position.getColumn();

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            checkPosition(row + 1, col, pieceColor);
            checkDiagonal(row + 1, col + 1, pieceColor);
            checkDiagonal(row + 1, col - 1, pieceColor);
            if (row == 2 && !isOccupied(new ChessPosition(row + 1, col))) {
                checkPosition(row + 2, col, pieceColor);
            }
        } else {
            checkPosition(row - 1, col, pieceColor);
            checkDiagonal(row - 1, col + 1, pieceColor);
            checkDiagonal(row - 1, col - 1, pieceColor);
            if (row == 7 && !isOccupied(new ChessPosition(row - 1, col))) {
                checkPosition(row - 2, col, pieceColor);
            }
        }


        return moves;
    }

    private void checkPosition(int row, int col, ChessGame.TeamColor color) {
        ChessPosition nextPosition = new ChessPosition(row, col);

        if (inBounds(nextPosition)) {
            if (!isOccupied(nextPosition)) {
                checkPromotion(row, color, nextPosition);
            }
        }
    }

    private void checkDiagonal(int row, int col, ChessGame.TeamColor color) {
        ChessPosition nextPosition = new ChessPosition(row, col);

        if (inBounds(nextPosition) && isOccupied(nextPosition) && !isSameColor(nextPosition, color)) {
            checkPromotion(row, color, nextPosition);
        }
    }

    private void checkPromotion(int row, ChessGame.TeamColor color, ChessPosition nextPosition) {
        if ((row == 8 && color == ChessGame.TeamColor.WHITE) || (row == 1 && color == ChessGame.TeamColor.BLACK)) {
            addPromotions(position, nextPosition);
        } else {
            moves.add(new ChessMove(position, nextPosition, null));
        }
    }

    private void addPromotions(ChessPosition position, ChessPosition nextPosition) {
        moves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.QUEEN));
    }
}
