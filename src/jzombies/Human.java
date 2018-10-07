/**
 * 
 */
package jzombies;

import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

/**
 * @author kcod
 *
 */
// Human -> 작가
public class Human {
	
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private int energy, startingEnergy;
//	public StoriBoard storiBoard;
	String ownStoriBoardName;

	public Human(ContinuousSpace<Object> space, Grid<Object> grid, int energy) {
		this.space = space;
		this.grid = grid;
		this.energy = startingEnergy = energy;
		this.ownStoriBoardName = "";	// isEmpty() works after setting this ""
	}

	@Watch(watcheeClassName = "jzombies.Zombie", watcheeFieldNames = "moved", 
			query = "within_vn 1", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void run() {
		// get the grid location of this Human
		GridPoint pt = grid.getLocation(this);

		// use the GridCellNgh class to create GridCells for the surrounding neighborhood.
		GridCellNgh<Zombie> nghCreator = new GridCellNgh<Zombie>(grid, pt,Zombie.class, 1, 1);
		List<GridCell<Zombie>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

		GridPoint pointWithLeastZombies = null;
		int minCount = Integer.MAX_VALUE;
		for (GridCell<Zombie> cell : gridCells) {
			if (cell.size() < minCount) {
				pointWithLeastZombies = cell.getPoint();
				minCount = cell.size();
			}
		}
		

		
		if (energy > 0) {
			moveTowards(pointWithLeastZombies);
			
		} else {
			energy = startingEnergy;	
		}
		
		System.out.println("check : " + this.ownStoriBoardName.isEmpty());
		
		// StoriBoard 생성하는 부분
		if(this.ownStoriBoardName.isEmpty()) {
			Context<Object> context = ContextUtils.getContext(this);
			NdPoint spacePt = space.getLocation(this);
			GridPoint ptBoard = grid.getLocation(this);
			
			this.ownStoriBoardName = "스토리네임";
			StoriBoard storiB = new StoriBoard(space, grid, this.ownStoriBoardName);
			context.add(storiB);
			System.out.println("111");
			space.moveTo(storiB, spacePt.getX(), spacePt.getY());
			System.out.println("222");
			grid.moveTo(storiB, ptBoard.getX(), ptBoard.getY());
			System.out.println("333");
			Network<Object> net = (Network<Object>)context.getProjection("infection network");
			System.out.println("444");
			net.addEdge(this, storiB);
			System.out.println("555");
		}
	}
	
	public void moveTowards(GridPoint pt) {
		// only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			space.moveByVector(this, 2, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
			energy--;
			

	
		}
	}

}
