package com.ms.inventory.infrastructure.adapters.out.http;

import lombok.Getter;

@Getter
public class JsonApiDocument<T>  {
    private Data<T> data;
    @Getter
    public static class Data<U> {
        private String id;
        private U attributes;
    }
}
