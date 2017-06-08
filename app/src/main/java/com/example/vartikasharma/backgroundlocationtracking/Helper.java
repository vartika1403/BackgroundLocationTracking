package com.example.vartikasharma.backgroundlocationtracking;


public class Helper {
    private static final String DIRECTION_API =
            "https://maps.googleapis.com/maps/api/directions/json?origin=";
    public static final String API_KEY = "AIzaSyCcdAWD65vqChfgWEd5KLc8JZW4a04qeDs";
    public static final int MY_SOCKET_TIMEOUT_MS = 5000;

    public static String getUrl(String originalLat, String originalLong,
                                String destinationLat, String destinationLong) {
        return Helper.DIRECTION_API +originalLat+ "," +originalLong+
                ",&destination="+destinationLat+","+destinationLong+"&key="+API_KEY;
    }
}
