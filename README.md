# JPublitio

Java language library for the [Publitio](https://publit.io/) website.

## Installing

This project uses [Maven](https://maven.apache.org/). Add the following dependency to your `pom.xml` file:

```xml
<dependency>
  <groupId>com.github.ennmichael</groupId>
  <artifactId>jpublitio</artifactId>
  <version>1.1</version>
</dependency>
```

Install instructions for other build systems can be found [here](https://search.maven.org/artifact/com.github.ennmichael/jpublitio/1.1/jar).

## Documentation

The docs for this project can be found [here](https://ennmichael.github.io/jpublitio/html/annotated.html).

## Example usage

```java
package com.mycompany.app;

import java.io.FileInputStream;
import java.util.Map;

import com.github.ennmichael.jpublitio.PublitioApi;

public class App 
{
    public static void main( String[] args )
    {
        // Find the API key and secret on your dashboard.
        var publitio = new PublitioApi("NsSyzZGG4NDkGbbkc44g", "6BWw4SalTm6qDnl6mH7gv54RtT9hpVuT");
        try {
            // Upload a new file.
            var input = new FileInputStream("/home/mogwai/other.png");
            var json = publitio.uploadFile("files/create", input);
            System.out.println(json);
            var fileId = json.getString("id");

            // Update the uploaded file info.
            json = publitio.put("/files/update/" + fileId, Map.of(
                "title", "A better title",
                "description", "A better description")
            );
            System.out.println(json);

            // Delete the uploaded file.
            json = publitio.delete("/files/delete/" + fileId);
            System.out.println(json);

            // List 5 files.
            json = publitio.get("files/list", Map.of("limit", "5"));
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Don't forget to close the PublitioApi instance.
                publitio.close();
            } catch(Exception e) {}
        }
    }
}
```
