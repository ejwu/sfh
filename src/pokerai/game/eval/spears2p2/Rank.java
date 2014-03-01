package pokerai.game.eval.spears2p2;
public enum Rank {
	Deuce	("2"), 
	Three	("3"), 
	Four	("4"), 
	Five	("5"), 
	Six		("6"),
	Seven	("7"), 
	Eight	("8"), 
	Nine	("9"), 
	Ten		("T"), 
	Jack	("J"), 
	Queen	("Q"), 
	King	("K"), 
	Ace		("A");
	
	private final String toString;
	private final static Rank[] valuesByChar = new Rank[255];
	static {
		for (Rank r : Rank.values()) {
			valuesByChar[(int)r.toString().toUpperCase().charAt(0)] = r;
			valuesByChar[(int)r.toString().toLowerCase().charAt(0)] = r;
		}
		
	}

	private Rank(String toString) {
		this.toString = toString;
	}
	
	public String toString() {
		return toString;
	}
	
	public static Rank parse(String s)  {
		Rank r = valuesByChar[(int)s.charAt(0)];
		if (r == null) {
			throw new RuntimeException("Unrecognized rank: " + s);
		}
		return r;
	}
	

}
