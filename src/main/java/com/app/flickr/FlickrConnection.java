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
	public String[] getTagedPhotos(int listSize, String[] tags) throws IOException, SAXException, FlickrException{
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
	    
	    //get search result and fetch the photo object and get imag's url
	    String[] urls = new String[listSize];
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
	public String[] getPopularPhotos(int listSize, int year, int month, int day, Set<String> extras) throws FlickrException, HttpException, IOException{
	    //Date
		Calendar c = new GregorianCalendar();
		c.set(year, month, day);
		//c.add(Calendar.DAY_OF_YEAR, -1);
		Date date = new Date(c.getTimeInMillis());

	    PhotoList<Photo> photoList = flickr.getInterestingnessInterface().getList(date, extras, listSize, 1);

	    //get search result and fetch the photo object and get imag's URL
	    String[] urls = new String[photoList.size()];
	    
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
			String[] urls = fc.getPopularPhotos(4, 2013, 9, 27, extras);
			
			int i = 0;
			for(String e : urls){
				i++;				
		        saveImage(e, i + ".jpg");
			}
			
			
		} catch (ParserConfigurationException e) {
			//Indicates a serious configuration error.
			e.printStackTrace();
			
			System.out.println(e.getMessage());
			
		} catch (IOException e) {
			/*
			 * Signals that an I/O exception of some sort has occurred. 
			 * This class is the general class of exceptions produced by 
			 * failed or interrupted I/O operations.
			 */
			e.printStackTrace();
			
			System.out.println(e.getMessage());
			
		} catch (FlickrException e) {
			e.getErrorCode();
			e.getErrorMessage();
			/*
			 *  1: Too many tags in ALL query 
					When performing an 'all tags' search, you may not specify more than 20 tags to join together.
				2: Unknown user
					A user_id was passed which did not match a valid flickr user.
				3: Parameterless searches have been disabled
					To perform a search with no parameters (to get the latest public photos, please use flickr.photos.getRecent instead).
				4: You don't have permission to view this pool
					The logged in user (if any) does not have permission to view the pool for this group.
				10: Sorry, the Flickr search API is not currently available.
					The Flickr API search databases are temporarily unavailable.
				11: No valid machine tags
					The query styntax for the machine_tags argument did not validate.
				12: Exceeded maximum allowable machine tags
					The maximum number of machine tags in a single query was exceeded.
				17: You can only search within your own contacts
					The call tried to use the contacts parameter with no user ID or a user ID other than that of the authenticated user.
				18: Illogical arguments
					The request contained contradictory arguments.
				100: Invalid API Key
					The API key passed was not valid or has expired.
				105: Service currently unavailable
					The requested service is temporarily unavailable.
				106: Write operation failed
					The requested operation failed due to a temporary issue.
				111: Format "xxx" not found
					The requested response format was not found.
				112: Method "xxx" not found
					The requested method was not found.
				114: Invalid SOAP envelope
					The SOAP envelope send in the request could not be parsed.
				115: Invalid XML-RPC Method Call
					The XML-RPC request document could not be parsed.
				116: Bad URL found
					One or more arguments contained a URL that has been used for abuse on Flickr.
			 */
			System.out.println(e.getMessage());
			
		} catch (SAXException e) {
			/*
			 * This class can contain basic error or warning information from either the 
			 * XML parser or the application: a parser writer or application writer can 
			 * subclass it to provide additional functionality. SAX handlers may throw 
			 * this exception or any exception subclassed from it.
			 * If the application needs to pass through other types of exceptions, 
			 * it must wrap those exceptions in a SAXException or an exception derived from a SAXException.
			 * If the parser or application needs to include information about a specific 
			 * location in an XML document, it should use the SAXParseException subclass.
			 */
			e.printStackTrace();
			
			System.out.println(e.getMessage());
			
		}
	}

}