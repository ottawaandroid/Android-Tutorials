package ca.christophersaunders.demos.crest;

import org.codehaus.jackson.annotate.JsonProperty;

public class Plant {
	@JsonProperty("name")
	String name;

	@JsonProperty("cost")
	int cost;

	@JsonProperty("attack")
	int attack;

	@JsonProperty("life")
	int life;

	public String toString(){
		return String.format("%s costs %d sun", name, cost);
	}
}