/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.darcytech.demo.mysql.node.entity;

import java.time.LocalDate;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hotelId;

    private Integer reviewIndex;

    @Type(type = "IntEnum")
    private Rating rating;

    private LocalDate checkInDate;

    @Nullable
    @Type(type = "IntEnum")
    private TripType tripType;

    private String title;

    @Type(type = "Json")
    private ReviewDetails details;

    public Review(Long hotelId, Integer reviewIndex, ReviewDetails details) {
        this.hotelId = hotelId;
        this.reviewIndex = reviewIndex;
        this.rating = details.getRating();
        this.checkInDate = details.getCheckInDate();
        this.tripType = details.getTripType();
        this.title = details.getTitle();
        this.details = details;
    }

}

