

import java.util.ArrayList;

public class Ship {
	private ArrayList<String> location = new ArrayList<String>();
	private Integer size;
	
	public Ship(Integer size){
		this.size=size;
	}
	public void setLocation(ArrayList<String> loc){
		this.location.addAll(loc);
	}
	public ArrayList<String> getLocation(){
		return location;
	}
	public Integer getSize(){
		return size;
	}
	public String checkGuess(String guess){
		String result = "miss";
		if(location.contains(guess)){
			location.remove(guess);
			result = location.isEmpty() ? "Sunk":"Hit";
		}
		return result;		
	}
}
