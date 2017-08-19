package test_project.test_project;

import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.PlaylistRequest;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.ClientCredentials;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.Track;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	final String clientId = "e824d11e5fb94d55a91ea55361769be2";
    	final String clientSecret = "a87fcff127184c77831acc8dfd3a9dfa";

    	final Api api = Api.builder()
    	  .clientId(clientId)
    	  .clientSecret(clientSecret)
    	  .build();

    	/* Create a request object. */
    	final ClientCredentialsGrantRequest request = api.clientCredentialsGrant().build();

    	/* Use the request object to make the request, either asynchronously (getAsync) or synchronously (get) */
    	final SettableFuture<ClientCredentials> responseFuture = request.getAsync();

    	/* Add callbacks to handle success and failure */
    	/*Futures.addCallback(responseFuture, new FutureCallback<ClientCredentials>() {
    	  public void onSuccess(ClientCredentials clientCredentials) {
    	    *//* The tokens were retrieved successfully! *//*
    	    System.out.println("Successfully retrieved an access token! " + clientCredentials.getAccessToken());
    	    System.out.println("The access token expires in " + clientCredentials.getExpiresIn() + " seconds");

    	    *//* Set access token on the Api object so that it's used going forward *//*
    	    api.setAccessToken(clientCredentials.getAccessToken());

    	    *//* Please note that this flow does not return a refresh token.
    	   * That's only for the Authorization code flow *//*
    	  }

    	  public void onFailure(Throwable throwable) {
    		  System.out.println("error: " + throwable.getMessage());
    	    *//* An error occurred while getting the access token. This is probably caused by the client id or
    	     * client secret is invalid. *//*
    	  }
    	});*/
    	
    	final PlaylistRequest request1 = api.getPlaylist("nellaloveklara", "0vDWTzHaumCDIgP7Tt5Q7e").build();
    	System.out.println(request1.toStringWithQueryParameters());
    	try {
    	   final Playlist playlist = request1.get();

    	   System.out.println("Retrieved playlist " + playlist.getName());
    	   System.out.println(playlist.getDescription());
    	   System.out.println("It contains " + playlist.getTracks().getTotal() + " tracks");

    	} catch (Exception e) {
    	   System.out.println("Something went wrong!" + e.getMessage());
    	}

    	final TrackSearchRequest request2 = api.searchTracks("Daft Punk").market("US").build();
    		
    	System.out.println(request2.toStringWithQueryParameters());
    	try {
    	   final Page<Track> trackSearchResult = request2.get();
    	   System.out.println("I got " + trackSearchResult.getTotal() + " results!");
    	   List<Track> tracks = trackSearchResult.getItems();
    	   for(Track t : tracks )
    	   {
    		   System.out.println(t.getUri());
    	   }
    	   
    	} catch (Exception e) {
    	   System.out.println("Something went wrong!" + e.getMessage());
    	}
	    	
    	/*
    	
    	//Api api = Api.DEFAULT_API; 
    	
    	Api api = Api.builder()
    			  .clientId("e824d11e5fb94d55a91ea55361769be2")
    			  .clientSecret("a87fcff127184c77831acc8dfd3a9dfa")
    			  .redirectURI("http://localhost:8888/callback")
    			  .build();
    	
    	final PlaylistRequest request = api.getPlaylist("nellaloveklara", "0vDWTzHaumCDIgP7Tt5Q7e").build();

    	try {
    	   final MyPlaylist playlist = request.get();

    	   System.out.println("Retrieved playlist " + playlist.getName());
    	   System.out.println(playlist.getDescription());
    	   System.out.println("It contains " + playlist.getTracks().getTotal() + " tracks");

    	} catch (Exception e) {
    	   System.out.println("Something went wrong!" + e.getMessage());
    	}
    	*/
    	/*
    	//api.authorizationCodeGrant("BQB5Mw9-S9Qz3NzRFv1tGkmUCFpM270xa42NHKuJVBy2jh_PrvLPEQaZ48ci63iJnGM3sgVwcuGFWdH5W4tH-fiPUlRxFLDQ_xzqzl8gjVYWikzB6XKCVGfWDFIeEe6mPI4yUgzl4zDik71sQc-A");
    	final TrackSearchRequest request = api.searchTracks("Daft Punk").market("US").build();

    	try {
    	   final Page<Track> trackSearchResult = request.get();
    	   System.out.println("I got " + trackSearchResult.getTotal() + " results!");
    	   List<Track> tracks = trackSearchResult.getItems();
    	   for(Track t : tracks )
    	   {
    		   System.out.println(t.getUri());
    	   }
    	   
    	} catch (Exception e) {
    	   System.out.println("Something went wrong!" + e.getMessage());
    	}
    	*/
    	/*
    	// Create an API instance. The default instance connects to https://api.spotify.com/.
    	Api api = Api.DEFAULT_API; 

    	// Create a request object for the type of request you want to make
    	AlbumRequest request = api.getAlbum("0dEIca2nhcxDUV8C5QkPYb").build();

    	// Retrieve an album
    	try {
    	  Album album = request.get();
    	  // Print the genres of the album
    	  List<String> genres = album.getGenres(); 
    	  for (String genre : genres) {
    	    System.out.println(genre);
    	  };

    	} catch (Exception e) {
    	  System.out.println("Could not get albums.");
    	}*/
    }
}
