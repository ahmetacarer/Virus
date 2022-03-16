import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OptimalVirusStrategy implements VirusStrategy {

    @Override
    public VirusMove doMove(Player currentPlayer, Player[][] playingField, ArrayList<VirusMove> moveList, int FieldSize) {
        long begin = System.nanoTime();
        VirusMove move = calculateBestMove(moveList,playingField, FieldSize);
        long end = System.nanoTime();
        double diffInSeconds = (end - begin) / 1_000_000_000.0;
        System.out.println(diffInSeconds);
        return move;
    }

    @Override
    public String getName() {
        return "Optimal concurrent";
    }

    public VirusMove calculateBestMove(ArrayList<VirusMove> moveList, Player[][] playingField, int fieldSize) {
        // vanwege multithreading gebruik ik de gespecialiseerde concurrencyhashmap
        ConcurrentHashMap<VirusMove, Integer> virusMoveHashMap = new ConcurrentHashMap<>();
        moveList
                // .parallelStream()
                .forEach(virusMove ->
                virusMoveHashMap.put(virusMove, getWins(500, virusMove, playingField, fieldSize)));
        return Collections.max(virusMoveHashMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    public int getWins(int numberOfGames, VirusMove  virusMove, Player[][] playingField, int fieldSize) {
        AtomicInteger wins = new AtomicInteger();
        Player player = playingField[virusMove.from.x][virusMove.from.y] == Player.GREEN ? Player.GREEN : Player.RED;
        Player enemy = player == Player.GREEN ? Player.RED : Player.GREEN;
        HashMap<Player,VirusStrategy> strategy = new HashMap<>();
        strategy.put(player, new RandomVirusStrategy());
        strategy.put(enemy, new RandomVirusStrategy());
        List<GameSimulation> values = new ArrayList<GameSimulation>();
        IntStream.range(0, numberOfGames)
                .boxed()
                .collect(Collectors.toList())
                .parallelStream()
                .forEach(num -> {
                    GameSimulation simulation = new GameSimulation(fieldSize);
                    simulation.setField(playingField);
                    simulation.doMove(virusMove);
                    Player winner = simulation.getTurn(strategy);
                    if (player == winner) wins.getAndIncrement();
                });
        return wins.get();
    }

}
