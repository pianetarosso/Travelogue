package it.Photos;

import java.io.Serializable;

// Classe necessaria per poter "salvare" la lista di immagini sulla SD
public class SimpleImage implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 */
	private String name;
	/**
	 */
	private int latitude=0;
	/**
	 */
	private int longitude=0;
	/**
	 */
	private String path="";
	/**
	 */
	private String address="";
	/**
	 */
	private long date;
	/**
	 */
	private long travelId;
	/**
	 */
	private long id;
	
	public SimpleImage() {}
	
	
	public void setTravelID(long id) {
		this.travelId=id;
	}
	
	/**
	 * @return
	 */
	public long getTravelId() {
		return travelId;
	}
	
	/**
	 * @return
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	public static SimpleImage parseToSimpleImages(Images i) {
		SimpleImage b = new SimpleImage();
		b.setAddress(i.getAddress());
		b.setDate(i.getDate());
		b.setLatitude(i.getLatitude());
		b.setLongitude(i.getLongitude());
		b.setName(i.getName());
		b.setPath(i.getPath());
		b.setTravelID(i.getTravelId());
		b.setId(i.getId());
		return b;
	}
	
	/**
	 * @return
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return
	 */
	public int getLatitude() {
		return latitude;
	}
	
	/**
	 * @return
	 */
	public int getLongitude() {
		return longitude;
	}
	
	/**
	 * @return
	 */
	public long getDate() {
		return date;
	}
	
	/**
	 * @return
	 */
	public String getPath() {
		return path;
	}
	
	
	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name=name;
	}
	
	/**
	 * @param address
	 */
	public void setAddress(String address) {
		this.address=address;
	}
	
	/**
	 * @param latitude
	 */
	public void setLatitude(int latitude) {
		this.latitude=latitude;
	}
	
	/**
	 * @param longitude
	 */
	public void setLongitude(int longitude) {
		this.longitude=longitude;
	}
	
	/**
	 * @param path
	 */
	public void setPath(String path) {
		this.path=path;
	}
	
	/**
	 * @param date
	 */
	public void setDate(long date) {
		this.date=date;
	}
}


