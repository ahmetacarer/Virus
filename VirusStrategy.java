import java.util.ArrayList;

public interface VirusStrategy {
    VirusMove doMove(Player currentPlayer, Player[][] playingField, ArrayList<VirusMove> moveList, int FieldSize);
    String getName();
}
