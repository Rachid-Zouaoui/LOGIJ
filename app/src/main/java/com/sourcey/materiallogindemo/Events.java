package com.sourcey.materiallogindemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rachid Zouaoui on 04/03/2017.
 */

public class Events {

        private String name;
        private int numOfSongs;
        private int thumbnail;

        public Events() {
        }

        public Events(String name, int numOfSongs, int thumbnail) {
            this.name = name;
            this.numOfSongs = numOfSongs;
            this.thumbnail = thumbnail;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNumOfSongs() {
            return numOfSongs;
        }

        public void setNumOfSongs(int numOfSongs) {
            this.numOfSongs = numOfSongs;
        }

        public int getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(int thumbnail) {
            this.thumbnail = thumbnail;
        }
    }
