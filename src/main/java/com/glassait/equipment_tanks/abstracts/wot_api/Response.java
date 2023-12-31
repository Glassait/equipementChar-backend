package com.glassait.equipment_tanks.abstracts.wot_api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class Response<T> {
    private String status;
    private Meta meta;
    private Map<String, T> data;
}
