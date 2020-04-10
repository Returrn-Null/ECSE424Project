package student_player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurBoardPanel;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurBonus;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurMalus;
import Saboteur.cardClasses.SaboteurMap;
import Saboteur.cardClasses.SaboteurTile;
import boardgame.BoardState;
import boardgame.Move;
import Saboteur.SaboteurBoard;

public class MyTools {

	private static final int EMPTY = -1;
	private static SaboteurTile[][] board;
	private static int[][] intboard;
	private static int PrevMin1;
	private static int PrevMin2;
	private static int PrevMin0;
	private static int MalusNum = 2;
	private static SaboteurTile prevClosest;
	private static final int[] posMiddle = {12,5};
	private static final int[] posRight = {12,7};
	private static final int[] posLeft = {12,3};
	private static final int[] startPos = {5,5};
	private static ArrayList<SaboteurMove> legalMoves;
	

	public static double getSomething() {
		return Math.random();
	}

	public static void updateBoard(SaboteurBoardState sbs) {
		board = sbs.getHiddenBoard();
		intboard = sbs.getHiddenIntBoard();
		legalMoves = sbs.getAllLegalMoves();
	}


	/**
	 * This method can be used to find how many cards we are from reaching an objective.
	 * @param sbs
	 * @return number of moves.
	 */
	//	public static int[] getNumMovesToObjective(ArrayList<int[]> targets, SaboteurBoardState sbs) {
	//		int[] numMoves = new int[3];
	//		int min0 = 80;
	//		int min1 = 80;
	//		int min2 = 80;
	//		int mini = 80;
	//		int minj = 80;
	//
	//		for(int i = 12; i>=0;i--) {
	//			for(int j = 12; j>=0; j--) {
	//				if(board[i][j] != null && pathExists(i,j)) {//if there is a card on the current position and path from spawn
	//					for(int a = 0;a<3;a++) {
	//						if(targets.get(a) == null) {
	//							continue;
	//						}
	//						//						if(a == 0) {
	//						//							int[] off = getOffset(targets.get(0),i,j);
	//						//							if(min0>Math.abs(off[0])+Math.abs(off[1])+off[2]) {
	//						//								min0 = Math.abs(off[0])+Math.abs(off[1])+off[2];
	//						//							}	
	//						//						}
	//						if(a == 1) {
	//							int[] off = getOffset(targets.get(1),i,j);
	//							if(min1>Math.abs(off[0])+Math.abs(off[1])+off[2]) {
	//								min1 = Math.abs(off[0])+Math.abs(off[1])+off[2];
	//								mini = i;
	//								minj = j;
	//								prevClosest = board[i][j];
	//							}
	//						}
	//						//						if(a == 2) {
	//						//							int[] off = getOffset(targets.get(2),i,j);
	//						//							if(min2>Math.abs(off[0])+Math.abs(off[1])+off[2]) {
	//						//								min2 = Math.abs(off[0])+Math.abs(off[1])+off[2];
	//						//							}
	//						//						}
	//					}
	//				}
	//				if(min1>PrevMin1) {//if a destroy card was used add one to the cost
	//					min1 = PrevMin1+1;
	//				}
	//				if(min0>PrevMin0) {
	//					min0 = PrevMin0+1;
	//				}
	//				if(min2>PrevMin2) {
	//					min2 = PrevMin2+1;
	//				}
	//			}
	//		}
	//		numMoves[0] = min0;
	//		numMoves[1] = min1;
	//		numMoves[2] = min2;
	//		return numMoves;
	//
	//	}

	//	public static int checkNumMoves(SaboteurBoardState sbs) {
	//
	//		int min1 = 80;
	//		int[] target = {12,5};
	//		for(int i = 12; i>=0;i--) {
	//			for(int j = 12; j>=0; j--) {
	//				if(board[i][j]!= null && pathExists(i,j)) {
	//					int[] off = getOffset(target,i,j);
	//					if(min1>Math.abs(off[0])+Math.abs(off[1])+off[2]) {
	//						min1 = Math.abs(off[0])+Math.abs(off[1])+off[2];
	//					}
	//				}
	//				//TODO case where distroy and no pathh exists (other if)
	//			}
	//		}
	//		return min1;
	//
	//	}

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


	public static SaboteurCard dropStrategy(SaboteurBoardState sbs) {
		ArrayList<SaboteurCard> hand = sbs.getCurrentPlayerCards();
		boolean map = false;
		boolean bonus = false;
		if(checkRevealed(sbs)>=2) {
			map = true;
		}
		if(MalusNum-CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Title:malus") == 0) {
			bonus = true;
		}

		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Title:map")!=0 && map) {
			return getIndexCardinHand(hand,"Title:map");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Title:bonus")!=0 && bonus) {
			return getIndexCardinHand(hand,"Title:bonus");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Title:12")!= 0) {
			return getIndexCardinHand(hand,"Title:12");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Title:13")!= 0) {
			return getIndexCardinHand(hand,"Title:13");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Title:14")!= 0) {
			return getIndexCardinHand(hand,"Title:14");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Title:15")!= 0) {
			return getIndexCardinHand(hand,"Title:15");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Title:2")!= 0) {
			return getIndexCardinHand(hand,"Title:2");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Title:3")!= 0) {
			return getIndexCardinHand(hand,"Title:3");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Title:11")!= 0) {
			return getIndexCardinHand(hand,"Title:11");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Title:4")!= 0) {
			return getIndexCardinHand(hand,"Title:4");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Title:7")!= 0) {
			return getIndexCardinHand(hand,"Title:7");
		}
		if(CheckNumOfCardInHand(sbs.getCurrentPlayerCards(),"Title:destroy")!= 0) {
			return getIndexCardinHand(hand,"Title:destroy");
		}
		else {
			Random rand = new Random();
			int random = rand.nextInt(8);//generate an int between 0 and 7.
			return hand.get(random);
		}

	}

	public static SaboteurCard getIndexCardinHand(ArrayList<SaboteurCard> s, String title) {
		for(SaboteurCard e :s) {
			if(e.getName().equals(title)) {
				return e; 
			}
		}
		return null;
	}



	public static int checkRevealed(SaboteurBoardState sbs) {

		int num = 0;
		if(board[12][5].getName().equals("Title:hidden1") ||board[12][5].getName().equals("Title:hidden2")|| board[12][5].getName().equals("Title:nugget")) {
			num++;
		}
		if(board[12][3].getName().equals("Title:hidden1") ||board[12][3].getName().equals("Title:hidden2")|| board[12][3].getName().equals("Title:nugget")) {
			num++;
		}
		if(board[12][7].getName().equals("Title:hidden1") ||board[12][7].getName().equals("Title:hidden2")|| board[12][7].getName().equals("Title:nugget")) {
			num++;
		}
		return num;
	}

	public static int CheckNumOfCardInHand(ArrayList<SaboteurCard>ss, String title) {
		int num = 0;
		for(SaboteurCard sc: ss) {
			if(sc.getName().equals(title)) {
				num++;
			}
		}
		return num; //TODO: don t forget to decrement malusNum global var when sending or receiving malus
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
	 * method checkCardInHand()
	 * used to check if a specific card is in our hand and can therefore be played
	 * @param SaboteurCard card 
	 * @return boolean: true if Card in agent's hand/ false otherwise
	 */
	public static boolean checkCardInHand(ArrayList<SaboteurCard> cards, SaboteurCard card) {
		//		return cards.contains(card);
		for(SaboteurCard saboteurCard : cards) {
			if(saboteurCard.getName().equals(card.getName())) {
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
			if(tiles[objectivePos[i][0]][objectivePos[i][1]].getName().equals("Title:hidden1") || 
					tiles[objectivePos[i][0]][objectivePos[i][1]].getName().equals("Title:hidden2") ||
					tiles[objectivePos[i][0]][objectivePos[i][1]].getName().equals("Title:nugget")) {
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
			if(tiles[objectivePos[random][0]][objectivePos[random][1]].getName()!= null) {
				random = (random + 1) %3;
			}
			else {
				return random;
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
				return myMove;
			}else {
				return null;
			}
		}else {
			return null;
		}
	}

	public static Move playBonus(SaboteurBoardState boardState) {
		int playerId = boardState.getTurnPlayer();
		int mallusStatus = boardState.getNbMalus(playerId);
		if(checkCardInHand(getCardsInHand(boardState), new SaboteurBonus()) && mallusStatus >0) {
			SaboteurMove myMove = new SaboteurMove(getCardFromHand(boardState, new SaboteurBonus()), 0, 0, playerId);
			if(boardState.isLegal(myMove)) {
				return myMove;
			}else {
				return null;
			}
		}else {
			return null;
		}
	}

	/**
	 * call this method only if at distance 1 from obj. if still hidden and or nugget.
	 * @param boardState
	 * @return
	 */
	public static Move preventOpponentFromWinning(SaboteurBoardState boardState) {
		//if at one card away from an objective but our agent does not have the required path card
		//either destroy the card making the path or put a card that closes off the path

		//if pour savoir si on a la bonne carte plusieurs if


		//else place a random card
		return null;
	}

	public static Move playMapCard(SaboteurBoardState boardState) {
		int playerId = boardState.getTurnPlayer();
		int discoveredObjectivesCounter = countRevealedObjectives(boardState);

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
				return myMove;
			}else {
				return null;
			}
		}else {
			return null;
		}
	}
	/**
	 * temporiser
	 * @param boardState
	 * @return
	 */
	//	public static Move tacticalDrop(SaboteurBoardState boardState) {
	//		//if 2 cards away from the closest objective or objective that we are
	//		//are going for, drop instead of putting a path card
	//		
	//		//check mallus status of opponent
	//		int playerId = boardState.getTurnPlayer();
	//		int mallusStatus = boardState.getNbMalus((playerId+1)%2);
	//		
	//		SaboteurCard cardDrop;
	//		if(checkNumMoves(boardState) == 2 && board[12][5].getName() == null && mallusStatus == 0) {
	//			cardDrop = dropStrategy(boardState);
	//			if(cardDrop!=null) {
	//				SaboteurMove move = new SaboteurMove(cardDrop,0,0,boardState.getTurnPlayer());
	//				return move;
	//			}
	//			else return null;
	//
	//		}
	//		else return null;
	//
	//	}

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
			//&& pathExists(sm.getPosPlayed()[0], sm.getPosPlayed()[1])
			//if get offset returns 1 at index 2 and no destroy card then don t consider
		}

		return sabMove;
	}

	public static Move buildPath2(SaboteurBoardState boardState) {
		ArrayList<SaboteurMove> tileMoves = allTileMove();
		SaboteurMove sabMove = null;
		int min = 80;
		boolean flag = false;

		ArrayList<SaboteurMove> middleM = checkBest(tileMoves, "middle");
		ArrayList<SaboteurMove> rightM = checkBest(tileMoves, "right");
		ArrayList<SaboteurMove> leftM = checkBest(tileMoves, "left");
		
		for(SaboteurMove sm :middleM) {
			int[] middle  = getOffset(posMiddle,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int i = middle[0]+middle[1]+middle[2];
			if(i<= min && !pathExists(12,5)) {
				if(flag){
					if( !checkpathN(sm.getPosPlayed()[0], sm.getPosPlayed()[1])) {
						flag = false;
					}
					else {
						flag = true;
					}
					min = i;
					sabMove = sm;
				}

			}
		}
		for(SaboteurMove sm: rightM) {
			int[] right  = getOffset(posRight,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int j =  right[0]+right[1]+right[2];
			if(j<=min && !pathExists(12,7)) {
				if( !checkpathN(sm.getPosPlayed()[0], sm.getPosPlayed()[1])) {
					flag = false;
				}
				else {
					flag = true;
				}
				if(flag){					
					min = j;
					sabMove = sm;
				}
			}
		}
		for(SaboteurMove sm: leftM) {
			int[] left  = getOffset(posLeft,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
			int k = left[0]+left[1]+left[2];
			if(k<=min && !pathExists(12,3)) {
				if( !checkpathN(sm.getPosPlayed()[0], sm.getPosPlayed()[1])) {
					flag = false;
				}
				else {
					flag = true;
				}
				if(flag){
					min = k;
					sabMove = sm;
				}

			}
		}
		
//		for(SaboteurMove sm : good) {
//			flag = false;
//			if( !checkpathN(sm.getPosPlayed()[0], sm.getPosPlayed()[1])) {
//				flag = false;
//			}
//			else {
//				flag = true;
//			}
//
//			int[] middle  = getOffset(posMiddle,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
//			int[] right  = getOffset(posRight,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
//			int[] left  = getOffset(posLeft,sm.getPosPlayed()[0],sm.getPosPlayed()[1], sm.getCardPlayed().getName());
//			int i = middle[0]+middle[1]+middle[2];
//			int j =  right[0]+right[1]+right[2];
//			int k = left[0]+left[1]+left[2];
//			if(i<= min && !pathExists(12,5)) {
//				if(flag){
//					min = i;
//					sabMove = sm;
//				}
//
//			}
//			if(j<=min && !pathExists(12,7)) {
//				if(flag){					
//					min = j;
//					sabMove = sm;
//				}
//			}
//			if(k<=min && !pathExists(12,3)) {
//				if(flag){
//					min = k;
//					sabMove = sm;
//				}
//
//			}
//			//&& pathExists(sm.getPosPlayed()[0], sm.getPosPlayed()[1])
//			//if get offset returns 1 at index 2 and no destroy card then don t consider
//		}
//			
//			//&& pathExists(sm.getPosPlayed()[0], sm.getPosPlayed()[1])
//			//if get offset returns 1 at index 2 and no destroy card then don t consider
//		
		return sabMove;
	}




	public static Move Drop(SaboteurBoardState boardState) {
		SaboteurCard cardDrop = dropStrategy(boardState);
		if(cardDrop!=null) {
			SaboteurMove move = new SaboteurMove(cardDrop,0,0,boardState.getTurnPlayer());
			return move;
		}
		return null;
	}

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
	public static boolean checkpathN(int i,int j) {

		if(i+1 < 14 && pathExists(i+1, j) == true) {
			return true;
		}
		if(j+1 < 14 && pathExists(i, j+1) == true) {
			return true;
		}
		if(i-1 > 0 && pathExists(i-1, j) == true) {
			return true;
		}
		if(j-1 > 0 && pathExists(i, j-1) == true) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * this method is used to see which cards yield the best progress amongst all the legal moves
	 * @return
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
			if(id.equals("4")|| id.equals("4_flip")) {
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








}