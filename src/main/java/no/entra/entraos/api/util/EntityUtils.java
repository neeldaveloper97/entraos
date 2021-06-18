package no.entra.entraos.api.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class EntityUtils {

	static ObjectMapper objectMapper = new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature())
            .findAndRegisterModules();;

	public static JsonNode jsonObject_toJsonNode(JsonObject jsonObject) throws JsonMappingException, JsonProcessingException {

		// Parse a JsonObject into a JSON string
		StringWriter stringWriter = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
			jsonWriter.writeObject(jsonObject);
		}
		String json = stringWriter.toString();

		// Parse a JSON string into a JsonNode
		JsonNode jsonNode = objectMapper.readTree(json);
		return jsonNode;
	}

	public static <T> T jsonObject_toObject(Class<T> type, JsonObject jsonObject) throws JsonMappingException, JsonProcessingException {

		// Parse a JsonObject into a JSON string
		StringWriter stringWriter = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
			jsonWriter.writeObject(jsonObject);
		}
		String json = stringWriter.toString();
		return objectMapper.readValue(json, type);

	}

	public static <T> JsonObject object_toJsonObject(T jsonObject)  {
		try {
			String json = objectMapper.writeValueAsString(jsonObject);
			StringReader stringReader = new StringReader(json);
			try (JsonReader jsonWriter = Json.createReader(stringReader)) {
				return jsonWriter.readObject();
			}	
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return JsonObject.EMPTY_JSON_OBJECT;
		}
	}

	public static <T> JsonArray objectList_toJsonArray(List<T> obj) {
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		obj.forEach(p -> {
			jsonArrayBuilder.add(object_toJsonObject(p));
		});

		return jsonArrayBuilder.build();
	}


	public static String object_mapToJsonString(Object val) {
		try {
			return objectMapper.writeValueAsString(val);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			if (val instanceof Collection<?>){
				return "[]";
			}

			if (val instanceof Map<?,?>){
				return "{}";
			}


		}
		return "{}";
	}

	public static <T> T mapFromJson(String json, Class<T> clazz) throws JsonMappingException, JsonProcessingException
    {
		return objectMapper.readValue(json, clazz);
	}

	public static <T> T mapFromJson(String json, TypeReference<T> clazz)
			throws JsonProcessingException {
		if(json==null || json.isEmpty()) {
			return null;
		}
		return objectMapper.readValue(json, clazz);
	}





}
