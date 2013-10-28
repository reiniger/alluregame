package com.app.flickr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpException;
import org.xml.sax.SAXException;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;

public class FlickrConnection {
	
	private Flickr flickr;
	private String keyAPI;
	private String secretAPI;
	
	FlickrConnection(String key, String secret) throws ParserConfigurationException{
		keyAPI = key;
		secretAPI = secret;
	    String svr = "www.flickr.com";
	    REST rest = new REST();
	    rest.setHost(svr);
	    
	    //initialize Flickr object with key and rest
	    flickr = new Flickr(keyAPI, secretAPI, rest);
	    Flickr.debugStream = false;
	}
	
	public String[] getTagedPhotos(int amountOfPhotos, String[] tags) throws IOException, SAXException, FlickrException{
		//initialize SearchParameter object, this object stores the search keyword
	    SearchParameters searchParams = new SearchParameters();
	    searchParams.setSort(SearchParameters.INTERESTINGNESS_DESC);
	   
	    //Setting tag keyword array
	    searchParams.setTags(tags);

	    //Initialize PhotosInterface object
	    PhotosInterface photosInterface = flickr.getPhotosInterface();
	    
	    //Execute search with entered tags
	    /*
	     * SearchParameters: 
	     * params - The search parameters
	     * perPage - The number of photos to show per page
	     * page - The page offset or results from page #
	     */
	    PhotoList<Photo> photoList = photosInterface.search(searchParams, amountOfPhotos, 1);
	    
	    //get search result and fetch the photo object and get imag's url
	    String[] urls = new String[amountOfPhotos];
	    if(photoList!= null){
	       //Get search result and check the size of photo result
	       for(int i = 0; i < photoList.size(); i++){
	          //get photo object
	          Photo photo = (Photo)photoList.get(i);
	          
	          //Get url photo 
	          urls[i] = photo.getLargeUrl(); 
	       }
	    }
	    return urls;
	}
	
	public String[] getPopularPhotos(int amountOfPhotos, int year, int month, int day, Set<String> extras) throws FlickrException, HttpException, IOException{
	    //Date
		/*
		 * 	year - the value used to set the YEAR calendar field.
			month - the value used to set the MONTH calendar field. Month value is 0-based. e.g., 0 for January.
			date - the value used to set the DAY_OF_MONTH calendar field.
		 */
		Calendar c = new GregorianCalendar();
		c.set(year, month, day);
		//c.add(Calendar.DAY_OF_YEAR, -1);
		Date date = new Date(c.getTimeInMillis());
		
	    /*
	     *  extras (Optional)
           A comma-delimited list of extra information to fetch for each returned record. 
           Currently supported fields are: description, license, date_upload, date_taken, 
           owner_name, icon_server, original_format, last_update, geo, tags, machine_tags, 
           o_dims, views, media, path_alias, url_sq, url_t, url_s, url_q, url_m, url_n, url_z, 
           url_c, url_l, url_o
	     */
	    
	    //500 photos method
	    //PhotoList<Photo> photoList = flickr.getInterestingnessInterface().getList();
	    
	    //random method
	    PhotoList<Photo> photoList = flickr.getInterestingnessInterface().getList(date, extras, amountOfPhotos, 1);
	    
	    //get search result and fetch the photo object and get imag's url
	    String[] urls = new String[photoList.size()];
	    String[] names = new String[photoList.size()];
	    if(photoList!= null){
	       //Get search result and check the size of photo result
	       for(int i = 0; i < photoList.size(); i++){
	          //get photo object
	          Photo photo = (Photo)photoList.get(i);
	          
	          //Get url photo 
	          urls[i] = photo.getLargeUrl();
	          
	          //Get photo name
	          names[i] = photo.getTitle();
	          System.out.println(photo.getTitle());	  
	       }
	    }
	    return urls;
	}
	
	public static void saveImage(String imageUrl, String destinationFile) throws IOException {
		URL url = new URL(imageUrl);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}
		is.close();
		os.close();
	}
	
	public static void main(String[] args) {
		String key = "ccddcb97e77341fece579964b7d5f522";
		String secret = "372d271099ef4de0";
		try {
			FlickrConnection fc = new FlickrConnection(key, secret);
			
			String[] tags = new String[]{"plane"};
			fc.getTagedPhotos(2, tags);
			
			HashSet<String> extras = new HashSet<String>();
		    extras.add("license");
		    extras.add("original_format");
			String[] urls = fc.getPopularPhotos(2, 2013, 9, 26, extras);
			
			int i = 0;
			for(String e : urls){
				i++;				
		        saveImage(e, i + ".jpg");
			}
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FlickrException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}