package com.cvsgo.entity;

import static org.hibernate.query.sqm.tree.SqmNode.log;

import jakarta.persistence.AttributeConverter;

public class RatingConverter implements AttributeConverter<Rating, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Rating rating) {
        if (rating == null) return null;
        return rating.getRating();
    }

    @Override
    public Rating convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;
        try {
            return Rating.fromCode(dbData);
        } catch (IllegalArgumentException e) {
            log.error("failure to convert cause");
            throw e;
        }
    }
}
