package test_project.test_project;

import java.util.ArrayList;

public class MyPlaylist {

	ArrayList<Song> my_songs;
	
	String playlist_id;
	String playlist_mood;
	String playlist_name;
	String playlist_description;
	
	public Song centroid;
	
	public MyPlaylist(ArrayList<Song> songs, String[] info)
	{
		my_songs = songs;
		
		playlist_id = info[0];
		playlist_mood = info[1];
		playlist_name = info[2];
		playlist_description = info[3];
		CalculateCentroid();
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
		
		int size = my_songs.size();
		
		for(Song song : my_songs)
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

		centroid = new Song(key, tempo, loudness, mode, danceability, energy, instrumentalness, acousticness, 0,0);
	}
}
