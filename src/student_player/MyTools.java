package student_player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurBoardPanel;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurTile;
import boardgame.BoardState;
import Saboteur.SaboteurBoard;

public class MyTools {

	private static final int EMPTY = -1;
	private static SaboteurTile[][] board;
	private static int[][] intboard;
	private static int PrevMin1;
	private static int PrevMin2;
	private static int PrevMin0;
	private static int MalusNum = 2;

	public static double getSomething() {
		return Math.random();
	}

	/**
	 * This method can be used to find how many cards we are from reaching an objective.
	 * @param sbs
	 * @return number of moves.
	 */
	public static int[] getNumMovesToObjective(ArrayList<int[]> targets, SaboteurBoardState sbs) {
		int[] numMoves = new int[3];
		int min0 = 80;
		int min1 = 80;
		int min2 = 80;
		int mini = 80;
		int minj = 80;
		board = sbs.getHiddenBoard();
		intboard = sbs.getHiddenIntBoard();
		for(int i = 12; i>=0;i--) {
			for(int j = 12; j>=0; j--) {
				if(board[i][j] != null && pathExists(i,j)) {//if there is a card on the current position and path from spawn
					for(int a = 0;a<3;a++) {
						if(targets.get(a) == null) {
							continue;
						}
						if(a == 0) {
							int[] off = getOffset(targets.get(0),i,j);
							if(min0>Math.abs(off[0])+Math.abs(off[1])+off[2]) {
								min0 = Math.abs(off[0])+Math.abs(off[1])+off[2];
							}	
						}
						if(a == 1) {
							int[] off = getOffset(targets.get(1),i,j);
							if(min1>Math.abs(off[0])+Math.abs(off[1])+off[2]) {
								min1 = Math.abs(off[0])+Math.abs(off[1])+off[2];
								mini = i;
								minj = j;
							}
						}
						if(a == 2) {
							int[] off = getOffset(targets.get(2),i,j);
							if(min2>Math.abs(off[0])+Math.abs(off[1])+off[2]) {
								min2 = Math.abs(off[0])+Math.abs(off[1])+off[2];
							}
						}
					}
				}
				if(min1>PrevMin1) {//if a destroy card was used add one to the cost
					min1 = PrevMin1+1;
				}
				if(min0>PrevMin0) {
					min0 = PrevMin0+1;
				}
				if(min2>PrevMin2) {
					min2 = PrevMin2+1;
				}
			}
		}
		numMoves[0] = min0;
		numMoves[1] = min1;
		numMoves[2] = min2;
		return numMoves;

	}

	/**
	 * This method can be used to determine how much tiles horizontally and vertically are needed to reach the target
	 * @param target
	 * @param current
	 * @return
	 */
	public static int[] getOffset(int[] target, int i, int j) {
		int[] offsets = new int[3];
		offsets[0] = target[0]-i;
		offsets[1] = target[1]-j;
		int[][] path = board[i][j].getPath();
		if(offsets[0]>0 && path[1][0] == 1 && path[1][1]==1) {
			offsets[2] = 0;
			return offsets;
		}
		if(offsets[1]>0 && path[1][1] ==1 && path[2][1] == 1) {
			offsets[2] = 0;
			return offsets;
		}
		if(offsets[1]<0 && path[1][1] == 1 && path[0][1] == 1) {
			offsets[2] = 0;
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
		if(board[12][3].getName().equals("Title:hidden1") ||board[12][5].getName().equals("Title:hidden2")|| board[12][5].getName().equals("Title:nugget")) {
			num++;
		}
		if(board[12][7].getName().equals("Title:hidden1") ||board[12][5].getName().equals("Title:hidden2")|| board[12][5].getName().equals("Title:nugget")) {
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
	 * method checkCardInHand()
	 * used to check if a specific card is in our hand and can therefore be played
	 * @param SaboteurCard card 
	 * @return boolean: true if Card in agent's hand/ false otherwise
	 */
	public static boolean checkCardInHand(ArrayList<SaboteurCard> cards, SaboteurCard card) {
		return cards.contains(card);
	}

}