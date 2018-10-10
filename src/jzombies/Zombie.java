/**
 * 
 */
package jzombies;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
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
// Zombie -> 투자자 PD
public class Zombie {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private boolean moved;
	private int countNum;
	private int coinAsset; 
	

	public Zombie(ContinuousSpace<Object> space, Grid<Object> grid, int count) {
		this.space = space;
		this.grid = grid;
		this.countNum = count;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		// get the grid location of this Zombie
		GridPoint pt = grid.getLocation(this);

		// use the GridCellNgh class to create GridCells for
		// the surrounding neighborhood.
		// 가까운 작가들이 모여 있는 곳으로 이동한다
		GridCellNgh<StoriBoard> nghCreator = new GridCellNgh<StoriBoard>(grid, pt, StoriBoard.class, 1, 1);
		List<GridCell<StoriBoard>> gridCells = nghCreator.getNeighborhood(true);
		// 이웃의 8 셀을 램덤으로 섞는다
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
 
		GridPoint pointWithMostStoriBoard = null;
		int maxCount = -1;
		for (GridCell<StoriBoard> cell : gridCells) {
			if (cell.size() > maxCount) {
				pointWithMostStoriBoard = cell.getPoint();
				maxCount = cell.size();
			}
		}
		// 작가가 몰려 있는 곳으로 움직인다.
		moveTowards(pointWithMostStoriBoard);
		//infect();
		staking();
	}

	public void moveTowards(GridPoint pt) {
		// only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint,otherPoint);
			space.moveByVector(this, 1, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
			// watchFieldName by Human run(), I think that false allocation is auto??
			moved = true;
		}
	}

	public void staking() {
		GridPoint pt = grid.getLocation(this);
		List<Object> storiBoardListTmp = new ArrayList<Object>();
		//System.out.println("storiBoardListTmp.size() : "+storiBoardListTmp.size());
		
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
			if (obj instanceof StoriBoard) {
				storiBoardListTmp.add(obj);
			}
		}

		
		if (storiBoardListTmp.size() > 0) {
			// If there are plural stories, only pick one story by random
			int index = RandomHelper.nextIntFromTo(0, storiBoardListTmp.size() - 1);
			//System.out.println("index : "+index);
			
			Object obj = storiBoardListTmp.get(index);
			//System.out.println("obj : " + obj.toString());
			NdPoint spacePt = space.getLocation(obj);
			Context<Object> context = ContextUtils.getContext(obj);
			//context.remove(obj);
			Network<Object> net = (Network<Object>)context.getProjection("staking network");
			
			//System.out.println("equals : " + net.equals((StoriBoard)obj));		
			//System.out.println("net.getName() : " + net.getName());		
			
		//	System.out.println("net.getDegree(obj) : " + net.getDegree(obj));
			System.out.println("net.isAdjacent(this, obj) : " + net.isAdjacent(this, obj));
			
			if(net.isAdjacent(this, obj)) {
				System.out.println("already staking");
			}else {
				net.addEdge(this,obj);
			}
			
			//Zombie zombie = new Zombie(space, grid);
			//context.add(zombie);
			//space.moveTo(zombie, spacePt.getX(), spacePt.getY());
			//grid.moveTo(zombie, pt.getX(), pt.getY());
			
			//Network<Object> net = (Network<Object>)context.getProjection("infection network");
			//net.addEdge(this, zombie);
		}
	}
}
