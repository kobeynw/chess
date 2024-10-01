package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();
    private ChessGame.TeamColor teamTurn = TeamColor.WHITE;

    public ChessGame() {
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private Collection<ChessMove> getMovesYieldingCheck(Collection<ChessMove> moves, ChessPiece piece) {
        TeamColor teamColor = piece.getTeamColor();
        Collection<ChessMove> movesToRemove = new ArrayList<>();

        for (ChessMove move : moves) {
            ChessPiece pieceToReplace = this.board.getPiece(move.getEndPosition());
            this.board.addPiece(move.getStartPosition(), null);
            this.board.addPiece(move.getEndPosition(), piece);

            if (isInCheck(teamColor)) {
                movesToRemove.add(move);
            }

            this.board.addPiece(move.getStartPosition(), piece);
            this.board.addPiece(move.getEndPosition(), pieceToReplace);

        }

        return movesToRemove;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = this.board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> moves;
        moves = piece.pieceMoves(this.board, startPosition);
        Collection<ChessMove> movesToRemove = getMovesYieldingCheck(moves, piece);

        for (ChessMove moveToRemove : movesToRemove) {
            moves.remove(moveToRemove);
        }

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

        ChessPiece piece = this.board.getPiece(startPosition);
        Collection<ChessMove> validMoves = validMoves(startPosition);

        if (piece != null
        && piece.getTeamColor() == this.teamTurn
        && validMoves != null
        && !validMoves.isEmpty()
        && validMoves.contains(move)) {
            this.board.addPiece(startPosition, null);

            if (promotionPiece != null) {
                piece = new ChessPiece(piece.getTeamColor(), promotionPiece);
            }

            this.teamTurn = (this.teamTurn == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK;
            this.board.addPiece(endPosition, piece);
        } else {
            throw new InvalidMoveException();
        }
    }

    private ChessPosition getKingPosition(TeamColor color) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = this.board.getPiece(position);

                if (piece != null) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color) {
                        return position;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        if (kingPosition == null) {
            return false;
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = this.board.getPiece(position);

                if (piece != null) {
                    if (piece.getTeamColor() != teamColor) {
                        Collection<ChessMove> possibleMoves = piece.pieceMoves(this.board, position);

                        for (ChessMove possibleMove : possibleMoves) {
                            if (possibleMove.getEndPosition().equals(kingPosition)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean testPossibleEscape(ChessMove possibleMove, ChessPiece piece, TeamColor teamColor) {
        boolean possibleCheck = false;
        ChessPiece pieceToReplace = this.board.getPiece(possibleMove.getEndPosition());
        this.board.addPiece(possibleMove.getStartPosition(), null);
        this.board.addPiece(possibleMove.getEndPosition(), piece);

        try {
            this.makeMove(possibleMove);
        } catch (InvalidMoveException e) {
            // Not necessary to catch exception
        }

        if (!isInCheck(teamColor)) {
            possibleCheck = true;
        }

        this.board.addPiece(possibleMove.getStartPosition(), piece);
        this.board.addPiece(possibleMove.getEndPosition(), pieceToReplace);

        return possibleCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        if (!isInCheck(teamColor) || kingPosition == null) {
            return false;
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = this.board.getPiece(position);
                if (piece == null) {
                    continue;
                }

                if (piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(this.board, position);

                    for (ChessMove possibleMove : possibleMoves) {
                        if (testPossibleEscape(possibleMove, piece, teamColor)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        if (isInCheck(teamColor) || kingPosition == null) {
            return false;
        }

        boolean isInStalemate = false;
        ChessPiece kingPiece = this.board.getPiece(kingPosition);
        Collection<ChessMove> moves = kingPiece.pieceMoves(this.board, kingPosition);

        for (ChessMove possibleMove : moves) {
            if (testPossibleEscape(possibleMove, kingPiece, teamColor)) {
                return false;
            } else {
                isInStalemate = true;
            }
        }

        return isInStalemate;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
