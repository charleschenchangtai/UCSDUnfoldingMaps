package module4;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
 
import parsing.ParseFeed;
import processing.core.PApplet;
//import processing.core.PGraphics;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
 	public class EarthquakeCityMap extends PApplet {	
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
		//使用顏色來區別地震強度
	    // Less than this threshold is a light earthquake
		public static final float THRESHOLD_MODERATE = 5;
		// Less than this threshold is a minor earthquake
		public static final float THRESHOLD_LIGHT = 4;

	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	//private UnfoldingMap map;
	private UnfoldingMap map;
	//private PGraphics pg;
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;
	// A List of country markers
	private List<Marker> countryMarkers;
 
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		if (offline) {
			  map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		map.zoomToLevel(2);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
		//earthquakesURL = "test1.atom";
		//earthquakesURL = "test2.atom";
		
		// WHEN TAKING THIS QUIZ: Uncomment the next line
		//earthquakesURL = "quiz1.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers 國家
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data 城市
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		// The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();
	    
		//     STEP 3: read in earthquake RSS feed 地震
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();	    

	    for(PointFeature feature : earthquakes) {
//	    	//adding some specified characters
//	    	markers.add(createMarker(feature));	
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));		    
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));		    
		  }
	    }

	    // could be used for debugging
	    printQuakes();
	 			    	    
	    
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	     //map.addMarkers(countryMarkers);
	     map.addMarkers(quakeMarkers);
	     map.addMarkers(cityMarkers);
	    // map.addMarkers(markers);
	    
	}  // End setup
	
	
//	private SimplePointMarker createMarker(PointFeature feature)
//	{  
//		SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
//		
//		Object magObj = feature.getProperty("magnitude");
//		float mag = Float.parseFloat(magObj.toString());		
//	    int yellow = color(255, 255, 0);		
//	    int blue=color(0,0,255);
//	    int red=color(255,0,0);
//		//Add code below to style the marker's size and color 
//	    // according to the magnitude of the earthquake.  
//	    // Don't forget about the constants THRESHOLD_MODERATE and 
//	    // THRESHOLD_LIGHT, which are declared above.
//	    // Rather than comparing the magnitude to a number directly, compare 
//	    // the magnitude to these variables (and change their value in the code 
//	    // above if you want to change what you mean by "moderate" and "light")
//	    //下面三個條件為題目描述:
//	    //Minor earthquakes (less than magnitude 4.0) will have blue markers and be small.	    
//	    if(mag<THRESHOLD_LIGHT) {
//	    	marker.setColor(blue);
//	    	marker.setRadius(10);
//	    }
//	    //Light earthquakes (between 4.0-4.9) will have yellow markers and be medium size.
//	    else if(mag<THRESHOLD_MODERATE) {
//	    		marker.setColor(yellow);
//	    		marker.setRadius(15);
//	    }
//	   //Moderate and higher earthquakes (5.0 and over) will have red markers and be largest.
//	    else {
//	    		marker.setColor(red);
//	    		marker.setRadius(20);
//	    }	    	    	    
//	    // Finally return the marker
//	    return marker;
//	}
	
	
	//this draw() method is only for layout of whole map
	public void draw() {
		background(0);
		map.draw();
		addKey();	
	}
	
	// helper method to draw key in GUI
	// TODO: Update this method as appropriate
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		rect(25, 50, 175, 250);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", 50, 75);
		
		fill(color(255, 0, 0));
		int TRI_SIZE=5;		 
		triangle(50, 125-TRI_SIZE, 50-TRI_SIZE,125+TRI_SIZE , 50+TRI_SIZE , 125+TRI_SIZE);
		fill(color(255, 255, 255));
		ellipse(50, 145, 10, 10); 
		
		fill(color(255, 255, 255));
		rect(50-5, 160-5, 10, 10);
		//底下為地震程度的顏色區別
		fill(color(255, 0, 0));
		ellipse(50, 205, 10, 10);
		fill(color(255, 255, 0));
		ellipse(50, 220, 10, 10);
		fill(color(0, 0, 255));
		ellipse(50, 235, 10, 10);
		fill(color(255, 255, 255));
		ellipse(50, 250, 10, 10);
		line(50-8,250-8,50+8,250+8);
		line(50+8,250-8,50-8,250+8);
		
		fill(0, 0, 0);
		text("City Marker", 75, 125);
		text("Land Quake", 75, 140);
		text("Ocean Quake", 75, 155);
		text("Size ~ Magnitude", 75, 185);
		text("Shallow", 75, 205);
		text("Medium", 75, 220);
		text("Deep", 75, 235);
		text("Past Day", 75, 250);
	}

	
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {
		
		
		// Loop over all the country markers.  
		// For each, check if the earthquake PointFeature is in the 
		// country in m.  Notice that isInCountry takes a PointFeature
		// and a Marker as input.  
		// If isInCountry ever returns true, isLand should return true.
		for (Marker m : countryMarkers) {
			 
			// TODO: Finish this method using the helper method isInCountry
			if(isInCountry(earthquake, m)){
				 		 
				return true;
			}
		}				
		// not inside any country
		return false;
	}
	
	/* prints countries with number of earthquakes as
	 * Country1: numQuakes1
	 * Country2: numQuakes2
	 * ...
	 * OCEAN QUAKES: numOceanQuakes
	 * */
	 
	private void printQuakes() 
	{
		
		// TODO: Implement this method
		// One (inefficient but correct) approach is to:
		//   Loop over all of the countries, e.g. using 
		//        for (Marker cm : countryMarkers) { ... }
		//        
		//      Inside the loop, first initialize a quake counter.
		//      Then loop through all of the earthquake
		//      markers and check to see whether (1) that marker is on land
		//     	and (2) if it is on land, that its country property matches 
		//      the name property of the country marker.   If so, increment
		//      the country's counter.
		
		// Here is some code you will find useful:
		// 
		//  * To get the name of a country from a country marker in variable cm, use:
		//     String name = (String)cm.getProperty("name");
		//  * If you have a reference to a Marker m, but you know the underlying object
		//    is an EarthquakeMarker, you can cast it:
		//       EarthquakeMarker em = (EarthquakeMarker)m;
		//    Then em can access the methods of the EarthquakeMarker class 
		//       (e.g. isOnLand)
		//  * If you know your Marker, m, is a LandQuakeMarker, then it has a "country" 
		//      property set.  You can get the country with:
		//        String country = (String)m.getProperty("country");
		 		 
		//總共地震標註數 		
		int totalWaterQuakes = quakeMarkers.size();
		//遍歷整個國家標註數
		for (Marker country : countryMarkers) {
			//在這一次的迴圈中,取得這一項國家名稱
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;			
			//遍歷整個地震標註數
			for (Marker marker : quakeMarkers)
			{
			    //使用Casting概念,因為我們要使用在 EarthquakeMarker 類別裡面的 isOnLand() 方法 
 				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
 				//如果地震發生處在陸地上
 				if (eqMarker.isOnLand()) {
 					//如果國家名稱.equal發生地震的國家名稱相符
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						  numQuakes++;
 					}
 				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake 
	// feature if it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}
