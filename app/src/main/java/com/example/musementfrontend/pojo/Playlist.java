package com.example.musementfrontend.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Playlist implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String playlistUrl;
    private Map<Artist, Integer> artistSongCounts;

    public Playlist(int id, String title, String playlistUrl){
        this.id = id;
        this.title = title;
        this.playlistUrl = playlistUrl;
        this.artistSongCounts = new HashMap<>();

        // TODO remove and parse from spotify
        artistSongCounts.put(
                new Artist(1, "OG Buda", null, "https://distribution.faceit-cdn.net/images/0391fa6d-4dd9-457d-a29c-fcad2af9fc7d.jpeg", null),
                12);
        artistSongCounts.put(
                new Artist(2, "Алла Пугачева", null, "https://i.pinimg.com/originals/c9/f9/ca/c9f9ca3db229b70dd3e9e5bb759bf50b.png", null),
                7);
        artistSongCounts.put(
                new Artist(3, "Дима Билан", null, "https://news.store.rambler.ru/img/d5cc141755ac48d799718e2dffd0b00f?img-1-resize=width%3A1280%2Cheight%3A1280%2Cfit%3Acover&img-format=auto", null),
                5);
        artistSongCounts.put(
                new Artist(4, "Face", null, "https://static.life.ru/ip/unsafe/rs:fill:1200:/gravity:ce/q:100/czM6Ly9saWZlLXN0YXRpYy9wdWJsaWNhdGlvbnMvMjAyNC8xMi8xNC83MTIzOTM3MTM5MTguMDczNy53ZWJw", null),
                9);
        artistSongCounts.put(
                new Artist(5, "Слава КПСС", null, "https://avatars.mds.yandex.net/i?id=ada9c29fed59713ac9cc2a1feaf0d2ce_l-8210081-images-thumbs&n=13", null),
                10);
        artistSongCounts.put(
                new Artist(6, "Wham!", null, "https://avatars.mds.yandex.net/get-kinopoisk-image/1599028/6966db62-743a-4293-9dc7-1ac84498aa7b/1920x", null),
                3);
        artistSongCounts.put(
                new Artist(7, "Отпетые мощенники", null, "https://lastfm.freetls.fastly.net/i/u/ar0/356a934433849c24b1e19e7aaba6561d.jpg", null),
                1);
        artistSongCounts.put(
                new Artist(8, "Adele", null, "https://avatars.mds.yandex.net/i?id=02bf105d1ea9b211864f769f4eeb1d4a_l-3809718-images-thumbs&n=13", null),
                3);
        artistSongCounts.put(
                new Artist(9, "Frank Sinatra", null, "https://pbs.twimg.com/media/FIM2s-yWUAECdcu.png", null),
                1);
        artistSongCounts.put(
                new Artist(10, "Uma2rman", null, "https://show-biz.by/profile/image/profile_image/966/xxlarge/crop=auto/_v=1d5941467748005", null),
                6);

    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getPlaylistUrl() {
        return playlistUrl;
    }

    public List<Map.Entry<Artist, Integer>> getSortedArtistSongCounts() {
        List<Map.Entry<Artist, Integer>> sortedList = new ArrayList<>(artistSongCounts.entrySet());
        Collections.sort(sortedList, new Comparator<Map.Entry<Artist, Integer>>() {
            @Override
            public int compare(Map.Entry<Artist, Integer> e1, Map.Entry<Artist, Integer> e2) {
                int countCompare = e2.getValue().compareTo(e1.getValue());
                if (countCompare == 0) {
                    return e1.getKey().getName().compareTo(e2.getKey().getName());
                }
                return countCompare;
            }
        });
        return sortedList;
    }
}
