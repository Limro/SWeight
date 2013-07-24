package iha.smap.startrainer.running;

public class RunningGPSPoint 
{
	public double Latitude;
	public double Longitude;
	public int RunningID;
	
	public RunningGPSPoint(double latitude, double longitude, int runningID) 
	{
		Latitude = latitude;
		Longitude = longitude;
		RunningID = runningID;
	}
}
