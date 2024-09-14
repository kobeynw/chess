package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves extends MoveCalculator {
    private final Collection<ChessMove> moves = new ArrayList<>();

    public QueenMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        super(piece, board, position);
    }

    public Collection<ChessMove> getQueenMoves() {
        int row = position.getRow();
        int col = position.getColumn();
        int nextRow = row;
        int nextCol = col;

        while (nextRow <= 8 && nextCol <= 8) {
            nextRow++;
            nextCol++;

            boolean endCheck = checkPosition(nextRow, nextCol);
            if (endCheck) {
                nextRow = row;
                nextCol = col;
                break;
            }
        }

        while (nextRow >= 1 && nextCol <= 8) {
            nextRow--;
            nextCol++;

            boolean endCheck = checkPosition(nextRow, nextCol);
            if (endCheck) {
                nextRow = row;
                nextCol = col;
                break;
            }
        }

        while (nextRow >= 1 && nextCol >= 1) {
            nextRow--;
            nextCol--;

            boolean endCheck = checkPosition(nextRow, nextCol);
            if (endCheck) {
                nextRow = row;
                nextCol = col;
                break;
            }
        }

        while (nextRow <= 8 && nextCol >= 1) {
            nextRow++;
            nextCol--;

            boolean endCheck = checkPosition(nextRow, nextCol);
            if (endCheck) {
                nextRow = row;
                nextCol = col;
                break;
            }
        }

        while (nextRow <= 8) {
            nextRow++;

            boolean endCheck = checkPosition(nextRow, nextCol);
            if (endCheck) {
                nextRow = row;
                break;
            }
        }

        while (nextRow >= 1) {
            nextRow--;

            boolean endCheck = checkPosition(nextRow, nextCol);
            if (endCheck) {
                nextRow = row;
                break;
            }
        }

        while (nextCol >= 1) {
            nextCol--;

            boolean endCheck = checkPosition(nextRow, nextCol);
            if (endCheck) {
                nextCol = col;
                break;
            }
        }

        while (nextCol <= 8) {
            nextCol++;

            boolean endCheck = checkPosition(nextRow, nextCol);
            if (endCheck) {
                break;
            }
        }

        return moves;
    }

    private boolean checkPosition(int nextRow, int nextCol) {
        ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
        ChessMove nextMove = new ChessMove(position, nextPosition, null);

        if (!moveBlocked(nextPosition)) {
            moves.add(nextMove);
            return canTakePiece(nextPosition);
        } else {
            return true;
        }
    }
}