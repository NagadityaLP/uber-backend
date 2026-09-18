package com.uberbackend.routing_service.provider.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GraphHopperResponse {

    private List<Path> paths;

    @Getter
    @Setter
    public static class Path {
        private double distance;
        private long time;
    }
}