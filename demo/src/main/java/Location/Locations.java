package Location;

import java.util.TreeMap;
import java.util.TreeSet;

import Character.Character;
import dataDeals.Pair;
public class Locations {
    private TreeMap<Integer,TreeMap<Integer,Character>> mapOfCharacter;//<维度X的识别码, 对应维度对应X坐标下，关于Y坐标排序的列表
    private TreeMap<Location,Integer>location_of_sepcialBlocks;
    private static TreeMap<Integer,int[][]> wholeMap;//维度，区块坐标（x,y）
    public Locations(){

    }
    public boolean addCharToMap(Character theCharacter){
        /* 还在思考该用什么数据结构，待完成 */
        return true;
    }

}
