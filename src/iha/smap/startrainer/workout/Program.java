package iha.smap.startrainer.workout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Program implements Serializable {
	private static final long serialVersionUID = 1L;

	public Program() {
		Name = "";
		Description = "";
		Exercises = new ArrayList<Exercise>();
	}
	
	public Program(String name, String desc) {
		Name = name;
		Description = desc;
		Exercises = new ArrayList<Exercise>();
	}

	public Program(String name, String desc, List<Exercise> exercises) {
		Name = name;
		Description = desc;
		Exercises = exercises;
	}

	public String Name;
	public String Description;
	public List<Exercise> Exercises;
}
