package test_project.test_project;

import java.util.*;

public class Main {

	static HashMap<String, Integer> global_label_counter = new HashMap<String, Integer>();
	static int correct_label = 0;
	static int wrong_label = 0;
	static float number_of_songs = 0;
	
	public static void main(String[] args) {
		
		String VISUALIZE_CLUSTERS_FILENAME = "visualize_clusters.txt";
		String USABLE_CLUSTERS_FILENAME = "usable_clusters.csv";
		
		//Collect data and clusters
		ArrayList<Song> song_dataset = DataManager.LoadSongs("full-dataset.csv");
		//String[] d = new String[]{"not_an_id", "not_a_label", "Not_a_mood", "blabla"};
		//MyPlaylist full_data = new MyPlaylist(song_dataset, d);

		ArrayList<MyPlaylist> playlist_dataset = DataManager.LoadPlaylists("playlist-normalized_2.csv");
		ArrayList<MyPlaylist>small_playlist = DataManager.LoadPlaylists("playlist_user_moods.csv");
		ArrayList<MyPlaylist>generation_playlist = DataManager.LoadPlaylists("playlist-normalized_2.csv");
		ArrayList<ClusterMean> clusters = null;
		ArrayList<ClusterMean> copy_cluster = null;
		ArrayList<ClusterMean> selected_cluster = null;
		//ArrayList<MyPlaylist> myPlaylist_dataset = DataManager.LoadPlaylists("playlist-normalized_2.csv");
		
		
		
		if(dataSaverLoader.DoesFileExist(USABLE_CLUSTERS_FILENAME))
		{
			clusters = DataManager.LoadClusters("usable_clusters_2.csv");
			//CalculateNearestNeighborPlaylist(full_data, clusters);*/
			for(MyPlaylist playlist : generation_playlist)
			{
				CalculateNearestNeighborPlaylist(playlist, clusters);
				number_of_songs += playlist.my_songs.size();
			}
			System.out.println("Correct: " + correct_label + " - wrong: " + wrong_label + " - percent correct: " + (correct_label/number_of_songs)*100 );

		}
		else
		{
			clusters = KMeans.KMeansPartition(100, song_dataset);
			copy_cluster = (ArrayList<ClusterMean>)clusters.clone();
			selected_cluster = new ArrayList<ClusterMean>();
			int playlist_repetitions = 5;
			for(MyPlaylist playlist : playlist_dataset)
			{
				for(int i = 0; i < playlist_repetitions; i++)
				{
					PlaylistDistanceToCluster(playlist, copy_cluster, selected_cluster, true);
					ResetClusterCounter(copy_cluster);
				}
				//CentroidToCentroidDistance(playlist, copy_cluster, selected_clusters, true);
			}

			for(ClusterMean c : selected_cluster)
			{
				System.out.println("Labels: " + c.label + "; Moods: " + c.mood + System.getProperty("line.separator"));
				dataSaverLoader.saveFile(USABLE_CLUSTERS_FILENAME, c.SaveFormat(), true);
				dataSaverLoader.saveFile(VISUALIZE_CLUSTERS_FILENAME, c.toString(), true);
			}

			CalculateNearestNeighborPlaylist(small_playlist.get(0), selected_cluster);

		}


		System.out.println("DONE");
		
		/*
		ArrayList<ClusterMean> clust = KMeans.KMeansPartition(50, song_dataset);
		ArrayList<ClusterMean> copy_cluster = (ArrayList<ClusterMean>)clust.clone();
		ArrayList<ClusterMean> selected_clusters = new ArrayList<ClusterMean>();
		
		for(MyPlaylist myPlaylist : myPlaylist_dataset)
		{
			PlaylistDistanceToCluster(myPlaylist, copy_cluster, selected_clusters, true);
			ResetClusterCounter(copy_cluster);
			//CentroidToCentroidDistance(myPlaylist, copy_cluster, selected_clusters, true);
		}

		for(ClusterMean c : selected_clusters)
		{
			System.out.println("Labels: " + c.label + "; Moods: " + c.mood + System.getProperty("line.separator"));
			dataSaverLoader.saveFile(CLUSTERS_FILENAME, c.toString(), true);
		}
		*/
		/*
		//Label clusters
		for(MyPlaylist playlist : myPlaylist_dataset)
		{
			for(Song song : playlist.my_songs)
			{
				DataManager.DistanceToClusters(song);
			}
			
			DataManager.LabelCluster(playlist.playlist_name, playlist.playlist_mood);
		}
		
		DataManager.PrintClusters();
		
		System.out.println("donee");
		*/
	}

	public static void CalculateNearestNeighborPlaylist(MyPlaylist playlist, ArrayList<ClusterMean> my_cluster)
	{
		int correct = 0;
		int wrong = 0;
		for(Song song : playlist.my_songs)
		{
			String result = CalculateNearestNeighborSong(song, my_cluster, 1);
			if(result.equals(playlist.playlist_mood)){
				correct++;
				correct_label++;
			}else{
				wrong++;
				wrong_label++;
			}
			System.out.println(playlist.playlist_mood + ";" + song.song_name + "; got label and mood: " + result);
		}
		HashmapPercentage(playlist.my_songs.size(), correct, wrong);
		ResetHashMapCounter();
	}
	
	public static String CalculateNearestNeighborSong(Song song, ArrayList<ClusterMean> my_cluster, int k)
	{
		HashMap<Integer, Double> ordered_distances = CalculateAllDistancesAndOrder(my_cluster, song);
		HashMap<String, Integer> label_counter = new HashMap<String, Integer>();
		
		Integer[] ordered_indices = Arrays.copyOf(ordered_distances.keySet().toArray(), ordered_distances.keySet().toArray().length, Integer[].class);
		
		for(int i = 0; i < k; i++)
		{
			String label = my_cluster.get(ordered_indices[i]).mood.get(0);
			
			if(!label_counter.containsKey(label))
				label_counter.put(label, 1);
			else
				label_counter.put(label, label_counter.get(label) + 1);
		}
		
		label_counter = sortLabelHashMapByValues(label_counter);

		String winner_label = (String) label_counter.keySet().toArray()[0];
		
		if(!global_label_counter.containsKey(winner_label))
			global_label_counter.put(winner_label, 1);
		else
			global_label_counter.put(winner_label, global_label_counter.get(winner_label) + 1);

		/*
		double min_distance = Double.MAX_VALUE;
		ClusterMean selected_cluster = null;
		for(ClusterMean cm : my_cluster)
		{
			double distance = cm.CalculateDistance(song);
			
			if(distance < min_distance)
			{
				selected_cluster = cm;
				min_distance = distance;
			}
		}
		*/
		
		
		//selected_cluster.counter++;
		
		return winner_label;
	}
	
	public static HashMap<Integer, Double> CalculateAllDistancesAndOrder(ArrayList<ClusterMean> my_cluster, Song song)
	{
		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		
		//Fill hashmap with all distances
		for(int i = 0; i < my_cluster.size(); i++)
		{
			result.put(i, my_cluster.get(i).CalculateDistance(song));
		}
		
		LinkedHashMap<Integer, Double> sorted_distance = sortHashMapByValues(result);
		
		return sorted_distance;
	}
	
	public static LinkedHashMap<String, Integer> sortLabelHashMapByValues(
	        HashMap<String, Integer> passedMap) {
		ArrayList<String> mapKeys = new ArrayList<String>(passedMap.keySet());
		ArrayList<Integer> mapValues = new ArrayList<Integer>(passedMap.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);

	    LinkedHashMap<String, Integer> sortedMap =
	        new LinkedHashMap<String, Integer>();

	    Iterator<Integer> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	    	Integer val = valueIt.next();
	        Iterator<String> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	            String key = keyIt.next();
	            Integer comp1 = passedMap.get(key);
	            Integer comp2 = val;

	            if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}
	
	public static LinkedHashMap<Integer, Double> sortHashMapByValues(
	        HashMap<Integer, Double> passedMap) {
		ArrayList<Integer> mapKeys = new ArrayList<Integer>(passedMap.keySet());
		ArrayList<Double> mapValues = new ArrayList<Double>(passedMap.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);

	    LinkedHashMap<Integer, Double> sortedMap =
	        new LinkedHashMap<Integer, Double>();

	    Iterator<Double> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	    	Double val = valueIt.next();
	        Iterator<Integer> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	            Integer key = keyIt.next();
	            Double comp1 = passedMap.get(key);
	            Double comp2 = val;

	            if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}
	
	public static String HashmapPercentage(float size, int correct, int wrong)
	{
		System.out.println("");
		for (Map.Entry<String, Integer> entry : global_label_counter.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    System.out.print(key);
		    System.out.print("; Counter: " + value);
		    float percent = (value * 100.0f)/size;
			System.out.print("; Percent: " + percent);
			System.out.println("");
		}

		System.out.println("Correct: " + correct + " - wrong: " + wrong + " - percent correct: " + (correct/size)*100 );


		System.out.println("");
		return "";
	}
	
	public static void ResetHashMapCounter()
	{
		for (String key : global_label_counter.keySet())
		{
		    global_label_counter.put(key, 0);
		}

	}
	
	public static String CalculatePercentage(float size, ArrayList<ClusterMean> my_cluster)
	{
		System.out.println("");
		
		for (ClusterMean c : my_cluster) {
			System.out.print(c.mood);
			System.out.print(c.label);
			System.out.print(" Counter: " + c.counter);
			float percent = (c.counter * 100.0f)/size;
			System.out.print(" Percent: " + percent);
			System.out.println("");
		}
		System.out.println("");
		//Calculate percentage.
		return "";
	}
	
	public static void CentroidToCentroidDistance(MyPlaylist myPlaylist, ArrayList<ClusterMean> cluster_to_remove, ArrayList<ClusterMean> cluster_to_add, boolean remove_cluster)
	{
		double min_distance = Double.MAX_VALUE;
		ClusterMean selected_cluster = null;
		for(ClusterMean cm : cluster_to_remove)
		{
			double distance = cm.CalculateDistance(myPlaylist.centroid);
			
			if(distance < min_distance)
			{
				selected_cluster = cm;
				min_distance = distance;
			}
		}
		
		selected_cluster.label.add(myPlaylist.playlist_name);
		selected_cluster.mood.add(myPlaylist.playlist_mood);
		cluster_to_add.add(selected_cluster);
		cluster_to_remove.remove(selected_cluster);
	}
	
	public static void PlaylistDistanceToCluster(MyPlaylist myPlaylist, ArrayList<ClusterMean> cluster_to_remove, ArrayList<ClusterMean> cluster_to_add, boolean remove_cluster)
	{
		
		for(Song song : myPlaylist.my_songs)
		{
			Main.SongDistanceToCluster(song, cluster_to_remove);
		}
		
		Main.LabelCluster(myPlaylist.playlist_name, myPlaylist.playlist_mood, cluster_to_remove, cluster_to_add, remove_cluster);
	}
	
	public static void SongDistanceToCluster(Song song, ArrayList<ClusterMean> clust)
	{
		double min_distance = Double.MAX_VALUE;
		ClusterMean selected_cluster = null;
		for(ClusterMean cm : clust)
		{
			double distance = cm.CalculateDistance(song);
			
			if(distance < min_distance)
			{
				selected_cluster = cm;
				min_distance = distance;
			}
		}
		
		selected_cluster.counter++;
	}
	
	public static void LabelCluster(String label, String mood, ArrayList<ClusterMean> cluster_to_remove,  ArrayList<ClusterMean> cluster_to_add, boolean remove_cluster)
	{
		int max_counter = Integer.MIN_VALUE;
		ClusterMean selected_cluster = null;
		
		for(ClusterMean cm : cluster_to_remove)
		{
			if(cm.counter > max_counter)
			{
				selected_cluster = cm;
				max_counter = cm.counter;
			}
		}
		
		selected_cluster.label.add(label);
		selected_cluster.mood.add(mood);
		cluster_to_add.add(selected_cluster);
		if(remove_cluster)
		{
			cluster_to_remove.remove(selected_cluster);
		}
	}
	
	public static void ResetClusterCounter(ArrayList<ClusterMean> clust)
	{
		for(ClusterMean cm : clust)
		{
			cm.counter = 0;
		}
	}
}
