import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistTest {
    Playlist playlist;
    Playlist playlist2;
    Song yesterday = new Song("Yesterday",50,100,"jazz","happy",true);
    Song Mojito = new Song("Mojito",100,200,"pop","happy",false);
    Song Go = new Song("Go",10,300,"hip pop","angry",true);
    Song simple = new Song("simple",30,70,"jazz","relax",true);
    Song baby = new Song("baby",200,180,"pop","chill",false);
    @BeforeEach
    void setUp() {
        yesterday.setPerformer(new Artist("Beatles"));
        yesterday.setAlbum(new Album("Yesterday"));
        Mojito.setPerformer(new Artist("Jay"));
        Mojito.setAlbum(new Album("the greatest work of arts"));
        Go.setPerformer(new Artist("john"));
        Go.setAlbum(new Album("nothing to do"));

        playlist = new Playlist();
        playlist.addSong(yesterday);
        playlist.addSong(Mojito);
        playlist.addSong(Go);
    }

    @Test
    void deleteSong() {
        playlist.deleteSong(Go);
        assertEquals(2,playlist.getSongList().size());
    }

    @Test
    void merge() {
        playlist2 = new Playlist();
        playlist2.addSong(Mojito);
        playlist2.addSong(simple);
        playlist2.addSong(baby);

        System.out.println("----------before merging----------");
        System.out.println("----playlist has----");
        for (Song song:playlist.getSongList()) {
            System.out.println(song.getName());
        }
        System.out.println("----playlist2 has----");
        for (Song song:playlist2.getSongList()) {
            System.out.println(song.getName());
        }

        playlist.merge(playlist2);
        System.out.println("--------------after merging---------");
        for (Song song:playlist.getSongList()) {
            System.out.println(song.getName());
        }
    }

    @Test
    void sortByLikes() {
        playlist.sortByLikes();
        for (Song song:playlist.getSongList()) {
            System.out.println(song.getName()+" " +song.likes);
        }
    }

    @Test
    void shuffle() {
        System.out.println("------------before shuffle-----------");
        for (Song song:playlist.getSongList()) {
            System.out.println(song.getName());
        }

        playlist.shuffle();
        System.out.println("------------after shuffle-------------");
        for (Song song:playlist.getSongList()) {
            System.out.println(song.getName());
        }
    }

    @Test
    void createPlaylistBy() {
        playlist.addSong(simple);
        playlist.addSong(baby);

        System.out.println("--------------create by likes >= 50 --------------");
        Playlist likes50 = playlist.createPlaylistBy("likes","50");
        for (Song song: likes50.getSongList()) {
            System.out.println(song.getName()+": likes is " +song.likes);
        }

        System.out.println("--------------create by BPM >= 200 --------------");
        Playlist BPM200 = playlist.createPlaylistBy("BPM","200");
        for (Song song: BPM200.getSongList()) {
            System.out.println(song.getName()+": BPM is " +song.BPM);
        }

        System.out.println("--------------create by genre jazz --------------");
        Playlist genreJazz = playlist.createPlaylistBy("genre","jazz");
        for (Song song: genreJazz.getSongList()) {
            System.out.println(song.getName()+": genre is " +song.genre);
        }

        System.out.println("--------------create by mood happy --------------");
        Playlist moodHappy = playlist.createPlaylistBy("mood","happy");
        for (Song song: moodHappy.getSongList()) {
            System.out.println(song.getName()+": mood is " +song.mood);
        }

        System.out.println("--------------create by hasBeenPlayed --------------");
        Playlist hasbeenPlayed = playlist.createPlaylistBy("hasBeenPlayed","true");
        for (Song song: hasbeenPlayed.getSongList()) {
            System.out.println(song.getName()+": hasBeenPlayed " +song.hasBeenPlayed);
        }

    }

    @Test
    void playlistToXML() throws IOException {
        playlist.playlistToXML();

    }

    @Test
    void playlistToJSON() throws IOException {
        playlist.playlistToJSON();
    }
}