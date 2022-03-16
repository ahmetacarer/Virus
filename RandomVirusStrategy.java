import java.util.ArrayList;
import java.util.Random;
public class RandomVirusStrategy implements VirusStrategy{
    static Random rand = new Random(2);
    @Override
    public VirusMove doMove(Player currentPlayer, Player[][] playingField, ArrayList<VirusMove> moveList, int fieldSize) {

        int chosenMove = rand.nextInt(moveList.size());
        return moveList.get(chosenMove);
    }

    @Override
    public String getName() {
        return "Random";
    }
}
