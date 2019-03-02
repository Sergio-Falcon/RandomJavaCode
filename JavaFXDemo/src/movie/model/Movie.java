package movie.model;

public class Movie {
	private String movieTitle;
	private int releaseYear;
	private String director;
	private String writer;
	private int rating;
	
	public Movie(String title, int releaseYear, String director, String writer, int rating) {
		this.movieTitle = title;
		this.releaseYear = releaseYear;
		this.director = director;
		this.writer = writer;
		this.rating = rating;
	}

	public String getMovieTitle() {
		return movieTitle;
	}

	public void setMovieTitle(String movieTitle) {
		this.movieTitle = movieTitle;
	}

	public int getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(int releaseYear) {
		this.releaseYear = releaseYear;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
}
