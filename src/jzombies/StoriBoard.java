package jzombies;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class StoriBoard {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	
	String storiName;

	public StoriBoard (ContinuousSpace<Object> space, Grid<Object> grid, String name) {
		this.space = space;
		this.grid = grid;
		//this.energy = startingEnergy = energy;
		//this.storiList = new List(storiName);
		this.storiName = name;
		System.out.println(this.storiName);
	}
	
	
}
