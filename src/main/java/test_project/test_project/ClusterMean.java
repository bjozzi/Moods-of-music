package test_project.test_project;

import java.util.ArrayList;

public class ClusterMean {

	public ArrayList<Song> cluster_members = new ArrayList<Song>();
	public Song centroid;
	public float x;
	public float y;
	
	public ArrayList<String> label = new ArrayList<String>();
	public ArrayList<String> mood = new ArrayList<String>();
	
	public int counter = 0;
	
	public ClusterMean(ArrayList<Song> cluster_data, float _x, float _y)
	{
		cluster_members = cluster_data;
		x = _x;
		y = _y;
		CalculateCentroid();
	}
	
	public ClusterMean(Song song)
	{
		centroid = song;
		x = 0;
		y = 0;
	}
	
	public ClusterMean()
	{
		x = 0;
		y = 0;
	}
	
	public void LoadLabelAndMood(String[] data)
	{
		label.add(data[0]);
		mood.add(data[1]);
	}
	
	public void CalculateCentroid()
	{
		double danceability = 0.0;
		double energy = 0.0;
		double key = 0.0;
		double tempo = 0.0;
		double loudness = 0.0;
		double mode = 0.0;
		double instrumentalness = 0.0;
		double acousticness = 0.0;
		int size = cluster_members.size();
		
		for(Song song : cluster_members)
		{
			danceability += song.danceability;
			energy += song.energy;
			key += song.key;
			tempo += song.tempo;
			loudness += song.loudness;
			mode += song.mode;
			instrumentalness += song.instrumentalness;
			acousticness += song.acousticness;

		}
		danceability /= size;
		energy /= size;
		key /= size;
		tempo /= size;
		loudness /= size;
		mode /= size;
		instrumentalness /= size;
		acousticness /= size;

		centroid = new Song(key, tempo, loudness, mode, danceability, energy, instrumentalness, acousticness, x, y);
	}
	
	public double CalculateDistance(Song song)
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
	
	public String SaveFormat()
	{
		String delimiter = ";";
		String save_string = "CLUSTER START" + System.getProperty("line.separator");
		save_string += label.get(0) + delimiter + mood.get(0) + System.getProperty("line.separator");
		save_string += "centroid" + delimiter + centroid.SaveFormat() + System.getProperty("line.separator");
		for(Song song : this.cluster_members)
		{
			save_string += "song" + delimiter + song.SaveFormat() + System.getProperty("line.separator");
		}
		save_string += "CLUSTER END";
		
		return save_string;
	}
	

	@Override
	public String toString() {
		String toPrintString = "-----------------------------------CLUSTER START------------------------------------------" + System.getProperty("line.separator");
		toPrintString += "Labels: " + label + "; Moods: " + mood + System.getProperty("line.separator");
		toPrintString += "Centroid: "+this.centroid.toString() + System.getProperty("line.separator");
		for(Song i : this.cluster_members)
		{
			toPrintString += i.toString() + System.getProperty("line.separator");
		}
		toPrintString += "-----------------------------------CLUSTER END-------------------------------------------" + System.getProperty("line.separator");
		return toPrintString;
	}
}
