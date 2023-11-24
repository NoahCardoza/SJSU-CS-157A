package com.example.demo.servlets.amenities;

import com.example.demo.beans.entities.Amenity;
import com.example.demo.beans.entities.AmenityTypeAttribute;
import com.example.demo.daos.AmenityTypeAttributeDao;
import jakarta.servlet.ServletRequest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static com.example.demo.daos.AmenityTypeAttributeDao.TYPE_BOOLEAN_ID;

public class AmenitiesTypeAttributeBooleanGroup {

    Long amenityId;
    Long typeId;
    List<AmenityTypeAttribute> attributes;

    public AmenitiesTypeAttributeBooleanGroup(Amenity amenity) {
        this.amenityId = amenity.getId();
        this.typeId = amenity.getAmenityTypeId();
        this.attributes = new ArrayList<>();
    }

    public void add(AmenityTypeAttribute attribute) {
        this.attributes.add(attribute);
    }

    @Override
    public String toString() {
        if (attributes.isEmpty()) {
            return "";
        }

        String booleanAttributes = "";
        StringJoiner joiner = new StringJoiner(System.lineSeparator());

        try{
            //get the attribute id and then match it
            for(AmenityTypeAttribute attribute : attributes){
                //gets you the values from the database for the amenityType
                String value = AmenityTypeAttributeDao.getInstance().getAllValuesForAttribute(attribute.getId(), amenityId);

                // may need to change, i'm unsure if there can be multiple values for one attribute
                if(value.isEmpty()){
                    joiner.add(attribute.getName() + ": N/A");
                }
                else {
                    joiner.add(attribute.getName() + ": " + value);
                }
            }

            booleanAttributes = joiner.toString();

            return booleanAttributes;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}
