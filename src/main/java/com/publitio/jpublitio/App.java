package com.publitio.jpublitio;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        var publitio = new PublitioApi("NsSyzZGG4NDkGbbkc44g", "6BWw4SalTm6qDnl6mH7gv54RtT9hpVuT");
        try {
            var input = new FileInputStream("/home/blind-fuck/ya.png");
            var json = publitio.delete("f");
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                publitio.close();
            } catch(Exception e) {}
        }
    }
}
