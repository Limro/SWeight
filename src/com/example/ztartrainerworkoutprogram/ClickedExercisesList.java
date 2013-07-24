package com.example.ztartrainerworkoutprogram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


	public class ClickedExercisesList implements Serializable
	{

		public ClickedExercisesList ()
		{
			clickedList = new ArrayList<Integer>();
		}
		
		public ClickedExercisesList (List<Integer> cList)
		{
			clickedList = cList;
		}
		public List<Integer> clickedList;

		private static final long serialVersionUID = 1L;
	
	}
