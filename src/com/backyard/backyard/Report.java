package com.backyard.backyard;

import android.provider.BaseColumns;

public class Report {
	int _id;
	String _sector;
	String _subsector;
	String _desc;
	String _issue;
	String _timeinmills;
	String _latitude;
	String _longitude;
	String _company;
	String _photo;
	String _audio;
	String _video;
	public Report(){}
	// constructor
    public Report(int id, String sector, String subsector,String issue, String company ,String desc,String timeinmills, String latitude,String longitude,String photo,String video,String audio){
        this._id = id;
        this._sector = sector;
        this._subsector = subsector;
        this._desc = desc;
        this._issue = issue;
        this._timeinmills = timeinmills;
        this._latitude = latitude;
        this._longitude = longitude;
        this._company = company;
        this._photo = photo;
        this._audio = audio;
        this._video = video;
    }
 
    // constructor
    /*public Report(String sector, String description){
        this._sector = sector;
        this._description = description;
    }*/
 // getting name
    public String getSector(){
        return this._sector;
    }
    public String getDescription(){
        return this._desc;
    }
    public String getCompany(){
        return this._company;
    }
    public String getPhoto(){
        return this._photo;
    }
    public String getVideo()
    {
    	return this._video;
    }
    public String getAudio()
    {
    	return this._audio;
    }
    
    // getting name
    public String getTime(){
    	int timeinmills = ((int) System.currentTimeMillis()); 
        return this._timeinmills;
    }
    public int getID() {
        return this._id;
      }
    public void setSector(String sector) {
        this._sector = sector;
      }
    public void setId(int id) {
        this._id = id;
      }
    public void setIssue(String issue)
    {
    	
    	this._issue = issue;
    }
    public void setDesc(String desc)
    {
    	
    	this._desc = desc;
    }
    public void setLat(String lat)
    {
    	
    	this._latitude = lat;
    }
    public void setLon(String lon)
    {
    	
    	this._longitude= lon;
    }
    public void setPhoto(String photo)
    {
    	
    	this._photo= photo;
    }
    public void setVideo(String video)
    {
    	
    	this._video= video;
    }
    public void setAudio(String audio)
    {
    	
    	this._audio = audio;
    }
    public void setCompany(String company)
    {
    	
    	this._company= company;
    }

}
