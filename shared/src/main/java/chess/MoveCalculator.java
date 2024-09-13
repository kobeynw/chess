package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MoveCalculator {
    public final ChessPiece piece;
    public final ChessBoard board;
    public final ChessPosition position;

    public MoveCalculator(ChessPiece piece, ChessBoard board, ChessPosition position) {
        this.piece = piece;
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> getMoves() {
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            BishopMoves bishopMoves = new BishopMoves(piece, board, position);
            return bishopMoves.getBishopMoves();
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            KingMoves kingMoves = new KingMoves(piece, board, position);
            return kingMoves.getKingMoves();
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            KnightMoves knightMoves = new KnightMoves(piece, board, position);
            return knightMoves.getKnightMoves();
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            PawnMoves pawnMoves = new PawnMoves(piece, board, position);
            return pawnMoves.getPawnMoves();
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            QueenMoves queenMoves = new QueenMoves(piece, board, position);
            return queenMoves.getQueenMoves();
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            RookMoves rookMoves = new RookMoves(piece, board, position);
            return rookMoves.getRookMoves();
        } else {
            return new ArrayList<>();
        }
    }

    public boolean moveBlocked(ChessPosition position) {
        if (position.getColumn() > 8 || position.getRow() > 8) {
            return true;
        } else if (position.getColumn() < 1 || position.getRow() < 1) {
            return true;
        }

        ChessPiece nextPiece = board.getPiece(position);
        if (nextPiece == null) {
            return false;
        } else {
            return piece.getTeamColor() == nextPiece.getTeamColor();
        }
    }

    public boolean canTakePiece(ChessPosition position) {
        ChessPiece nextPiece = board.getPiece(position);
        if (nextPiece == null) {
            return false;
        }

        return piece.getTeamColor() != nextPiece.getTeamColor();
    }
}
