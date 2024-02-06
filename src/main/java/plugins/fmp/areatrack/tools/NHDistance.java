package plugins.fmp.areatrack.tools;



public interface NHDistance<T> 
{
	double computeDistance(T s1, T s2) throws NHFeatureException;
}