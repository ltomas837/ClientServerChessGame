package pieces;

/**
 * This class is a factory used to give an exact clone of the piece.
 */
public class PieceFactoryPrototype {

    /**
     * Gives a clone of a piece.
     *
     * @param piece The piece to clone
     * @return The cloned piece
     */
    public static Piece getPiece(Piece piece){

        if ( piece == null )
            return null;

        switch ( piece.str() ) {
            case "pawn":
                Pawn pawn =  new Pawn(piece.getColor());
                if ( ((Pawn) piece).isInPassing() )
                    pawn.setInPassing(true);
                if ( ((Pawn) piece).hasMoved() )
                    pawn.setHasMoved(true);
                return pawn;
            case "bishop":
                return new Bishop(piece.getColor());
            case "knight":
                return new Knight(piece.getColor());
            case "rook":
                Rook rook =  new Rook(piece.getColor());
                if ( !((Rook) piece).canCastle() )
                    rook.setCanCastle(false);
                return rook;
            case "queen":
                return new Queen(piece.getColor());
            case "king":
                King king =  new King(piece.getColor());
                if ( !((King) piece).canCastle() )
                    king.setCanCastle(false);
                return king;
            default:
                return null;
        }
    }

}
