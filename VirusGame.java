import java.awt.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class VirusGame {

    private final Player[][] playingField;
    private final int fieldSize;
    private Player currentPlayer;

    private enum Msg {
        FIELD,
        CANTMOVE,
        WINNER,
        MOVE
    }

    private void printMsg(Msg msg){
        printMsg(msg, null);
    }
    private void printMsg(Msg msg, Player player) {
        printMsg(msg, player, null,null);
    }
    private void printMsg(Msg msg, Player player, VirusMove move) {
        printMsg(msg, player, move, null);
    }
    private void printMsg(Msg msg, Player player, VirusStrategy strategy) {
        printMsg(msg, player, null, strategy);
    }
    private void printMsg(Msg msg, Player player, VirusMove move, VirusStrategy strategy){
        PrintStream to;
        String playerText = "Red";
        if (player == Player.GREEN){
            playerText = "Green";
        }
        switch(msg){
            case FIELD:
                to = System.err;
                int countRed = 0;
                int countGreen = 0;
                for (int i = 0; i < fieldSize; i++){
                    to.print(i + "  ");
                    for (int j = 0; j < fieldSize; j++) {
                        char symbol;
                        switch (playingField[i][j]) {
                            case EMPTY: symbol = '.'; break;
                            case RED: symbol = 'R'; countRed++; break;
                            case GREEN: symbol = 'G'; countGreen++; break;
                            default: symbol = 'x'; break;
                        }
                        to.print(symbol + " ");
                    }
                    to.println();
                }
                to.print("   ");
                for (int a = 0; a < fieldSize; a++){
                    to.print(a+" ");
                }
                to.println();
                to.println("Red: " + countRed);
                to.println("Green: " + countGreen);
                to.println();
                break;
            case WINNER:
                to = System.err;
                to.printf("%s player has won the game using the %s strategy!\n",playerText,strategy.getName());
                break;
            case CANTMOVE:
                to = System.err;
                to.println(playerText + " can't move.");
                break;
            case MOVE:
                to = System.err;
                to.printf("%s moves (%d,%d) to (%d,%d).\n", playerText,(int)move.from.getX(), (int)move.from.getY(), (int)move.to.getX(), (int)move.to.getY());
                break;
            default:
                to = System.err;
                to.println("--");
                break;
        }
    }

    private void initField(){
        for (int i = 0; i < fieldSize; i++){
            for (int j = 0; j < fieldSize; j++){
                playingField[i][j] = Player.EMPTY;
                if (i <= 1){
                    if (j >= fieldSize -2){
                        playingField[i][j] = Player.GREEN;
                    }
                }
                else if (i >= fieldSize - 2){
                    if (j <= 1){
                        playingField[i][j] = Player.RED;
                    }
                }
            }
        }
    }

    private Player getWinner(){
        int countRed = 0;
        int countGreen = 0;
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (playingField[i][j] == Player.RED){
                    countRed++;
                }
                else if (playingField[i][j] == Player.GREEN){
                    countGreen++;
                }
            }
        }
        return countRed > countGreen?Player.RED:Player.GREEN;
    }

    private boolean checkDone(){
        int countRed = 0;
        int countGreen = 0;
        boolean emptyFound = false;
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (playingField[i][j] == Player.EMPTY){
                    emptyFound = true;
                }
                else if (playingField[i][j] == Player.RED){
                    countRed++;
                }
                else{
                    countGreen++;
                }
            }
        }

        return countRed == 0 || countGreen == 0 || !emptyFound;

    }

    private void switchPlayer(){
        if (currentPlayer == Player.RED){
            currentPlayer = Player.GREEN;
        }
        else{
            currentPlayer = Player.RED;
        }
    }

    private boolean canMove(ArrayList<VirusMove> moveList){
        int numberOfMoves = 0;
        Point from;
        Point to;
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (playingField[i][j] == currentPlayer){
                    from = new Point(i, j);
                    for (int x = i - 2; x <= i + 2;x++){
                        for (int y = j - 2; y <= j + 2; y++){
                            if (y >= 0 && x >= 0 && y < fieldSize && x < fieldSize && playingField[x][y] == Player.EMPTY){
                                to = new Point(x, y);
                                moveList.add(new VirusMove(from,to));
                                numberOfMoves++;
                            }
                        }
                    }
                }
            }
        }
        return numberOfMoves > 0;
    }

    private void doMove(VirusMove move){
        int fromX = (int) move.from.getX();
        int fromY = (int) move.from.getY();
        int toX = (int) move.to.getX();
        int toY = (int) move.to.getY();
        if (Math.abs(fromX-toX) > 1 || Math.abs(fromY - toY) > 1){
            playingField[fromX][fromY] = Player.EMPTY;
        }
        playingField[toX][toY] = currentPlayer;
        //take enemy viruses
        for (int x = toX - 1; x <= toX + 1; x++){
            for (int y = toY - 1; y <= toY + 1; y++){
                if (y >= 0 && x >= 0 && y < fieldSize && x < fieldSize && playingField[x][y] != Player.EMPTY){
                    playingField[x][y] = currentPlayer;
                }
            }
        }
    }

    private Player game(HashMap<Player, VirusStrategy> strategy){
        initField();
        currentPlayer = Player.GREEN;
        printMsg(Msg.FIELD);
        while (!checkDone()){
            ArrayList<VirusMove> moveList = new ArrayList<>();
            if (canMove(moveList)) {
                VirusMove move = strategy.get(currentPlayer).doMove(currentPlayer, playingField, moveList, fieldSize);
                doMove(move);
                printMsg(Msg.MOVE,currentPlayer,move);
                printMsg(Msg.FIELD);
            }
            else{
                printMsg(Msg.CANTMOVE,currentPlayer);
            }
            switchPlayer();
        }
        Player winner = getWinner();
        printMsg(Msg.WINNER, winner, strategy.get(winner));
        return winner;
    }

    public void virusSingleGame(VirusStrategy greenPlayer, VirusStrategy redPlayer){
        HashMap<Player,VirusStrategy> strategy= new HashMap<>();
        strategy.put(Player.RED, redPlayer);
        strategy.put(Player.GREEN, greenPlayer);
        game(strategy);
    }

    public void virusTournament(ArrayList<VirusStrategy> algorithms){
        //init
        Player winner = null;
        HashMap<Player,VirusStrategy> strategy= new HashMap<>();
        int[][] scoretable = new int[algorithms.size()][algorithms.size()];
        for (int i = 0; i < algorithms.size(); i++){
            for (int j = 0; j < algorithms.size(); j++) {
                scoretable[i][j] = 0;
            }
        }

        //play all matches
        for (int i = 0; i < algorithms.size(); i++){
            for (int j = i + 1; j < algorithms.size(); j++){
                //first match
                strategy.put(Player.RED, algorithms.get(i));
                strategy.put(Player.GREEN, algorithms.get(j));
                winner = game(strategy);
                if (winner == Player.RED){
                    scoretable[i][j] = 1;
                }
                else{
                    scoretable[i][j] = -1;
                }

                //return match
                strategy.put(Player.RED, algorithms.get(j));
                strategy.put(Player.GREEN, algorithms.get(i));
                winner = game(strategy);
                if (winner == Player.RED){
                    scoretable[j][i] = 1;
                }
                else{
                    scoretable[j][i] = -1;
                }
            }
        }

        //calculate scores
        int[] scores = new int[algorithms.size()];
        for (int a = 0; a < algorithms.size(); a++) {
            scores[a]=0;
        }
        for (int i = 0; i < algorithms.size(); i++) {
            for (int j = 0; j < algorithms.size(); j++) {
                if (scoretable[i][j]==1){
                    scores[i]++;
                }
                else if (scoretable[i][j]==-1){
                    scores[j]++;
                }
            }
        }
        

    }

    public VirusGame(int fieldSize){
        this.fieldSize = fieldSize;
        playingField = new Player[fieldSize][fieldSize];
    }
}
