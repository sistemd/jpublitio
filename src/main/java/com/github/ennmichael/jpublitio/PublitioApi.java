package com.github.ennmichael.jpublitio;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParsingException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;

// XXX Be careful about what you have to close

/**
 * This class represents the main interface to the Publitio API.
 * 
 * <p>
 * This class implements Closeable. Make sure to call the close method
 * so that the underlying HTTP client will get closed.
 * </p>
 */
public class PublitioApi implements Closeable
{
    private final CloseableHttpClient client = HttpClients.createDefault();

    private final Random random = new Random();

    private final String key;

    private final String secret;

    /**
     * Create a new PublitioApi object for the given key and secret.
     * 
     * @param key Key used when authenticating with the API. You can find this on your
     *            Publitio dashboard at https://publit.io/dashboard.
     * @param secret Secret used when authenticating with the API. You can find this on your
     *               Publitio dashboard at https://publit.io/dashboard.
     */
    public PublitioApi(String key, String secret)
    {
        this.key = key;
        this.secret = secret;
    }

    public JsonObject get(String path) throws Exception
    {
        return get(path, null);
    }

    /**
     * Make a GET API call.
     * 
     * @param path Target endpoint path, such as "/files/list" or "files/show/<file_id>".
     * @param parameters Optional query parameters to send with the request.
     * @return Parsed JSON response.
     */
    public JsonObject get(String path, Map<String, String> parameters) throws Exception
    {
        var request = new HttpGet(createUri(path, parameters));
        var response = client.execute(request);
        try
        {
            return parseResponse(response);
        }
        finally
        {
            response.close();
        }
    }

    public JsonObject put(String path) throws Exception
    {
        return put(path, null);
    }

    /**
     * Make a PUT API call.
     * 
     * @param path Target endpoint path, such as "/files/update/<file_id>".
     * @param parameters Optional query parameters to send with the request.
     * @return Parsed JSON response.
     */
    public JsonObject put(String path, Map<String, String> parameters) throws Exception
    {
        var request = new HttpPut(createUri(path, parameters));
        var response = client.execute(request);
        try
        {
            return parseResponse(response);
        }
        finally
        {
            response.close();
        }
    }

    public JsonObject delete(String path) throws Exception
    {
        return delete(path, null);
    }

    /**
     * Make a DELETE API call.
     * 
     * @param path Target endpoint path, such as "/files/delete/<file_id>".
     * @param parameters Optional query parameters to send with the request.
     * @return Parsed JSON response.
     */
    public JsonObject delete(String path, Map<String, String> parameters) throws Exception
    {
        var request = new HttpDelete(createUri(path, parameters));
        var response = client.execute(request);
        try
        {
            return parseResponse(response);
        }
        finally
        {
            response.close();
        }
    }

    public JsonObject uploadFile(String path, InputStream input) throws Exception
    {
        return uploadFile(path, input, null);
    }

    /**
     * Upload a file to the given endpoint. This can be used with endpoints such as
     * "/files/create" and "watermarks/create".
     * 
     * @param path The target endpoint path.
     * @param parameters Optional query parameters to send with the request.
     * @return Parsed JSON response.
     */
    public JsonObject uploadFile(String path, InputStream input, Map<String, String> parameters) throws Exception
    {
        var request = new HttpPost(createUri(path, parameters));
        var entity = MultipartEntityBuilder.create()
            .addPart("file", new InputStreamBody(input, "file"))
            .build();
        request.setEntity(entity);
        var response = client.execute(request);
        try
        {
            return parseResponse(response);
        }
        finally
        {
            response.close();
        }
    }

    private URI createUri(String path, Map<String, String> parameters) throws Exception
    {
        var uriBuilder = new URIBuilder()
            .setScheme("https")
            .setHost("api.publit.io/v1")
            .setPath(path);
        if (parameters != null) {
            for (var entry : parameters.entrySet())
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
        }
        addAuthQuery(uriBuilder);
        return uriBuilder.build();
    }

    private void addAuthQuery(URIBuilder uriBuilder) throws Exception
    {
        var timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        var nonce = String.valueOf(random.nextInt(100000000 - 10000000) + 10000000);
        var signature = DigestUtils.sha1Hex(timestamp + nonce + secret);
        uriBuilder.addParameter("api_key", key);
        uriBuilder.addParameter("api_timestamp", timestamp);
        uriBuilder.addParameter("api_nonce", nonce);
        uriBuilder.addParameter("api_signature", signature);
    }

    private static JsonObject parseResponse(HttpResponse response) throws Exception
    {
        try
        {
            var reader = Json.createReader(response.getEntity().getContent());
            return reader.readObject();
        }
        catch (JsonParsingException e)
        {
            throw new JsonException("Failed to parse JSON properly. This might be because " +
                                    "you made an API call to an invalid endpoint, or an internal " +
                                    "server error occurred.");
        }
    }

    public void close() throws IOException
    {
        client.close();
    }
}
