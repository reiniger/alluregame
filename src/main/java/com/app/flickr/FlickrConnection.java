package com.app.flickr;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
	
	/** Method create Flickr class instance.
	@param key Account's flickr key.
	@param secret Account's flickr shared secret.
	*/
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
	
	/** Method returns String massive of photos URLs with specified tags.
	@param listSize Number of tagged photos you want to get from flickr.com.
	@param tags Photos with that tags you'll get.
	*/
	public PhotoList<Photo> getTagedPhotos(int listSize, String[] tags) throws IOException, SAXException, FlickrException{
		//initialize SearchParameter object, this object stores the search keyword
	    SearchParameters searchParams = new SearchParameters();
	    searchParams.setSort(SearchParameters.INTERESTINGNESS_DESC);
	    //make investigation
	   
	    //Setting tag keyword array
	    searchParams.setTags(tags);

	    //Initialize PhotosInterface object
	    PhotosInterface photosInterface = flickr.getPhotosInterface();
	    
	    /*
	     * Execute search with entered tags
	     * SearchParameters: 
	     * params - The search parameters
	     * perPage - The number of photos to show per page
	     * page - The page offset or results from page #
	     */
	    PhotoList<Photo> photoList = photosInterface.search(searchParams, listSize, 1);
	    
	    return photoList;
	}
	
	/** Method returns String massive of popular photos URLs related with specified date.
	@param listSize Number of tagged photos you want to get from flickr.com.
	@param year The YEAR calendar field.
	@param month The MONTH calendar field. Month value is 0-based. e.g., 0 for January.
	@param date The DAY_OF_MONTH calendar field.
	@param extras A comma-delimited list of extra information to fetch for each returned record. 
           Currently supported fields are: description, license, date_upload, date_taken, 
           owner_name, icon_server, original_format, last_update, geo, tags, machine_tags, 
           o_dims, views, media, path_alias, url_sq, url_t, url_s, url_q, url_m, url_n, url_z, 
           url_c, url_l, url_o
	*/
	public PhotoList<Photo> getPopularPhotos(int listSize, int year, int month, int day, Set<String> extras) throws FlickrException, HttpException, IOException{
	    //Date
		Calendar c = new GregorianCalendar();
		c.set(year, month, day);
		//c.add(Calendar.DAY_OF_YEAR, -1);
		Date date = new Date(c.getTimeInMillis());

	    PhotoList<Photo> photoList = flickr.getInterestingnessInterface().getList(date, extras, listSize, 1);

	    return photoList;
	}
}