package ReadInDatabase;

import java.util.HashMap;
import java.util.Map;

public class GenreData {

        private Map<String, String> genreCodes;

        private void initializeGenres(){
            // Initialize the HashMap and populate it with the code-category pairs
            genreCodes = new HashMap<>();
            genreCodes.put("Susp", "Thriller");
            genreCodes.put("CnR", "Cops and Robbers");
            genreCodes.put("Dram", "Drama");
            genreCodes.put("West", "Western");
            genreCodes.put("Myst", "Mystery");
            genreCodes.put("S.F.", "Science Fiction");
            genreCodes.put("ScFi", "Science Fiction");
            genreCodes.put("Advt", "Adventure");
            genreCodes.put("Horr", "Horror");
            genreCodes.put("Romt", "Romantic");
            genreCodes.put("Comd", "Comedy");
            genreCodes.put("Musc", "Musical");
            genreCodes.put("Docu", "Documentary");
            genreCodes.put("Porn", "Pornography");
            genreCodes.put("Noir", "Noir");
            genreCodes.put("BioP", "Biographical Picture");
            genreCodes.put("TV", "TV show");
            genreCodes.put("TVs", "TV series");
            genreCodes.put("TVm", "TV miniseries");
            genreCodes.put("Actn", "Action");
            genreCodes.put("Disa", "Disaster");
            genreCodes.put("Epic", "Epic");
            genreCodes.put("Faml", "Family");
            genreCodes.put("AvGa", "Avant Garde");
            genreCodes.put("Cart", "Cartoon");
            genreCodes.put("Surl", "Sureal");
            genreCodes.put("Hist", "History");
            genreCodes.put("Ctxx", "Other");
        }
        public GenreData() {
            initializeGenres();
        }
        String name;

        public GenreData(String name) {
            initializeGenres();
            if(genreCodes.containsKey(name)){
                this.name = genreCodes.get(name);
            }
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            if(genreCodes.containsKey(name)){
                this.name = genreCodes.get(name);
            }
        }

        @Override
        public String toString() {
            return "Genre{" +
                    "name='" + name + '\'' +
                    '}';
        }
        public static void main(String[] args) {
            GenreData test = new GenreData("Dram");
            System.out.println(test);
        }
    }


