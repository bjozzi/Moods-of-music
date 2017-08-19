package test_project.test_project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class DataManager {
	
	public static ArrayList<Float> student_attributes_mean 					= new ArrayList<Float>();
	public static ArrayList<Float> student_attributes_standard_deviation 	= new ArrayList<Float>();
	public static ArrayList<ClusterMean>  clusters = new ArrayList<ClusterMean>();
	
	public static ArrayList<ClusterMean> LoadClusters(String data_to_load)
	{
		//TODO: Check if file exists.
	
		
		
		
		
		
		
		ArrayList<ClusterMean> datalist = new ArrayList<ClusterMean>();
		ArrayList<Song> cluster_songs = new ArrayList<Song>();
		try
		{
			String[][] data = dataSaverLoader.readDataFile(data_to_load,";", "-", false);
			ClusterMean my_cluster = null;
			for(String[] cluster_data : data)
			{
				if(cluster_data[0].contains("CLUSTER START"))
				{
					//Starting cluster
					my_cluster = new ClusterMean();
				}
				else if(cluster_data[0].contains("CLUSTER END"))
				{
					//closing cluster
					my_cluster.cluster_members = cluster_songs;
					datalist.add(my_cluster);
				}
				else if(cluster_data[0].equals("song"))
				{
					//this is a song
					Song song_to_add = new Song(cluster_data, true, true);
					cluster_songs.add(song_to_add);
				}
				else if(cluster_data[0].equals("centroid"))
				{
					//This is the centroid
					Song centroid = new Song(cluster_data, false, true);
					my_cluster.centroid = centroid;
				}
				else
				{
					//This is the label and mood information of the clusterr
					my_cluster.LoadLabelAndMood(cluster_data);
				}
			}
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return datalist;
	}
	
	public static ArrayList<Song> LoadSongs(String data_to_load)
	{
		ArrayList<Song> datalist = new ArrayList<Song>();
		ArrayList<ArrayList<Song>> cluster_songs = new ArrayList<ArrayList<Song>>();
		HashMap<String, Integer> ClustersAndIndices = new HashMap<String, Integer>();
		String cluster = "";
		//First step load in data from the text file.
		try {
			String[][] data = dataSaverLoader.readDataFile(data_to_load,";", "-", true);
						
			ArrayList<Integer> games_played_accumulative = new ArrayList<Integer>();
			int counter = 0;
			for(String[] song : data)
			{
				Song song_to_add = new Song(song, true);
				datalist.add(song_to_add);
				System.out.println("song: " + song[8] + ", id: " + counter);
				counter++;
				cluster = song[13] + "," + song[14];
				if(ClustersAndIndices.containsKey(cluster))
				{
					cluster_songs.get(ClustersAndIndices.get(cluster)).add(song_to_add);
				}
				else
				{
					cluster_songs.add(new ArrayList<Song>());
					cluster_songs.get(cluster_songs.size() - 1).add(song_to_add);
					ClustersAndIndices.put(cluster, cluster_songs.size() - 1);
				}
			}

			for(ArrayList<Song> c : cluster_songs)
			{
				ClusterMean cm = new ClusterMean(c, c.get(0).cluster_x, c.get(0).cluster_y);
				//System.out.println("cluster x = " + cm.x + ", cluster y = " + cm.y);
				clusters.add(cm);
			}
			
			System.out.println(clusters.size());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return datalist;
	}
	
	public static ArrayList<MyPlaylist> LoadPlaylists(String data_to_load)
	{
		ArrayList<MyPlaylist> datalist = new ArrayList<MyPlaylist>();

		//First step load in data from the text file.
		try {
			String[][] data = dataSaverLoader.readDataFile(data_to_load,";", "-", true);
			ArrayList<Song> playlist_songs = new ArrayList<Song>();
			String[] primary_pi = new String[4];
			primary_pi[0] = "not a playlist id";
			boolean first_time = true;
			int counter = 0;
			
			for(String[] playlist : data)
			{
				String[] playlist_info = PlaylistInfo(playlist);
				String[] song = ComposeSongFromPlaylist(playlist);
				
				if(playlist_info[0].equals(primary_pi[0]))
				{
					Song song_to_add = new Song(song, false);
					playlist_songs.add(song_to_add);
				}
				else
				{
					if(!first_time)
					{
						MyPlaylist pl = new MyPlaylist(playlist_songs, primary_pi);
						datalist.add(pl);
					}
					else
					{
						first_time = false;
					}
					
					primary_pi = playlist_info;
					playlist_songs = new ArrayList<Song>();
					System.out.println();
					System.out.println("MyPlaylist name: " + primary_pi[2]);
					System.out.println();
					Song song_to_add = new Song(song, false);
					playlist_songs.add(song_to_add);
				}
				
				System.out.println("Song name: " + song[8] + "; id: " + counter);
				
				counter++;
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return datalist;
		
	}
	
	public static String[] PlaylistInfo(String[] playlist_song)
	{
		String[] s = new String[playlist_song.length];
		
		s[0] = playlist_song[6];
		s[1] = playlist_song[7];
		s[2] = playlist_song[8];
		s[3] = playlist_song[9];
		
		return s;
	}
	
	public static String[] ComposeSongFromPlaylist(String[] playlist_song)
	{
		String[] s = new String[playlist_song.length];
		
		s[0] = playlist_song[0];
		s[1] = playlist_song[1];
		s[2] = playlist_song[2];
		s[3] = playlist_song[3];
		s[4] = playlist_song[4];
		s[5] = playlist_song[5];
		s[6] = playlist_song[10];
		s[7] = playlist_song[11];
		s[8] = playlist_song[12];
		s[9] = playlist_song[13];
		s[10] = playlist_song[14];
		s[11] = playlist_song[15];
		s[12] = playlist_song[16];
		
		return s;
	}
	
	public static void DistanceToClusters(Song song)
	{
		double min_distance = Double.MAX_VALUE;
		ClusterMean selected_cluster = null;
		for(ClusterMean cm : clusters)
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
	
	public static void LabelCluster(String label, String mood)
	{
		int max_counter = Integer.MIN_VALUE;
		ClusterMean selected_cluster = null;
		
		for(ClusterMean cm : clusters)
		{
			if(cm.counter > max_counter)
			{
				selected_cluster = cm;
				max_counter = cm.counter;
			}
		}
		
		selected_cluster.label.add(label);
		selected_cluster.mood.add(mood);
		
		ResetClusterCounter();
	}
	
	public static void ResetClusterCounter()
	{
		for(ClusterMean cm : clusters)
		{
			cm.counter = 0;
		}
	}
	
	public static void PrintClusters()
	{
		for(ClusterMean cm : clusters)
		{
			System.out.println();
			System.out.println("Cluster x: " + cm.x + ", Cluster y: " + cm.y + ", containing: " + cm.cluster_members.size() + " songs");
			System.out.println(cm.label);
			System.out.println(cm.mood);
		}
	}
	
	/*
	public static int CalculateMode(ArrayList<Student> dataset, int att_to_calculate)
	{
		//int return_value = 0;
		int[] mode;
		
		switch(att_to_calculate)
		{
		case 0:
			
			mode = new int[2];
			for(Student s : dataset)
			{
				mode[s.gender.binary_value]++;
			}
			break;
		case 1:
			mode = new int[5];
			for(Student s : dataset)
			{
				mode[s.degree.nominal_attribute.ordinal()]++;
			}
			break;
		case 2:
			mode = new int[5];
			for(Student s : dataset)
			{
				mode[s.course_motivation.nominal_attribute.ordinal()]++;
			}
			break;
		case 3:
			mode = new int[6];
			for(Student s : dataset)
			{
				mode[s.game_time.ordinal_attribute.ordinal()]++;
			}
			break;
		default:
			return -1;
		}
		
		int index = 0;
		int max = 0;
		
		for(int i = 0; i < mode.length; i++)
		{
			if(mode[i] > max)
			{
				index = i;
				max = mode[i];
			}
		}
		
		return index;
		
	}

	public static Object AllSameClass(ArrayList<Student> dataset) {
		GamesPlayedValue s = dataset.get(0).games_played.discrete_value;
		
		for(Student student : dataset)
		{
			if(!student.games_played.discrete_value.equals(s))
				return null;
		}
		
		return s;
	}

	public static Object MainstreamClass(ArrayList<Student> dataset) {
		
		int[] values = new int[GamesPlayedValue.values().length];
		
		for(Student student : dataset)
		{
			values[student.games_played.discrete_value.ordinal()]++;
		}
		
		int index = 0;
		int max = 0;
		
		for(int i = 0; i < values.length; i++)
		{
			if(values[i] > max)
			{
				index = i;
				max = values[i];
			}
		}
		
		//Arrays.sort(values);
		
		return GamesPlayedValue.values()[index];
		
	}

	public static ArrayList<ArrayList<Student>> GenerateDataSetFromAttribute(ArrayList<Student> dataset, Object selected_attribute) {
		// TODO Auto-generated method stub
		
		Object[] possible_values = getAttributeValues(selected_attribute);
		int possible_values_length = possible_values.length;
		ArrayList<ArrayList<Student>> mini_datasets = new ArrayList<ArrayList<Student>>(possible_values_length);
		int[] values = new int[possible_values_length];
		
		//Maybe initialize each arraylist "test"
		for(int i = 0; i < possible_values_length; ++i)
		{
			mini_datasets.add(new ArrayList<Student>());
		}
		
		for(Student student : dataset)
		{
			int student_value = student.getKeyAttributeValue(selected_attribute);
			mini_datasets.get(student_value).add(student);
		}

		return mini_datasets;

	}

	public static int[] ClassesRepetitionInDataSet(ArrayList<Student> dataset) {
		
		Object[] possible_classes = VideoGames_Time.GameTimeValue.values();
		int possible_classes_length = possible_classes.length;
		int[] results = new int[possible_classes_length];
		
		for(Student tuple : dataset)
		{
			results[tuple.game_time.ordinal_attribute.ordinal()]++;
		}
		
		return results;
	}
	*/

}
