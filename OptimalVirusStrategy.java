import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.*;

public class OptimalVirusStrategy implements VirusStrategy {


    @Override
    public VirusMove doMove(Player currentPlayer, Player[][] playingField, ArrayList<VirusMove> moveList, int FieldSize) {
        return calculateBestMove2(moveList,playingField, FieldSize);
    }

    @Override
    public String getName() {
        return "Optimal Algorithm";
    }

//    public VirusMove calculateBestMove(ArrayList<VirusMove> moveList, Player[][] playingField, int fieldSize) {
//        VirusMove bestMove = moveList.get(0);
//        int mostWins = 0;
//        for (VirusMove virusMove :
//             moveList) {
//            int wins = getWins(50, virusMove, playingField, fieldSize);
//            if (wins > mostWins)
//            {
//                bestMove = virusMove;
//                mostWins = wins;
//            }
//        }
//        return bestMove;
//    }

    public VirusMove calculateBestMove2(ArrayList<VirusMove> moveList, Player[][] playingField, int fieldSize) {
        // vanwege multithreading gebruik ik de gespecialiseerde concurrencyhashmap
        // ? is een hashmap wel nodig?
        // todo: wellicht de hashmap vervangen
        ConcurrentHashMap<VirusMove, Integer> virusMoveHashMap = new ConcurrentHashMap<>();
        ArrayList<Thread> threads = new ArrayList<>();
        for (VirusMove virusMove:
             moveList) {
            // maakt een nieuwe thread aan die de hashmap moet vullen met het aantal wins.
            Thread thread = new Thread(){
                public void run(){
                    virusMoveHashMap.put(virusMove, getWins(200, virusMove, playingField, fieldSize));
                }};
            thread.start();
            threads.add(thread);
        }
        // Er moet gewacht worden op alle threads voordat er naar de meeste wins gekeken kan worden
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return Collections.max(virusMoveHashMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    // simulate numberOfGames and wins
    public int getWins(int numberOfGames, VirusMove  virusMove, Player[][] playingField, int fieldSize) {
        // todo: Hier kan ook multithreading worden toegepast.
        int wins = 0;
        GameSimulation simulation = new GameSimulation(fieldSize);
        Player player = playingField[virusMove.from.x][virusMove.from.y] == Player.GREEN ? Player.GREEN : Player.RED;
        Player enemy = player == Player.GREEN ? Player.RED : Player.GREEN;
        HashMap<Player,VirusStrategy> strategy = new HashMap<>();
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
