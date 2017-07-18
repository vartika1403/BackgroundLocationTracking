package com.example.vartikasharma.backgroundlocationtracking;


public class Helper {
    private static final String DIRECTION_API =
            "https://maps.googleapis.com/maps/api/directions/json?origin=";
    public static final String API_KEY = "AIzaSyCcdAWD65vqChfgWEd5KLc8JZW4a04qeDs";
    public static final int MY_SOCKET_TIMEOUT_MS = 5000;

    public static String getUrl(String originalLat, String originalLong,
                                String destinationLat, String destinationLong, boolean value) {
        return Helper.DIRECTION_API + originalLat + "," + originalLong +
                "&destination="+ destinationLat +","+ destinationLong + "&alternatives=true";
      /*  return Helper.DIRECTION_API +"75+9th+Ave+New+York,+NY" +"&destination=MetLife+Stadium+1+MetLife+Stadium+Dr+East+Rutherford,+NJ+07073"
                + "&departure_time=1541202457" + "&traffic_model=best_guess";*/
    }
}
