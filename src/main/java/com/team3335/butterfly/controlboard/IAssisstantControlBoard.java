package com.team3335.butterfly.controlboard;

public interface IAssisstantControlBoard {
	/* Just sit there and look pretty, will ya? */
	
	public default UnknownTruth getAmIAlive() {return null;}
	
	
	public enum UnknownTruth {
		//YES,
		NO,
		MAYBE,
		WHO_EVEN_KNOWS_AT_THIS_POINT,
		IS_THIS_THE_REAL_LIFE_IS_THIS_JUST_FANTASY,
		UNDEFINED,
		NO_SOLUTION
	}
}
