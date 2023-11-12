
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
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

    public int inconsistent_entry = 0;
    public int duplicate_entry = 0;
    public static void main(String[] args) {
        MovieHandler spe = new MovieHandler();
        spe.runMain();
    }

    void runMain(){
        parseDocument();
        printMovies();
        printData();
    }

    public void printData() {
//        System.out.println("Valid Movies Found: " + movieList.size());
        System.out.println("\tDuplicate Movie Entries: " + duplicate_entry);
        System.out.println("\tInconsistent Movie Entries: " + inconsistent_entry);
    }
    public void printMovies(){
        for (MovieData movie: movieList) {
            System.out.println(movie.toString());
        }
        System.out.println(movieList.size());
        System.out.println(movieHashMap.size());
    }
    public void parseDocument(){
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            //parse the file and also register this class for call backs
            File movieFile = new File("stanford-movies/mains243.xml");
            InputStream inputStream= new FileInputStream(movieFile);
            Reader reader = new InputStreamReader(inputStream, "ISO-8859-1");

            InputSource is = new InputSource(reader);
            is.setEncoding("ISO-8859-1");
            sp.parse(is, this);

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
                if(currentMovie.getGenres().isEmpty()){
                    currentMovie.setInvalidEntry(true);
                }
                if(currentMovie.getTitle().toLowerCase().contains("Unknown".toLowerCase()) || currentMovie.getDirector().toLowerCase().contains("Unknown".toLowerCase())){
                    currentMovie.setInvalidEntry(true);
                }

                if(!currentMovie.isInvalidEntry() && !movieHashMap.containsKey(currentMovie.getId())){
                    movieList.add(currentMovie);
                    movieHashMap.put(currentMovie.getId(), currentMovie);
                } else if (!currentMovie.isInvalidEntry()) {
                    duplicate_entry++;
                } else if (!movieHashMap.containsKey(currentMovie.getId())) {
                    inconsistent_entry++;
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
