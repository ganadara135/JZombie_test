package jzombies;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class StoriBoard {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	
	public int storiNum;
	public RewardPi pi;
	String storiTitle;

	public StoriBoard (ContinuousSpace<Object> space, Grid<Object> grid, String title) {
		this.space = space;
		this.grid = grid;
		//this.energy = startingEnergy = energy;
		//this.storiList = new List(storiName);
		this.pi = new RewardPi();
		this.storiTitle = title;
		//System.out.println(this.storiTitle);
	}
	
	public String getTitle() {
		return this.storiTitle;
	}
	
	public int getTotalStaking() {
		return pi.getTotalStaking();
	}
	
	public void setTotalStaking(int staking) {
		pi.setTotalStaking(staking);
	}

}
