	/*
	 * Copyright (c) 2018 Oleg Sklyar. All rights reserved
	 */

	import static org.junit.Assert.assertEquals;

	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.List;

	import org.junit.Test;

	import com.fasterxml.jackson.databind.MapperFeature;
	import com.fasterxml.jackson.databind.ObjectMapper;


	public class TestDeserialization50386188 {

		public static class Response {

			public static class ResponseDataType {
				public String name;

				public String siteId;

				public String type;

				public long x;

				public long y;
			}

			public int statusCode;

			public String message;

			public long executionTime;

			public List<ResponseDataType> ResponseData = new ArrayList<>();
		}

		private static final String data = "{\"StatusCode\":0,\"Message\":null,\"ExecutionTime\":0,\"ResponseData\":[{\"Name\":\"name1\",\"SiteId\":\"1234\",\"Type\":\"Type1\",\"X\":\"1234567\",\"Y\":\"123456\"},{\"Name\":\"Name2\",\"SiteId\":\"2134\",\"Type\":\"Type2\",\"X\":\"1234567\",\"Y\":\"1234567\"},{\"Name\":\"Name3\",\"SiteId\":\"3241\",\"Type\":\"Type3\",\"X\":\"1234567\",\"Y\":\"1234567\"},{\"Name\":\"Name4\",\"SiteId\":\"4123\",\"Type\":\"Type4\",\"X\":\"123456\",\"Y\":\"123456\"}]}";

		@Test
		public void deserialize_response_withJackson_ok() throws IOException {
			Response response = new ObjectMapper()
				.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
				.readValue(data, Response.class);
			assertEquals(4, response.ResponseData.size());
			assertEquals(1234567, response.ResponseData.get(2).x);
			assertEquals(1234567, response.ResponseData.get(2).y);
		}
	}
