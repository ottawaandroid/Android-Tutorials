package ca.christophersaunders.demos.crest;

public class Plant {
	@JsonProperty("name")
	String name;

	@JsonProperty("cost")
	int cost;

	@JsonProperty("attack")
	int attack;

	@JsonProperty("life")
	int life;
}