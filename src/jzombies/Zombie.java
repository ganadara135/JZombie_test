/**
 * 
 */
package jzombies;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
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
import repast.simphony.util.collections.IndexedIterable;
import repast.simphony.valueLayer.GridValueLayer;

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
//	private int stakingAsset;
	//public ArrayList<Object> recordOfmoving;
	public int intervalStep;
	public StoriBoard intervalStori = null;
	GridPoint pointWithMostStoriBoard = null;
	//public JZombiesBuilder globalEnv;
	int maxStoriLimit;

	

	public Zombie(ContinuousSpace<Object> space, Grid<Object> grid, int count, int coin) {
		this.space = space;
		this.grid = grid;
		this.countNum = count;
		//recordOfmoving = new ArrayList<Object>();
		intervalStep = 0;
		this.coinAsset = coin;
		 
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		maxStoriLimit = (Integer) params.getValue("max_stori");
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		
		
		
		if( !checkMaxLimitStaking() ) {
			System.out.println("스테이킹할 돈이 없습니다.");
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return;
		}
		
		if(intervalStep == 0){		
		
			intervalStep = 3;
			intervalStori = null;
			pointWithMostStoriBoard = null;
			
			// get the grid location of this Zombie
			GridPoint pt = grid.getLocation(this);
						
			Context<Object> contextTmp = ContextUtils.getContext(this);
			//System.out.println("Storiboard Count : " + contextTmp.getObjects(StoriBoard.class).size());
			
			IndexedIterable<Object> storiIter  = contextTmp.getObjects(StoriBoard.class);
			//System.out.println("Storiboard Count : " + storiIter.size());
			//System.out.println(this);
	//		System.out.println("투자자 죄표 : " + pt);
			
			//Context<Object> context = ContextUtils.getContext(obj);
			Network<Object> net = (Network<Object>)contextTmp.getProjection("staking network");
			
			StoriBoard storiTmp = null;	
			
			
			
			System.out.println("스토리갯수 : " + storiIter.size());
			
			if(storiIter.size() > 19 && storiIter.size() <= 20) {				
			
				for (int n=0; n < storiIter.size(); n++) {
					System.out.println(" text : "+ ((StoriBoard)storiIter.get(n)).pi.getTotalStaking());
				}
			}
			
			
			if(storiIter.size() > 3) { // 전체적으로 최소 3개 이상의 스토리가 만들어지면 이 방식으로 투자자 움직임
				//System.out.println("투자자 X 위치 : " + pt.getX());
				//System.out.println("투자자 Y 위치 : " + pt.getY());
				
				//int distanceSPD = pt.getX() + pt.getY();
				//int distanceMin = 50; //Grid 범위 최대값
				//int distanceTmp = 0;
				//투자자와 스토리가 가장 가까운 곳으로 이동한다. 대신 스토리는 넷엣지가 없어야 함.
				//GridPoint storiTmp = null;
//				do { // 엣지가 이미 연결되어 있으면 해당 스토리는 건너뜀
//					storiTmp = (StoriBoard) storiIter.get(RandomHelper.nextIntFromTo(0, storiIter.size()-1));
//				}while(net.isAdjacent(this, storiTmp));
				storiTmp = (StoriBoard) storiIter.get(RandomHelper.nextIntFromTo(0, storiIter.size()-1));
				for(int y=0; (y < 10 && net.isAdjacent(this, storiTmp)); y++) {
					storiTmp = (StoriBoard) storiIter.get(RandomHelper.nextIntFromTo(0, storiIter.size()-1));
				}
				
				pointWithMostStoriBoard = grid.getLocation(storiTmp);
				intervalStori = storiTmp;
				
				/*
				for (int i=0; i < storiIter.size(); i++) {				
					storiTmp = (StoriBoard) storiIter.get(i);
					
					if(!net.isAdjacent(this, storiTmp)) { // 엣지가 이미 연결되어 있으면 해당 스토리는 건너뜀
						GridPoint storiGridTmp = grid.getLocation(storiTmp);
						distanceTmp = (int) grid.getDistance(pt, storiGridTmp);
						if(distanceTmp <= distanceMin) {				        	
				        	distanceMin = distanceTmp;			
							pointWithMostStoriBoard = storiGridTmp;
							intervalStori = storiTmp;
						}
					}				
				}
				*/
			}else { // 기존에 좀비가 움직이는 방식으로 작동
				// use the GridCellNgh class to create GridCells for the surrounding neighborhood.		
				// 가까운 작가들이 모여 있는 곳으로 이동한다
				GridCellNgh<StoriBoard> nghCreator = new GridCellNgh<StoriBoard>(grid, pt, StoriBoard.class, 1, 1);
				List<GridCell<StoriBoard>> gridCells = nghCreator.getNeighborhood(true);
				
				// 이웃의 8 셀을 램덤으로 섞는다
				SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		 
				//GridPoint pointWithMostStoriBoard = null;
				int maxCount = -1;
				for (GridCell<StoriBoard> cell : gridCells) {
					if (cell.size() > maxCount) {
						pointWithMostStoriBoard = cell.getPoint();
						maxCount = cell.size();
					}
				}
			}		
			
		}else {
			pointWithMostStoriBoard = grid.getLocation(intervalStori);
		}
		// 스토리보드가 몰려 있는 곳으로 움직인다.
		moveTowards(pointWithMostStoriBoard);
		staking();
		
		intervalStep--;
	}

	public void moveTowards(GridPoint pt) {
		//System.out.println("Storiboard 좌표 : " + pt);
		if(pt != null) {
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
		}else {
			System.out.println("투자자 안 움직임");
		}
	}

	public void staking() {
		GridPoint pt = grid.getLocation(this);
		List<Object> storiBoardListTmp = new ArrayList<Object>();
		
		// 같은 위치에 스토리가 있으면
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
			if (obj instanceof StoriBoard) {
				storiBoardListTmp.add(obj);
			}
		}
		
		//System.out.println("storiBoardListTmp.size() : "+storiBoardListTmp.size());
		if (storiBoardListTmp.size() > 0) {
			// If there are plural stories, only pick one story by random
			int index = RandomHelper.nextIntFromTo(0, storiBoardListTmp.size() - 1);
			//System.out.println("index : "+index);
			
			StoriBoard obj = (StoriBoard)storiBoardListTmp.get(index);
			//System.out.println("obj : " + obj.toString());
			NdPoint spacePt = space.getLocation(obj);
			Context<Object> context = ContextUtils.getContext(obj);
			//context.remove(obj);
			Network<Object> net = (Network<Object>)context.getProjection("staking network");
			
//			GridValueLayer resourceLayer = (GridValueLayer)context..getValueLayer("StoriBoard");

//			resourceLayer.set(resource, grid.getLocation(this).getX(), grid.getLocation(this).getY());
			//context.getAgentLayer(StoriBoard.class);
			//System.out.println("equals : " + net.equals((StoriBoard)obj));		
			//System.out.println("net.getName() : " + net.getName());		
			
		//	System.out.println("net.getDegree(obj) : " + net.getDegree(obj));
		//	System.out.println("net.isAdjacent(this, obj) : " + net.isAdjacent(this, obj));
			
			//int tmp = this.coinAsset-1;
			//System.out.println("tmp : " + tmp);
			if(this.coinAsset > 0) {
				this.coinAsset -= 1;			
				obj.pi.doStaking(this.coinAsset);
				if(net.isAdjacent(this, obj)) {
					System.out.println("already staking");
					
				}else {
					net.addEdge(this,obj);
					System.out.println("Title : " + obj.getTitle());									
				}
				System.out.println("Staking Asset : " + obj.pi.getTotalStaking());
			}			
		}
	}
	
	public boolean checkMaxLimitStaking() {
		//System.out.println("투자자 자산 : " + this.coinAsset);
		if(this.coinAsset > 0) {
			return true;
		}
		return false;	
	}
}
