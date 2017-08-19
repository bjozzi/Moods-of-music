package test_project.test_project;

public class Song {
	String uri;
	String artist;
	String song_name;
	
	double danceability;
	double energy;
	double key;
	double tempo;
	double loudness;
	double mode;
	double instrumentalness;
	double acousticness;
	
	float cluster_x;
	float cluster_y;
	
	public Song (String [] values, boolean clustered)
	{
		key 				= Double.parseDouble(values[2]);
		tempo 				= Double.parseDouble(values[3]);
		loudness 			= Double.parseDouble(values[4]);
		mode 				= Double.parseDouble(values[5]);
		uri 				= values[6];
		artist 				= values[7];
		song_name 			= values[8];
		danceability 		= Double.parseDouble(values[9]);
		energy 				= Double.parseDouble(values[10]);
		instrumentalness 	= Double.valueOf(values[11]);
		acousticness 		= Double.valueOf(values[12]);
		
		if(clustered)
		{
			cluster_x 			= Float.parseFloat(values[13]);
			cluster_y 			= Float.parseFloat(values[14]);
		}
	}
	
	public Song (String [] values, boolean clustered, boolean for_cluster)
	{
		key 				= Double.parseDouble(values[1]);
		tempo 				= Double.parseDouble(values[2]);
		loudness 			= Double.parseDouble(values[3]);
		mode 				= Double.parseDouble(values[4]);
		uri 				= values[5];
		artist 				= values[6];
		song_name 			= values[7];
		danceability 		= Double.parseDouble(values[8]);
		energy 				= Double.parseDouble(values[9]);
		instrumentalness 	= Double.valueOf(values[10]);
		acousticness 		= Double.valueOf(values[11]);
		
		if(clustered)
		{
			cluster_x 			= Float.parseFloat(values[12]);
			cluster_y 			= Float.parseFloat(values[13]);
		}
	}
	
	public Song(double _key, 
				double _tempo, 
				double _loudness, 
				double _mode, 
				double _danceability, 
				double _energy, 
				double _instrumentalness, 
				double _acousticness,
				float _cluster_x,
				float _cluster_y)
	{
		key = _key;
		tempo = _tempo;
		loudness = _loudness;
		mode = _mode;
		danceability = _danceability;
		energy = _energy;
		instrumentalness = _instrumentalness;
		acousticness = _acousticness;
		cluster_x = _cluster_x;
		cluster_y = _cluster_y;
	}
	
	public String SaveFormat()
	{
		String delimiter = ";";
		return 	key + delimiter +
				tempo + delimiter +
				loudness + delimiter +
				mode + delimiter +
				uri + delimiter +
				artist + delimiter +
				song_name + delimiter +
				danceability + delimiter +
				energy + delimiter +
				instrumentalness + delimiter +
				acousticness + delimiter +
				cluster_x + delimiter +
				cluster_y;
	}
	
	@Override
	public String toString() {
		return "Song{" +
				"uri='" + uri + '\'' +
				", artist='" + artist + '\'' +
				", song_name='" + song_name + '\'' +
				", danceability=" + danceability +
				", energy=" + energy +
				", key=" + key +
				", tempo=" + tempo +
				", loudness=" + loudness +
				", mode=" + mode +
				", instrumentalness=" + instrumentalness +
				", acousticness=" + acousticness +
				", cluster_x=" + cluster_x +
				", cluster_y=" + cluster_y +
				'}';
	}
}
