package jzombies;

public class RewardPi {

	public int totalStaking;
	//public double species;  // the species concentration
	
	public RewardPi() {
		totalStaking = 0;
	}
	
	public void doStaking(int staking) {
		totalStaking += staking;
		
		System.out.println("totalStaking : " + totalStaking);
	}
	
	public int getTotalStaking() {
		return totalStaking;
	}
	
	public void setTotalStaking(int staking) {
		this.totalStaking = staking;
	}
	
}
