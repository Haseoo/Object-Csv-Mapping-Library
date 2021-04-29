package com.github.haseoo.ocm.api;

import lombok.Value;


@Value
public class InMemoryCsvFile {
    String fileName;
    String[] data;

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append("InMemoryCsvFile:");
        builder.append(String.format("%nfileName:%s%n", fileName));
        builder.append("data:\n");
        for (String datum : data) {
            builder.append(datum);
            builder.append('\n');
        }
        return builder.toString();
    }
}