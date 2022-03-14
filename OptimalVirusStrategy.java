import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class OptimalVirusStrategy implements VirusStrategy {


    @Override
    public VirusMove doMove(Player currentPlayer, Player[][] playingField, ArrayList<VirusMove> moveList, int FieldSize) {
        return calculateBestMove(moveList,playingField, FieldSize);
    }

    @Override
    public String getName() {
        return "Optimal Algorithm";
    }

    public VirusMove calculateBestMove(ArrayList<VirusMove> moveList, Player[][] playingField, int fieldSize) {
        VirusMove bestMove = moveList.get(0);
        int mostWins = 0;
        for (VirusMove virusMove :
             moveList) {
            int wins = getWins(100, virusMove, playingField, fieldSize);
            if (wins > mostWins)
            {
                bestMove = virusMove;
                mostWins = wins;
            }
        }
        return bestMove;
    }

//    public VirusMove calculateBestMove(ArrayList<VirusMove> moveList, Player[][] playingField, int fieldSize) {
//        // ConcurrentHashMap<VirusMove, Integer> virusMoveHashMap = new ConcurrentHashMap<>();
//        HashMap<VirusMove, Integer> virusMoveHashMap = new HashMap<>();
//        //  todo: make it multi threaded :D
//        //moveList.forEach(virusMove -> virusMoveHashMap.put(virusMove, CompletableFuture.supplyAsync(() -> getWins(100, virusMove, playingField, fieldSize)).join()));
//        moveList.forEach(virusMove -> virusMoveHashMap.put(virusMove, getWins(100, virusMove, playingField, fieldSize)));
//        return Collections.max(virusMoveHashMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
//    }

    // simulate numberOfGames and wins
    public int getWins(int numberOfGames, VirusMove  virusMove, Player[][] playingField, int fieldSize) {
        int wins = 0;
        GameSimulation simulation = new GameSimulation(fieldSize);
        Player player = playingField[virusMove.from.x][virusMove.from.y] == Player.GREEN ? Player.GREEN : Player.RED;
        Player enemy = player == Player.GREEN ? Player.RED : Player.GREEN;
        HashMap<Player,VirusStrategy> strategy= new HashMap<>();
        strategy.put(player, new RandomVirusStrategy());
        strategy.put(enemy, new RandomVirusStrategy());
        for (int i = 0; i < numberOfGames; i++) {
            simulation.setField(playingField);
            simulation.doMove(virusMove);
            Player winner = simulation.game(strategy);
            if (player == winner) wins++;
        }
        return wins;
    }
}
