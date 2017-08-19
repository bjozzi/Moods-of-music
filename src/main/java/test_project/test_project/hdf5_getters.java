/*


Thierry Bertin-Mahieux (2010) Columbia University
tb2332@columbia.edu


This code contains a set of getters functions to access the fields
from an HDF5 song file (regular file with one song or summary file
with many songs) in Java.
The goal is to reproduce the Python getters behaviour.
Our aim is only to show how to use the HDF5 files with Java, the
code is not optimized at all!

NOTE ON 2D ARRAYS: pitches and timbre are supposed to be #segs x 12
They are stored in 1D array by concatenating rows, e.g. elem 20
should be row 1 elem 8.
To get element of row r and column c from an array a, call a[r*12+c]

To get a faster code, you should load metadata/songs and analysis/songs
only once, see the Matlab code in /MatlabSrc for inspiration.

This is part of the Million Song Dataset project from
LabROSA (Columbia University) and The Echo Nest.


Copyright 2010, Thierry Bertin-Mahieux

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package test_project.test_project;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.PlaylistRequest;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//import ncsa.hdf.hdf5lib.*;


/**
 * Class containing static methods to open a HDF5 song file and access its content.
 */
public class hdf5_getters
{

	/****************************************** MAIN *****************************************/
/*    try (Stream<Path> paths = Files.walk(Paths.get(filename))) {
    paths.filter(x -> !Files.isDirectory(x)).forEach(x -> printSong(x.toString()));
} catch (IOException e) {
    e.printStackTrace();
}*/

	static ArrayList<String> artists = new ArrayList<String>();
	static ArrayList<String> Songs = new ArrayList<String>();
	static String SEPARATION_CHARACTER=";";
	static String FILENAME = "mood_playlist_valence.csv";
	static String ARTIST_FILENAME = "artist_dataset.csv";
	static boolean SAVE_DATA=true;

	public static void main(String[] args) throws IOException, InterruptedException {

		if(SAVE_DATA)
		{
			//getSongDetails();
			try {
				getPlaylistData();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		else
		{
			System.out.println("Not saving anything");
			System.out.println("files already complete.");
		}
	}

	private static void getPlaylistData() throws InterruptedException {



		String tuple = "";
		File f = new File("my-data/" + FILENAME);
		if(!f.exists())
		{

			tuple += "PLAYLISTID" + SEPARATION_CHARACTER;
			tuple += "PLAYLIST" + SEPARATION_CHARACTER;
			tuple += "PLAYLISTDESCRIPTION" + SEPARATION_CHARACTER;
			tuple += "URI" + SEPARATION_CHARACTER;
			tuple += "ARTIST" + SEPARATION_CHARACTER;
			tuple += "SONG" + SEPARATION_CHARACTER;
			tuple += "DURATION" + SEPARATION_CHARACTER;
			tuple += "POPULARITY" + SEPARATION_CHARACTER;
			tuple += "DANCEABILITY" + SEPARATION_CHARACTER;
			tuple += "ENERGY" + SEPARATION_CHARACTER;
			tuple += "KEY" + SEPARATION_CHARACTER;
			tuple += "TEMPO" + SEPARATION_CHARACTER;
			tuple += "LOUDNESS" + SEPARATION_CHARACTER;
			tuple += "MODE" + SEPARATION_CHARACTER;
			tuple += "INSTRUMENTALNESS" + SEPARATION_CHARACTER;
			tuple += "ACOUSTICNESS" + SEPARATION_CHARACTER;
			tuple += "VALENCE" + SEPARATION_CHARACTER;
			tuple += "LIVENESS" + SEPARATION_CHARACTER;
			tuple += "SPEECHINESS";


			dataSaverLoader.saveFile(FILENAME, tuple, true);
		}

		//We get all the artists
		f = new File("my-data/" + ARTIST_FILENAME);

		final String clientId = "c0cb649875b84d2693adccecc708a99b";
		final String clientSecret = "5e621c2e761b4a83b87b07f26bc83c3c";

		final API2 api = APIAuth(clientId, clientSecret);


		ArrayList<String> playlists = new ArrayList<String>();
		playlists = getPlaylists();
		//songs from 2000 playlists.add("spotify_denmark:35FeJC6bSj8GnaPcfH9Xqa");
		/*playlists.add("spotify:0GAdYvlIkfKBUnuHbQAT5c"); // 50
		playlists.add("spotify:0ApdHY8K71F9WrIWbgiI2G"); // 60
		playlists.add("spotify:00K2xasnm9pDQk53SzNCht"); // 70
		playlists.add("spotify:5wDvHZhgPBlWyDEZ3jSMF4"); // 80
		playlists.add("spotify:2uAichKSjJSyrmal8Kb3W9"); // 90
		playlists.add("spotify:3UybCDm2O3JPQChfCG02EG"); // 00*/
		int counter2 = 0;
		for (counter2 = 21; counter2 < playlists.size(); counter2++)
		{

			String owner = playlists.get(counter2).split(":")[0];
			String playlistId= playlists.get(counter2).split(":")[1];
			final PlaylistRequest request = api.getPlaylist(owner, playlistId).build();

			//System.out.println(request2.toStringWithQueryParameters());
			try {
				System.out.println(request.toString());
				final Playlist playlist = request.get();

				System.out.println("Retrieved playlist " + playlist.getName());
				System.out.println(playlist.getDescription());
				System.out.println("It contains " + playlist.getTracks().getTotal() + " tracks");
				List<PlaylistTrack> tracks = playlist.getTracks().getItems();
				int counter = 0;


				for(PlaylistTrack p : tracks )
				{
					if(counter > 100)
						break;

					Track t = p.getTrack();
					String id = t.getUri().split(":")[2];

					if(Songs.contains(t.getName()))
						continue;

					Songs.add(t.getName());
					tuple = "";
					tuple += playlistId + SEPARATION_CHARACTER;
					tuple += "\"" + playlist.getName() + "\"" +  SEPARATION_CHARACTER;
					tuple += "\"" + playlist.getDescription() +"\""+ SEPARATION_CHARACTER;
					tuple += t.getUri() + SEPARATION_CHARACTER;
					tuple += "\""+ t.getArtists().get(0).getName() +"\""+ SEPARATION_CHARACTER;
					tuple += "\""+t.getName() +"\""+ SEPARATION_CHARACTER;
					tuple += t.getDuration() + SEPARATION_CHARACTER;
					tuple += t.getPopularity() + SEPARATION_CHARACTER;

					//We get audiofeature of song.
					AudioFeatureRequest request1 = api.getAudioFeature(id).build();

					try {
						printAudioFeatures(tuple, request1);

					} catch (Exception e) {
						System.out.println("AudioFeature went wrong!" + e.getMessage());
						if(e.getMessage().equals("429")) {
							System.out.println(t.getUri());
							TimeUnit.SECONDS.sleep(30);
						}
					}

					counter++;

				}

			} catch (Exception e) {
				System.out.println("MyPlaylist went wrong!" + e.getMessage());
				if(e.getMessage().equals("429"))
					System.out.println(playlistId);
				TimeUnit.SECONDS.sleep(30);
			}

		}
	}

	private static void printAudioFeatures(String tuple, AudioFeatureRequest request1) throws IOException, WebApiException {
		final AudioFeature audioFeatureResult = request1.get();

		tuple += audioFeatureResult.getDanceability() + SEPARATION_CHARACTER;
		tuple += audioFeatureResult.getEnergy() + SEPARATION_CHARACTER;
		tuple += audioFeatureResult.getKey() + SEPARATION_CHARACTER;
		tuple += audioFeatureResult.getTempo() + SEPARATION_CHARACTER;
		tuple += audioFeatureResult.getLoudness() + SEPARATION_CHARACTER;
		tuple += audioFeatureResult.getMode() + SEPARATION_CHARACTER;
		tuple += audioFeatureResult.getInstrumentalness() + SEPARATION_CHARACTER;
		tuple += audioFeatureResult.getAcousticness() + SEPARATION_CHARACTER;
		tuple += audioFeatureResult.getValence() + SEPARATION_CHARACTER;
		tuple += audioFeatureResult.getLiveness() + SEPARATION_CHARACTER;
		tuple += audioFeatureResult.getSpeechiness();
		dataSaverLoader.saveFile(FILENAME, tuple, true);
	}

	private static API2 APIAuth(String clientId, String clientSecret) {
		final API2 api = API2.builder()
				.clientId(clientId)
				.clientSecret(clientSecret)
				.build();

		//We authenticate.

	    	/* Create a request object. */
		final ClientCredentialsGrantRequest request = api.clientCredentialsGrant().build();

	    	/* Use the request object to make the request, either asynchronously (getAsync) or synchronously (get) */
		final SettableFuture<ClientCredentials> responseFuture = request.getAsync();

	    	/* Add callbacks to handle success and failure */
		Futures.addCallback(responseFuture, new FutureCallback<ClientCredentials>() {
			public void onSuccess(ClientCredentials clientCredentials) {
	    	    /* The tokens were retrieved successfully! */
				System.out.println("Successfully retrieved an access token! " + clientCredentials.getAccessToken());
				System.out.println("The access token expires in " + clientCredentials.getExpiresIn() + " seconds");


	    	    /* Set access token on the Api object so that it's used going forward */
				api.setAccessToken(clientCredentials.getAccessToken());

	    	    /* Please note that this flow does not return a refresh token.
	    	   * That's only for the Authorization code flow */
			}

			public void onFailure(Throwable throwable) {
				System.out.println("error: " + throwable.getMessage());
	    	    /* An error occurred while getting the access token. This is probably caused by the client id or
	    	     * client secret is invalid. */
			}
		});
		return api;
	}

	private static void getSongDetails() throws IOException, InterruptedException {
		int starting_point = 0;
		String filename = "C:\\Users\\Embajador\\Documents\\workspace\\test_project\\MillionSongSubset\\"
				+ "data";
		//filename = "C:\\Users\\User\\Desktop\\MillionSongSubset\\data";
		String tuple = "";
		File f = new File("my-data/" + FILENAME);
		if(!f.exists())
		{

			tuple += "URI" + SEPARATION_CHARACTER;
			tuple += "ARTIST" + SEPARATION_CHARACTER;
			tuple += "SONG" + SEPARATION_CHARACTER;
			tuple += "DURATION" + SEPARATION_CHARACTER;
			tuple += "POPULARITY" + SEPARATION_CHARACTER;
			tuple += "DANCEABILITY" + SEPARATION_CHARACTER;
			tuple += "ENERGY" + SEPARATION_CHARACTER;
			tuple += "KEY" + SEPARATION_CHARACTER;
			tuple += "TEMPO" + SEPARATION_CHARACTER;
			tuple += "LOUDNESS" + SEPARATION_CHARACTER;
			tuple += "MODE" + SEPARATION_CHARACTER;
			tuple += "INSTRUMENTALNESS" + SEPARATION_CHARACTER;
			tuple += "ACOUSTICNESS" + SEPARATION_CHARACTER;
			tuple += "VALENCE" + SEPARATION_CHARACTER;
			tuple += "LIVENESS" + SEPARATION_CHARACTER;
			tuple += "SPEECHINESS";

			dataSaverLoader.saveFile(FILENAME, tuple, true);
		}


		//We load the .csv with the artist names
		String[][] all_artists = dataSaverLoader.readDataFile(ARTIST_FILENAME, SEPARATION_CHARACTER, "-", false);

		final String clientId = "277cf4a53cd649009226e2d5ec676c62";
		final String clientSecret = "154a2fe12e474b8cb876eee8e7aee606";

		final API2 api = APIAuth(clientId, clientSecret);

		int counter2 = 0;
		for (counter2 = 2802; counter2 < all_artists.length; counter2++)
		{
			final TrackSearchRequest request2 = api.searchTracks(all_artists[counter2][0]).market("US").build();

			//System.out.println(request2.toStringWithQueryParameters());
			try {
				final Page<Track> trackSearchResult = request2.get();
				System.out.println("I got " + trackSearchResult.getTotal() + " results!");
				List<Track> tracks = trackSearchResult.getItems();
				int counter = 0;

				for(Track t : tracks )
				{
					if(counter > 100)
						break;

					String id = t.getUri().split(":")[2];

					if(Songs.contains(t.getName()))
						continue;

					Songs.add(t.getName());
					tuple = "";

					tuple += t.getUri() + SEPARATION_CHARACTER;
					tuple += all_artists[counter2][0] + SEPARATION_CHARACTER;
					tuple += t.getName() + SEPARATION_CHARACTER;
					tuple += t.getDuration() + SEPARATION_CHARACTER;
					tuple += t.getPopularity() + SEPARATION_CHARACTER;

					//We get audiofeature of song.
					AudioFeatureRequest request1 = api.getAudioFeature(id).build();

					try {
						printAudioFeatures(tuple, request1);

					} catch (Exception e) {
						System.out.println("Something went wrong!" + e.getMessage());
						if(e.getMessage().equals("429")) {
							System.out.println(t.getUri());
							TimeUnit.SECONDS.sleep(15);
						}
					}

					counter++;

				}

			} catch (Exception e) {
				System.out.println("Something went wrong!" + e.getMessage());
				if(e.getMessage().equals("429")){
					System.out.println("LATER");
					TimeUnit.SECONDS.sleep(15);
				}
			}

		}
	}

	private static ArrayList<String> getPlaylists() {
		ArrayList playlists = new ArrayList();
		playlists.add("sonymusic:6dm9jZ2p8iGGTLre7nY4hf");
		playlists.add("spotify:5eSMIpsnkXJhXEPyRQCTSc");
		playlists.add("spotify:2U3mZqDktE7UJ1gE4eVoUv");
		playlists.add("spotify:5Z9xJvDtHpB6m5zHgJC5zR");
		playlists.add("spotify:4qstWgP2KMRSiTY3a1fF2R");
		playlists.add("spotify:4dgsG6S4O8ZaTFk1gQWCk0");
		playlists.add("sonymusicdenmark:7KVLbszRKLrhCOUIc6v3G4");
		playlists.add("spotify:5tnBjatmVR3VtfVMOpS87h");
		playlists.add("spotify:7BixMZxL4bhgULJQ5wPbUz");
		playlists.add("spotify:0186RkeoJsHWEQy0ssDAus");
		playlists.add("topsify:6Qf2sXTjlH3HH30Ijo6AUp");
		playlists.add("spotify:65V6djkcVRyOStLd8nza8E");
		playlists.add("sonymusicdenmark:5OPGPy507sFtOh6YsYWqR0");
		playlists.add("spotify:4jCr1WuRoFiUbcqlja4dWR");
		playlists.add("spotify:6wJ5YGQ6LDLNQqW1QhSDGX");
		playlists.add("spotify_denmark:2sAFTMQPzajwVmK95OIHI4");
		playlists.add("sonymusicdenmark:5Yb3kwhqdvB207sHBclVCv");
		playlists.add("spotify_denmark:5DX05geTnrkKInGl59bG6r");
		playlists.add("spotify:0ExbFrTy6ypLj9YYNMTnmd");
		playlists.add("spotify:1oXl0OHlE1mPDChMa8Y0Ax");
		playlists.add("spotify:63dDpdoVHvx5RkK87g4LKk");

		playlists.add("playlistmenorway:5sYv1lcTkKMo33cvmJL7DK");
		playlists.add("iloveplaylists:1V93SRHKAhfJ83uFY8YtAg");
		playlists.add("minnakinkku:6dlpROv62b9VhOzZ6eUab5");
		playlists.add("gmcauchi94:6kKert9woW6jsyXFRu2K1c");
		playlists.add("gkraft:5wjeLEA6Sn4TuyAlUwLGl7");
		playlists.add("kinehund:0v6MmRkqD3aelsmP5ZuL02");
		playlists.add("katieud07:6ax84ovki69cW938DivI38");

		return playlists;
	}





}