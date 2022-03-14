import java.util.ArrayList;
import java.util.Random;
public class RandomVirusStrategy implements VirusStrategy{

    @Override
    public VirusMove doMove(Player currentPlayer, Player[][] playingField, ArrayList<VirusMove> moveList, int fieldSize) {
        Random rand = new Random();
        int chosenMove = rand.nextInt(moveList.size());
        return moveList.get(chosenMove);
    }

    @Override
    public String getName() {
        return "Random";
    }
}
