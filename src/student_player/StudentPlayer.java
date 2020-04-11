package student_player;

import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.SaboteurBoardState;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260783060");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(SaboteurBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
    	Move myMove;
    	
    	//first update board with current board state
    	MyTools.updateBoard(boardState);
    	
    	//then start selecting moves by going through the strategy 
    	
        if(MyTools.playMalus(boardState) != null) {
        	myMove = MyTools.playMalus(boardState);
        	return myMove;
        }
//        
        if(MyTools.playBonus(boardState) != null) {
        	myMove = MyTools.playBonus(boardState);
        	return myMove;
        }
//        
//        if(MyTools.preventOpponentFromWinning(boardState) != null) {
//        	myMove = MyTools.preventOpponentFromWinning(boardState);
//        	return myMove;
//        }
          
//        if(MyTools.getClosest()>2 && MyTools.playMapCard(boardState) != null) {
//        	myMove = MyTools.playMapCard(boardState);
//        	return myMove;
//        }
//        
//        if(MyTools.tacticalDrop(boardState) != null) {
//        	myMove = MyTools.tacticalDrop(boardState);
//        	return myMove;
//        }
        if(MyTools.buildPath2(boardState) != null) {
        	myMove = MyTools.buildPath2(boardState);
        	return myMove;
        }
        
        else {
        // Is random the best you can do?
        myMove = boardState.getRandomMove();    
        }
        // Return your move to be processed by the server.
        return myMove;
    }
}