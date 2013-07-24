package iha.smap.startrainer.workout;

import java.io.Serializable;


public class Exercise implements Serializable
{

	private static final long serialVersionUID = 1L;
	public Exercise(String name, Integer sets,  Integer Rep, Integer kilo)
	{
		Name = name;
		Sets = sets;
		Repetitions = Rep;
		Kilo = kilo;
		MaxSets = sets;
	}
	
	public String Name;
	public Integer Sets;
	public Integer Repetitions;
	public Integer MaxSets;
	public Integer Kilo;
}
