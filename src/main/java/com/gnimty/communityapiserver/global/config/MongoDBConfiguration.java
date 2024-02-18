package com.gnimty.communityapiserver.global.config;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoDBConfiguration {


    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
            new MongoOffsetDateTimeWriter(),
            new MongoOffsetDateTimeReader()
        ));
    }

    public class MongoOffsetDateTimeReader implements Converter<Document, OffsetDateTime> {

        @Override
        public OffsetDateTime convert(final Document document) {
            final Date dateTime = document.getDate(MongoOffsetDateTimeWriter.DATE_FIELD);
            final ZoneOffset offset = ZoneOffset.of(document.getString(MongoOffsetDateTimeWriter.OFFSET_FIELD));
            return OffsetDateTime.ofInstant(dateTime.toInstant(), offset);
        }
    }

    public class MongoOffsetDateTimeWriter implements Converter<OffsetDateTime, Document> {

        public static final String DATE_FIELD = "dateTime";
        public static final String OFFSET_FIELD = "offset";

        @Override
        public Document convert(final OffsetDateTime offsetDateTime) {
            final Document document = new Document();
            document.put(DATE_FIELD, Date.from(offsetDateTime.toInstant()));
            document.put(OFFSET_FIELD, offsetDateTime.getOffset().toString());
            return document;
        }

    }

}
