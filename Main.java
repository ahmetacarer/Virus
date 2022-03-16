import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        long begin = System.nanoTime();

        VirusGame virusGame = new VirusGame(7);
        virusGame.virusSingleGame(new OptimalVirusStrategy(), new RandomVirusStrategy());

        long end = System.nanoTime();
        double diffInSeconds = (end - begin) / 1_000_000_000.0;
        System.out.println("Total seconds: " + diffInSeconds);

//        GameSimulation game = new GameSimulation(7);
//        HashMap<Player, VirusStrategy> hm = new HashMap<>();
//        hm.put(Player.GREEN, new OptimalVirusStrategy());
//        hm.put(Player.RED, new RandomVirusStrategy());
//        int wins = 0;
//        for (int i = 0; i < 5; i++) {
//            Player winner = game.game(hm);
//            if (winner == Player.GREEN)
//            {
//                wins++;
//            }
//            System.out.printf("%d wins out of %d games %n", wins, (i + 1));
//        }
    }
}
