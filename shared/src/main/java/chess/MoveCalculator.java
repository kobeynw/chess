package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MoveCalculator {
    public final ChessGame.TeamColor pieceColor;
    public final ChessPiece.PieceType type;
    public final ChessBoard board;
    public final ChessPosition position;

    public MoveCalculator(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, ChessBoard board, ChessPosition position) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> getMoves() {
        if (type == ChessPiece.PieceType.BISHOP) {
            BishopMoves bishop = new BishopMoves(pieceColor, type, board, position);
            return bishop.getBishopMoves();
        }
        else if (type == ChessPiece.PieceType.KNIGHT) {
            KnightMoves knight = new KnightMoves(pieceColor, type, board, position);
            return knight.getKnightMoves();
        } else if (type == ChessPiece.PieceType.KING) {
            KingMoves king = new KingMoves(pieceColor, type, board, position);
            return king.getKingMoves();
        } else if (type == ChessPiece.PieceType.ROOK) {
            RookMoves rook = new RookMoves(pieceColor, type, board, position);
            return rook.getRookMoves();
        } else if (type == ChessPiece.PieceType.QUEEN) {
            QueenMoves queen = new QueenMoves(pieceColor, type, board, position);
            return queen.getQueenMoves();
        } else if (type == ChessPiece.PieceType.PAWN) {
            PawnMoves pawn = new PawnMoves(pieceColor, type, board, position);
            return pawn.getPawnMoves();
        } else {
            return new ArrayList<>();
        }
    }

    public boolean inBounds(ChessPosition position) {
        return (position.getRow() <= 8 && position.getColumn() <= 8 && position.getRow() >= 1 && position.getColumn() >= 1);
    }

    public boolean isOccupied(ChessPosition position) {
        ChessPiece nextPiece = board.getPiece(position);
        return nextPiece != null;
    }

    public boolean isSameColor(ChessPosition position, ChessGame.TeamColor color) {
        ChessPiece nextPiece = board.getPiece(position);

        if (nextPiece != null) {
            return nextPiece.getTeamColor() == color;
        } else {
            return false;
        }
    }

    public Collection<ChessMove> checkLine(int row, int col, int rowSign, int colSign, Collection<ChessMove> moves) {
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

        return moves;
    }

    public Collection<ChessMove> checkPosition(int row, int col, Collection<ChessMove> moves) {
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

        return moves;
    }
}
