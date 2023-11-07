package ReadInDatabase;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
public class MovieHandler extends DefaultHandler {
    private StringBuilder characters;
    private MovieData currentMovie;
    private GenreData currentGenre;

    public List<MovieData> getMovieList() {
        return movieList;
    }

    public HashMap<String, MovieData> getMovieHashMap() {
        return movieHashMap;
    }

    private HashMap<String, MovieData> movieHashMap;

    private List<MovieData> movieList;
    private String director;

    public static void main(String[] args) {
        MovieHandler spe = new MovieHandler();
        spe.runMain();
    }

    void runMain(){
        parseDocument();
        printMovies();
    }

    private void printMovies(){
        for (MovieData movie: movieList) {
            System.out.println(movie.toString());
        }
        System.out.println(movieList.size());
    }
    public void parseDocument(){
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            //parse the file and also register this class for call backs
            sp.parse("stanford-movies/mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public void startDocument() throws SAXException {
        movieList = new ArrayList<>();
        movieHashMap = new HashMap<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        characters = new StringBuilder();

        if ("film".equalsIgnoreCase(qName)) {
            currentMovie = new MovieData();
            currentMovie.setInvalidEntry(false);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("dirname".equalsIgnoreCase(qName)) {
            director = characters.toString();
        }

        try{
            if ("film".equalsIgnoreCase(qName)) {
                currentMovie.setDirector(director);
                if(!currentMovie.isInvalidEntry()){
                    movieList.add(currentMovie);
                    movieHashMap.put(currentMovie.getId(), currentMovie);
                }
            } else if ("t".equalsIgnoreCase(qName)) {
                currentMovie.setTitle(characters.toString().strip());
            }  else if ("year".equalsIgnoreCase(qName)) {
                currentMovie.setYear(Integer.parseInt(characters.toString().strip()));
            } else if ("fid".equalsIgnoreCase(qName) || "filmed".equalsIgnoreCase(qName)){
                currentMovie.setId(characters.toString().strip());
            } else if ("cat".equalsIgnoreCase(qName)){
                String[] parsedGenres = characters.toString().strip().split(" ");
                for (String g: parsedGenres){
                    GenreData test = new GenreData(g);
                    currentMovie.addGenre(test);
                }

            }
        }catch (Exception NumberFormatException){
            currentMovie.setInvalidEntry(true);
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        characters.append(new String(ch, start, length));
    }

    public List<MovieData> getMovies() {
        return movieList;
    }
}
