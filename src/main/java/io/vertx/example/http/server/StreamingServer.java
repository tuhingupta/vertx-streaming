package io.vertx.example.http.server;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.example.util.Runner;

public class StreamingServer extends AbstractVerticle {
	
	// Convenience method so you can run it in your IDE
	  public static void main(String[] args) {
	    Runner.runExample(StreamingServer.class);
	  }

	  
	  private static ObjectMapper mapper = new ObjectMapper();
	  private static JsonFactory factory = mapper.getJsonFactory();
	  
	  InputStream in = null;
	  JsonParser parser = null;
	  List<Object> results = new ArrayList<Object>();

	  @Override
	  public void start() throws Exception {
		
		System.out.println("Server started at localhost:8998 ...");
	    vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
	    	
	    	long byteswritten = 0;

			@Override
			public void handle(HttpServerRequest request) {
				
				
				request.handler(new Handler<Buffer>() {

					@Override
					public void handle(Buffer buffer) {
						
						byteswritten += buffer.length();
						
						System.out.println("--> "+buffer.toString());
						
						request.headers().set("content-length", String.valueOf(byteswritten));
						
						
						//---------------------jackson parser
							
						try {
							  in = this.getClass().getResourceAsStream("input.json");
							  parser = factory.createJsonParser(in);
							  parser.nextToken();// JsonToken.START_OBJECT
							  JsonToken token = null;
							  while( (token = parser.nextToken()) == JsonToken.FIELD_NAME ) {
							    String name = parser.getText();
							    parser.nextToken(); // JsonToken.START_OBJECT
							   // results.add(parser.readValueAs(classMap.get(name)));
							  }
							  // ASSERT: token = JsonToken.END_OBJECT
							}catch(Exception e){
							 e.printStackTrace();
							}
							finally {
							//  IOUtils.closeQuietly(in);
							  try {
							    parser.close();
							  }
							  catch( Exception e ) {}
							}
						
						///-------ends---jackson parser
						
						
					}
					
				
				});//request.handler
				
				request.endHandler(new Handler<Void>() {

					@Override
					public void handle(Void event) {
						
						System.out.println("reaching here..");
						
						request.response().setStatusCode(202).setStatusMessage("bytes written" + byteswritten);
						request.response().end();
						
						System.out.println("bytes written --> "+byteswritten);
						
					}
				
				});
				
				
			}
  	
	    	
	    }).listen(8998);
	  }

}
