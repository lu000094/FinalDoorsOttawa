package com.algonquinlive.lu000094.doorsopenottawa.parsers;


import com.algonquinlive.lu000094.doorsopenottawa.model.Building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BuildingJSONParser {

    public static List<Building> parseFeed(String content) {

        try {
            JSONObject jsonResponse = new JSONObject(content);
            JSONArray buildingArray = jsonResponse.getJSONArray("buildings");
            List<Building> buildingList = new ArrayList<>();

            for (int i = 0; i < buildingArray.length(); i++) {

                JSONObject obj = buildingArray.getJSONObject(i);
                Building building = new Building();
                building.setBuildingId(obj.getInt("buildingId"));
                building.setName(obj.getString("name"));
                //building.setAddress(obj.getString("address")+",Ottawa, Ontario");
                //building.setAddress(obj.getString("address")+" Ottawa");
                building.setAddress(obj.getString("address"));
                building.setImage(obj.getString("image"));
                building.setDescription(obj.getString("description"));
                JSONArray open_hoursArray = obj.getJSONArray("open_hours");
                for(int j = 0; j < open_hoursArray.length(); j++){
                    JSONObject hourObj = open_hoursArray.getJSONObject(j);
                    try {
                        building.addDate(hourObj.getString("date"));
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
                building.setIsFavarite(0);
                buildingList.add(building);
            }

            return buildingList;
        } catch (JSONException e) {

            e.printStackTrace();
            return null;
        }
    }
}
