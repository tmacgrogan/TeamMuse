import java.util.Comparator;

public class MetadataComparator implements Comparator<Track>{
	String field;
	Boolean reverse = false;

	public MetadataComparator(String field){
		this.field = field;
	}
	

	public int compare(Track track0, Track track1) {
		int result = 0;
		if(field ==  "Name"){
			result = track0.getTitle().compareTo(track1.getTitle());
		}
		else if(field ==  "Artist"){
			result = track0.getArtist().compareTo(track1.getArtist());
		}
		else if(field ==  "Album"){
			result = track0.getAlbum().compareTo(track1.getAlbum());
		}
		else if(field ==  "Genre"){
			result = track0.getGenre().compareTo(track1.getGenre());
		}
		else if(field == "Date Added"){
			result = track0.getImportDate().compareTo(track1.getImportDate());
		}
		
		if(reverse) result *= -1;
		
		
		return result;
	}
	
	public String getField(){
		return field;
	}
	
	public void setField(String field){
		if(this.field == field) this.reverse();
		else {
			this.field = field;
			this.reverse = false;
		}
	}
	
	public void reverse(){
		reverse = !reverse;
	}	
}
