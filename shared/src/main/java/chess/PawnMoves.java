package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves extends MoveCalculator {
    private final Collection<ChessMove> moves = new ArrayList<>();

    public PawnMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        super(piece, board, position);
    }

    public Collection<ChessMove> getPawnMoves() {
        int row = position.getRow();
        int col = position.getColumn();

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            checkPosition(row + 1, col);
            checkDiagonal(row + 1, col + 1);
            checkDiagonal(row + 1, col - 1);
        } else {
            checkPosition(row - 1, col);
            checkDiagonal(row - 1, col + 1);
            checkDiagonal(row - 1, col - 1);
        }

        return moves;
    }

    private void checkPosition(int nextRow, int nextCol) {
        ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
        ChessMove nextMove = new ChessMove(position, nextPosition, null);

        if (position.getColumn() <= 8 && position.getRow() <= 8 && position.getColumn() >= 1 && position.getRow() >= 1) {
            ChessPiece nextPiece = board.getPiece(nextPosition);
            if (nextPiece == null) {
                if (nextMove.getEndPosition().getRow() == 8 || nextMove.getEndPosition().getRow() == 1) {
                    moves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.QUEEN));
                } else {
                    moves.add(nextMove);
                }

                if (nextRow == 3) {
                    checkPosition(nextRow + 1, nextCol);
                } else if (nextRow == 6) {
                    checkPosition(nextRow - 1, nextCol);
                }
            }
        }
    }

    private void checkDiagonal(int nextRow, int nextCol) {
        ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
        ChessMove nextMove = new ChessMove(position, nextPosition, null);

        if (position.getColumn() <= 8 && position.getRow() <= 8 && position.getColumn() >= 1 && position.getRow() >= 1) {
            ChessPiece nextPiece = board.getPiece(nextPosition);
            if (nextPiece != null) {
                if (nextPiece.getTeamColor() != piece.getTeamColor()) {
                    if (nextMove.getEndPosition().getRow() == 8 || nextMove.getEndPosition().getRow() == 1) {
                        moves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.QUEEN));
                    } else {
                        moves.add(nextMove);
                    }
                }
            }
        }
    }
}