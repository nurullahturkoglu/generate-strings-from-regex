
package Automata_DFA;

import java.util.HashSet;
import java.util.Set;


public class LeafNode extends Node {

    private int num;
    private Set<Integer> followPos;

    public LeafNode(String symbol, int num) {
        super(symbol);
        this.num = num;
        followPos = new HashSet<>();
    }

    /**
     * @return the num
     */
    public int getNum() {
        return num;
    }

    /**
     * @param num the num to set
     */
    public void setNum(int num) {
        this.num = num;
    }
    
    public void addToFollowPos(int number){
        followPos.add(number);
    }

    /**
     * @return the followPos
     */
    public Set<Integer> getFollowPos() {
        return followPos;
    }

    /**
     * @param followPos the followPos to set
     */
    public void setFollowPos(Set<Integer> followPos) {
        this.followPos = followPos;
    }
}
