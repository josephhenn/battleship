

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;

public class Game {

	private ArrayList<Ship> p1 = new ArrayList<Ship>();
	private ArrayList<Ship> p2 = new ArrayList<Ship>();
	Random rand = new Random();
	private Integer ships = rand.nextInt(9) + 1;
	private Integer boardSize = rand.nextInt(16)+8;
	
	public void setup(){
		for(int i = ships; i > 0; i--){
			p1.add(new Ship(3));
			p2.add(new Ship(3));
		}
		setLocations(p1);
		setLocations(p2);
	}

	private void setLocations(ArrayList<Ship> s1){
		Random rand = new Random();
		ArrayList<String> locationToSet = new ArrayList<String>();
		ArrayList<String> temp = null;
		Integer let, num, incl, incn;
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		boolean work;
		for(int i = 0; i<s1.size(); i++){
			work = false;
			start:
				while(!work){
					locationToSet.clear();
					work = true;
					let = rand.nextInt(boardSize);
					num = 1 + rand.nextInt(boardSize);
					if(num%2==0){
						incl = 1;
						incn = 0;
					}
					else{
						incl = 0;
						incn = 1;
					}
					for(int j = 0; j < s1.get(i).getSize(); j++){
						String loc = "" + alpha.charAt(let) + num;
						let += incl;
						num += incn;
						for(int k = 0; k < s1.size(); k++){
							if(k != i){
								temp = s1.get(k).getLocation();
								if(temp.contains(loc)){
									work = false;
									continue start;
								}
							}
						}
						locationToSet.add(loc);
					}
					s1.get(i).setLocation(locationToSet);
				}
		}
	}
	public Integer numPieces(){
		return ships;
	}
	public Integer boardSize(){
		return boardSize;
	}
	public ArrayList<Ship> player1List(){
		return p1;
	}
	public ArrayList<Ship> player2List(){
		return p2;
	}
}

