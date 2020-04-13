package student_player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurBonus;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDestroy;
import Saboteur.cardClasses.SaboteurDrop;
import Saboteur.cardClasses.SaboteurMalus;
import Saboteur.cardClasses.SaboteurMap;
import Saboteur.cardClasses.SaboteurTile;
import boardgame.Move;

public class MyTools {

	private static SaboteurTile[][] board;
	private static int[][] intboard;
	private static int MalusNum = 2;
	private static final int[] posMiddle = {12,5};
	private static final int[] posRight = {12,7};
	private static final int[] posLeft = {12,3};
	private static final int[] startPos = {5,5};
	private static ArrayList<SaboteurMove> legalMoves;
	private static int mapPlayed = 0;

	public static double getSomething() {
		return Math.random();
	}

	public static void updateBoard(SaboteurBoardState sbs) {
		board = sbs.getHiddenBoard();
		intboard = sbs.getHiddenIntBoard();
		legalMoves = sbs.getAllLegalMoves();
		if(sbs.getTurnNumber() == 2 || sbs.getTurnNumber() == 1) {
			mapPlayed = 0;
			MalusNum = 2;
		}
	}

	/**
	 * This method can be used to determine how much tiles horizontally and vertically are needed to reach the target
	 * @param target
	 * @param current
	 * @return
	 */
	public static int[] getOffset(int[] target, int i, int j, String name) {
		String idx = name.split(":")[1];
		int[] offsets = new int[3];
		offsets[0] = target[0]-i;
		offsets[1] = target[1]-j;
		int[][] path = SaboteurTile.initializePath(idx);
		if(offsets[0]>0 && path[1][0] == 1 && path[1][1]==1) {
			offsets[2] = 0;
			offsets[0]--;
			offsets[1]--;
			return offsets;
		}
		if(offsets[1]>0 && path[1][1] ==1 && path[2][1] == 1) {
			offsets[2] = 0;
			offsets[0]--;
			offsets[1]--;
			return offsets;
		}
		if(offsets[1]<0 && path[1][1] == 1 && path[0][1] == 1) {
			offsets[2] = 0;
			offsets[0]--;
			offsets[1]--;
			return offsets;
		}
		offsets[2] = 1; //the third offset is to see if we need to use a destroy card or not
		return offsets;
	}

	/**
	 * this method can be used to check if there is a path between the start tile and a target tile. Inspired from
	 * SaboteurBoardState method.
	 * @param current
	 * @param target
	 * @return
	 */
	public static boolean pathExists(int i, int j) {
		if(board[i][j] == null) {
			return false;
		}
		if(i == 5 && j == 5) {
			return true;
		}
		ArrayList<int[]> originTargets = new ArrayList<>();
		originTargets.add(new int[]{5,5}); 
		int[] target = {i, j};

		if (cardPath(originTargets, target, true)) { //checks that there is a cardPath
			System.out.println("card path found"); //todo remove
			//next: checks that there is a path of ones.
			ArrayList<int[]> originTargets2 = new ArrayList<>();
			//the starting points
			originTargets2.add(new int[]{5*3+1, 5*3+1});
			originTargets2.add(new int[]{5*3+1, 5*3+2});
			originTargets2.add(new int[]{5*3+1, 5*3});
			originTargets2.add(new int[]{5*3, 5*3+1});
			originTargets2.add(new int[]{5*3+2, 5*3+1});
			//get the target position in 0-1 coordinate
			int[] targetPos2 = {target[0]*3+1, target[1]*3+1};
			if (cardPath(originTargets2, targetPos2, false)) {
				return true;
			}
			else{
				return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * see if an element is contained in an arrayList. Inspired from given code.
	 * @param a
	 * @param o
	 * @return
	 */
	public static boolean containsIntArray(ArrayList<int[]> a,int[] o){ //the .equals used in Arraylist.contains is not working between arrays..
		if (o == null) {
			for (int i = 0; i < a.size(); i++) {
				if (a.get(i) == null)
					return true;
			}
		} else {
			for (int i = 0; i < a.size(); i++) {
				if (Arrays.equals(o, a.get(i)))
					return true;
			}
		}
		return false;
	}


	/**
	 * card path method from SaboteurBoardState
	 * @param originTargets
	 * @param targetPos
	 * @param usingCard
	 * @return
	 */
	public static Boolean cardPath(ArrayList<int[]> originTargets,int[] targetPos,Boolean usingCard){
		// the search algorithm, usingCard indicate weither we search a path of cards (true) or a path of ones (aka tunnel)(false).
		ArrayList<int[]> queue = new ArrayList<>(); //will store the current neighboring tile. Composed of position (int[]).
		ArrayList<int[]> visited = new ArrayList<int[]>(); //will store the visited tile with an Hash table where the key is the position the board.
		visited.add(targetPos);
		if(usingCard) addUnvisitedNeighborToQueue(targetPos,queue,visited,14,usingCard);
		else addUnvisitedNeighborToQueue(targetPos,queue,visited,14*3,usingCard);
		while(queue.size()>0){
			int[] visitingPos = queue.remove(0);
			if(containsIntArray(originTargets,visitingPos)){
				return true;
			}
			visited.add(visitingPos);
			if(usingCard) addUnvisitedNeighborToQueue(visitingPos,queue,visited,14,usingCard);
			else addUnvisitedNeighborToQueue(visitingPos,queue,visited,14*3,usingCard);
			System.out.println(queue.size());
		}
		return false;
	}
	/**
	 * method from SaboteurBoardState.
	 * @param pos
	 * @param queue
	 * @param visited
	 * @param maxSize
	 * @param usingCard
	 */
	public static void addUnvisitedNeighborToQueue(int[] pos,ArrayList<int[]> queue, ArrayList<int[]> visited,int maxSize,boolean usingCard){
		int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0}};
		int i = pos[0];
		int j = pos[1];
		for (int m = 0; m < 4; m++) {
			if (0 <= i+moves[m][0] && i+moves[m][0] < maxSize && 0 <= j+moves[m][1] && j+moves[m][1] < maxSize) { //if the hypothetical neighbor is still inside the board
				int[] neighborPos = new int[]{i+moves[m][0],j+moves[m][1]};
				if(!containsIntArray(visited,neighborPos)){
					if(usingCard && board[neighborPos[0]][neighborPos[1]]!=null) queue.add(neighborPos);
					else if(!usingCard && intboard[neighborPos[0]][neighborPos[1]]==1) queue.add(neighborPos);
				}
			}
		}
	}

	public static int  getIndex(SaboteurBoardState sbs, SaboteurCard tile) {
		int i = 0;
		ArrayList<SaboteurCard> s = getCardsInHand(sbs);
		for(SaboteurCard e :s) {
			if(e.getName().equals(tile.getName())) {
				return i; 
			}
			i++;
		}
		return -1;
	}

	public static SaboteurCard dropStrategy(SaboteurBoardState sbs) {
		ArrayList<SaboteurCard> hand = sbs.getCurrentPlayerCards();
		boolean map = false;
		boolean bonus = false;

		if(checkRevealed(sbs)>=2) {
			map = true;
		}

		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(), "Bonus") > 2) {
			return getIndexCardinHand(hand, "Bonus");
		}

		if(MalusNum-CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Malus") == 0) {
			bonus = true;
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Map")!=0 && map) {
			return getIndexCardinHand(hand,"Map");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Bonus")!=0 && bonus) {
			return getIndexCardinHand(hand,"Bonus");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Tile:4")!= 0) {
			return getIndexCardinHand(hand,"Tile:4");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Tile:12")!= 0) {
			return getIndexCardinHand(hand,"Tile:12");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Tile:13")!= 0) {
			return getIndexCardinHand(hand,"Tile:13");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Tile:14")!= 0) {
			return getIndexCardinHand(hand,"Tile:14");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Tile:15")!= 0) {
			return getIndexCardinHand(hand,"Tile:15");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Tile:2")!= 0) {
			return getIndexCardinHand(hand,"Tile:2");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Tile:3")!= 0) {
			return getIndexCardinHand(hand,"Tile:3");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Tile:11")!= 0) {
			return getIndexCardinHand(hand,"Tile:11");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Destroy")!= 0) {
			return getIndexCardinHand(hand,"Destroy");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Tile:7")!= 0) {
			return getIndexCardinHand(hand,"Tile:7");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Tile:5")!= 0) {
			return getIndexCardinHand(hand,"Tile:5");
		}
		else {
			Random rand = new Random();
			//generate an int between 0 and the current number of cards in hand
			int random = rand.nextInt(getNumberOfCardsInHand(sbs));
			return hand.get(random);
		}
	}

	public static int getNumberOfCardsInHand(SaboteurBoardState sbs) {
		return sbs.getCurrentPlayerCards().size();
	}

	public static SaboteurCard getIndexCardinHand(ArrayList<SaboteurCard> s, String tile) {
		for(SaboteurCard e :s) {
			if(e.getName().equals(tile)) {
				return e; 
			}
		}
		return null;
	}



	public static int checkRevealed(SaboteurBoardState sbs) {

		int num = 0;
		if(board[12][5].getName().equals("Tile:hidden1") ||board[12][5].getName().equals("Tile:hidden2")|| board[12][5].getName().equals("Tile:nugget")) {
			num++;
		}
		if(board[12][3].getName().equals("Tile:hidden1") ||board[12][3].getName().equals("Tile:hidden2")|| board[12][3].getName().equals("Tile:nugget")) {
			num++;
		}
		if(board[12][7].getName().equals("Tile:hidden1") ||board[12][7].getName().equals("Tile:hidden2")|| board[12][7].getName().equals("Tile:nugget")) {
			num++;
		}
		return num;
	}

	public static int CheckNumOfCardInHand(ArrayList<SaboteurCard>ss, String tile) {
		int num = 0;
		for(SaboteurCard sc: ss) {
			if(sc.getName().equals(tile)) {
				num++;
			}
		}
		return num;
	}


	public static int countNumOfSpecialCards(SaboteurBoardState boardState) {
		int i = 0;
		ArrayList<SaboteurCard> cards = getCardsInHand(boardState);
		i = i + CheckNumOfCardInHand(cards, "Destroy");
		i = i + CheckNumOfCardInHand(cards, "Bonus");
		i = i + CheckNumOfCardInHand(cards, "Malus");
		i = i + CheckNumOfCardInHand(cards, "Map");
		return i;
	}



	/**
	 * method getCardsInHand()
	 * @return ArrayList<Card> in the agent's hand
	 */
	public static ArrayList<SaboteurCard> getCardsInHand(SaboteurBoardState boardState) {
		//getCurrentPlayerCards() works in a way such that it will return only the hand 
		//of the player currently playing his turn, since we will be calling it only when
		//our agent plays, we will always get our agent's hand
		ArrayList<SaboteurCard> cards = boardState.getCurrentPlayerCards();
		return cards;
	}

	/**
	 * method getCardFromHand()
	 * @param SaboteurCard card
	 * @return SaboteurCard card
	 */
	public static SaboteurCard getCardFromHand(SaboteurBoardState boardState, SaboteurCard card) {
		ArrayList<SaboteurCard> cards = getCardsInHand(boardState);
		for(SaboteurCard saboteurCard: cards) {
			if(saboteurCard.getName().equals(card.getName())) {
				return saboteurCard;
			}
		}
		return null;
	} 
	/**
	 * same with string
	 */
	public static SaboteurCard getCardFromHand(SaboteurBoardState boardState, String card) {
		ArrayList<SaboteurCard> cards = getCardsInHand(boardState);
		for(SaboteurCard saboteurCard: cards) {
			if(saboteurCard.getName().equals(card)) {
				return saboteurCard;
			}
		}
		return null;
	} 
	/**
	 * method checkCardInHand()
	 * used to check if a specific card is in our hand and can therefore be played
	 * @param SaboteurCard card 
	 * @return boolean: true if Card in agent's hand/ false otherwise
	 */
	public static boolean checkCardInHand(ArrayList<SaboteurCard> cards, SaboteurCard card) {

		for(SaboteurCard saboteurCard : cards) {
			if(saboteurCard.getName().equals(card.getName())) {
				return true;
			}
		}
		return false;
	}
	/**
	 * same method but with string
	 * @param cards
	 * @param card
	 * @return
	 */
	public static boolean checkCardInHand(ArrayList<SaboteurCard> cards, String card) {

		for(SaboteurCard saboteurCard : cards) {
			if(saboteurCard.getName().equals(card)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * method countRevealedObjectives
	 * @param boardState
	 * @return number of revealed objectives
	 */
	public static int countRevealedObjectives(SaboteurBoardState boardState) {
		SaboteurTile[][] tiles = board;
		int [][] objectivePos = SaboteurBoardState.hiddenPos;
		int discoveredObjectivesCounter = 0;
		for(int i = 0; i < 3; i++) {
			if(tiles[objectivePos[i][0]][objectivePos[i][1]].getName().equals("Tile:hidden1") || 
					tiles[objectivePos[i][0]][objectivePos[i][1]].getName().equals("Tile:hidden2") ||
					tiles[objectivePos[i][0]][objectivePos[i][1]].getName().equals("Tile:nugget")) {
				discoveredObjectivesCounter++;
			}
		}
		return discoveredObjectivesCounter;
	}

	public static int selectHiddenObjectiveToUncover(SaboteurBoardState boardState) {
		Random rand = new Random();
		int random = rand.nextInt(3); //returns a random int between 0 and 2
		SaboteurTile[][] tiles = board;
		int [][] objectivePos = SaboteurBoardState.hiddenPos;
		while(true) {
			if(tiles[objectivePos[random][0]][objectivePos[random][1]].getName().equals("Tile:8")) {
				return random;
			}
			else {
				random = (random + 1) %3;
			}
		}
	}	


	/**
	 * METHODS FOR SELECTING MOVES IN ORDER OF PRIORITY
	 * CHECK MOVE IS LEGAL BEFORE RETURNING IT
	 */

	public static Move playMalus(SaboteurBoardState boardState) {
		if(checkCardInHand(getCardsInHand(boardState), new SaboteurMalus())) {
			int playerId = boardState.getTurnPlayer();
			SaboteurMove myMove = new SaboteurMove(getCardFromHand(boardState, new SaboteurMalus()), 0, 0, playerId);
			if(boardState.isLegal(myMove)) {
				MalusNum--;
				return myMove;
			}		
		}
		return null;
	}

	public static Move playBonus(SaboteurBoardState boardState) {
		int playerId = boardState.getTurnPlayer();

		int mallusStatus = boardState.getNbMalus(playerId);
		if(checkCardInHand(getCardsInHand(boardState), new SaboteurBonus()) && mallusStatus >0) {
			SaboteurMove myMove = new SaboteurMove(getCardFromHand(boardState, new SaboteurBonus()), 0, 0, playerId);
			if(boardState.isLegal(myMove)) {
				MalusNum--;
				return myMove;
			}		
		}
		return null;
	}

	public static Move playMapCard(SaboteurBoardState boardState) {
		int playerId = boardState.getTurnPlayer();
		int discoveredObjectivesCounter = checkRevealed(boardState);

		if(discoveredObjectivesCounter > 1) {
			//means 2 or more objectives have been revealed,
			//we know where the nugget is, no need to play anymore Map cards
			return null;
		}
		//else need to choose which hidden objective to discover
		int hiddenObjectiveIndex = selectHiddenObjectiveToUncover(boardState);
		if(checkCardInHand(getCardsInHand(boardState), new SaboteurMap())){
			SaboteurMove myMove = new SaboteurMove(getCardFromHand(boardState, new SaboteurMap()), 
					SaboteurBoardState.hiddenPos[hiddenObjectiveIndex][0],
					SaboteurBoardState.hiddenPos[hiddenObjectiveIndex][1], playerId);
			if(boardState.isLegal(myMove)) {
				mapPlayed++;
				return myMove;
			}else {
				return null;
			}
		}else {
			return null;
		}
	}
	/**
	 * Initial Version of main logic for placing tile cards on the board
	 * @param boardState
	 * @return Move
	 **/
	public static Move buildPath(SaboteurBoardState boardState) {
		ArrayList<SaboteurMove> tileMoves = allTileMove();
		SaboteurMove sabMove = null;
		int min = 80;
		boolean flag = false;
		for(SaboteurMove sm : tileMoves) {
			flag = false;
			if( !checkpathN(sm.getPosPlayed()[0], sm.getPosPlayed()[1])) {
				flag = false;
			}
			else {
				flag = true;
			}

			int[] middle  = getOffset(posMiddle,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int[] right  = getOffset(posRight,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int[] left  = getOffset(posLeft,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int i = middle[0]+middle[1]+middle[2];
			int j =  right[0]+right[1]+right[2];
			int k = left[0]+left[1]+left[2];

			if(i<= min && !pathExists(12,5)) {
				if(flag){
					min = i;
					sabMove = sm;
				}

			}
			if(j<=min && !pathExists(12,7)) {
				if(flag){					
					min = j;
					sabMove = sm;
				}
			}
			if(k<=min && !pathExists(12,3)) {
				if(flag){
					min = k;
					sabMove = sm;
				}
			}
		}

		return sabMove;
	}

	/**
	 * Improved Version of buildPath
	 * @param boardState
	 * @return Move
	 */
	public static Move buildPath2(SaboteurBoardState boardState) {
		SaboteurMove sabMove = null;

		if( pathExists(12,5)) {
			SaboteurMove move = targetLink(12, 5,  boardState);
			if(move != null) {
				return move;
			}
		}
		if(pathExists(12,3)) {
			SaboteurMove move = targetLink(12, 3,  boardState);
			if(move != null) {
				return move;
			}
		}
		if(pathExists(12,7)) {
			SaboteurMove move = targetLink(12, 7,  boardState);
			if(move != null) {
				return move;
			}
		}
		sabMove = checkLastRow(boardState);
		if(sabMove != null) {
			return sabMove;
		}

		ArrayList<SaboteurMove> tileMoves = allTileMove();
		int min = 1000;
		int maxRow = 5;
		boolean flag = false;

		ArrayList<SaboteurMove> middleM = checkBest(tileMoves, "middle");
		ArrayList<SaboteurMove> rightM = checkBest(tileMoves, "right");
		ArrayList<SaboteurMove> leftM = checkBest(tileMoves, "left");

		for(SaboteurMove sm :middleM) {
			int[] middle  = getOffset(posMiddle,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int[] right  = getOffset(posRight,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int[] left  = getOffset(posLeft,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int j =  Math.abs(right[0])+Math.abs(right[1])+Math.abs(right[2]);
			int k = Math.abs(left[0])+Math.abs(left[1])+Math.abs(left[2]);	
			int i = Math.abs(middle[0])+Math.abs(middle[1])+Math.abs(middle[2]);
			int mean = i+j+k;
			if(mean< min && middle[2] != 1 ) {
				if(flag){
					if( !checkpathN(sm.getPosPlayed()[0], sm.getPosPlayed()[1])) {
						flag = false;
					}
					else {
						flag = true;
					}
					min = mean;
					sabMove = sm;
					maxRow = sm.getPosPlayed()[0];
				}

			}
			if(mean == min ) {
				if(maxRow < sm.getPosPlayed()[0]) {
					sabMove = sm;
				}
			}
		}
		for(SaboteurMove sm: rightM) {
			int[] middle  = getOffset(posMiddle,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int[] right  = getOffset(posRight,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int[] left  = getOffset(posLeft,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int j =  Math.abs(right[0])+Math.abs(right[1])+Math.abs(right[2]);
			int k = Math.abs(left[0])+Math.abs(left[1])+Math.abs(left[2]);	
			int i = Math.abs(middle[0])+Math.abs(middle[1])+Math.abs(middle[2]);
			int mean = i+j+k;
			if(mean<min && right[2] != 1) {
				if( !checkpathN(sm.getPosPlayed()[0], sm.getPosPlayed()[1])) {
					flag = false;
				}
				else {
					flag = true;
				}
				if(flag){					
					min = mean;
					sabMove = sm;
					maxRow = sm.getPosPlayed()[0];
				}
			}
			if(mean == min ) {
				if(maxRow < sm.getPosPlayed()[0]) {
					sabMove = sm;
				}
			}
		}
		for(SaboteurMove sm: leftM) {
			int[] middle  = getOffset(posMiddle,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int[] right  = getOffset(posRight,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int[] left  = getOffset(posLeft,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int j =  Math.abs(right[0])+Math.abs(right[1])+Math.abs(right[2]);
			int k = Math.abs(left[0])+Math.abs(left[1])+Math.abs(left[2]);	
			int i = Math.abs(middle[0])+Math.abs(middle[1])+Math.abs(middle[2]);
			int mean = i+j+k;
			if(mean<min  && left[2] != 1) {
				if( !checkpathN(sm.getPosPlayed()[0], sm.getPosPlayed()[1])) {
					flag = false;
				}
				else {
					flag = true;
				}
				if(flag){
					min = mean;
					sabMove = sm;
					maxRow = sm.getPosPlayed()[0];
				}

			}
			if(mean == min ) {
				if(maxRow < sm.getPosPlayed()[0]) {
					sabMove = sm;
				}
			}
		}
		if(sabMove == null || sabMove.getPosPlayed()[0] <5) {
			sabMove = Drop(boardState);
		}
		return sabMove;
	}
	/**
	 * Drop method
	 * @param boardState
	 * @return SaboteurMove for dropping a card
	 */
	public static SaboteurMove Drop(SaboteurBoardState boardState) {
		SaboteurCard cardDrop = dropStrategy(boardState);
		if(cardDrop!=null) {

			SaboteurMove move = new SaboteurMove(new SaboteurDrop(),getIndex(boardState,cardDrop),0,boardState.getTurnPlayer());
			if(boardState.isLegal(move)) {
				return move;
			}
		}
		return null;
	}

	/**
	 * Return an ArrayList of all possible Moves that use a Tile Card
	 * @return ArrayList<SaboteurMove>
	 */
	public static ArrayList<SaboteurMove> allTileMove() {
		ArrayList<SaboteurMove> tileMoves = new ArrayList<SaboteurMove>();
		for(SaboteurMove sm : legalMoves) {
			if(sm.getCardPlayed() instanceof SaboteurTile) {
				tileMoves.add(sm);
			}
			else continue;
		}
		return tileMoves;
	}
	
	/**
	 * checks if the neighbors of the given tile have a path
	 * and that the 
	 * @param i -row
	 * @param j -column
	 * @return boolean
	 */
	public static boolean checkpathN(int i,int j) {

		if(i+1 < 14 && pathExists(i+1, j) == true) {
			SaboteurTile first = board[i+1][j];
			if(!checkIsBad(first.getName())) {
				return true;
			}
		}
		if(j+1 < 14 && pathExists(i, j+1) == true) {
			SaboteurTile first = board[i][j+1];
			if(!checkIsBad(first.getName())) {
				return true;
			}
		}
		if(i-1 > 0 && pathExists(i-1, j) == true) {
			SaboteurTile first = board[i-1][j];
			if(!checkIsBad(first.getName())) {
				return true;
			}
		}
		if(j-1 > 0 && pathExists(i, j-1) == true) {
			SaboteurTile first = board[i][j-1];
			if(!checkIsBad(first.getName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * this method is used to see which cards yield the best progress amongst all the legal moves
	 * @return ArrayList<SaboteurMove>
	 */
	public static ArrayList<SaboteurMove> checkBest(ArrayList<SaboteurMove> sm, String target){
		int[] offset = new int[3];
		ArrayList<SaboteurMove> good = new ArrayList<>();
		for(SaboteurMove e : sm) {

			if(target.equals("middle")) {
				offset  = getOffset(posMiddle,e.getPosPlayed()[0],e.getPosPlayed()[1], e.getCardPlayed().getName());
			}
			else if(target.equals("right")) {

				offset  = getOffset(posRight,e.getPosPlayed()[0],e.getPosPlayed()[1], e.getCardPlayed().getName());

			}
			else if(target.equals("left")) {
				offset  = getOffset(posLeft,e.getPosPlayed()[0],e.getPosPlayed()[1], e.getCardPlayed().getName());
			}
			String name = e.getCardPlayed().getName();
			String id = name.split(":")[1];
			if(id.equals("1") || id.equals("2")||
					id.equals("2_flip")||id.equals("3")||
					id.equals("3_flip")||id.equals("4_flip")||
					id.equals("4")||id.equals("11")||
					id.equals("11_flip")||id.equals("12")||
					id.equals("12_flip")||id.equals("13")||
					id.equals("14")||id.equals("14_flip")||
					id.equals("15")) {
				continue;
			}
			int[][] path = SaboteurTile.initializePath(id);

			if(offset[0] > 0 && offset[1] == 0 && path[1][1] == 1 && path[1][0] == 1) {
				good.add(e);
				continue;
			}
			if(offset[0] < 0 && offset[1] == 0 && path[1][1] == 1 && path[1][2] == 1) {
				good.add(e);
				continue;
			}
			if(offset[1] < 0 && offset[0] == 0 && path[1][1] == 1 && path[0][1] == 1) {
				good.add(e);
				continue;
			}
			if(offset[1]>0 && offset[0] == 0 && path[1][1] == 1 && path[2][1] == 1) {
				good.add(e);
				continue;
			}
			if(offset[0] > 0 && offset[1] < 0 && path[1][1] == 1) {
				if(path[0][1] == 1 ||path[1][0] == 1 ) {
					good.add(e);
					continue;
				}
				else continue;

			}
			if(offset[0] > 0 && offset[1] > 0 && path[1][1] == 1) {
				if(path[2][1] == 1 || path[1][0] == 1 ) {
					good.add(e);
					continue;
				}
				else continue;

			}
		}
		return good;

	}
	/**
	 * method responsible for placing the correct tile card when one objective is discovered
	 * and must reach other objectives
	 * @param i -row 
	 * @param j -column
	 * @param sbs SaboteurBoardState
	 * @return SaboteurMove
	 */
	public static SaboteurMove targetLink(int i, int j, SaboteurBoardState sbs) {
		ArrayList<SaboteurCard> cards = sbs.getCurrentPlayerCards();
		if(i == 12 && j == 5) {
			if(board[12][7].getName().equals("Tile:nugget")) {

				if(checkCardInHand(cards, "Tile:8")){
					SaboteurCard card = getCardFromHand(sbs,"Tile:8");
					SaboteurMove move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
				if(checkCardInHand(cards, "Tile:9") || checkCardInHand(cards, "Tile:9_flip")){
					SaboteurCard card = getCardFromHand(sbs,"Tile:9");
					SaboteurMove move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
					else {
						String idx = card.getName().split(":")[1];
						SaboteurTile tile = new SaboteurTile(idx);
						tile = tile.getFlipped();
						move = new SaboteurMove(tile,12,6,sbs.getTurnPlayer());
						if(sbs.isLegal(move)) {
							return move;
						}
					}
				}
				if(checkCardInHand(cards, "Tile:10")) {
					SaboteurCard card = getCardFromHand(sbs,"Tile:10");
					SaboteurMove move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
			}
			if(board[12][3].getName().equals("Tile:nugget")) {

				if(checkCardInHand(cards, "Tile:8")){
					SaboteurCard card = getCardFromHand(sbs,"Tile:8");
					SaboteurMove move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
				if(checkCardInHand(cards, "Tile:9") || checkCardInHand(cards, "Tile:9_flip")){
					SaboteurCard card = getCardFromHand(sbs,"Tile:9");
					SaboteurMove move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
					else {
						String idx = card.getName().split(":")[1];
						SaboteurTile tile = new SaboteurTile(idx);
						tile = tile.getFlipped();
						move = new SaboteurMove(tile,12,4,sbs.getTurnPlayer());
						if(sbs.isLegal(move)) {
							return move;
						}
					}
				}
				if(checkCardInHand(cards, "Tile:10")) {
					SaboteurCard card = getCardFromHand(sbs,"Tile:10");
					SaboteurMove move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
			}
			if(board[12][3].getName().equals("Tile:8")) {

				if(checkCardInHand(cards, "Tile:8")){
					SaboteurCard card = getCardFromHand(sbs,"Tile:8");
					SaboteurMove move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
				if(checkCardInHand(cards, "Tile:9") || checkCardInHand(cards, "Tile:9_flip")){
					SaboteurCard card = getCardFromHand(sbs,"Tile:9");
					SaboteurMove move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
					else {
						String idx = card.getName().split(":")[1];
						SaboteurTile tile = new SaboteurTile(idx);
						tile = tile.getFlipped();
						move = new SaboteurMove(tile,12,4,sbs.getTurnPlayer());
						if(sbs.isLegal(move)) {
							return move;
						}
					}
				}
				if(checkCardInHand(cards, "Tile:10")) {
					SaboteurCard card = getCardFromHand(sbs,"Tile:10");
					SaboteurMove move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
			}
			if(board[12][7].getName().equals("Tile:8")) {

				if(checkCardInHand(cards, "Tile:8")){
					SaboteurCard card = getCardFromHand(sbs,"Tile:8");
					SaboteurMove move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
				if(checkCardInHand(cards, "Tile:9") || checkCardInHand(cards, "Tile:9_flip")){
					SaboteurCard card = getCardFromHand(sbs,"Tile:9");
					SaboteurMove move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
					else {
						String idx = card.getName().split(":")[1];
						SaboteurTile tile = new SaboteurTile(idx);
						tile = tile.getFlipped();
						move = new SaboteurMove(tile,12,6,sbs.getTurnPlayer());
						if(sbs.isLegal(move)) {
							return move;
						}
					}
				}
				if(checkCardInHand(cards, "Tile:10")) {
					SaboteurCard card = getCardFromHand(sbs,"Tile:10");
					SaboteurMove move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
			}


		}
		if(i == 12 && j == 3) {
			if(checkCardInHand(cards, "Tile:8")){
				SaboteurCard card = getCardFromHand(sbs,"Tile:8");
				SaboteurMove move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
			if(checkCardInHand(cards, "Tile:9") || checkCardInHand(cards, "Tile:9_flip")){
				SaboteurCard card = getCardFromHand(sbs,"Tile:9");
				SaboteurMove move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
				else {
					String idx = card.getName().split(":")[1];
					SaboteurTile tile = new SaboteurTile(idx);
					tile = tile.getFlipped();
					move = new SaboteurMove(tile,12,4,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
			}
			if(checkCardInHand(cards, "Tile:10")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:10");
				SaboteurMove move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
		}
		if( i == 12 && j == 7) {
			if(checkCardInHand(cards, "Tile:8")){
				SaboteurCard card = getCardFromHand(sbs,"Tile:8");
				SaboteurMove move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
			if(checkCardInHand(cards, "Tile:9") || checkCardInHand(cards, "Tile:9_flip")){
				SaboteurCard card = getCardFromHand(sbs,"Tile:9");
				SaboteurMove move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
				else {
					String idx = card.getName().split(":")[1];
					SaboteurTile tile = new SaboteurTile(idx);
					tile = tile.getFlipped();
					move = new SaboteurMove(tile,12,6,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
			}
			if(checkCardInHand(cards, "Tile:10")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:10");
				SaboteurMove move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @return int - minimal distance between closest tile to the closest objective
	 */
	public static int getClosest() {
		int min = 80;
		int mini =0;
		int minj = 0;
		for(int i = 0; i<14;i++) {
			for(int j = 0; j<14;j++) {
				if(board[i][j] != null) {
					if(i == 12 && j == 5) {
						continue;
					}
					if(i == 12 && j == 7) {
						continue;
					}
					if(i == 12 && j == 3) {
						continue;
					}
					int[] a = getOffset(posMiddle,i,j, board[i][j].getName());
					int[] b = getOffset(posRight,i,j, board[i][j].getName());
					int[] c = getOffset(posLeft,i,j, board[i][j].getName());
					int x = Math.abs(a[0])+ Math.abs(a[1])+ Math.abs(a[2]);
					int y =  Math.abs(b[0])+ Math.abs(b[1])+ Math.abs(b[2]);
					int z =  Math.abs(c[0])+ Math.abs(c[1])+ Math.abs(c[2]);
					if(x<min) {
						min = x;
						mini = i;
						minj = j;
					}
					if(y<min) {
						min = y;
						mini = i;
						minj = j;
					}
					if(z<min) {
						min = z;
						mini = i;
						minj = j;
					}
				}
			}
		}
		return min;
	}

	/**
	 * Checks if there is a tile with a path on row 11, the last row before the objectives are placed.
	 * If this is the case, returns the most optimal move to make depending on the location and which
	 * objectives have been discovered
	 * @param sbs SaboteurBoardState
	 * @return SaboteurMove
	 */
	public static SaboteurMove checkLastRow(SaboteurBoardState sbs) {
		SaboteurMove move = null;
		ArrayList<SaboteurCard> cards = sbs.getCurrentPlayerCards();

		if(pathExists(11,4)) {
			if(checkCardInHand(cards, "Tile:8")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:8");
				move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
			if(checkCardInHand(cards, "Tile:9_flip")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:9_flip");
				move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
			if(checkCardInHand(cards, "Tile:9")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:9");
				String idx = card.getName().split(":")[1];
				SaboteurTile tile = new SaboteurTile(idx);
				tile = tile.getFlipped();
				move = new SaboteurMove(tile,12,4,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
			if(board[12][5].getName().equals("Tile:nugget") || !board[12][3].getName().equals("Tile:nugget")) {
				if(checkCardInHand(cards, "Tile:7")) {
					SaboteurCard card = getCardFromHand(sbs,"Tile:7");
					move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
			}
			if(board[12][3].getName().equals("Tile:nugget") || !board[12][5].getName().equals("Tile:nugget")) {
				if(checkCardInHand(cards, "Tile:5_flip")) {
					SaboteurCard card = getCardFromHand(sbs,"Tile:5_flip");
					move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
				if(checkCardInHand(cards, "Tile:5")) {
					SaboteurCard card = getCardFromHand(sbs,"Tile:5");
					String idx = card.getName().split(":")[1];
					SaboteurTile tile = new SaboteurTile(idx);
					tile = tile.getFlipped();
					move = new SaboteurMove(tile,12,4,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
			}
			else {
				if(checkCardInHand(cards, "Tile:7")) {
					SaboteurCard card = getCardFromHand(sbs,"Tile:7");
					move = new SaboteurMove(card,12,4,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
			}
		}

		if(pathExists(11,6)) {
			if(checkCardInHand(cards, "Tile:8")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:8");
				move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
			if(checkCardInHand(cards, "Tile:9_flip")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:9_flip");
				move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
			if(checkCardInHand(cards, "Tile:9")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:9");
				String idx = card.getName().split(":")[1];
				SaboteurTile tile = new SaboteurTile(idx);
				tile = tile.getFlipped();
				move = new SaboteurMove(tile,12,6,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
			if(board[12][5].getName().equals("Tile:nugget") || !board[12][7].getName().equals("Tile:nugget")) {
				if(checkCardInHand(cards, "Tile:5_flip")) {
					SaboteurCard card = getCardFromHand(sbs,"Tile:5_flip");
					move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
				if(checkCardInHand(cards, "Tile:5")) {
					SaboteurCard card = getCardFromHand(sbs,"Tile:5");
					String idx = card.getName().split(":")[1];
					SaboteurTile tile = new SaboteurTile(idx);
					tile = tile.getFlipped();
					move = new SaboteurMove(tile,12,6,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
			}
			if(board[12][7].getName().equals("Tile:nugget") || !board[12][5].getName().equals("Tile:nugget")) {

				if(checkCardInHand(cards, "Tile:7")) {
					SaboteurCard card = getCardFromHand(sbs,"Tile:7");
					move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
			}
			else {
				if(checkCardInHand(cards, "Tile:5_flip")) {
					SaboteurCard card = getCardFromHand(sbs,"Tile:5_flip");
					move = new SaboteurMove(card,12,6,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
				if(checkCardInHand(cards, "Tile:5")) {
					SaboteurCard card = getCardFromHand(sbs,"Tile:5");
					String idx = card.getName().split(":")[1];
					SaboteurTile tile = new SaboteurTile(idx);
					tile = tile.getFlipped();
					move = new SaboteurMove(tile,12,6,sbs.getTurnPlayer());
					if(sbs.isLegal(move)) {
						return move;
					}
				}
			}
		}

		if(pathExists(11,8)) {
			if(checkCardInHand(cards, "Tile:5_flip")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:5_flip");
				move = new SaboteurMove(card,12,8,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
			if(checkCardInHand(cards, "Tile:5")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:5");
				String idx = card.getName().split(":")[1];
				SaboteurTile tile = new SaboteurTile(idx);
				tile = tile.getFlipped();
				move = new SaboteurMove(tile,12,8,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}

			if(checkCardInHand(cards, "Tile:6")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:6");
				move = new SaboteurMove(card,12,8,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
			if(checkCardInHand(cards, "Tile:8")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:8");
				move = new SaboteurMove(card,12,8,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
		}
		if(pathExists(11,2)) {
			if(checkCardInHand(cards, "Tile:7")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:7");
				move = new SaboteurMove(card,12,2,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
			if(checkCardInHand(cards, "Tile:6_flip")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:6_flip");
				move = new SaboteurMove(card,12,2,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
			if(checkCardInHand(cards, "Tile:6")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:6");
				String idx = card.getName().split(":")[1];
				SaboteurTile tile = new SaboteurTile(idx);
				tile = tile.getFlipped();
				move = new SaboteurMove(tile,12,2,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}

			if(checkCardInHand(cards, "Tile:8")) {
				SaboteurCard card = getCardFromHand(sbs,"Tile:8");
				move = new SaboteurMove(card,12,2,sbs.getTurnPlayer());
				if(sbs.isLegal(move)) {
					return move;
				}
			}
		}
		return null;
	}

	/**
	 * Destroy method logic
	 * Look for the closest card to the objectives using the same logic as getClosest()
	 * then check what type of card it is, depending if it is a card that blocks the path or not, destroy it
	 * @param sbs SaboteurBoardState
	 * @return SaboteurMove for destroying a card
	 */
	public static SaboteurMove destroy(SaboteurBoardState sbs) {
		ArrayList<SaboteurCard> cards = sbs.getCurrentPlayerCards();
		if(!checkCardInHand(cards, "Destroy")) {
			return null;
		}
		int min = 80;
		int mini =0;
		int minj = 0;
		for(int i = 0; i<14;i++) {
			for(int j = 0; j<14;j++) {
				if(board[i][j] != null) {
					if(i == 12 && j == 5) {
						continue;
					}
					if(i == 12 && j == 7) {
						continue;
					}
					if(i == 12 && j == 3) {
						continue;
					}
					int[] a = getOffset(posMiddle,i,j, board[i][j].getName());
					int[] b = getOffset(posRight,i,j, board[i][j].getName());
					int[] c = getOffset(posLeft,i,j, board[i][j].getName());
					int x = Math.abs(a[0])+ Math.abs(a[1])+ Math.abs(a[2]);
					int y =  Math.abs(b[0])+ Math.abs(b[1])+ Math.abs(b[2]);
					int z =  Math.abs(c[0])+ Math.abs(c[1])+ Math.abs(c[2]);
					if(x<min) {
						min = x;
						mini = i;
						minj = j;
					}
					if(y<min) {
						min = y;
						mini = i;
						minj = j;
					}
					if(z<min) {
						min = z;
						mini = i;
						minj = j;
					}
				}
			}
		}
		SaboteurTile st = board[mini][minj];
		if(st.getName().equals("Tile:1") || st.getName().equals("Tile:2")||
				st.getName().equals("Tile:2_flip")||st.getName().equals("Tile:3")||
				st.getName().equals("Tile:3_flip")||st.getName().equals("Tile:4_flip")||
				st.getName().equals("Tile:4")||st.getName().equals("Tile:11")||
				st.getName().equals("Tile:11_flip")||st.getName().equals("Tile:12")||
				st.getName().equals("Tile:12_flip")||st.getName().equals("Tile:13")||
				st.getName().equals("Tile:14")||st.getName().equals("Tile:14_flip")||
				st.getName().equals("Tile:15")) {

			SaboteurMove destroy = new SaboteurMove(getCardFromHand(sbs,new SaboteurDestroy()),mini,minj,sbs.getTurnPlayer());
			if(!sbs.isLegal(destroy)) {
				throw new IllegalArgumentException("NOT CREATING MOVE PROPERLY");
			}
			return destroy;
		}

		return null;
	}

	/**
	 * Returns true if the card is a card which does not help for constructing paths
	 * false otherwise
	 * @param name of card
	 * @return boolean
	 */
	public static boolean checkIsBad(String name) {

		if(name.equals("Tile:1") || name.equals("Tile:2")||
				name.equals("Tile:2_flip")||name.equals("Tile:3")||
				name.equals("Tile:3_flip")||name.equals("Tile:4_flip")||
				name.equals("Tile:4")||name.equals("Tile:11")||
				name.equals("Tile:11_flip")||name.equals("Tile:12")||
				name.equals("Tile:12_flip")||name.equals("Tile:13")||
				name.equals("Tile:14")||name.equals("Tile:14_flip")||
				name.equals("Tile:15")) {
			return true;
		}
		return false;
	}

}