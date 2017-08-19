package test_project.test_project;

import java.util.ArrayList;
import java.util.Random;

public class KMeans {

	@SuppressWarnings("unchecked")
	public static ArrayList<ClusterMean> KMeansPartition(int k, ArrayList<Song> data)
	{
		Random rand = new Random();
		ArrayList<ClusterMean> clusters = new ArrayList<ClusterMean>();
		ArrayList<ClusterMean> old_clusters = new ArrayList<ClusterMean>();
		
		//First step (create random cluster points)
		//Partition objects into k nonempty subsets
		
		for(int i = 0; i < k; i++)
		{
			Song s = data.get(rand.nextInt(data.size()));
			clusters.add(new ClusterMean(s));
			clusters.get(i).cluster_members.add(s);
			//clusters.get(i).centroid = cluster_points.get(i);
		}
		
		//old_clusters = (ArrayList<KMeanCluster>) clusters.clone();
		
		//second step
		//Compute seed points as the centroids of the clusters
		//of the current partition (the centroid is the center, i.e.,
		//mean point, of the cluster)
		
		
		
		int counter = 0;
		while(/*CheckChanges(clusters, old_clusters, k) ||*/ counter != 25)
		{
			System.out.println("Clustering!! counter: " + counter );
			old_clusters = (ArrayList<ClusterMean>) clusters.clone();
			
			for(ClusterMean cluster : clusters)
			{
				cluster.cluster_members.clear();
			}
			
			
			//third step
			//Assign each object to the cluster with the nearest
			//seed point
			for(Song song : data)
			{
				int index = 0;
				double best_distance = Float.MAX_VALUE;
				double distance;
				
				for(int i = 0; i < k; i++)
				{
					distance = CalculateDistance(song, clusters.get(i).centroid);
					
					if(distance < best_distance)
					{
						best_distance = distance;
						index = i;
					}
				}
				
				clusters.get(index).cluster_members.add(song);
			}
			
			for(ClusterMean cluster : clusters)
			{
				cluster.CalculateCentroid();
			}
			counter++;
			
		}
		
		
		//four step
		//Go back to Step 2, stop when no more new
		//assignment
		
		return clusters;
		
	}
	
	public static double CalculateDistance(Song song, Song centroid)
	{
		double result = 0.0;

		result += Math.pow(centroid.key - song.key, 2);
		result += Math.pow(centroid.tempo - song.tempo, 2);
		result += Math.pow(centroid.loudness - song.loudness, 2);
		result += Math.pow(centroid.mode - song.mode, 2);
		result += Math.pow(centroid.danceability - song.danceability, 2);
		result += Math.pow(centroid.energy - song.energy, 2);
		result += Math.pow(centroid.instrumentalness - song.instrumentalness, 2);
		result += Math.pow(centroid.acousticness - song.acousticness, 2);
		
		result = Math.sqrt(result);
		
		return result;
	}
}
