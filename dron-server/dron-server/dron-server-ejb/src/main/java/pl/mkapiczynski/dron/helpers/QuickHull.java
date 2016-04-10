package pl.mkapiczynski.dron.helpers;

import java.awt.Point;
import java.util.ArrayList;

import pl.mkapiczynski.dron.database.Location;

public class QuickHull {
	 public ArrayList<Location> quickHull(ArrayList<Location> points)
	    {
	        ArrayList<Location> convexHull = new ArrayList<Location>();
	        if (points.size() < 3)
	            return (ArrayList) points.clone();
	 
	        int minPoint = -1, maxPoint = -1;
	        double minX = Double.MAX_VALUE;
	        double maxX = Double.MIN_VALUE;
	        for (int i = 0; i < points.size(); i++)
	        {
	            if (points.get(i).getLatitude() < minX)
	            {
	                minX = points.get(i).getLatitude();
	                minPoint = i;
	            }
	            if (points.get(i).getLatitude() > maxX)
	            {
	                maxX = points.get(i).getLatitude();
	                maxPoint = i;
	            }
	        }
	        Location A = points.get(minPoint);
	        Location B = points.get(maxPoint);
	        convexHull.add(A);
	        convexHull.add(B);
	        points.remove(A);
	        points.remove(B);
	 
	        ArrayList<Location> leftSet = new ArrayList<Location>();
	        ArrayList<Location> rightSet = new ArrayList<Location>();
	 
	        for (int i = 0; i < points.size(); i++)
	        {
	        	Location p = points.get(i);
	            if (pointLocation(A, B, p) == -1)
	                leftSet.add(p);
	            else if (pointLocation(A, B, p) == 1)
	                rightSet.add(p);
	        }
	        hullSet(A, B, rightSet, convexHull);
	        hullSet(B, A, leftSet, convexHull);
	 
	        return convexHull;
	    }
	 
	    public double distance(Location A, Location B, Location C)
	    {
	        double ABx = B.getLatitude() - A.getLatitude();
	        double ABy = B.getLongitude() - A.getLongitude();
	        double num = ABx * (A.getLongitude() - C.getLongitude()) - ABy * (A.getLatitude() - C.getLatitude());
	        if (num < 0)
	            num = -num;
	        return num;
	    }
	 
	    public void hullSet(Location A, Location B, ArrayList<Location> set,
	            ArrayList<Location> hull)
	    {
	        int insertPosition = hull.indexOf(B);
	        if (set.size() == 0)
	            return;
	        if (set.size() == 1)
	        {
	        	Location p = set.get(0);
	            set.remove(p);
	            hull.add(insertPosition, p);
	            return;
	        }
	        double dist = Double.MIN_VALUE;
	        int furthestPoint = -1;
	        for (int i = 0; i < set.size(); i++)
	        {
	        	Location p = set.get(i);
	            double distance = distance(A, B, p);
	            if (distance > dist)
	            {
	                dist = distance;
	                furthestPoint = i;
	            }
	        }
	        Location P = set.get(furthestPoint);
	        set.remove(furthestPoint);
	        hull.add(insertPosition, P);
	 
	        // Determine who's to the left of AP
	        ArrayList<Location> leftSetAP = new ArrayList<Location>();
	        for (int i = 0; i < set.size(); i++)
	        {
	        	Location M = set.get(i);
	            if (pointLocation(A, P, M) == 1)
	            {
	                leftSetAP.add(M);
	            }
	        }
	 
	        // Determine who's to the left of PB
	        ArrayList<Location> leftSetPB = new ArrayList<Location>();
	        for (int i = 0; i < set.size(); i++)
	        {
	        	Location M = set.get(i);
	            if (pointLocation(P, B, M) == 1)
	            {
	                leftSetPB.add(M);
	            }
	        }
	        hullSet(A, P, leftSetAP, hull);
	        hullSet(P, B, leftSetPB, hull);
	 
	    }
	 
	    public int pointLocation(Location A, Location B, Location P)
	    {
	        double cp1 = (B.getLatitude() - A.getLatitude()) * (P.getLongitude() - A.getLongitude()) - (B.getLongitude() - A.getLongitude()) * (P.getLatitude() - A.getLatitude());
	        if (cp1 > 0)
	            return 1;
	        else if (cp1 == 0)
	            return 0;
	        else
	            return -1;
	    }
}
