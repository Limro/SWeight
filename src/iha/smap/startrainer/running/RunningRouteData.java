package iha.smap.startrainer.running;
public class RunningRouteData 
{
	public int RunningIDEntry;
	public long DateEntry;
	public double MaxSpeedEntry;
	public double AvgSpeedEntry;
	public double DistanceEntry;
	public String TimeEntry;
	
	public RunningRouteData(int RunningID, long date, double maxSpeed, double avgSpeed, double distance, String time) 
	{
		RunningIDEntry = RunningID;
		DateEntry = date;
		MaxSpeedEntry = maxSpeed;
		AvgSpeedEntry = avgSpeed;
		DistanceEntry = distance;
		TimeEntry = time;
	}

}
